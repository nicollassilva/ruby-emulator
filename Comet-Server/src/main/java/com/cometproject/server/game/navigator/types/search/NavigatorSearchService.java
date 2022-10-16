package com.cometproject.server.game.navigator.types.search;

import com.cometproject.api.config.Configuration;
import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.groups.types.IGroupData;
import com.cometproject.api.game.players.data.components.messenger.IMessengerFriend;
import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.api.game.rooms.settings.RoomAccessType;
import com.cometproject.server.game.navigator.NavigatorManager;
import com.cometproject.server.game.navigator.types.Category;
import com.cometproject.server.game.navigator.types.publics.PublicRoom;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomPromotion;
import com.cometproject.server.network.messages.outgoing.navigator.updated.NavigatorSearchResultSetMessageComposer;
import com.cometproject.server.tasks.CometTask;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NavigatorSearchService implements CometTask {
    private static NavigatorSearchService searchServiceInstance;

    private Executor searchExecutor = Executors.newFixedThreadPool(Integer.parseInt(Configuration.currentConfig().get("comet.system.navigatorSearchThreads")));

    public NavigatorSearchService() {
//        CometThreadManager.getInstance().executePeriodic(this, 0, 3000, TimeUnit.MILLISECONDS);
    }

    public static List<IRoomData> order(List<IRoomData> rooms, int limit) {
        try {
            rooms.sort((room1, room2) -> {
                boolean is1Active = RoomManager.getInstance().isActive(room1.getId());
                boolean is2Active = RoomManager.getInstance().isActive(room2.getId());

                    return ((!is2Active ? 0 : RoomManager.getInstance().get(room2.getId()).getEntities().playerCount()) -
                            (!is1Active ? 0 : RoomManager.getInstance().get(room1.getId()).getEntities().playerCount()));
            });
        } catch (Exception ignored) {

        }

        List<IRoomData> returnRooms = new LinkedList<>();

        for (IRoomData roomData : rooms) {
            if (returnRooms.size() >= limit) {
                break;
            }

            returnRooms.add(roomData);
        }

        return returnRooms;
    }

    public static NavigatorSearchService getInstance() {
        if (searchServiceInstance == null) {
            searchServiceInstance = new NavigatorSearchService();
        }

        return searchServiceInstance;
    }

    @Override
    public void run() {
        // TODO: Cache navigator search results.
    }

    public void submitRequest(Player player, String category, String data) {
        this.searchExecutor.execute(() -> {
            if (data.isEmpty()) {
                // send categories.
                List<Category> categoryList = Lists.newArrayList();

                for (Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                    if (navigatorCategory.getCategory().equals(category)) {
                        if (navigatorCategory.isVisible() && !navigatorCategory.getCategoryType().toString().equalsIgnoreCase("with_rights") && !navigatorCategory.getCategoryType().toString().equalsIgnoreCase("with_friends") && !navigatorCategory.getCategoryType().toString().equalsIgnoreCase("my_groups") && !navigatorCategory.getCategoryType().toString().equalsIgnoreCase("my_friends_rooms"))
                            categoryList.add(navigatorCategory);
                    }

                    if (category.equals("myworld_view")) {
                        if (navigatorCategory.getCategoryType().toString().equalsIgnoreCase("my_friends_rooms")) {
                            boolean friendsRoomsNotEmpty = false;

                            for (IMessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                                if (friendsRoomsNotEmpty) {
                                    continue;
                                }

                                if (messengerFriend.isInRoom()) {
                                    final PlayerEntity friendEntity = (PlayerEntity) messengerFriend.getSession().getPlayer().getEntity();
                                    if (friendEntity != null) {
                                        if (friendEntity.getRoom().getData().getOwnerId() == friendEntity.getPlayerId()) {
                                            if (!checkRoomVisibility(player, friendEntity.getRoom())) continue;

                                            friendsRoomsNotEmpty = true;
                                        }
                                    }
                                }
                            }

                            if (friendsRoomsNotEmpty) {
                                categoryList.add(navigatorCategory);
                            }
                        }

                        if (navigatorCategory.getCategoryType().toString().equalsIgnoreCase("with_friends")) {
                            boolean withFriendsRoomsNotEmpty = false;

                            for (IMessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                                if (withFriendsRoomsNotEmpty) {
                                    continue;
                                }

                                if (messengerFriend.isInRoom()) {
                                    PlayerEntity friendEntity = (PlayerEntity) messengerFriend.getSession().getPlayer().getEntity();

                                    if (friendEntity != null && !friendEntity.getPlayer().getSettings().getHideOnline()) {
                                        if (!checkRoomVisibility(player, friendEntity.getRoom())) continue;

                                        withFriendsRoomsNotEmpty = true;
                                    }
                                }
                            }

                            if (withFriendsRoomsNotEmpty) {
                                categoryList.add(navigatorCategory);
                            }
                        }

                        if (navigatorCategory.getCategoryType().toString().equalsIgnoreCase("my_groups")) {
                            boolean groupHomeRoomsNotEmpty = false;

                            for (int groupId : player.getGroups()) {
                                if (groupHomeRoomsNotEmpty) {
                                    continue;
                                }

                                IGroupData groupData = GameContext.getCurrent().getGroupService().getData(groupId);

                                if (groupData != null) {
                                    IRoomData roomData =
                                            GameContext.getCurrent().getRoomService().getRoomData(groupData.getRoomId());

                                    if (roomData != null) {
                                        groupHomeRoomsNotEmpty = true;
                                    }
                                }
                            }

                            if (groupHomeRoomsNotEmpty) {
                                categoryList.add(navigatorCategory);
                            }
                        }

                        if (navigatorCategory.getCategoryType().toString().equalsIgnoreCase("with_rights") && player.getRoomsWithRights().size() > 0) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                if (categoryList.size() == 0) {
                    for (Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                        if (navigatorCategory.getCategoryType().toString().toLowerCase().equals(category) && navigatorCategory.isVisible()) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                if (categoryList.size() == 0) {
                    for (Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                        if (navigatorCategory.getCategoryId().equals(category) && navigatorCategory.isVisible()) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                player.getSession().send(new NavigatorSearchResultSetMessageComposer(category, data, categoryList, player));
            } else {
                player.getSession().send(new NavigatorSearchResultSetMessageComposer("hotel_view", data, null, player));
            }
        });
    }

    public List<IRoomData> search(Category category, Player player, boolean expanded) {
        List<IRoomData> rooms = Lists.newCopyOnWriteArrayList();

        switch (category.getCategoryType()) {
            case MY_ROOMS:
                if (player.getRooms() == null) {
                    break;
                }

                for (Integer roomId : new LinkedList<>(player.getRooms())) {
                    if (GameContext.getCurrent().getRoomService().getRoomData(roomId) == null) continue;

                    rooms.add(GameContext.getCurrent().getRoomService().getRoomData(roomId));
                }
                break;

            case MY_FAVORITES:
                List<IRoomData> favouriteRooms = Lists.newArrayList();

                if (player.getNavigator() == null) {
                    return rooms;
                }

                for (Integer roomId : player.getNavigator().getFavouriteRooms()) {
                    if (favouriteRooms.size() == 50) break;

                    final IRoomData roomData = GameContext.getCurrent().getRoomService().getRoomData(roomId);

                    if (roomData != null) {
                        favouriteRooms.add(roomData);
                    }
                }

                rooms.addAll(order(favouriteRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                favouriteRooms.clear();
                break;

            case POPULAR:
                rooms.addAll(order(RoomManager.getInstance().getRoomsByCategory(-1, 1, player), expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                break;

            case CATEGORY:
                rooms.addAll(order(RoomManager.getInstance().getRoomsByCategory(category.getId(), player), expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                break;

            case TOP_PROMOTIONS:
                List<IRoomData> promotedRooms = Lists.newArrayList();

                for (RoomPromotion roomPromotion : RoomManager.getInstance().getRoomPromotions().values()) {
                    if (roomPromotion != null) {
                        IRoomData roomData = GameContext.getCurrent().getRoomService().getRoomData(roomPromotion.getRoomId());

                        if (roomData != null) {
                            promotedRooms.add(roomData);
                        }
                    }
                }

                rooms.addAll(order(promotedRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                promotedRooms.clear();
                break;

            case PUBLIC:
                final List<IRoomData> publicRooms = Lists.newArrayList();

                for (PublicRoom publicRoom : NavigatorManager.getInstance().getPublicRooms(category.getCategoryId()).values()) {
                    final IRoomData roomData = GameContext.getCurrent().getRoomService().getRoomData(publicRoom.getRoomId());

                    if (roomData != null) {
                        publicRooms.add(roomData);
                    }

                }

                rooms.addAll(order(publicRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                publicRooms.clear();
                break;

            case STAFF_PICKS:
                List<IRoomData> staffPicks = Lists.newArrayList();

                for (int roomId : NavigatorManager.getInstance().getStaffPicks()) {
                    IRoomData roomData = GameContext.getCurrent().getRoomService().getRoomData(roomId);

                    if (roomData != null) {
                        staffPicks.add(roomData);
                    }
                }

                rooms.addAll(order(staffPicks, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                staffPicks.clear();
                break;

            case MY_GROUPS:
                List<IRoomData> groupHomeRooms = Lists.newArrayList();

                for (int groupId : player.getGroups()) {
                    IGroupData groupData = GameContext.getCurrent().getGroupService().getData(groupId);

                    if (groupData != null) {
                        IRoomData roomData = GameContext.getCurrent().getRoomService().getRoomData(groupData.getRoomId());

                        if (roomData != null) {
                            groupHomeRooms.add(roomData);
                        }
                    }
                }

                rooms.addAll(order(groupHomeRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                groupHomeRooms.clear();
                break;

            case MY_FRIENDS_ROOMS:
                List<IRoomData> friendsRooms = Lists.newArrayList();

                if (player.getMessenger() == null) {
                    return rooms;
                }

                for (IMessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                    if (messengerFriend.isInRoom()) {
                        final PlayerEntity friendEntity = (PlayerEntity) messengerFriend.getSession().getPlayer().getEntity();
                        if (friendEntity != null) {
                            if (!friendsRooms.contains(friendEntity.getRoom().getData())) {
                                if (friendEntity.getRoom().getData().getOwnerId() == friendEntity.getPlayerId()) {
                                    if (!checkRoomVisibility(player, friendEntity.getRoom())) continue;

                                    friendsRooms.add(friendEntity.getRoom().getData());
                                }
                            }
                        }
                    }
                }

                rooms.addAll(order(friendsRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                friendsRooms.clear();
                break;

            case WITH_FRIENDS:
                List<IRoomData> withFriendsRooms = Lists.newArrayList();

                if (player.getMessenger() == null) {
                    return rooms;
                }

                for (IMessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                    if (messengerFriend.isInRoom()) {
                        PlayerEntity friendEntity = (PlayerEntity) messengerFriend.getSession().getPlayer().getEntity();

                        if (friendEntity != null && !friendEntity.getPlayer().getSettings().getHideOnline()) {
                            if (!withFriendsRooms.contains(friendEntity.getRoom().getData())) {
                                if (!checkRoomVisibility(player, friendEntity.getRoom())) continue;

                                withFriendsRooms.add(friendEntity.getRoom().getData());
                            }
                        }
                    }
                }

                rooms.addAll(order(withFriendsRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                withFriendsRooms.clear();
                break;

            case WITH_RIGHTS:
                if (player.getRoomsWithRights() == null) {
                    break;
                }

                for (Integer roomId : new LinkedList<>(player.getRoomsWithRights())) {
                    if (GameContext.getCurrent().getRoomService().getRoomData(roomId) == null) continue;

                    rooms.add(GameContext.getCurrent().getRoomService().getRoomData(roomId));
                }
                break;
        }

        return rooms;
    }

    /**
     * Check if this player can see this room in navigator search
     *  true if:
     *      player has full control access
     *      player is group member with rights
     *      player have rights
     * @return true if player can see it, otherwise returns false.
     */
    public static boolean checkRoomVisibility(Player player, Room targetRoom) {
        if(player.getPermissions().getRank().roomFullControl())
            return true;

        if (targetRoom.getData().getAccess() == RoomAccessType.INVISIBLE) {
            return targetRoom.getRights().hasRights(player.getId());
        }

        return true;
    }
}
