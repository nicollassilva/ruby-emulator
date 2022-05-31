package com.cometproject.server.game.rooms.objects.entities.types;

import com.cometproject.api.game.pets.IPetData;
import com.cometproject.api.game.players.data.PlayerAvatar;
import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.pets.PetManager;
import com.cometproject.server.game.pets.data.PetMonsterPlantData;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.ai.BotAI;
import com.cometproject.server.game.rooms.objects.entities.types.ai.pets.PetAI;
import com.cometproject.server.game.rooms.objects.entities.types.ai.pets.PetGesture;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.avatar.LeaveRoomMessageComposer;
import com.cometproject.server.storage.queries.pets.PetDao;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PetEntity extends RoomEntity {
    private IPetData data;
    private PetAI ai;
    public boolean canSwim;
    private int freeCommandTicks = -1;

    private int cycleCount = 0;

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public PetEntity(IPetData data, int identifier, Position startPosition, int startBodyRotation, int startHeadRotation, Room roomInstance) {
        super(identifier, startPosition, startBodyRotation, startHeadRotation, roomInstance);

        this.data = data;
        this.ai = new PetAI(this);
    }

    public void setData(IPetData data) {
        this.data = data;
    }

    public void setAi(PetAI ai) {
        this.ai = ai;
    }

    @Override
    public boolean joinRoom(Room room, String password) {
        return true;
    }

    @Override
    protected void finalizeJoinRoom() {

    }

    @Override
    public void leaveRoom(boolean isOffline, boolean isKick, boolean toHotelView) {
        this.leaveRoom(false);
    }

    public void leaveRoom(boolean save) {
        if (save) {
            PetDao.savePosition(this.getPosition().getX(), this.getPosition().getY(), this.data.getId());
        }

        if (this.getMountedEntity() != null) {
            final RoomEntity entity = this.getMountedEntity();
            entity.getMountedEntity().setHasMount(false);

            this.setMountedEntity(null);

            entity.getPosition().setZ(entity.getTile().getWalkHeight());

            entity.updateVisibility(false);
            entity.updateVisibility(true);

            entity.markNeedsUpdate();
            //entity.moveTo(entity.getPosition().squareInFront(entity.getBodyRotation()));

            entity.setMountedEntity(null);
            entity.setHasMount(false);
            entity.applyEffect(null);
        }

        this.getRoom().getEntities().removeEntity(this);
        this.getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(this.getId()));
        this.attributes.clear();
    }

    @Override
    public boolean onChat(String message) {
        return false;
    }

    @Override
    public boolean onRoomDispose() {
//        PetDao.savePosition(this.getPosition().getX(), this.getPosition().getY(), this.data.getId());

        this.getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(this.getId()));

        this.attributes.clear();
        return true;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getMotto() {
        return null;
    }

    @Override
    public String getFigure() {
        return null;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.data.getId());
        msg.writeString(this.data.getName());
        msg.writeString("PET_MOTTO");

        String composer = data.getLook().toLowerCase() + " ";

        if (this.getData().getTypeId() == 15) {
            composer += new StringBuilder().append(this.getData().isSaddled() ? "3" : "2").append(" 2 ").append(this.getData().getHair()).append(" ").append(this.getData().getHairDye()).append(" 3 ").append(this.getData().getHair()).append(" ").append(this.getData().getHairDye()).append(this.getData().isSaddled() ? "0 4 9 0" : "").toString();
        } else {
            composer += "2 2 -1 0 3 -1 0";
        }

        msg.writeString(composer);
        msg.writeInt(this.getId());

        msg.writeInt(this.getPosition().getX());
        msg.writeInt(this.getPosition().getY());
        msg.writeDouble(this.getPosition().getZ());

        msg.writeInt(0); // 2 = user 4 = bot 0 = pet ??????
        msg.writeInt(2); // 1 = user 2 = pet 3 = bot ??????

        msg.writeInt(this.data.getRaceId());

        msg.writeInt(this.data.getOwnerId());
        msg.writeString(this.data.getOwnerName());
        msg.writeInt(1);
        msg.writeBoolean(true); // has saddle
        msg.writeBoolean(this.hasMount()); // has rider?

        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeString("");
    }

    public void composeUpdate(IComposer msg) {
        final boolean isMonsterPlant = this instanceof MonsterPlantEntity;

        msg.writeInt(this.getId());
        msg.writeInt(this.getData().isAnyRider() ? 1 : 0);
        msg.writeBoolean((isMonsterPlant && ((PetMonsterPlantData) this.getData()).canBreed()));
        msg.writeBoolean((isMonsterPlant && ((PetMonsterPlantData) this.getData()).isFullyGrown()));
        msg.writeBoolean((isMonsterPlant && ((PetMonsterPlantData) this.getData()).isDead()));
        msg.writeBoolean(false);
    }

    public void composeInformation(IComposer msg) {
        if (!(this instanceof MonsterPlantEntity)) {
            return;
        }

        final PetMonsterPlantData pet = ((PetMonsterPlantData) this.getData());

        msg.writeInt(pet.getId());
        msg.writeString(pet.getPlantName());
        msg.writeInt(pet.getGrowthStage());
        msg.writeInt(pet.getGrowthStage());
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(pet.getOwnerId());
        msg.writeInt(this.daysSinceBirthday(pet.getBirthday()));
        msg.writeString(PlayerManager.getInstance().getAvatarByPlayerId(pet.getOwnerId(), PlayerAvatar.USERNAME_FIGURE).getUsername());
        msg.writeInt(pet.getRarity());
        msg.writeBoolean(false);
        msg.writeBoolean(false);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeBoolean(pet.canBreed());
        msg.writeBoolean(pet.isFullyGrown());
        msg.writeBoolean(pet.isDead());
        msg.writeInt(pet.getRarity());
        msg.writeInt(pet.getBody().getLifeTime());// time to live
        msg.writeInt(pet.getLastFood()); //
        msg.writeInt(pet.remainingGrowTime());
        msg.writeBoolean(pet.isActive());
    }

    public IPetData getData() {
        return data;
    }

    public int getCycleCount() {
        return this.cycleCount;
    }

    public void decrementCycleCount() {
        cycleCount--;
    }

    public void incrementCycleCount() {
        cycleCount++;
    }

    public void resetCycleCount() {
        this.cycleCount = 0;
    }

    @Override
    public BotAI getAI() {
        return ai;
    }

    public PetAI getPetAI() {
        return this.ai;
    }

    @Override
    public void setAttribute(String attributeKey, Object attributeValue) {
        if (this.attributes.containsKey(attributeKey)) {
            this.attributes.replace(attributeKey, attributeValue);
        } else {
            this.attributes.put(attributeKey, attributeValue);
        }
    }

    public int daysSinceBirthday(long birthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday * 1000L);
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeInMillis(System.currentTimeMillis());
        return newCalendar.get(6) - calendar.get(6);
    }

    @Override
    public void onReachedTile(RoomTile tile) {
    }

    @Override
    public Object getAttribute(String attributeKey) {
        return this.attributes.get(attributeKey);
    }

    @Override
    public boolean hasAttribute(String attributeKey) {
        return this.attributes.containsKey(attributeKey);
    }

    @Override
    public void removeAttribute(String attributeKey) {
        this.attributes.remove(attributeKey);
    }
}
