package com.cometproject.api.game.achievements.types;

public enum AchievementType {
    AVATAR_LOOKS("ACH_AvatarLooks"),
    MOTTO("ACH_Motto"),
    RESPECT_GIVEN("ACH_RespectGiven"),
    RESPECT_EARNED("ACH_RespectEarned"),
    ROOM_ENTRY("ACH_RoomEntry"),
    REGISTRATION_DURATION("ACH_RegistrationDuration"),
    PET_RESPECT_GIVEN("ACH_PetRespectGiver"),
    PET_LOVER("ACH_PetLover"),
    GIFT_GIVER("ACH_GiftGiver"),
    GIFT_RECEIVER("ACH_GiftReceiver"),
    BB_TILES_LOCKED("ACH_BattleBallTilesLocked"),
    BB_PLAYER("ACH_BattleBallPlayer"),
    BB_WINNER("ACH_BattleBallWinner"),
    FREEZING_PLAYERS("ACH_EsA"),
    GAME_PLAYER_EXPERIENCE("ACH_GamePlayerExperience"),
    GAME_AUTHOR_EXPERIENCE("ACH_GameAuthorExperience"),
    ONLINE_TIME("ACH_AllTimeHotelPresence"),
    LOGIN("ACH_Login"),
    FRIENDS_LIST("ACH_FriendListSize"),
    CAMERA_PHOTO("ACH_CameraPhotoCount"),
    FOOTBALL_GOAL("ACH_FootballGoalScored"),
    CROSS_TRAINER("ACH_CrossTrainer"),
    TRAMPOLINIST("ACH_Trampolinist"),
    JOGGER("ACH_Jogger"),
    ICE_SKATES("ACH_TagC"),
    ROLLER_SKATES("ACH_RbTagC"),
    SKATEBOARD_SLIDE("ACH_SkateBoardSlide"),
    SKATEBOARD_JUMP("ACH_SkateBoardJump"),
    BUILDER_FURNI_COUNT("ACH_RoomDecoFurniCount"),
    BUILDER_BLACK_HOLES_COUNT("ACH_RoomDecoHoleFurniCount"),
    BUILDER_SNOWBOARD_COUNT("ACH_snowBoardBuild"),
    BUILDER_ICESKATES_COUNT("ACH_TagA"),
    BUILDER_ROLLERSKATES_COUNT("ACH_RbTagA"),
    ROOM_WALLPAPER_COUNT("ACH_RoomDecoWallpaper"),
    ROOM_FLOOR_COLOR_COUNT("ACH_RoomDecoFloor"),
    ROOM_LANDSCAPE_COUNT("ACH_RoomDecoLandscape"),
    ROOM_SETTINGS_DOOR_MODE("ACH_SelfModDoorModeSeen"),
    ROOM_SETTINGS_FLOOD_FILTER("ACH_SelfModChatFloodFilterSeen"),
    ROOM_SETTINGS_FILTER("ACH_SelfModRoomFilterSeen"),
    ROOM_SETTINGS_CAN_MUTE("ACH_SelfModIgnoreSeen"),
    ROOM_SETTINGS_CAN_KICK("ACH_SelfModKickSeen"),
    ROOM_SETTINGS_CAN_BAN("ACH_SelfModBanSeen"),
    ROOM_SETTINGS_CHAT_SCROLL_SPEED("ACH_SelfModChatScrollSpeedSeen"),
    FURNIMATIC_QUEST("ACH_FurnimaticQuest"),
    ROOM_SETTINGS_CHAT_DISTANCE("ACH_SelfModChatHearRangeSeen"),
    FEATURED_ROOM_BY_STAFF("ACH_Spr"),
    POSTIT_RECEIVED("ACH_NotesReceived"),
    POSTIT_SENT("ACH_NotesLeft"),
    HORSE_JUMPING("ACH_HorseJumping"),
    HORSE_CONSECUTIVE_JUMPS("ACH_HorseConsecutiveJumpsCount"),
    HORSE_JUMPING_IN_ROOM("ACH_RoomHorseJumpCount"),
    HORSE_CAN_MOUNT_OWN("ACH_HorseRent"),
    TRADER_PASS("ACH_TraderPass"),
    KISS_RECEIVED("ACH_KissEarned"),
    KISS_SENT("ACH_KissGiven");

    private final String groupName;

    AchievementType(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public static AchievementType getTypeByName(String name) {
        for (final AchievementType type : AchievementType.values()) {
            if (type.groupName.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
