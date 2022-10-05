package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.furniture.types.CrackableReward;
import com.cometproject.api.game.furniture.types.CrackableType;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.players.components.types.inventory.InventoryItem;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.storage.api.StorageContext;
import com.google.common.collect.Sets;
import org.apache.commons.lang.math.NumberUtils;

import java.util.concurrent.TimeUnit;

public class CrackableFloorItem extends RoomItemFloor {

    public CrackableFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (!NumberUtils.isNumber(this.getItemData().getData()))
            this.getItemData().setData("0");
    }

    @Override
    public boolean onInteract(RoomEntity entity, int state, boolean isWiredTrigger) {
        final CrackableReward crackableReward = ItemManager.getInstance().getCrackableRewards().get(this.getItemData().getItemId());

        if (crackableReward == null || crackableReward.getCrackableType() != CrackableType.CLICK) {
            return false;
        }

        if (isWiredTrigger || !(entity instanceof PlayerEntity)) {
            return false;
        }

        this.handleHit(entity, crackableReward);

        return true;
    }

    public void onEntityStepOn(RoomEntity entity) {
        if( !(entity instanceof PlayerEntity)) {
            return;
        }

        final CrackableReward crackableReward = ItemManager.getInstance().getCrackableRewards().get(this.getItemData().getItemId());
        if (crackableReward == null || crackableReward.getCrackableType() != CrackableType.STEPON) {
            return;
        }

        this.handleHit(entity, crackableReward);
    }

    @Override
    public void composeItemData(IComposer msg) {
        msg.writeInt(0);
        msg.writeInt(7);

        int state = Integer.parseInt(this.getItemData().getData());
        final CrackableReward crackableReward = ItemManager.getInstance().getCrackableRewards().get(this.getItemData().getItemId());

        if (crackableReward != null) {
            msg.writeString(this.calculateState(crackableReward.getHitRequirement(), state));
            msg.writeInt(state);//state
            msg.writeInt(crackableReward.getHitRequirement());//max
        } else {
            msg.writeString(this.calculateState(20, state));
            msg.writeInt(state);//state
            msg.writeInt(20);//max
        }
    }

    private void handleHit(RoomEntity entity, CrackableReward reward) {
        final Player player = ((PlayerEntity) entity).getPlayer();

        if(player.getData().getId() != this.getItemData().getOwnerId()) return;

        if(reward.getRequiredEffect() > 0 && entity.getCurrentEffect().getEffectId() != reward.getRequiredEffect())
            return;

        final Position posInFront = this.getPosition().squareInFront(this.getRotation());
        final boolean positionsIsEquals = posInFront.equals(entity.getPosition());

        if (!positionsIsEquals) {
            entity.moveTo(posInFront.getX(), posInFront.getY());
            return;
        }

        int hits = Integer.parseInt(this.getItemData().getData());
        int maxHits = reward.getHitRequirement();

        if (hits < maxHits) {
            hits++;
            this.getItemData().setData(hits);
            this.sendUpdate();

            if(hits == maxHits) {
                CometThreadManager.getInstance().executeSchedule(() -> this.giveReward(player, reward), 1, TimeUnit.SECONDS);
            }
        }
    }

    private void giveReward(Player player, CrackableReward reward) {
        // we're open!
        int result = reward.getRandomReward();
        switch (reward.getRewardType()) {
            case ITEM:
                // we need to turn into this item!
                final FurnitureDefinition itemDefinition = ItemManager.getInstance().getDefinition(result);

                if (itemDefinition != null) {
                    this.getRoom().getItems().removeItem(this, player.getSession(), false);

                    final PlayerItem playerItem = new InventoryItem(this.getId(), itemDefinition.getId(), "");
                    player.getInventory().addItem(playerItem);

                    this.getRoom().getItems().placeFloorItem(playerItem, this.getPosition().getX(), this.getPosition().getY(), this.getRotation(), player);

                    StorageContext.getCurrentContext().getRoomItemRepository().setBaseItem(playerItem.getId(), itemDefinition.getId());
                }
                break;

            case COINS:
                player.getData().increaseCredits(result);
                player.sendBalance();
                player.getData().save();
                break;

            case VIP_POINTS:
                player.getData().increaseVipPoints(result);
                player.sendBalance();
                player.getData().save();
                break;

            case ACTIVITY_POINTS:
                player.getData().increaseActivityPoints(result);
                player.sendBalance();
                player.getData().save();
                break;

            case BADGE:
                player.getInventory().addBadge(reward.getRewardData(), true, true);
        }
    }

    private int calculateState(int maxHits, int currentHits) {
        if(this.getDefinition().getItemName().contains("hween")) {
            return (int) Math.floor((1.0D / ((double) maxHits / (double) currentHits) * 20.0D));
        }
        if(this.getDefinition().getItemName().contains("pinata")) {
            return (int) Math.floor((1.0D / ((double) maxHits / (double) currentHits) * 8.0D));
        }
        return (int) Math.floor((1.0D / ((double) maxHits / (double) currentHits) * 14.0D));
    }
}
