package com.cometproject.server.game.rooms.objects.items.types.floor.wired.base;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionShowMessage;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.*;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonOrEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonUnseenEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.negative.WiredNegativeConditionTriggererOnFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.WiredConditionTriggererOnFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOnFurni;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.wired.dialog.WiredTriggerMessageComposer;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.cometproject.server.utilities.RandomUtil;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WiredTriggerItem extends WiredFloorItem {
    private static final Logger log = LogManager.getLogger(WiredTriggerItem.class.getName());

    public WiredTriggerItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        boolean wasSuccess;

        if (this.suppliesPlayer() && entity == null) {
            return false;
        }

        //this.flash();

        wasSuccess = WiredTriggerItem.startExecute(entity, data, this.getItemsOnStack(), true);
        return wasSuccess;
    }

    public static boolean startExecute(RoomEntity entity, Object data, List<RoomItemFloor> items, boolean requiredConditions) {
        try {
            final List<WiredActionItem> wiredActions = Lists.newArrayList();
            final List<WiredConditionItem> wiredConditions = Lists.newArrayList();

            boolean useRandomEffect = false;
            WiredAddonUnseenEffect unseenEffectItem = null;
            boolean canExecute = true;
            boolean userNoItemsAnimationEffect = false;
            boolean playerNoAnimationEffect = false;
            boolean useOrEffect = false;

            for (final RoomItemFloor floorItem : items) {
                if (floorItem instanceof WiredActionItem)
                    wiredActions.add(((WiredActionItem) floorItem));

                if (requiredConditions) {
                    if (floorItem instanceof WiredConditionItem) {
                        wiredConditions.add((WiredConditionItem) floorItem);
                    } else if (floorItem instanceof WiredAddonUnseenEffect && unseenEffectItem == null) {
                        unseenEffectItem = ((WiredAddonUnseenEffect) floorItem);
                    } else if (floorItem instanceof WiredAddonRandomEffect) {
                        useRandomEffect = true;
                    } else if (floorItem instanceof WiredAddonNoItemsAnimateEffect) {
                        userNoItemsAnimationEffect = true;
                    } else if (floorItem instanceof WiredAddonNoPlayersAnimateEffect) {
                        playerNoAnimationEffect = true;
                    } else if (floorItem instanceof WiredAddonOrEffect) {
                        useOrEffect = true;
                    }
                }
            }

            if (unseenEffectItem != null && unseenEffectItem.getSeenEffects().size() >= wiredActions.size())
                unseenEffectItem.getSeenEffects().clear();


            final Map<String, AtomicBoolean> completedConditions = new HashMap<>();

            for (final WiredConditionItem conditionItem : wiredConditions) {
                //conditionItem.flash();
                if (conditionItem instanceof WiredConditionTriggererOnFurni && !(conditionItem instanceof WiredNegativeConditionTriggererOnFurni)) {
                    if (!completedConditions.containsKey(conditionItem.getClass() + ""))
                        completedConditions.put(conditionItem.getClass() + "", new AtomicBoolean(false));

                    if (conditionItem.evaluate(entity, data))
                        completedConditions.get(conditionItem.getClass() + "").set(true);
                } else {
                    completedConditions.put(conditionItem.getClass() + "" + conditionItem.getVirtualId(), new AtomicBoolean(false));
                    if (conditionItem.evaluate(entity, data))
                        completedConditions.get(conditionItem.getClass() + "" + conditionItem.getVirtualId()).set(true);
                }
            }

            for (final AtomicBoolean conditionState : completedConditions.values()) {
                if (!conditionState.get()) {
                    canExecute = false;
                    break;
                }
            }

            // if there is an "or" xtra effect then we check if just one condition is valid, if it's valid we can execute the effects!
            if(useOrEffect) {
                if(!completedConditions.isEmpty()) canExecute = true;
            }

            if (canExecute && wiredActions.size() >= 1) {
                boolean wasSuccess = false;

                if (useRandomEffect) {
                    final WiredActionItem actionItem = wiredActions.get(RandomUtil.getRandomInt(0, wiredActions.size() - 1));

                    if (actionItem != null) {
                        actionItem.setUseItemsAnimation(!userNoItemsAnimationEffect);
                        actionItem.setUsesPlayersAnimations(!playerNoAnimationEffect);

                        if (WiredTriggerItem.executeEffect(actionItem, entity, data)) {
                            wasSuccess = true;
                        }
                    }

                } else if (unseenEffectItem != null) {
                    final Comparator<WiredActionItem> comparator = (x, y) -> Double.compare(y.getPosition().getZ(), x.getPosition().getZ()); // ordre décroissant
                    wiredActions.sort(comparator.reversed());

                    for (final WiredActionItem actionItem : wiredActions) {
                        if (!unseenEffectItem.getSeenEffects().contains(actionItem.getId())) {
                            unseenEffectItem.getSeenEffects().add(actionItem.getId());

                            actionItem.setUseItemsAnimation(!userNoItemsAnimationEffect);
                            actionItem.setUsesPlayersAnimations(!playerNoAnimationEffect);

                            if (WiredTriggerItem.executeEffect(actionItem, entity, data)) {
                                wasSuccess = true;
                            }

                            break;
                        }
                    }
                } else {
                    final int limit = 4;
                    int executeActionEffectCount = 0;
                    int executeActionHanditemCount = 0;
                    int executeActionFreezeCount = 0;
                    int executeActionDanceCount = 0;
                    int executeActionFastwalkCount = 0;
                    int executeActionMsg = 0;

                    final Comparator<WiredActionItem> comparator = (x, y) -> Double.compare(y.getPosition().getZ(), x.getPosition().getZ()); // ordre décroissant
                    wiredActions.sort(comparator.reversed());

                    for (final WiredActionItem actionItem : wiredActions) {
                        if (actionItem.requiresPlayer() && entity == null)
                            continue;

                        if (actionItem instanceof WiredActionShowMessage || actionItem instanceof WiredCustomShowMessageRoom) {
                            if (executeActionMsg >= 8) {
                                continue;
                            }

                            executeActionMsg++;
                        }

                        if (actionItem instanceof WiredCustomEnable) {
                            if (executeActionEffectCount >= limit) {
                                continue;
                            }

                            executeActionEffectCount++;
                        }

                        actionItem.setUseItemsAnimation(!userNoItemsAnimationEffect);
                        actionItem.setUsesPlayersAnimations(!playerNoAnimationEffect);

                        if (actionItem instanceof WiredCustomHanditem) {
                            if (executeActionHanditemCount >= limit) {
                                continue;
                            }

                            executeActionHanditemCount++;
                        }

                        if (actionItem instanceof WiredCustomFreeze) {
                            if (executeActionFreezeCount >= limit) {
                                continue;
                            }

                            executeActionFreezeCount++;
                        }

                        if (actionItem instanceof WiredCustomDance) {
                            if (executeActionDanceCount >= limit) {
                                continue;
                            }

                            executeActionDanceCount++;
                        }

                        if (actionItem instanceof WiredCustomFastWalk) {
                            if (executeActionFastwalkCount >= limit) {
                                continue;
                            }

                            executeActionFastwalkCount++;
                        }

                        if (WiredTriggerItem.executeEffect(actionItem, entity, data)) {
                            wasSuccess = true;
                        }
                    }
                }

                return wasSuccess;
            }

        } catch (Exception e) {
            /*
            e.printStackTrace();
            log.error("Error during WiredTrigger evaluation", e);

             */
        }

        // tell the event that called the trigger that it was not a success!
        return false;
    }

    public static <T extends RoomItemFloor> List<T> getTriggers(Room room, Class<T> clazz) {
        final List<T> triggers = Lists.newArrayList();
        final List<Position> position = Lists.newArrayList();

        for (final RoomItemFloor floorItem : room.getItems().getByClass(clazz)) {
            final Position newPosition = new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY());

            if (!position.contains(newPosition) || floorItem instanceof WiredTriggerWalksOffFurni || floorItem instanceof WiredTriggerWalksOnFurni) {
                position.add(newPosition);
                triggers.add((T) floorItem);
            }
        }

        position.clear();
        return triggers;
    }

    private static boolean executeEffect(WiredActionItem actionItem, RoomEntity entity, Object data) {
        //actionItem.flash();
        return actionItem.evaluate(entity, data);
    }

    public MessageComposer getDialog() {
        return new WiredTriggerMessageComposer(this);
    }

    public List<WiredActionItem> getIncompatibleActions() {
        // create an empty list to add the incompatible actions
        final List<WiredActionItem> incompatibleActions = Lists.newArrayList();

        // check whether or not this current trigger supplies a player
        if (!this.suppliesPlayer()) {
            // if it doesn't, loop through all items on current tile
            for (final RoomItemFloor floorItem : this.getItemsOnStack()) {
                if (floorItem instanceof WiredActionItem) {
                    // check whether the item needs a player to perform its action
                    if (((WiredActionItem) floorItem).requiresPlayer()) {
                        // if it does, add it to the incompatible actions list!
                        incompatibleActions.add(((WiredActionItem) floorItem));
                    }
                }
            }
        }

        return incompatibleActions;
    }

    public abstract boolean suppliesPlayer();
}
