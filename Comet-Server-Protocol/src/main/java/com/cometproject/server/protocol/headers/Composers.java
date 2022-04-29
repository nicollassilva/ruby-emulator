package com.cometproject.server.protocol.headers;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Composers {
    // Current: PRODUCTION-201611291003-338511768
    // Previous: PRODUCTION-201709192204-203982672
    public static final int PingComposer = 3928; // 1111
    public static final int OpenLinkMessageComposer = 2023; // 2669
    public final static int NuxAlertComposer = 2023;
    public static final int WiredRewardMessageComposer = 178; // 2960
    public static final int HeightMapMessageComposer = 2753; // 1010
    public static final int CallForHelpPendingCallsMessageComposer = 1121; // 2804
    public static final int ChatMessageComposer = 6490; // 2174 -> 1446 | ruby swf -> 6490
    public static final int GroupMembersMessageComposer = 1200; // 610
    public static final int OpenBotActionMessageComposer = 1618; // 1469
    public static final int UserObjectMessageComposer = 2725; // 1513
    public static final int ActionMessageComposer = 6635; // 1536 -> 1631 | ruby swf -> 6635
    public static final int ManageGroupMessageComposer = 3965; // 891
    public static final int FloodControlMessageComposer = 566; // 3603
    public static final int FlatControllerAddedMessageComposer = 2088; // 3622
    public static final int TradingClosedMessageComposer = 1373; // 3671
    public static final int FlatCreatedMessageComposer = 1304; // 3230
    public static final int ScrSendUserInfoMessageComposer = 954; // 3459
    public static final int CheckPetNameMessageComposer = 1503; // 3920
    public static final int QuestAbortedMessageComposer = 3027; // 2671
    public static final int RespectPetNotificationMessageComposer = 2788; // 3856
    public static final int PromotableRoomsMessageComposer = 2468; // 2283
    public static final int CloseConnectionMessageComposer = 122; // 1297
    public static final int CfhTopicsInitMessageComposer = 325; // 2333
    public static final int WiredEffectConfigMessageComposer = 1434; // 2726
    public static final int FriendListUpdateMessageComposer = 2800; // 3872
    public static final int ObjectAddMessageComposer = 1534; // 3340
    public static final int NavigatorCollapsedCategoriesMessageComposer = 1543; // 1146
    public static final int RoomRightsListMessageComposer = 1284; // 3321
    public static final int TradingUpdateMessageComposer = 2024; // 2560
    public static final int CarryObjectMessageComposer = 1474; // 3112
    public static final int NewGroupInfoMessageComposer = 2808; // 2279
    public static final int RoomForwardMessageComposer = 160; // 319
    public static final int GroupFurniSettingsMessageComposer = 3293; // 1777
    public static final int CreditBalanceMessageComposer = 3475; // 1556
    public static final int CatalogUpdatedMessageComposer = 1866; // 489
    public static final int UserTypingMessageComposer = 1717; // 3293
    public static final int ObjectRemoveMessageComposer = 2703; // 2039
    public static final int RoomEntryInfoMessageComposer = 749; // 3383
    public static final int CatalogOfferMessageComposer = 3388; // 2600
    public static final int CatalogIndexMessageComposer = 1032; // 808
    public static final int ThreadsListDataMessageComposer = 1073; // 2597
    public static final int GroupFurniConfigMessageComposer = 420; // 1460
    public static final int HabboUserBadgesMessageComposer = 1087; // 54
    public static final int FlatAccessibleMessageComposer = 3783;// 3140
    public static final int ModeratorInitMessageComposer = 2696; // 2772
    public static final int FloorPlanSendDoorMessageComposer = 1664; // 2201
    public static final int SleepMessageComposer = 1797; // 157
    public static final int FlatControllerRemovedMessageComposer = 1327; // 257
    public static final int UniqueMachineIDMessageComposer = 6489; // 602 // IN PRODUCTION 201611 = 1488
    public static final int ItemAddMessageComposer = 2187; // 366
    public static final int GroupForumDataMessageComposer = 3011; // 2023
    public static final int UpdateFreezeLivesMessageComposer = 2324; // 1652
    public static final int NavigatorSettingsMessageComposer = 2875;// 1776
    public static final int ItemUpdateMessageComposer = 2009; // 1481
    public static final int AchievementsMessageComposer = 305; // 2028
    public static final int LatencyResponseMessageComposer = 10; // 2757
    public static final int RoomReadyMessageComposer = 2031; // 3334
    public static final int HabboActivityPointNotificationMessageComposer = 2275; // 2474
    public static final int BuddyListMessageComposer = 3130; // 2891
    public static final int YoutubeDisplayPlaylistsMessageComposer = 1112; // 1882
    public static final int TradingCompleteMessageComposer = 2720; // 511
    public static final int PetInformationMessageComposer = 2901; // 3345
    public static final int ModeratorRoomChatlogMessageComposer = 3434; // 1708
    public static final int MOTDNotificationMessageComposer = 2035; // 408
    public static final int GroupInfoMessageComposer = 1702; // 3190
    public static final int SlideObjectBundleMessageComposer = 3207; // 352
    public static final int FurniListRemoveMessageComposer = 159; // 1748
    public static final int FurniListNotificationMessageComposer = 2103; // 1310
    public static final int RoomInfoUpdatedMessageComposer = 3297; // 3966
    public static final int AvatarEffectMessageComposer = 1167; // 2398
    public static final int RoomNoRightsComposer = 2392;
    public static final int OpenConnectionMessageComposer = 758; // 3450
    public static final int FurniListMessageComposer = 994; // 1669
    public static final int PostUpdatedMessageComposer = 324; // 2551
    public static final int UserFlatCatsMessageComposer = 1562; // 2986
    public static final int ObjectUpdateMessageComposer = 3776; // 1125
    public static final int ThreadUpdatedMessageComposer = 2528; // 517
    public static final int HabboSearchResultMessageComposer = 973; // 3766
    public static final int RespectNotificationMessageComposer = 2815; // 3489
    public static final int PetHorseFigureInformationMessageComposer = 1924; // 3937
    public static final int MessengerInitMessageComposer = 1605; // 913
    public static final int ModeratorUserInfoMessageComposer = 2866; // 134
    public static final int YouAreControllerMessageComposer = 780; // 3668
    public static final int RoomRatingMessageComposer = 482; // 3267
    public static final int RefreshFavouriteGroupMessageComposer = 876; // 1579
    public static final int AvailabilityStatusMessageComposer = 2033; // 1769
    public static final int AchievementUnlockedMessageComposer = 806; // 684
    public static final int FlatAccessDeniedMessageComposer = 878; // 448
    public static final int NavigatorFlatCatsMessageComposer = 3244; // 3851
    public static final int UsersMessageComposer = 374; // 2775
    public static final int SecretKeyMessageComposer = 3885; // 3465
    public static final int TradingStartMessageComposer = 2505; // 2825
    public static final int RoomSettingsDataMessageComposer = 1498; // 3075
    public static final int NewBuddyRequestMessageComposer = 2219; // 2311
    public static final int DoorbellMessageComposer = 2309; // 1099
    public static final int OpenGiftMessageComposer = 56; // 3980
    public static final int CantConnectMessageComposer = 899; // 1945
    public static final int FloorHeightMapMessageComposer = 1301; // 3841
    public static final int SellablePetBreedsMessageComposer = 3331; // 569
    public static final int AchievementScoreMessageComposer = 1968; // 896
    public static final int BuildersClubMembershipMessageComposer = 1452; // 2286
    public static final int PetTrainingPanelMessageComposer = 1164; // 720
    public static final int QuestCompletedMessageComposer = 949; // 2999
    public static final int UserRightsMessageComposer = 411; // 1081
    public static final int ForumsListDataMessageComposer = 3001; // 2103
    public static final int UserChangeMessageComposer = 3920; // 2098
    public static final int ModeratorUserChatlogMessageComposer = 3377; // 2334
    public static final int GiftWrappingConfigurationMessageComposer = 2234; // 1035
    public static final int FloorPlanFloorMapMessageComposer = 3990; // 3542
    public static final int ThreadReplyMessageComposer = 2049; // 2413
    public static final int GroupCreationWindowMessageComposer = 2159; // 870
    public static final int GetGuestRoomResultMessageComposer = 687; // 1826
    public static final int RoomNotificationMessageComposer = 1992; // 3531
    public static final int InitCryptoMessageComposer = 6347; // 2904 // IN PRODUCTION 201611 = 1347
    public static final int SoundSettingsMessageComposer = 513; // 1001
    public static final int WiredTriggerConfigMessageComposer = 383; // 3478
    public static final int ItemsMessageComposer = 1369; // 2649
    public static final int PurchaseOKMessageComposer = 869; // 479
    public static final int BadgeEditorPartsMessageComposer = 2238; // 2579
    public static final int NewConsoleMessageMessageComposer = 1587; // 2606
    public static final int HideWiredConfigMessageComposer = 1155; // 1991
    public static final int CatalogPageMessageComposer = 804;// 2412
    public static final int AddExperiencePointsMessageComposer = 2156; // 1117
    public static final int AvatarEffectsMessageComposer = 340; // 899
    public static final int QuestListMessageComposer = 3625; // 2566
    public static final int UnbanUserFromRoomMessageComposer = 3429; // 1784
    public static final int WiredConditionConfigMessageComposer = 1108; // 1810
    public static final int StickyNoteMessageComposer = 2202; // 2208
    public static final int SanctionStatusMessageComposer = 2221; // 1053
    public static final int ObjectsMessageComposer = 1778; // 1264
    public static final int RoomVisualizationSettingsMessageComposer = 3547; // 2988
    public static final int PromoArticlesMessageComposer = 286; // 253
    public static final int MaintenanceStatusMessageComposer = 1350; // 2724
    public static final int BuddyRequestsMessageComposer = 280; // 1151
    public static final int AuthenticationOKMessageComposer = 6495; // 3054 // IN PRODUCTION 201611 = 2491
    public static final int QuestStartedMessageComposer = 230; // 325
    public static final int BotInventoryMessageComposer = 3086; // 3095
    public static final int PerkAllowancesMessageComposer = 2586; // 3189
    public static final int RoomEventMessageComposer = 1840; // 1488
    public static final int RoomMuteSettingsMessageComposer = 2533; // 3071
    public static final int ModeratorSupportTicketResponseMessageComposer = 934; // 1825
    public static final int YouTubeDisplayVideoMessageComposer = 1411; // 29
    public static final int RoomPropertyMessageComposer = 2454; // 424
    public static final int ModeratorSupportTicketMessageComposer = 3609; // 283
    public static final int RoomInviteMessageComposer = 3870; // 1378
    public static final int FurniListUpdateMessageComposer = 3151; // 1604
    public static final int BadgesMessageComposer = 717; // 2969
    public static final int NavigatorSearchResultSetMessageComposer = 2690; // 3984
    public static final int IgnoreStatusMessageComposer = 207; // 3919
    public static final int ShoutMessageComposer = 1036; // 3944
    public static final int MoodlightConfigMessageComposer = 2710; // 2266
    public static final int FurnitureAliasesMessageComposer = 1723; // 316
    public static final int TradingErrorMessageComposer = 217; // 1386
    public static final int ProfileInformationMessageComposer = 3898; // 1897
    public static final int ModeratorRoomInfoMessageComposer = 1333; // 154
    public static final int CampaignMessageComposer = 1745; // 2081
    public static final int LoveLockDialogueMessageComposer = 3753; // 318
    public static final int PurchaseErrorMessageComposer = 1404; // 1408
    public static final int PopularRoomTagsResultMessageComposer = 2012; // 2547
    public static final int GiftWrappingErrorMessageComposer = 1517; // 1434
    public static final int WhisperMessageComposer = 2704; // 2810
    public static final int CatalogItemDiscountMessageComposer = 2347; // 3575
    public static final int HabboGroupBadgesMessageComposer = 2402; // 1333
    public static final int CanCreateRoomMessageComposer = 378; // 277
    public static final int ThreadDataMessageComposer = 509; // 3052
    public static final int TradingFinishMessageComposer = 1001; // 1940
    public static final int DanceMessageComposer = 2233; // 1276
    public static final int GenericErrorMessageComposer = 1600; // 2856
    public static final int NavigatorPreferencesMessageComposer = 518 ; // 2123
    public static final int MutedMessageComposer = 826 ; // 1671
    public static final int BroadcastMessageAlertMessageComposer = 3801 ; // 82
    public static final int YouAreOwnerMessageComposer = 339 ; // 2539
    public static final int ModeratorTicketChatlogMessageComposer = 607 ; // 3492
    public static final int BadgeDefinitionsMessageComposer = 2501 ; // 1924
    public static final int UserRemoveMessageComposer = 2661 ; // 2848
    public static final int RoomSettingsSavedMessageComposer = 948 ; // 539
    public static final int ModeratorUserRoomVisitsMessageComposer = 1752 ; // 2415
    public static final int NavigatorLiftedRoomsMessageComposer = 3104 ; // 3709
    public static final int NavigatorMetaDataParserMessageComposer = 3052 ; // 2631
    public static final int GetRelationshipsMessageComposer = 2016 ; // 3068
    public static final int ItemRemoveMessageComposer = 3208 ; // 2091
    public static final int ThreadCreatedMessageComposer = 1862 ; // 871
    public static final int EnforceCategoryUpdateMessageComposer = 3896 ; // 2621
    public static final int AchievementProgressedMessageComposer = 2107 ; // 2167
    public static final int ActivityPointsMessageComposer = 2018 ; // 3304
    public static final int PetInventoryMessageComposer = 3522 ; // 3808
    public static final int GetRoomBannedUsersMessageComposer = 1869 ; // 2712
    public static final int UserUpdateMessageComposer = 1640 ; // 2694
    public static final int FavouritesMessageComposer = 151 ; // 2753
    public static final int WardrobeMessageComposer = 3315 ; // 1178
    public static final int LoveLockFurniFriendConfirmedMessageComposer = 382 ; // 2027
    public static final int TradingAcceptMessageComposer = 2568 ; // 1464
    public static final int SongInventoryMessageComposer = 2602 ; // 484
    public static final int SongIdMessageComposer = 1381 ; // 3538
    public static final int SongDataMessageComposer = 3365 ; // 1822
    public static final int PlaylistMessageComposer = 34 ; // 1080
    public static final int PlayMusicMessageComposer = 469 ; // 1401
    public static final int QuickPollMessageComposer = 2665 ; // 1232
    public static final int QuickPollResultMessageComposer = 2589 ; // 2178
    public static final int QuickPollResultsMessageComposer = 1066 ; // 3139
    public static final int InitializePollMessageComposer = 3785 ; // 3726
    public static final int PollMessageComposer = 2997 ; // 430
    public static final int AvatarAspectUpdateMessageComposer = 2429 ; // 2786
    public static final int YouAreSpectatorMessageComposer = 1033 ; // 2666
    public static final int UpdateStackMapMessageComposer = 558 ; // 2730
    public static final int UpdateUsernameMessageComposer = 118 ; // 393
    public static final int UserNameChangeMessageComposer = 2182 ; // 898
    public static final int NameChangeUpdateMessageComposer = 563 ; // 3436
    public static final int SendHotelViewLooksMessageComposer = 3005 ; // 791
    public static final int GuideSessionAttachedMessageComposer = 1591 ; // 2485
    public static final int GuideSessionDetachedMessageComposer = 138 ; // 1443
    public static final int GuideSessionStartedMessageComposer = 3209 ; // 1219
    public static final int GuideSessionEndedMessageComposer = 1456 ; // 91
    public static final int GuideSessionErrorMessageComposer = 673 ; // 3550
    public static final int GuideSessionMessageMessageComposer = 841 ; // 3845
    public static final int GuideSessionRequesterRoomMessageComposer = 1847 ; // 2905
    public static final int GuideSessionInvitedToGuideRoomMessageComposer = 219 ; // 3149
    public static final int GuideSessionPartnerIsTypingMessageComposer = 1016 ; // 3101
    public static final int GuideToolsMessageComposer = 1548 ; // 2857
    public static final int GuardianNewReportReceivedMessageComposer = 735 ; // 2350
    public static final int GuardianVotingRequestedMessageComposer = 143 ; // 2094
    public static final int GuardianVotingVotesMessageComposer = 1829 ; // 2692
    public static final int GuardianVotingResultMessageComposer = 3276 ; // 1103
    public static final int GuardianVotingTimeEndedMessageComposer = 30 ; // 3015
    public static final int ModToolReportReceivedAlertMessageComposer = 3635 ; // 3124
    public static final int BullyReportClosedMessageComposer = 2674 ; // 1998
    public static final int BullyReportRequestMessageComposer = 3463 ; // 2586
    public static final int BullyReportedMessageMessageComposer = 3285 ; // 3004
    public static final int HelperRequestDisabledMessageComposer = 1651 ; // 2817
    public static final int UserTagsMessageComposer = 1255 ; // 2212
    public static final int GetRoomFilterListMessageComposer = 2937 ; // 1798
    public static final int NavigatorSavedSearchesMessageComposer = 3984 ; // 2853
    public static final int FindFriendsProcessResultMessageComposer = 1210 ; // 932
    public static final int NavigatorFavoritedRoomMessageComposer = 2524 ; // 3846
    public static final int FollowErrorMessageComposer = 3048 ; // 344
    public static final int FriendRequestErrorMessageComposer = 892 ; // 2711
    public static final int BotErrorMessageComposer = 639 ; // 2605
    public static final int PetErrorMessageComposer = 2913 ; // 3987
    public static final int GroupAcceptMemberErrorMessageComposer = 818 ; // 2983
    public static final int RemoveGroupFromRoomMessageComposer = 3129 ; // 740
    public static final int RefreshGroupMembersListMessageComposer = 2445 ; // 3186
    public static final int GroupMemberUpdateMessageComposer = 265 ; // 3263
    public static final int GroupConfirmRemoveMemberMessageComposer = 1876 ; // 2328
    public static final int RemoveBotMessageComposer = 233 ; // 1551
    public static final int ReceivedHandItemMessageComposer = 354 ; // 935
    public static final int LimitedEditionSoldOutMessageComposer = 377 ; // 2979
    public static final int LoveLockDialogueFinishedMessageComposer = 770 ; // 2355
    public static final int RoomChatSettingsMessageComposer = 1191 ; // 2114
    public static final int AddBotMessageComposer = 1352 ; // 1357
    public static final int PurchaseUnavailableErrorMessageComposer = 3770 ; // 2866
    public static final int GroupFavoritePlayerUpdateMessageComposer = 3403 ; // 3770
    public static final int JoinGroupErrorMessageComposer = 762 ; // 141
    public static final int RoomActionMessageComposer = -1 ; //! No Matches: 1550
    public static final int PetBreedingMessageComposer = 634 ; // 3099
    public static final int PetBreedingCompleteMessageComposer = 2527 ; // 3034
    public static final int PetBreedingStartedMessageComposer = 1625 ; // 1692
    public static final int PetPackageMessageComposer = 2380 ; // 3781
    public static final int PetPackageOpenedMessageComposer = 546 ; // 1278
    public static final int FigureSetIdsMessageComposer = 1450 ; // 745
    public static final int ThumbnailSavedMessageComposer = 3595 ; // 1101
    public static final int PhotoPreviewMessageComposer = 3696 ; // 3115
    public static final int PhotoPriceMessageComposer = 3878 ; // 953
    public static final int CameraPublishWaitMessageComposer = 2057 ; // 2622
    public static final int PurchasedPhotoMessageComposer = 2783 ; // 3859
    public static final int YouArePlayingGameMessageComposer = 448 ; // 613
    public static final int GameListMessageComposer = 222 ; // 1187
    public static final int GameAchievementsMessageComposer = 1689 ; // 2719
    public static final int GameAccountStatusMessageComposer = 2893 ; // 1821
    public static final int LoadGameMessageComposer = 3654 ; // 2610
    public static final int GameCenterFeaturedPlayersMessageComposer = 3097 ; // 307
    public static final int GameAchievementsListMessageComposer = 2265 ; // 730
    public static final int GameCenterGameMessageComposer = 3805 ; // 1805
    public static final int BaseJumpJoinQueueMessageComposer = 2260 ; // 2933
    public static final int BaseJumpLeaveQueueMessageComposer = 1477 ; // 2679
    public static final int BaseJumpLoadGameURL = 2624 ; // 2021
    public static final int BaseJumpUnloadGame = 1715 ; // 1936
    public static final int Game2WeeklyLeaderboardMessageComposer = 2196 ; // 2082
    public static final int Game2WeeklySmallLeaderboardMessageComposer = 3512 ; // 3166
    public static final int NewUserIdentityMessageComposer = 3738 ; // 2048
    public static final int NewUserGiftMessageComposer = 3575 ; // 3122 NuxGiftSelectionViewMessageComposer
    public static final int WelcomeGiftMessageComposer = 2707 ; // 2343 NuxGiftEmailViewMessageComposer
    public static final int WelcomeGiftErrorMessageComposer = 2293 ; // 3157
    public static final int PetBoughtNotificationMessageComposer = 1111 ; // 3107
    public static final int AdventCalendarDataMessageComposer = 2531 ; // 1072 CampaignCalendarDataMessageComposer
    public static final int AdventCalendarProductMessageComposer = 2551 ; // 1500 CalendarPrizesMessageComposer
    public static final int NewYearResolutionMessageComposer = 66 ; // 3133
    public static final int PetBreedingStartFailedMessageComposer = 2621 ; // 3136
    public static final int MarketplaceOwnItemsMessageComposer = 3884 ; // 3137
    public static final int EpicPopupFrameMessageComposer = 3945 ; // 75
    public static final int ClubCenterDataMessageComposer = 3277 ; // 3148
    public static final int VipTutorialsStartMessageComposer = 2278 ; // 1564
    public static final int ClubDataMessageComposer = 2405 ; // 3667
    public static final int ExtendClubMessageMessageComposer = 3964 ; // 2896
    public static final int ClubGiftReceivedMessageComposer = 659 ; // 2791
    public static final int ClubGiftsMessageComposer = 619 ; // 3187
    public static final int PickMonthlyClubGiftNotificationMessageComposer = 2188 ; // 1140
    public static final int NotEnoughPointsTypeMessageComposer = 3914 ; // 3154
    public static final int EnableNotificationsComposer = 3284 ; // 85
    public static final int NewNavigatorCategoryUserCountMessageComposer = 1455 ; // 3171
    public static final int HotelClosedAndOpensMessageComposer = 3728 ; // 2172
    public static final int HotelClosesAndWillOpenAtMessageComposer = 2771 ; // 192
    public static final int HotelWillCloseInMinutesMessageComposer = 1050 ; // 2859
    public static final int MisteryBoxDataMessageComposer = 2833 ; // 2320 MisteryBoxDataMessageComposer
    public static final int MysticBoxPrizeMessageComposer = 3712 ; // 2185 MisteryBoxRewardMessageComposer
    public static final int MysticBoxStartOpenMessageComposer = 3201 ; // 3480 MisteryBoxOpenMessageComposer
    public static final int MysticBoxCloseMessageComposer = 596 ; // 2755 MisteryBoxCloseMessageComposer
    public static final int JukeBoxPlayListUpdatedMessageComposer = 1748 ; // 144
    public static final int JukeBoxPlayListAddSongMessageComposer = 1140 ; // 3224
    public static final int JukeBoxPlaylistFullMessageComposer = 105 ; // 2826
    public static final int RentableItemBuyOutPriceMessageComposer = 35 ; // 2204  RentOfferMessageComposer
    public static final int RoomMessagesPostedCountMessageComposer = 1634 ; // 2218
    public static final int CraftableProductsMessageComposer = 1000 ; // 2221
    public static final int CraftingRecipeMessageComposer = 2774 ; // 1493
    public static final int CraftingResultMessageComposer = 618 ; // 3261
    public static final int CraftingComposerFourMessageComposer = 2124 ; // 1787

    public static final int PetLevelUpMessageComposer = 859 ; // 1200
    public static final int StaffAlertAndOpenHabboWayMessageComposer = 1683 ; // 1201
    public static final int StaffAlertWIthLinkAndOpenHabboWayMessageComposer = 1890 ; // 392
    public static final int StaffAlertWithLinkMessageComposer = 2030 ; // 2491
    public static final int MessengerErrorMessageComposer = 896 ; // 178
    public static final int PetBreedingStartMessageComposer = 1746 ; // 2232
    public static final int OpenRoomCreationWindowMessageComposer = 2064 ; // 1217
    public static final int CloseWebPageMessageComposer = 426 ; // 3276
    public static final int OldPublicRoomsMessageComposer = 2726 ; // 3277
    public static final int HotelViewNextLTDAvailableMessageComposer = 44 ; // 3324
    public static final int HotelViewExpiringCatalogPageCommposer = 2515 ; // 1341
    public static final int HotelViewConcurrentUsersMessageComposer = 2737 ; // 1433
    public static final int HotelViewCustomTimerMessageComposer = 3926 ; // 824
    public static final int HotelViewHideCommunityVoteButtonMessageComposer = 1435 ; // 3935
    public static final int HotelViewCommunityGoalMessageComposer = 2525 ; // 888
    public static final int CantScratchPetNotOldEnoughMessageComposer = 1130 ; // 265
    public static final int ModToolIssueResponseAlertMessageComposer = 3796 ; // 270
    public static final int ModToolIssueUpdateMessageComposer = 3150 ; // 279
    public static final int PrivateRoomsMessageComposer = 52 ; // 2332
    public static final int OtherTradingDisabledMessageComposer = 1254 ; // 2140
    public static final int YouTradingDisabledMessageComposer = 3058 ; // 2342
    public static final int VerifyMobilePhoneWindowMessageComposer = 2890 ; // 1542 SMSVerificationWindowMessageComposer
    public static final int VerifyMobilePhoneCodeWindowMessageComposer = 800 ; // 2132 EmailVerificationWindowMessageComposer
    public static final int VerifyMobilePhoneDoneMessageComposer = 91 ; // 3369  SMSVerificationCompleteMessageComposer
    public static final int MarketplaceItemInfoMessageComposer = 725 ; // 3388
    public static final int NewYearResolutionCompletedMessageComposer = 740 ; // 1361
    public static final int NewYearResolutionProgressMessageComposer = 3370 ; // 1372
    public static final int RedeemVoucherOKMessageComposer = 3336 ; // 3420
    public static final int RedeemVoucherErrorMessageComposer = 714 ; // 703
    public static final int RoomInviteErrorMessageComposer = 462 ; // 3429
    public static final int RemovePetMessageComposer = 3253 ; // 1427
    public static final int HabboWayQuizComposer2 = 2927 ; // 405
    public static final int MarketplaceSellItemMessageComposer = 54 ; // 3477
    public static final int TalentLevelUpdateMessageComposer = 638 ; // 3482
    public static final int MinimailNewMessageComposer = 1911 ; // 1450
    public static final int MinimailCountMessageComposer = 2803 ; // 1836
    public static final int ItemStateMessageComposer=2376 ; // 2535
    public static final int ItemStateComposer2 = 3431 ; // 1172
    public static final int ItemExtraDataMessageComposer = 2547 ; // 1478
    public static final int ItemsDataUpdateMessageComposer = 1453 ; // 2846
    public static final int DailyQuestMessageComposer = 1878 ; // 1485 DailyQuestMessageComposer
    public static final int UpdateFailedMessageComposer = 156 ; // 572
    public static final int RoomEditSettingsErrorMessageComposer = 1555 ; // 2623
    public static final int GuildForumsUnreadMessagesCountMessageComposer = 2379 ; // 3656
    public static final int ModToolSanctionDataMessageComposer = 2782 ; // 1610
    public static final int TalentTrackEmailFailedMessageComposer = 1815 ; // 2643
    public static final int TalentTrackMessageComposer = 3406 ; // 1744
    public static final int UserCitizenshipMessageComposer = 1203 ; // 2645
    public static final int RoomAdErrorMessageComposer = 1759 ; // 3697
    public static final int WatchAndEarnRewardMessageComposer = 2125 ; // 2675
    public static final int RentableSpaceInfoMessageComposer = 3559 ; // 2684
    public static final int ModToolIssueHandlerDimensionsMessageComposer = 1576 ; // 652
    public static final int YoutubeMessageComposer3 = 1554 ; // 658
    public static final int ConnectionErrorMessageComposer = 1004 ; // 668
    public static final int InventoryItemUpdateMessageComposer = 104 ; // 3747
    public static final int MarketplaceBuyErrorMessageComposer = 2032 ; // 3760 check
    public static final int CameraCompetitionStatusMessageComposer = 133 ; // 2743
    public static final int CatalogModeMessageComposer = 3828 ; // 2746
    public static final int CanCreateEventMessageComposer = 2599 ; // 1733
    public static final int FriendToolbarNotificationMessageComposer = 3082 ; // 1737 FriendNotificationMessageComposer
    public static final int FriendPrivateMessageMessageComposer = 2998 ; // 1750
    public static final int PetStatusUpdateMessageComposer = 1907 ; // 751
    public static final int AddPetMessageComposer = 2101 ; // 761
    public static final int CompetitionEntrySubmitResultMessageComposer = 1177 ; // 764
    public static final int ConvertedForwardToRoomMessageComposer = 1331 ; // 778
    public static final int EffectsListEffectEnableMessageComposer = 1959 ; // 1812
    public static final int EffectsListAddMessageComposer = 2867 ; // 2849
    public static final int EffectsListRemoveMessageComposer = 2228 ; // 449
    public static final int RemoveRoomEventMessageComposer = 3479 ; // 1814
    public static final int PetLevelUpdatedMessageComposer = 2824 ; // 3865
    public static final int MarketplaceCancelSaleMessageComposer = 3264 ; // 801
    public static final int CustomNotificationMessageComposer = 909 ; // 1838
    public static final int UpdateStackHeightTileHeight = 2816 ; // 3890
    public static final int ModToolComposerTwoMessageComposer = 2335 ; // 878
    public static final int BonusRareMessageComposer = 1533 ; // 3951
    public static final int HabboMall = 1237 ; // 897
    public static final int MessengerErrorComposer = 896; // 193 MessengerErrorMessageComposer
    public static final int RoomUserRemoveRights = 84 ; // 2949
    public static final int TalentTrackEmailVerified = 612 ; // 2950
    public static final int GuildEditFail = 3988 ; // 3979
    public static final int MarketplaceItemPosted = 1359 ; // 3982 check
    public static final int ErrorLogin = 4000 ; // 4000
    public static final int ModToolComposerOne = 3192 ; // 949
    public static final int TargetedOffer = 119 ; // 1976
    public static final int MarketplaceConfig = 1823 ; // 2011 check
    public static final int PostItStickyPoleOpen = 2366 ; // 2015
    public static final int AddUserBadge = 2493 ; // 3051
    public static final int VerifyMobileNumber = 3639 ; // 2033 SMSVerificationOfferMessageComposer
    public static final int SubmitCompetitionRoom = 3841 ; // 2038
    public static final int MarketplaceOffers = 680 ; // 3066

    public static final int EmailVerificationWindowMessageComposer = 800; // 2132
    public static final int SMSVerificationCompleteMessageComposer = 91; // 3369
    public static final int IsFirstLoginOfDayComposer = 793;
    public static final int IgnoredUsersComposer = 126;

    //Whisper Group
    public static final int WhiperGroupComposer = 1118;

    //SNOWSTORM
    public static final int SnowStormGameStartedComposer = 5000;
    public static final int SnowStormQuePositionComposer = 5001;
    public static final int SnowStormStartBlockTickerComposer = 5002;
    public static final int SnowStormStartLobbyCounterComposer = 5003;
    public static final int SnowStormUnusedAlertGenericComposer = 5004;
    public static final int SnowStormLongDataComposer = 5005;
    public static final int SnowStormGameEndedComposer = 5006;
    public static final int SnowStormQuePlayerAddedComposer = 5008;
    public static final int SnowStormPlayAgainComposer = 5009;
    public static final int SnowStormGamesLeftComposer = 5010;
    public static final int SnowStormQuePlayerRemovedComposer = 5011;
    public static final int SnowStormGamesInformationComposer = 5012;
    public static final int SnowStormLongData2Composer = 5013;
    public static final int UNUSED_SNOWSTORM_5014 = 5014;
    public static final int SnowStormGameStatusComposer = 5015;
    public static final int SnowStormFullGameStatusComposer = 5016;
    public static final int SnowStormOnStageStartComposer = 5017;
    public static final int SnowStormintializeGameArenaViewComposer = 5018;
    public static final int SnowStormRejoinPreviousRoomComposer = 5019;
    public static final int UNKNOWN_SNOWSTORM_5020 = 5020;
    public static final int SnowStormLevelDataComposer = 5021;
    public static final int SnowStormOnGameEndingComposer = 5022;
    public static final int SnowStormUserChatMessageComposer = 5023;
    public static final int SnowStormOnStageRunningComposer = 5024;
    public static final int SnowStormOnStageEndingComposer = 5025;
    public static final int SnowStormintializedPlayersComposer = 5026;
    public static final int SnowStormOnPlayerExitedArenaComposer = 5027;
    public static final int SnowStormGenericErrorComposer = 5028;
    public static final int SnowStormUserRematchedComposer = 5029;

    // Infobus Polls
    public static final int StartInfobusPollMessageComposer = 5200;
    public static final int GetInfobusPollResultsMessageComposer = 5201;

    // Recycler
    public static final int RecyclerCompleteMessageComposer = 468;
    public static final int ReloadRecyclerMessageComposer = 3433;
    public static final int RecyclerLogicMessageComposer = 3164 ; // 769

    private static final Map<Integer, String> composerPacketNames = new HashMap<>();

    static {
        try {
            for (Field field : Composers.class.getDeclaredFields()) {
                if (!Modifier.isPrivate(field.getModifiers()))
                    composerPacketNames.put(field.getInt(field.getName()), field.getName());
            }
        } catch (Exception ignored) {

        }
    }

    public static String valueOfId(int packetId) {
        if (composerPacketNames.containsKey(packetId)) {
            return composerPacketNames.get(packetId);
        }

        return "UnknownMessageComposer";
    }
}
