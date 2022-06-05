package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.nux.NuxGiftEmailViewMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.storage.queries.catalog.SlotMachineDao;
import com.cometproject.server.utilities.RandomUtil;


public class SlotMachineFloorItem extends RoomItemFloor {
    private boolean isInUse = false;

    public SlotMachineFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!this.getPosition().touching(entity.getPosition())) {
                entity.moveTo(this.getPosition().squareInFront(this.getRotation()).getX(), this.getPosition().squareBehind(this.getRotation()).getY());
                return false;
            }
        }

        entity.cancelWalk();
        entity.lookTo(this.getPosition().squareInFront(this.getRotation()).getX() - 1, this.getPosition().squareBehind(this.getRotation()).getY() - 1);

        if (this.isInUse) {
            ((PlayerEntity) entity).getPlayer().getSession().send(new WhisperMessageComposer(this.getVirtualId(), "Esta máquina está sendo usada, por favor espere.", 34));
            return false;
        }

        if(((PlayerEntity) entity).getPlayer().getData().getVipPoints() < ((PlayerEntity) entity).getBetAmount() || ((PlayerEntity) entity).getBetAmount() == 0){
            ((PlayerEntity) entity).getPlayer().getSession().send(new WhisperMessageComposer(this.getVirtualId(), "Você não tem o valor que deseja apostar ou sua aposta é 0. Ajuste sua aposta com :apostar (quantia)", 34));
            return false;
        }

        this.isInUse = true;
        boolean isWin = false;

        ((PlayerEntity) entity).getPlayer().getData().decreaseVipPoints(((PlayerEntity) entity).getBetAmount());

        String rand1 = "";
        String rand2 = "";
        String rand3 = "";

        int random1 = RandomUtil.getRandomInt(1, 3);
        int random2 = RandomUtil.getRandomInt(1, 3);
        int random3 = RandomUtil.getRandomInt(1, 3);

        if(random1 == random2 && random2 == random3 && random1 == random3) {
            int multiplier = 0;
            isWin = true;
            String image = "";

            switch (random1) {
                case 1:
                    multiplier = 10;
                    image = "bet_star";
                    break;
                case 2:
                    multiplier = 5;
                    image = "bet_heart";
                    break;
                case 3:
                    multiplier = 2;
                    image = "bet_skull";
                    break;
            }

            ((PlayerEntity) entity).getPlayer().getSession().send(new NotificationMessageComposer(image, Locale.getOrDefault("", "Você ganhou %q Diamantes com o caça-níqueis.\n\n(%b x %m)")
                    .replace("%q", ((PlayerEntity) entity).getBetAmount() * multiplier + "")
                    .replace("%b", ((PlayerEntity) entity).getBetAmount() + "")
                    .replace("%m", multiplier + "")));

            ((PlayerEntity) entity).getPlayer().getData().increaseVipPoints(((PlayerEntity) entity).getBetAmount() * multiplier);

        }

        switch (random1){
            case 1: rand1="¥"; break;
            case 2: rand1="|"; break;
            case 3: rand1="ª"; break;
        }

        switch (random2){
            case 1: rand2="¥"; break;
            case 2: rand2="|"; break;
            case 3: rand2="ª"; break;
        }

        switch (random3){
            case 1: rand3="¥"; break;
            case 2: rand3="|"; break;
            case 3: rand3="ª"; break;
        }

        ((PlayerEntity) entity).getPlayer().getSession().send(new TalkMessageComposer(entity.getId(), "Você tirou " + rand1 + " " + rand2 + " " + rand3 + ".", ChatEmotion.NONE, 34));
        ((PlayerEntity) entity).getPlayer().sendBalance();

        this.setTicks(RoomItemFactory.getProcessTime(1));

        this.getItemData().setData("1");
        this.sendUpdate();

        this.saveData();

        SlotMachineDao.insertBet(((PlayerEntity) entity).getPlayer().getData().getId(), "slot_machine", ((PlayerEntity) entity).getBetAmount() + "", Comet.getTime() + "", isWin ? "win" : "share");
        return true;
    }

        @Override
        public void onPlaced () {
            if (!"0".equals(this.getItemData().getData())) {
                this.getItemData().setData("0");
            }
        }

        @Override
        public void onPickup () {
            this.cancelTicks();
        }

        @Override
        public void onTickComplete () {
            this.isInUse = false;

            this.getItemData().setData("0");
            this.sendUpdate();

            this.saveData();
        }
}
