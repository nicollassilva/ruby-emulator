package com.cometproject.server.game.utilities.validator;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.types.IClothingItem;
import com.cometproject.server.game.players.types.Player;
import com.google.common.collect.Lists;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ClothingValidationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClothingValidationManager.class);

    public static String FIGUREDATA_URL = "";
    public static boolean VALIDATE_ON_HC_EXPIRE = true;
    public static boolean VALIDATE_ON_LOGIN = true;
    public static boolean VALIDATE_ON_CHANGE_LOOKS = true;
    public static boolean VALIDATE_ON_MIMIC = true;
    public static boolean VALIDATE_ON_MANNEQUIN = true;
    public static boolean VALIDATE_ON_FBALLGATE = true;

    private static final FigureData FIGUREDATA = new FigureData();

    public static void reloadFiguredata(String newUrl) {
        try {
            FIGUREDATA.parseXML(newUrl);
        } catch (Exception e) {
            VALIDATE_ON_HC_EXPIRE = false;
            VALIDATE_ON_LOGIN = false;
            VALIDATE_ON_CHANGE_LOOKS = false;
            VALIDATE_ON_MIMIC = false;
            VALIDATE_ON_MANNEQUIN = false;
            VALIDATE_ON_FBALLGATE = false;
            LOGGER.error("Caught exception", e);
        }
    }

    public static String validateLook(Player player) {
        return validateLook(player.getData().getFigure(), FigureGender.fromString(player.getData().getGender()), player.getSubscription().isValid(), player.getWardrobe().getClothing());
    }

    public static String validateLook(Player player, String look, FigureGender gender) {
        return validateLook(look, gender, player.getSubscription().isValid(), player.getWardrobe().getClothing());
    }

    public static boolean isInvalidLook(Player player, String look, FigureGender gender) {
        final String[] newFigureParts = look.split("\\.");
        final List<String> parts = Lists.newArrayList(validateLook(look, gender, player.getSubscription().isValid()).split("\\."));
        for (final String newFigurePart : newFigureParts) { // We need this because validate() method shuffle the figure parts... workaround
            if(!parts.contains(newFigurePart))
                return true;
        }

        return false;
    }

    public static String validateLook(String look, FigureGender gender) {
        return validateLook(look, gender, false, new HashSet<>());
    }

    public static String validateLook(String look, FigureGender gender, boolean isHC) {
        return validateLook(look, gender, isHC, new HashSet<>());
    }

    public static String validateLook(String look, FigureGender gender, boolean isHC, Set<IClothingItem> clothing) {
       if(!CometSettings.FIGURE_VALIDATION)
           return look;

        if(FIGUREDATA.palettes.size() == 0 || FIGUREDATA.settypes.size() == 0)
            return look;


        try {
            String[] newLookParts = look.split(Pattern.quote("."));
            ArrayList<String> lookParts = new ArrayList<>();

            THashMap<SetType, String[]> parts = new THashMap<>();
            final Set<Integer> ownedClothing = new HashSet<>(clothing.size());
            for (final IClothingItem clothingItem : clothing) {
                for (int i = 0; i < clothingItem.getParts().length; i++) {
                    ownedClothing.add(clothingItem.getParts()[i]);
                }
            }

            // add mandatory settypes
            for (String lookpart : newLookParts) {
                if (lookpart.contains("-")) {
                    String[] data = lookpart.split(Pattern.quote("-"));
                    final SetType setType = SetType.fromString(data[0]);
                    FiguredataSettype settype = FIGUREDATA.settypes.get(setType);
                    if (settype != null) {
                        parts.put(setType, data);
                    }
                }
            }

            FIGUREDATA.settypes.entrySet().stream().filter(x -> !parts.containsKey(x.getKey())).forEach(x ->
            {
                FiguredataSettype settype = x.getValue();

                if (gender.equals(FigureGender.Male) && !isHC && !settype.mandatoryMale0)
                    return;

                if (gender.equals(FigureGender.Female) && !isHC && !settype.mandatoryFemale0)
                    return;

                if (gender.equals(FigureGender.Male) && isHC && !settype.mandatoryMale1)
                    return;

                if (gender.equals(FigureGender.Female) && isHC && !settype.mandatoryFemale1)
                    return;

                parts.put(x.getKey(), new String[]{x.getKey().getCode()});
            });


            parts.forEach((key, data) -> {
                try {
                    if (data.length >= 1) {
                        FiguredataSettype settype = FIGUREDATA.settypes.get(key);
                        if (settype == null) {
                            //throw new Exception("Set type " + data[0] + " does not exist");
                            return;
                        }

                        FiguredataPalette palette = FIGUREDATA.palettes.get(settype.paletteId);
                        if (palette == null) {
                            throw new Exception("Palette " + settype.paletteId + " does not exist");
                        }

                        int setId;
                        FiguredataSettypeSet set;


                        setId = Integer.parseInt(data.length >= 2 ? data[1] : "-1");
                        set = settype.getSet(setId);

                        if (set == null || (set.club && !isHC) || !set.selectable || (set.sellable && !ownedClothing.contains(set.id)) || (!set.gender.equals(FigureGender.Unisex) && !set.gender.equals(gender))) {
                            if (gender.equals(FigureGender.Male) && !isHC && !settype.mandatoryMale0)
                                return;

                            if (gender.equals(FigureGender.Female) && !isHC && !settype.mandatoryFemale0)
                                return;

                            if (gender.equals(FigureGender.Male) && isHC && !settype.mandatoryMale1)
                                return;

                            if (gender.equals(FigureGender.Female) && isHC && !settype.mandatoryFemale1)
                                return;

                            set = settype.getFirstNonHCSetForGender(gender);
                            setId = set.id;
                        }

                        ArrayList<String> dataParts = new ArrayList<>();

                        int color1 = -1;
                        int color2 = -1;

                        if (set.colorable) {
                            color1 = data.length >= 3 ? Integer.parseInt(data[2]) : -1;
                            FiguredataPaletteColor color = palette.getColor(color1);
                            if (color == null || (color.club && !isHC)) {
                                color1 = palette.getFirstNonHCColor().id;
                            }
                        }

                        if (data.length >= 4 && set.colorable) {
                            color2 = Integer.parseInt(data[3]);
                            FiguredataPaletteColor color = palette.getColor(color2);
                            if (color == null || (color.club && !isHC)) {
                                color2 = palette.getFirstNonHCColor().id;
                            }
                        }

                        dataParts.add(settype.type.getCode());
                        dataParts.add("" + setId);

                        if (color1 > -1) {
                            dataParts.add("" + color1);
                        }

                        if (color2 > -1) {
                            dataParts.add("" + color2);
                        }

                        lookParts.add(String.join("-", dataParts));
                    }
                } catch (Exception e) {
                    //habbo.alert(e.getMessage());
                    LOGGER.error("Error in clothing validation", e);
                }
            });

            return String.join(".", lookParts);
        }
        catch (Exception e){
            e.printStackTrace();
            return look;
        }
    }
}
