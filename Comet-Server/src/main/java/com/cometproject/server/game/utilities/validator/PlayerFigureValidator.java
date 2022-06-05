package com.cometproject.server.game.utilities.validator;

import com.cometproject.api.config.CometSettings;
import com.cometproject.server.boot.Comet;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Steve Winfield (IDK Project)
 */
public class PlayerFigureValidator {

    private static Map<Integer, Map<Integer, PlayerFigureColor>> palettes;
    private static Map<String, PlayerFigureSetType> setTypes;
    private static Map<String, Map<Integer, List<String>>> mandatorySetTypes;
    private static List<Integer> listexeptionsetmap;
    private static List<Integer> listexeptioncolors;

    public static boolean CheckFilterChar(String data) {
        final char[] letters = data.toLowerCase().toCharArray();
        final String allowedCharacters = "abcdefghijklmnopqrstuvwxyz1234567890-.";

        for (final char chr : letters) {
            if (!allowedCharacters.contains(chr + "")) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> setmapexeption() {
        List<Integer> data = new ArrayList<Integer>();
        data.add(99999);
        data.add(11090314);
        data.add(0);
        data.add(12090314);
        data.add(13090314);
        data.add(78322);
        data.add(5964);
        data.add(15863);
        data.add(1536);
        data.add(742327);
        data.add(7823);
        data.add(32);
        data.add(450);
        data.add(9767);
        data.add(9563333);
        data.add(2738000);
        data.add(3379);
        data.add(3375);
        data.add(3122);
        data.add(6661);
        data.add(3382);
        data.add(30);
        data.add(6662);
        data.add(71788);
        data.add(9002);
        data.add(3);
        data.add(2);
        data.add(318);
        data.add(140);
        data.add(874);
        data.add(305);
        data.add(33);
        data.add(316);
        data.add(8);
        data.add(200);
        data.add(3387);
        data.add(33811);
        data.add(30166666);
        data.add(91289496);
        data.add(91932046);
        data.add(3024);
        data.add(3388);
        data.add(33821);
        data.add(9921);
        data.add(595902031);
        data.add(3384);
        data.add(3376);
        data.add(2729);
        data.add(333555);
        data.add(999);
        data.add(20);
        data.add(329);
        data.add(160);
        data.add(12);
        data.add(616);
        data.add(956);
        data.add(334);
        data.add(332);
        data.add(7557);
        data.add(21015);
        data.add(322);
        data.add(9888);
        data.add(88188);
        data.add(757013);
        data.add(1);
        data.add(2911);
        data.add(317);
        data.add(31);
        data.add(307);
        data.add(100);
        data.add(757014);
        data.add(18);
        data.add(757012);
        data.add(201);
        data.add(326);
        data.add(321);
        data.add(9000);
        data.add(130903);
        data.add(9);
        data.add(306);
        data.add(181);
        data.add(337);
        data.add(335);
        data.add(301);
        data.add(9992);
        data.add(336);
        data.add(338);
        data.add(102);
        data.add(3373);
        data.add(33444121);
        data.add(88133305);
        data.add(3601111);
        data.add(30060);
        data.add(3380);
        data.add(30061);
        data.add(3381);
        data.add(3386);
        data.add(33810);
        data.add(33771);
        data.add(3385);
        data.add(595);
        data.add(3378);
        data.add(3374);
        data.add(3383);
        data.add(30010);
        data.add(583200);
        data.add(33850);
        data.add(33820);
        data.add(3377);
        data.add(1204);
        data.add(310);
        data.add(9999);
        data.add(6690);
        data.add(2799);
        data.add(3010);
        data.add(2459);
        data.add(7775);
        data.add(319);
        data.add(1000);
        data.add(308);
        data.add(4998);
        data.add(3494);
        data.add(7570);
        data.add(538);
        data.add(75700);
        data.add(90);
        data.add(921);
        data.add(320);
        data.add(3543);
        data.add(6);
        data.add(92115);
        data.add(5);
        data.add(20705);
        data.add(921152);
        data.add(161);
        data.add(55);
        data.add(99975);
        data.add(907);
        data.add(7105);
        data.add(14);
        data.add(314);
        data.add(999248);
        data.add(92);
        data.add(981199);
        data.add(2016);
        data.add(5994);
        data.add(327);
        data.add(25101);
        data.add(7);
        data.add(99);
        data.add(451);
        return data;
    }

    public static List<Integer> colorsexeption() {
        List<Integer> data = new ArrayList<Integer>();
        data.add(1);
        data.add(0);
        data.add(14);
        data.add(11);
        data.add(15363);
        data.add(1536);
        data.add(15863);
        data.add(1586);
        data.add(140);
        data.add(9);
        data.add(10);
        data.add(13);
        data.add(120);
        data.add(1318);
        data.add(15364);
        data.add(8);
        data.add(7);
        data.add(6);
        data.add(5);
        data.add(132);
        data.add(142);
        data.add(15);
        data.add(153);
        data.add(125);
        data.add(122);
        data.add(139);
        return data;
    }

    public static boolean isValidFigureCode(final String figureCode, final String genderCode) {
        if (!CometSettings.PLAYER_FIGURE_VALIDATION_ALLOW) {
            return true;
        }

        if (figureCode == null) {
            return false;
        }

        try {
            final String gender = genderCode.toLowerCase();

            if (!gender.equals("m") && !gender.equals("f")) {
                return false;
            }

            final String[] sets = figureCode.split("\\.");
            final List<String> mandatorySets = PlayerFigureValidator.mandatorySetTypes.get(gender).get(2);

            if (sets.length < mandatorySets.size()) {
                return false;
            }

            final List<String> containedSets = new ArrayList<>();

            for (final String set : sets) {
                final String[] setData = set.split("-");

                if (setData.length < 2) { //3 de base on passe a 2
                    return false;
                }

                final String setType = setData[0].toLowerCase();

                if (!PlayerFigureValidator.setTypes.containsKey(setType)) {
                    return false;
                }

                final PlayerFigureSetType setTypeInstance = PlayerFigureValidator.setTypes.get(setType);
                final Map<Integer, PlayerFigureSet> setMap = setTypeInstance.getSets();
                final int setId = Integer.parseInt(setData[1]);

                if (!setMap.containsKey(setId) && !listexeptionsetmap.contains(setId)) { //exception sur certains look
                    return false;
                }

                if (setData.length != 2 && !listexeptionsetmap.contains(setId)) // si look a 2 on passe pas le filtre couleurs
                {
                    int colorIdbug = 0;

                    if (setData.length > 3)
                        colorIdbug = Integer.parseInt(setData[3]);

                    if ((!listexeptioncolors.contains(colorIdbug)) && setData.length > 3) // exception couleurs
                    {
                        final PlayerFigureSet setInstance = setMap.get(setId);

                        if (!setInstance.isSelectable() || (setData.length - 2) < setInstance.getColorCount()) {
                            return false;
                        }

                        for (int i = 0; i < setInstance.getColorCount(); ++i) {
                            final int colorId = Integer.parseInt(setData[i + 2]);

                            if (!PlayerFigureValidator.palettes.get(setTypeInstance.getPaletteId()).containsKey(colorId)) {
                                return false;
                            }

                            final PlayerFigureColor colorInstance = PlayerFigureValidator.palettes.get(setTypeInstance.getPaletteId()).get(colorId);

                            if (!colorInstance.isSelectable()) {
                                return false;
                            }
                        }
                    }
                }

                containedSets.add(setType);
            }

            for (final String mandatorySet : mandatorySets) {
                if (!containedSets.contains(mandatorySet) && !mandatorySet.equals("lg")) {
                    return false;
                }
            }

            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}