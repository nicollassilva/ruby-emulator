package com.cometproject.server.game.rooms.objects.items.types.floor.wired;

import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.memory.WiredMemoryBox;
import com.google.common.collect.Lists;
import org.checkerframework.checker.units.qual.A;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WiredMemoryUtil {
    public static final String ATTRIBUTE = "wired_memory_value";
    private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());

    public static double readMemoryFrom(PlayerEntity entity){
        return WiredMemoryUtil.parseDoubleOrZero(WiredMemoryUtil.getOrCreateMemoryValue(entity));
    }

    //TODO: if it's memory do this, otherwise parse text raw string
    public static double readMemoryFrom(WiredFloorItem box){
        return WiredMemoryUtil.parseDoubleOrZero(WiredMemoryUtil.getOrCreateMemoryValue(box).getExtraData());
    }

    public static void setMemoryInto(PlayerEntity entity, double value){
        entity.setAttribute(ATTRIBUTE, String.format("%.2f", value));
    }
    public static void setMemoryInto(WiredFloorItem box, double value){
        WiredItemSnapshot snapshot = WiredMemoryUtil.getOrCreateMemoryValue(box);
        snapshot.setExtraData(String.format("%.2f", value));
        box.getWiredData().getSnapshots().put(box.getId(), snapshot);
        box.saveData();
    }

    public static List<WiredMemoryBox> getMemoriesBoxFrom(WiredFloorItem wired) {
        final List<WiredMemoryBox> boxes = Lists.newArrayList();
        for (final long itemId : wired.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = wired.getRoom().getItems().getFloorItem(itemId);
            if (floorItem instanceof WiredMemoryBox) {
                boxes.add((WiredMemoryBox) floorItem);
            }
        }

        return boxes;
    }

    public static String getOrCreateMemoryValue(PlayerEntity entity) {
        if(entity.hasAttribute(ATTRIBUTE))
            return (String)entity.getAttribute(ATTRIBUTE);

        entity.setAttribute(ATTRIBUTE,"0");
        return (String)entity.getAttribute(ATTRIBUTE);
    }

    public static WiredItemSnapshot getOrCreateMemoryValue(WiredFloorItem wired) {
        if (wired.getWiredData().getSnapshots().containsKey(wired.getId()))
            return wired.getWiredData().getSnapshots().get(wired.getId());

        final WiredItemSnapshot snapshot = new WiredItemSnapshot(wired);
        snapshot.setExtraData(WiredMemoryUtil.doubleToString(WiredMemoryUtil.parseDoubleOrZero(wired.getWiredData().getText())));
        wired.getWiredData().getSnapshots().put(wired.getId(), snapshot);
        return snapshot;
    }

    public static double parseDoubleOrZero(String data) {
        try {
            return numberFormat.parse(data).doubleValue();
        } catch (Exception ignore) {
            return 0D;
        }
    }

    public static String doubleToString(double value) {
        try{
            return numberFormat.format(value);
        }
        catch (Exception ignore){
            return "0";
        }
    }
}
