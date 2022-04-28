package com.cometproject.server.game.gamecenter.games.battleball.room;

import com.cometproject.api.game.rooms.models.CustomFloorMapData;
import com.cometproject.api.game.rooms.settings.RoomAccessType;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.gamecenter.games.battleball.BattleBall;
import com.cometproject.server.game.gamecenter.games.battleball.items.SpawnPoint;
import com.cometproject.server.game.gamecenter.games.battleball.util.RoomStatus;
import com.cometproject.server.game.gamecenter.games.battleball.util.Teams;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.items.SendFloorItemMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;
import com.cometproject.server.storage.queries.rooms.RoomItemDao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BattleBallRoom {

    public int roomId;
    public int result;
    public RoomStatus status;
    public int timeToStart;
    public int turn;
    public int winner;

    public String owner;
    public Room map;
    public int mapId;
    public int pickedTeam = 0;

    public boolean lobbyFull;

    public final Map<Integer, Session> players = new ConcurrentHashMap<Integer, Session>(BattleBall.GAME_MAX_PLAYERS);

    public SpawnPoint spawnsBlue;
    public SpawnPoint spawnsRed;
    public SpawnPoint spawnsGreen;
    public SpawnPoint spawnsYellow;

    public static Teams[] teamList = {};


    public BattleBallRoom(int id) {
        super();

        roomId = id;

        loadMap("Coral Beach");

    }

    public Room getRoom() {
        return map;
    }

    public void deleteRoom() {
        for(Map.Entry<Long, RoomItemFloor> item : this.getRoom().getItems().getFloorItems().entrySet()) {
            RoomItemDao.deleteItem(item.getKey());
        }
        RoomDao.deleteRoom(this.getRoom().getId());
    }

    public void addItem(int id, int baseId, int x, int y, int rot, double height, String data) {
        //RoomItemDao.addFloorItem(map.getId(), x, y, height, rot, data, baseId);

        RoomItemFloor floorItem = map.getItems().addFloorItem(id, baseId, map, 0, "Battle Ball", x, y, rot, height, data, null);

        final RoomTile tileInstance = map.getMapping().getTile(floorItem.getPosition().getX(), floorItem.getPosition().getY());

        if (tileInstance != null) {
            tileInstance.reload();

            map.getEntities().broadcastMessageModeBuild(tileInstance);
        }


        map.getEntities().broadcastMessage(new SendFloorItemMessageComposer(floorItem));

        floorItem.onPlaced();
        floorItem.saveData();
    }

    public void loadMap(String name) {

        switch(name) {
            case "Coral Beach":

                String model = "" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "xtxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "xxxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "xxxxxxxx0000000000000000000xxxxxxx\r" +
                        "xxxxxxxx0000000000000000000xxxxxxx\r" +
                        "xxxxxxxx0000000000000000000xxxxxxx\r" +
                        "xxxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "x2222xxx00000000xxx00000000xxx2222\r" +
                        "x2222xxx00000000xxx00000000xxx2222\r" +
                        "x222221000000000xxx000000000122222\r" +
                        "x222221000000000xxx000000000122222\r" +
                        "x2222xxx00000000xxx00000000xxx2222\r" +
                        "x2222xxx00000000xxx00000000xxx2222\r" +
                        "xxxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "xxxxxxxx0000000000000000000xxxxxxx\r" +
                        "xxxxxxxx0000000000000000000xxxxxxx\r" +
                        "xxxxxxxx0000000000000000000xxxxxxx\r" +
                        "xxxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "xxxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "xxxxxxxx00000000xxx00000000xxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\r";


                int creationTime = (int) Comet.getTime();

                mapId = RoomManager.getInstance().createRoom(name, "Battle Ball game arena", new CustomFloorMapData(1, 2, 2, model, 1), 9, 8, 0, creationTime,0, 0, "", true);
                map = RoomManager.getInstance().get(mapId);
                map.getData().setAccess(RoomAccessType.INVISIBLE);

                map.load();

                for(int i = 8; i <= 13; ++i) {
                    for (int j = 1; j <= 4; ++j) {
                        this.addItem((int) (Math.random() * (999999999 - 9999999)), 1000011315, j, i, 0, 2, "0");
                    }
                }

                for(int i = 1; i <= 20; ++i) {
                    for (int j = 8; j <= 15; ++j) {
                        this.addItem((int) (Math.random() * (999999999 - 9999999)), 1000011315, j, i, 0, 0, "0");
                    }
                }

                for(int i = 1; i <= 20; ++i) {
                    for (int j = 19; j <= 26; ++j) {
                        this.addItem((int) (Math.random() * (999999999 - 9999999)), 1000011315, j, i, 0, 0, "0");
                    }
                }

                for(int i = 8; i <= 13; ++i) {
                    for (int j = 30; j <= 33; ++j) {
                        this.addItem((int) (Math.random() * (999999999 - 9999999)), 1000011315, j, i, 0, 2, "0");
                    }
                }

//                this.addItem((int) (Math.random() * (999999999 - 9999999)), 399996, 1, 2, 1, 29, "state\t0\timageUrl\t/public/swf/c_images/background/coral_beach_trees.png\toffsetX\t-505\toffsetY\t1977\toffsetZ\t13000");
//
//                this.addItem((int) (Math.random() * (999999999 - 9999999)), 399996, 1, 2, 1, 29, "state\t0\timageUrl\t/public/swf/c_images/background/coral_beach.png\toffsetX\t-505\toffsetY\t1977\toffsetZ\t11700");
//
//                this.addItem((int) (Math.random() * (999999999 - 9999999)), 31484, 1, 2, 1, 29, "137;#;51;#;146");

                spawnsRed = new SpawnPoint(2, 10, 2,2);
                spawnsBlue = new SpawnPoint(32, 10, 2,6);

                teamList = new Teams[]{
                        Teams.RED,
                        Teams.BLUE
                };


                map.setAttribute("bb_game", true);

                break;

            case "test":

                mapId = 207;
                map = RoomManager.getInstance().get(mapId);
                map.getData().setAccess(RoomAccessType.INVISIBLE);

                map.load();


                spawnsRed = new SpawnPoint(2, 10, 2,2);
                spawnsBlue = new SpawnPoint(32, 10, 2,6);

                teamList = new Teams[]{
                        Teams.RED,
                        Teams.BLUE
                };


                map.setAttribute("bb_game", true);

                break;
        }
    }



}
