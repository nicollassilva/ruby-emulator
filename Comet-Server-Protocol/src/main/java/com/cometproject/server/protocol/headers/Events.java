package com.cometproject.server.protocol.headers;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


public class Events {
    // Current: PRODUCTION-201611291003-338511768
    // Previous: PRODUCTION-201709192204-203982672
    public static final short PongEvent = 2596 ; // 1552
    public static final short RequestTalentTrackEvent = 196;
    public static final short ConfirmUsernameMessageEvent = 6789 ; // 1590 // IN PRODUCTION 201611 = 3878
    public static final short GetRoomBannedUsersMessageEvent = 2267 ; // 2652
    public static final short GetPetInventoryMessageEvent = 3095 ; // 567
    public static final short DropHandItemMessageEvent = 2814 ; // 2846
    public static final short ReleaseTicketMessageEvent = 1572 ; // 3042
    public static final short GetModeratorRoomInfoMessageEvent = 707 ; // 1299
    public static final short KickUserMessageEvent = 1320 ; // 3838
    public static final short SaveWiredEffectConfigMessageEvent = 2281 ; // 1843
    public static final short RespectPetMessageEvent = 3202 ; // 31
    public static final short GenerateSecretKeyMessageEvent = 773 ; // 412
    public static final short HotelViewRequestLTDAvailabilityMessageEvent = 410;
    public static final short GetModeratorTicketChatlogsMessageEvent = 211 ; // 3461
    public static final short GetAchievementsMessageEvent = 219 ; // 3047
    public static final short SaveWiredTriggerConfigMessageEvent = 1520 ; // 2167
    public static final short AcceptGroupMembershipMessageEvent = 3386 ; // 2891
    public static final short GetGroupFurniSettingsMessageEvent = 2651 ; // 2666
    public static final short TakeAdminRightsMessageEvent = 722 ; // 2542
    public static final short RemoveAllRightsMessageEvent = 2683 ; // 2398
    public static final short UpdateThreadMessageEvent = 3045 ; // 777
    public static final short ManageGroupMessageEvent = 1004 ; // 2697
    public static final short ModifyRoomFilterListMessageEvent = 3001 ; // 2973
    public static final short SSOTicketMessageEvent = 6788 ; // 286 // IN PRODUCTION 201611 = 2419
    public static final short JoinGroupMessageEvent = 998 ; // 2529
    public static final short DeclineGroupMembershipMessageEvent = 1894 ; // 2688
    public static final short UniqueIDMessageEvent = 6785 ; // 921 IN PRODUCTION 201611 = 2490
    public static final short RemoveMyRightsMessageEvent = 3182 ; // 3596
    public static final short PurchaseTargetOfferEvent = 1826;
    public static final short TargetOfferStateEvent = 2041;
    public static final short PetUseItemMessageEvent = 1328 ; // 2160
    public static final short GetPetInformationMessageEvent = 2934 ; // 983
    public static final short GiveHandItemMessageEvent = 2941 ; // 2094
    public static final short UpdateFigureDataMessageEvent = 2730 ; // 3509
    public static final short RemoveGroupMemberMessageEvent = 593 ; // 3326
    public static final short EventLogMessageEvent = 6780 ; // 658 // IN PRODUCTION 201611 = 3457
    public static final short RefreshCampaignMessageEvent = 2912 ; // 2260
    public static final short GetPromotableRoomsMessageEvent = 2283 ; // 2372
    public static final short UseOneWayGateMessageEvent = 2765 ; // 3521
    public static final short AddStickyNoteMessageEvent = 2248 ; // 890
    public static final short GetSelectedBadgesMessageEvent = 2091 ; // 904
    public static final short UpdateStickyNoteMessageEvent = 3666 ; // 3742
    public static final short CloseTicketMesageEvent = 2067 ; // 3406
    public static final short RequestBuddyMessageEvent = 3157 ; // 3619
    public static final short GetFurnitureAliasesMessageEvent = 3898 ; // 3253
    public static final short GetRoomSettingsMessageEvent = 3129 ; // 3700
    public static final short RequestFurniInventoryMessageEvent = 3150 ; // 3750
    public static final short ModerationKickMessageEvent = 2582 ; // 794
    public static final short OpenFlatConnectionMessageEvent = 6805 ; // 2450
    public static final short SpectateRoomMessageEvent = 3093 ; // 1187
    public static final short DanceMessageEvent = 2080 ; // 1126
    public static final short RemoveBuddyMessageEvent = 1689 ; // 1106
    public static final short LatencyTestMessageEvent = 295 ; // 2348
    public static final short InfoRetrieveMessageEvent = 357 ; // 2401
    public static final short YouTubeGetNextVideo = 3005 ; // 1294
    public static final short SetObjectDataMessageEvent = 3608 ; // 2194
    public static final short MessengerInitMessageEvent = 2781 ; // 1186
    public static final short PickUpBotMessageEvent = 3323 ; // 2856
    public static final short ActionMessageEvent = 6810 ; // 3017
    public static final short LookToMessageEvent = 3301 ; // 3557
    public static final short ToggleMoodlightMessageEvent = 2296 ; // 3146
    public static final short FollowFriendMessageEvent = 3997 ; // 2
    public static final short PickUpPetMessageEvent = 1581 ; // 1207
    public static final short GetSellablePetBreedsMessageEvent = 1756 ; // 171
    public static final short GetForumUserProfileMessageEvent = 2249 ; // 2593
    public static final short GetForumsListDataMessageEvent = 873 ; // 559
    public static final short IgnoreUserMessageEvent = 1117 ; // 2097
    public static final short DeleteRoomMessageEvent = 532 ; // 638
    public static final short StartQuestMessageEvent = 3604 ; // 2425
    public static final short GetGiftWrappingConfigurationMessageEvent = 418 ; // 3335
    public static final short UpdateGroupIdentityMessageEvent = 3137 ; // 1885
    public static final short RideHorseMessageEvent = 1036 ; // 45
    public static final short ApplySignMessageEvent = 1975 ; // 1195
    public static final short FindRandomFriendingRoomMessageEvent = 1703 ; // 1275
    public static final short GetModeratorUserChatlogMessageEvent = 1391 ; // 1925
    public static final short GetWardrobeMessageEvent = 2742 ; // 2699
    public static final short MuteUserMessageEvent = 3485 ; // 3753
    public static final short UpdateForumSettingsMessageEvent = 2214 ; // 2339
    public static final short ApplyDecorationMessageEvent = 711 ; // 1388
    public static final short GetBotInventoryMessageEvent = 3848 ; // 1199
    public static final short UseHabboWheelMessageEvent = 2144 ; // 2452
    public static final short EditRoomPromotionMessageEvent = 3991 ; // 22
    public static final short GetModeratorUserInfoMessageEvent = 3295 ; // 1588
    public static final short PlaceBotMessageEvent = 1592 ; // 3391
    public static final short GetCatalogPageMessageEvent = 412 ; // 2148
    public static final short GetThreadsListDataMessageEvent = 436 ; // 276
    public static final short ShoutMessageEvent = 2085 ; // 1795
    public static final short DiceOffMessageEvent = 1533 ; // 3670
    public static final short LetUserInMessageEvent = 1644 ; // 754
    public static final short SetActivatedBadgesMessageEvent = 644 ; // 1429
    public static final short UpdateGroupSettingsMessageEvent = 3435 ; // 164
    public static final short ApproveNameMessageEvent = 2109 ; // 321
    public static final short SubmitNewTicketMessageEvent = 1691 ; // 2586
    public static final short DeleteGroupMessageEvent = 1134 ; // 3593
    public static final short DeleteStickyNoteMessageEvent = 3336 ; // 2506
    public static final short GetGroupInfoMessageEvent = 2991 ; // 1415
    public static final short GetStickyNoteMessageEvent = 3964 ; // 3686
    public static final short DeclineBuddyMessageEvent = 2890 ; // 1591
    public static final short OpenGiftMessageEvent = 3558 ; // 2066
    public static final short GiveRoomScoreMessageEvent = 3582 ; // 337
    public static final short SetGroupFavouriteMessageEvent = 3549 ; // 538
    public static final short SetMannequinNameMessageEvent = 2850 ; // 1301
    public static final short RoomDimmerSavePresetMessageEvent = 1648 ; // 1488
    public static final short UpdateGroupBadgeMessageEvent = 1991 ; // 1088
    public static final short PickTicketMessageEvent = 15 ; // 1001
    public static final short SetTonerMessageEvent = 2880 ; // 2186
    public static final short RespectUserMessageEvent = 2694 ; // 2377
    public static final short DeleteGroupThreadMessageEvent = 1397 ; // 1549
    public static final short DeleteGroupReplyMessageEvent = 286 ; // 1616
    public static final short CreditFurniRedeemMessageEvent = 3115 ; // 1009
    public static final short InitDiffieHandshake = 3110;
    public static final short ModerationMsgMessageEvent = 1840 ; // 975
    public static final short ToggleYouTubeVideoMessageEvent = 2069 ; // 1777
    public static final short UpdateNavigatorSettingsMessageEvent = 1740 ; // 3724
    public static final short ToggleMuteToolMessageEvent = 3637 ; // 36
    public static final short ChatMessageEvent = 1314 ; // 1831
    public static final short SaveRoomSettingsMessageEvent = 1969 ; // 1090
    public static final short PurchaseFromCatalogAsGiftMessageEvent = 1411 ; // 2142
    public static final short GetGroupCreationWindowMessageEvent = 798 ; // 1907
    public static final short GiveAdminRightsMessageEvent = 2894 ; // 1587
    public static final short GetGroupMembersMessageEvent = 312 ; // 1048
    public static final short ModerateRoomMessageEvent = 3260 ; // 2949
    public static final short GetForumStatsMessageEvent = 3149 ; // 2932
    public static final short GetPromoArticlesMessageEvent = 1827 ; // 1293
    public static final short SitMessageEvent = 2235 ; // 1805
    public static final short SetSoundSettingsMessageEvent = 1367 ; // 1718
    public static final short ModerationCautionMessageEvent = 229 ; // 2849
    public static final short InitializeFloorPlanSessionMessageEvent = 3559 ; // 698
    public static final short ModeratorActionMessageEvent = 3842 ; // 1992
    public static final short PostGroupContentMessageEvent = 3529 ; // 324
    public static final short GetModeratorRoomChatlogMessageEvent = 2587 ; // 329
    public static final short GetUserFlatCatsMessageEvent = 3027 ; // 1761
    public static final short RemoveRightsMessageEvent = 2064 ; // 1877
    public static final short ModerationBanMessageEvent = 2766 ; // 265
    public static final short CanCreateRoomMessageEvent = 2128 ; // 3866
    public static final short UseWallItemMessageEvent = 210 ; // 3032
    public static final short PlaceObjectMessageEvent = 1258 ; // 1268
    public static final short OpenBotActionMessageEvent = 1986 ; // 643
    public static final short GetEventCategoriesMessageEvent = 1782 ; // 1735
    public static final short GetRoomEntryDataMessageEvent = 2300 ; // 2195
    public static final short MoveWallItemMessageEvent = 168 ; // 1038
    public static final short UpdateGroupColoursMessageEvent = 1764 ; // 1131
    public static final short HabboSearchMessageEvent = 1210 ; // 1145
    public static final short CommandBotMessageEvent = 2624 ; // 436
    public static final short GetInfobusPollsResultsMessageEvent = 6200;// 2978
    public static final short SetCustomStackingHeightMessageEvent = 3839 ; // 3794
    public static final short UnIgnoreUserMessageEvent = 2061 ; // 2879
    public static final short GetGuestRoomMessageEvent = 2230 ; // 3646
    public static final short SetMannequinFigureMessageEvent = 2209 ; // 190
    public static final short AssignRightsMessageEvent = 808 ; // 2578
    public static final short GetYouTubeTelevisionMessageEvent = 336 ; // 1183
    public static final short SetMessengerInviteStatusMessageEvent = 1086 ; // 2702
    public static final short UpdateFloorPropertiesMessageEvent = 875 ; // 400
    public static final short GetMoodlightConfigMessageEvent = 2813 ; // 2090
    public static final short PurchaseRoomPromotionMessageEvent = 777 ; // 2937
    public static final short SendRoomInviteMessageEvent = 1276 ; // 282
    public static final short ModerationMuteMessageEvent = 1945 ; // 3861
    public static final short SetRelationshipMessageEvent = 3768 ; // 681
    public static final short ChangeMottoMessageEvent = 2228 ; // 3079
    public static final short UnbanUserFromRoomMessageEvent = 992 ; // 451
    public static final short GetRoomRightsMessageEvent = 3385 ; // 551
    public static final short PurchaseGroupMessageEvent = 230 ; // 1934
    public static final short CreateFlatMessageEvent = 2752 ; // 859
    public static final short OpenHelpToolMessageEvent = 3267 ; // 1708
    public static final short ThrowDiceMessageEvent = 1990 ; // 348
    public static final short SaveWiredConditionConfigMessageEvent = 3203 ; // 2001
    public static final short GetCatalogOfferMessageEvent = 2594 ; // 1313
    public static final short PurchaseFromCatalogMessageEvent = 3492 ; // 3250
    public static final short PickupObjectMessageEvent = 3456 ; // 3064
    public static final short CancelQuestMessageEvent = 2397 ; // 3297
    public static final short NavigatorSearchMessageEvent = 249 ; // 946
    public static final short MoveAvatarMessageEvent = 6801 ; // 3802
    public static final short GetClientVersionMessageEvent = 6798 ; // 4000 IN PRODUCTION 201611 = 4000
    public static final short InitializeNavigatorMessageEvent = 2110 ; // 3231
    public static final short GetRoomFilterListMessageEvent = 1911 ; // 1973
    public static final short WhisperMessageEvent = 1543 ; // 88
    public static final short InitCryptoMessageEvent = 6799 ; // 3347 // IN PRODUCTION 201611 = 2688
    public static final short GetPetTrainingPanelMessageEvent = 2161 ; // 2691
    public static final short MoveObjectMessageEvent = 248 ; // 2955
    public static final short StartTypingMessageEvent = 1597 ; // 1266
    public static final short GoToHotelViewMessageEvent = 105 ; // 2644
    public static final short GetExtendedProfileMessageEvent = 3265 ; // 3455
    public static final short SendMsgMessageEvent = 3567 ; // 1750
    public static final short CancelTypingMessageEvent = 1474 ; // 978
    public static final short GetGroupFurniConfigMessageEvent = 367 ; // 2183
    public static final short RemoveGroupFavouriteMessageEvent = 1820 ; // 1332
    public static final short PlacePetMessageEvent = 2647 ; // 2174
    public static final short ModifyWhoCanRideHorseMessageEvent = 1472 ; // 579
    public static final short GetRelationshipsMessageEvent = 2138 ; // 716
    public static final short GetCatalogIndexMessageEvent = 223 ; // 2069
    public static final short ScrGetUserInfoMessageEvent = 3166 ; // 857
    public static final short ConfirmLoveLockMessageEvent = 3775 ; // 357
    public static final short RemoveSaddleFromHorseMessageEvent = 186 ; // 2118
    public static final short AcceptBuddyMessageEvent = 137 ; // 2363
    public static final short GetQuestListMessageEvent = 3333 ; // 3227
    public static final short SaveWardrobeOutfitMessageEvent = 800 ; // 1577
    public static final short BanUserMessageEvent = 1477 ; // 1414
    public static final short GetThreadDataMessageEvent = 232 ; // 1832
    public static final short GetBadgesMessageEvent = 2769 ; // 1723
    public static final short UseFurnitureMessageEvent = 99 ; // 788
    public static final short GoToFlatMessageEvent = 685 ; // 586
    public static final short GetModeratorUserRoomVisitsMessageEvent = 3526 ; // 2828
    public static final short GetSanctionStatusMessageEvent = 2746 ; // 1654
    public static final short SetChatPreferenceMessageEvent = 1262 ; // 3378
    public static final short ResizeNavigatorMessageEvent = 3159 ; // 3072
    public static final short RenderRoomMessageEvent = 3226 ; // 1184
    public static final short SongInventoryMessageEvent = 2304 ; // 1218
    public static final short SongIdMessageEvent = 3189 ; // 37
    public static final short SongDataMessageEvent = 3082 ; // 2675
    public static final short PlaylistMessageEvent = 1435 ; // 1176
    public static final short PlaylistAddMessageEvent = 753 ; // 52
    public static final short PlaylistRemoveMessageEvent = 3050 ; // 1562
    public static final short StaffPickRoomMessageEvent = 1918 ; // 1920
    public static final short SubmitPollAnswerMessageEvent = 3505 ; // 2978
    public static final short GetPollMessageEvent = 109 ; // 1422
    public static final short UpdateSnapshotsMessageEvent = 3373 ; // 290
    public static final short MarkAsReadMessageEvent = 1855 ; // 2073
    public static final short InitTradeMessageEvent = 1481 ; // 3722
    public static final short TradingOfferItemMessageEvent = 3107 ; // 3376
    public static final short TradingOfferItemsMessageEvent = 1263 ; // 3395
    public static final short TradingRemoveItemMessageEvent = 3845 ; // 240
    public static final short TradingAcceptMessageEvent = 3863 ; // 2165
    public static final short TradingCancelMessageEvent = 2551 ; // 1926
    public static final short TradingModifyMessageEvent = 1444 ; // 945
    public static final short TradingConfirmMessageEvent = 2760 ; // 561
    public static final short TradingCancelConfirmMessageEvent = 2341 ; // 1703
    public static final short RedeemVoucherMessageEvent = 339 ; // 1191
    public static final short ChangeNameMessageEvent = 2977 ; // 3124
    public static final short CheckValidNameMessageEvent = 3950 ; // 3946
    public static final short RequestGuideToolMessageEvent = 1922 ; // 2599
    public static final short RequestGuideAssistanceMessageEvent = 3338 ; // 2761
    public static final short GuideUserTypingMessageEvent = 519 ; // 3302
    public static final short GuideReportHelperMessageEvent = 3969 ; // 2581
    public static final short GuideRecommendHelperMessageEvent = 477 ; // 3563
    public static final short GuideUserMessageMessageEvent = 3899 ; // 3062
    public static final short GuideCancelHelpRequestMessageEvent = 291 ; // 903
    public static final short GuideHandleHelpRequestMessageEvent = 1424 ; // 835
    public static final short GuideVisitUserMessageEvent = 1052 ; // 2353
    public static final short GuideInviteUserMessageEvent = 234 ; // 2156
    public static final short GuideCloseHelpRequestMessageEvent = 887 ; // 175
    public static final short GuardianNoUpdatesWantedMessageEvent = 2501 ; // 2267
    public static final short GuardianVoteMessageEvent = 3961 ; // 3625
    public static final short GuardianAcceptRequestMessageEvent = 3365 ; // 706
    public static final short RequestReportUserBullyingMessageEvent = 3786 ; // 2385
    public static final short ReportBullyMessageEvent = 3060 ; // 318
    public static final short GetUserTagsMessageEvent = 17 ; // 1468
    public static final short FindNewFriendsMessageEvent = 516 ; // 488
    public static final short SaveNavigatorSearchMessageEvent = 2226 ; // 3301
    public static final short DeleteNavigatorSavedSearchMessageEvent = 1954 ; // 2235
    public static final short AddFavouriteRoomMessageEvent = 3817 ; // 3523
    public static final short DeleteFavouriteRoomMessageEvent = 309 ; // 1969
    public static final short SaveFootballGateMessageEvent = 924 ; // 177
    public static final short GroupConfirmRemoveMemberMessageEvent = 3593 ; // 423
    public static final short GetGroupPartsMessageEvent = 813 ; // 2047
    public static final short SetRoomCameraFollowMessageEvent = 1461 ; // 3527
    public static final short BreedPetsMessageEvent = 3382 ; // 2162
    public static final short OpenPetPackageMessageEvent = 3698 ; // 455
    public static final short RedeemClothingMessageEvent = 3374 ; // 3162
    public static final short EquipEffectMessageEvent = 2959 ; // 2255
    public static final short ThumbnailMessageEvent = 1982 ; // 2046
    public static final short PurchasePhotoMessageEvent = 2408 ; // 1554
    public static final short PurchasePhotoXXLMessageEvent = 3959;
    public static final short PhotoPricingMessageEvent = 796 ; // 654
    public static final short GetGameListMessageEvent = 2399 ; // 1288
    public static final short GetGameAchievementsMessageEvent = 741 ; // 97
    public static final short GetGameStatusMessageEvent = 2914 ; // 1740
    public static final short JoinGameQueueMessageEvent = 1458 ; // 3654
    public static final short NavigatorSaveViewModeMessageEvent = 1202 ; // 597
    public static final short JavascriptCallbackMessageEvent = 314 ; // 2433
    public static final short UserNuxMessageEvent = 1299 ; // 1025 ProcessNUXMessageEvent
    public static final short CancelPollMessageEvent = 1773 ; // 525
    public static final short RequestTargetOfferMessageEvent = 2487 ; // 2076
    public static final short RequestUserCreditsMessageEvent = 273 ; // 3598
    public static final short RequestCraftingRecipesAvailableMessageEvent = 3086 ; // 3106
    public static final short CraftingCraftItemMessageEvent = 3591 ; // 1556
    public static final short CraftingCraftSecretMessageEvent = 1251 ; // 3359
    public static final short CraftingAddRecipeMessageEvent = 633 ; // 442
    public static final short RequestCraftingRecipesMessageEvent = 1173 ; // 1838
    public static final short UnknowEvent1 = 1371 ; // 1838

    public static final short RequestClubGiftsMessageEvent = 487 ; // 3115
    public static final short GetClubDataEvent = 3285 ; // 1948
    public static final short HotelViewRequestCommunityGoalMessageEvent = 1145 ; // 3634
    public static final short HotelViewRequestBonusRareMessageEvent = 957 ; // 3968
    public static final short HotelViewRequestConcurrentUsersMessageEvent = 1343 ; // 469
    public static final short HotelViewConcurrentUsersButtonMessageEvent = 3872 ; // 2615
    public static final short HotelViewClaimBadgeMessageEvent = 3077 ; // 3169
    public static final short ClientVariablesMessageEvent = 1053 ; // 3638
    public static final short MovePetMessageEvent = 3449 ; // 3160
    public static final short RequestFriendRequestMessageEvent = 2448 ; // 1642
    public static final short RequestRecylerLogicMessageEvent = 398 ; // 624
    public static final short RequestResolutionMessageEvent = 359 ; // 3699
    public static final short RequestCreditsMessageEvent = 2650 ; // 2687
    public static final short ToggleMonsterplantBreedableMessageEvent = 3379 ; // 130
    public static final short RequestUserCitizenshipMessageEvent = 2127 ; // 3719
    public static final short NavigatorCollapseCategoryMessageEvent = 1834 ; // 783
    public static final short NavigatorUncollapseCategoryMessageEvent = 637 ; // 1676
    public static final short AdventCalendarOpenDayMessageEvent = 2257 ; // 3437 CheckCalendarDayMessageEvent
    public static final short AdventCalendarForceOpenMessageEvent = 3889 ; // 3214
    public static final short FloorPlanEditorRequestBlockedTilesMessageEvent = 1687 ; // 3219
    public static final short TestInventoryMessageEvent = 3500 ; // 2716
    public static final short PickNewUserGiftMessageEvent = 1822 ; // 3248
    public static final short JukeBoxRequestPlayListMessageEvent = 1325 ; // 1202
    public static final short AmbassadorAlertMessageEvent = 2996 ; // 2230
    public static final short AmbassadorVisitCommandMessageEvent = 2970 ; // 3890
    public static final short ModToolSanctionTradeLockMessageEvent = 3742 ; // 1722
    public static final short ReloadRecyclerMessageEvent = 1342 ; // 3268
    public static final short SearchRoomsMessageEvent = 3943 ; // 3269
    public static final short SearchRoomsMyFavoriteMessageEvent = 2578 ; // 3448
    public static final short SearchRoomsFriendsOwnMessageEvent = 2266 ; // 866
    public static final short SearchRoomsFriendsNowMessageEvent = 1786 ; // 1484
    public static final short SearchRoomsInGroupMessageEvent = 39 ; // 3093
    public static final short SearchRoomsVisitedMessageEvent = 2264 ; // 3977
    public static final short SearchRoomsWithRightsMessageEvent = 272 ; // 1995
    public static final short BuyItemMessageEvent = 1603 ; // 1222
    public static final short RequestMeMenuSettingsMessageEvent = 2388 ; // 1742
    public static final short AddEntityToGroupWhisperMessageEvent = 1118;
    public static final short GetMarketplaceConfigMessageEvent = 2597 ; // 1231
    public static final short SellItemMessageEvent = 3447 ; // 360
    public static final short TakeBackItemMessageEvent = 434 ; // 756
    public static final short GetClubDataMessageEvent = 3285 ; // 1259
    public static final short RequestCatalogModeMessageEvent = 1195 ; // 263
    public static final short EnableEffectMessageEvent = 1752 ; // 1291
    public static final short GetHabboGuildBadgesMessageEvent = 21 ; // 278
    public static final short CameraPublishToWebMessageEvent = 2068 ; // 1817
    public static final short RequestItemInfoMessageEvent = 3288 ; // 3372
    public static final short GameCenterLoadGameMessageEvent = 1054 ; // 1615
    public static final short GameCenterRequestGameStatusMessageEvent = 11 ; // 2861
    public static final short GameCenterRequestAccountStatusMessageEvent = 3171 ; // 1389
    public static final short RequestSellItemMessageEvent = 848 ; // 308
    public static final short RequestHighestScoreRoomsMessageEvent = 2939 ; // 3613
    public static final short RequestPublicRoomsMessageEvent = 1229 ; // 2312
    public static final short RequestPromotedRoomsMessageEvent = 2908 ; // 888
    public static final short RequestPromotionRoomsMessageEvent = 1075 ; // 2362
    public static final short RequestPopularRoomsMessageEvent = 2758 ; // 2961
    public static final short RequestTagsMessageEvent = 826 ; // 1189
    public static final short RentSpaceMessageEvent = 2946 ; // 490
    public static final short RentSpaceCancelMessageEvent = 1667 ; // 1345
    public static final short RecycleMessageEvent = 2771 ; // 3430
    public static final short RequestFriendsMessageEvent = 1523 ; // 3464
    public static final short CompostMonsterplantMessageEvent = 3835 ; // 1932
    public static final short BreedPets = 1638 ; // 2462
    public static final short RequestCatalogIndexMessageEvent = 2529 ; // 928
    public static final short SavePostItStickyPoleMessageEvent = 3283 ; // 1441
    public static final short RequestOwnItemsMessageEvent = 2105 ; // 940
    public static final short RequestOffersMessageEvent = 2407 ; // 1005
    public static final short RequestTalentTrackMessageEvent = 196 ; // 3568
    public static final short RequestMyRoomsMessageEvent = 2277 ; // 1529
    public static final short RequestDiscountEvent = 223;

    // Recycler
    public static final short OpenRecycleBoxEvent = 9434;
    public static final short ReloadRecyclerEvent = 1342;
    public static final short RequestRecylerLogicEvent = 398;
    public static final short RecycleEvent = 2771;

    //Gamecenter
    public static final short GetWeeklyLeaderboardEvent = 654;// 3195;
    public static final short UnknowLeaderboardEvent = 2565;// 3195;
    public static final short LastWeekLeaderboardWidgetEvent = 6002;// 3195;

    //Subscriptions
    public static final short GetClubPresentMessageEvent = 2276; // 1948
    public static final short GetPresentsPageMessageEvent = 487; // 3115

    //new
    public static final short GetWelcomeGiftMessageEvent = 66;
    public static final short ConfirmWelcomeGiftMessageEvent = 2638;
    public static final short DeleteItemOnInventoryMessageEvent = 9004;
    public static final short UseRandomStateItemMessageEvent = 3617;

    //WhisperGroup
    public static final short WhiperGroupMessageEvent = 1118;

    //Kisses
    public static final short KissesMessageEvent = 9003;
    public static final short NewKissesMessageEvent = 9004;

    //Landing new
    public static final short LTDCountdownMessageEvent = 271; // 3896
    public static final short VerifyEmailMessageEvent = 2721;// 2533;

    //SNOWSTORM
    public static final short UNKNOWN_SNOWSTORM_6009 = 6009;

    public static final short UNKNOWN_SNOWSTORM_6011 = 6011;

    public static final short SnowStormJoinQueueEvent = 6012;

    public static final short UNKNOWN_SNOWSTORM_6015 = 6015;
    public static final short UNKNOWN_SNOWSTORM_6016 = 6016;

    public static final short UNKNOWN_SNOWSTORM_6022 = 6022;
    public static final short UNKNOWN_SNOWSTORM_6023 = 6023;
    public static final short UNKNOWN_SNOWSTORM_6024 = 6024;
    public static final short UNKNOWN_SNOWSTORM_6025 = 6025;
    public static final short UNKNOWN_SNOWSTORM_2502 = 2502;
    public static final short SnowStormUserPickSnowballEvent = 6026;

    private static final Map<Integer, String> eventPacketNames = new HashMap<>();

    static {
        try {
            for (Field field : Events.class.getDeclaredFields()) {
                if (!Modifier.isPrivate(field.getModifiers()))
                    eventPacketNames.put(field.getInt(field.getName()), field.getName());
            }
        } catch (Exception ignored) {

        }
    }

    public static String valueOfId(int packetId) {
        if (eventPacketNames.containsKey(packetId)) {
            return eventPacketNames.get(packetId);
        }

        return "UnknownMessageEvent";
    }
}