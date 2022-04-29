package com.cometproject.server.protocol.headers;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


public class Events {
    // Current: PRODUCTION-201611291003-338511768
    // Previous: PRODUCTION-201709192204-203982672
    public static final int PongEvent = 2596 ; // 1552
    public static final int RequestTalentTrackEvent = 196;
    public static final int ConfirmUsernameMessageEvent = 6789 ; // 1590 // IN PRODUCTION 201611 = 3878
    public static final int GetRoomBannedUsersMessageEvent = 2267 ; // 2652
    public static final int GetPetInventoryMessageEvent = 3095 ; // 567
    public static final int DropHandItemMessageEvent = 2814 ; // 2846
    public static final int ReleaseTicketMessageEvent = 1572 ; // 3042
    public static final int GetModeratorRoomInfoMessageEvent = 707 ; // 1299
    public static final int KickUserMessageEvent = 1320 ; // 3838
    public static final int SaveWiredEffectConfigMessageEvent = 2281 ; // 1843
    public static final int RespectPetMessageEvent = 3202 ; // 31
    public static final int GenerateSecretKeyMessageEvent = 773 ; // 412
    public static final int HotelViewRequestLTDAvailabilityMessageEvent = 410;
    public static final int GetModeratorTicketChatlogsMessageEvent = 211 ; // 3461
    public static final int GetAchievementsMessageEvent = 219 ; // 3047
    public static final int SaveWiredTriggerConfigMessageEvent = 1520 ; // 2167
    public static final int AcceptGroupMembershipMessageEvent = 3386 ; // 2891
    public static final int GetGroupFurniSettingsMessageEvent = 2651 ; // 2666
    public static final int TakeAdminRightsMessageEvent = 722 ; // 2542
    public static final int RemoveAllRightsMessageEvent = 2683 ; // 2398
    public static final int UpdateThreadMessageEvent = 3045 ; // 777
    public static final int ManageGroupMessageEvent = 1004 ; // 2697
    public static final int ModifyRoomFilterListMessageEvent = 3001 ; // 2973
    public static final int SSOTicketMessageEvent = 6788 ; // 286 // IN PRODUCTION 201611 = 2419
    public static final int JoinGroupMessageEvent = 998 ; // 2529
    public static final int DeclineGroupMembershipMessageEvent = 1894 ; // 2688
    public static final int UniqueIDMessageEvent = 6785 ; // 921 IN PRODUCTION 201611 = 2490
    public static final int RemoveMyRightsMessageEvent = 3182 ; // 3596
    public static final int PurchaseTargetOfferEvent = 1826;
    public static final int TargetOfferStateEvent = 2041;
    public static final int PetUseItemMessageEvent = 1328 ; // 2160
    public static final int GetPetInformationMessageEvent = 2934 ; // 983
    public static final int GiveHandItemMessageEvent = 2941 ; // 2094
    public static final int UpdateFigureDataMessageEvent = 2730 ; // 3509
    public static final int RemoveGroupMemberMessageEvent = 593 ; // 3326
    public static final int EventLogMessageEvent = 6780 ; // 658 // IN PRODUCTION 201611 = 3457
    public static final int RefreshCampaignMessageEvent = 2912 ; // 2260
    public static final int GetPromotableRoomsMessageEvent = 2283 ; // 2372
    public static final int UseOneWayGateMessageEvent = 2765 ; // 3521
    public static final int AddStickyNoteMessageEvent = 2248 ; // 890
    public static final int GetSelectedBadgesMessageEvent = 2091 ; // 904
    public static final int UpdateStickyNoteMessageEvent = 3666 ; // 3742
    public static final int CloseTicketMesageEvent = 2067 ; // 3406
    public static final int RequestBuddyMessageEvent = 3157 ; // 3619
    public static final int GetFurnitureAliasesMessageEvent = 3898 ; // 3253
    public static final int GetRoomSettingsMessageEvent = 3129 ; // 3700
    public static final int RequestFurniInventoryMessageEvent = 3150 ; // 3750
    public static final int ModerationKickMessageEvent = 2582 ; // 794
    public static final int OpenFlatConnectionMessageEvent = 2312 ; // 2450
    public static final int SpectateRoomMessageEvent = 3093 ; // 1187
    public static final int DanceMessageEvent = 2080 ; // 1126
    public static final int RemoveBuddyMessageEvent = 1689 ; // 1106
    public static final int LatencyTestMessageEvent = 295 ; // 2348
    public static final int InfoRetrieveMessageEvent = 357 ; // 2401
    public static final int YouTubeGetNextVideo = 3005 ; // 1294
    public static final int SetObjectDataMessageEvent = 3608 ; // 2194
    public static final int MessengerInitMessageEvent = 2781 ; // 1186
    public static final int PickUpBotMessageEvent = 3323 ; // 2856
    public static final int ActionMessageEvent = 2456 ; // 3017
    public static final int LookToMessageEvent = 3301 ; // 3557
    public static final int ToggleMoodlightMessageEvent = 2296 ; // 3146
    public static final int FollowFriendMessageEvent = 3997 ; // 2
    public static final int PickUpPetMessageEvent = 1581 ; // 1207
    public static final int GetSellablePetBreedsMessageEvent = 1756 ; // 171
    public static final int GetForumUserProfileMessageEvent = 2249 ; // 2593
    public static final int GetForumsListDataMessageEvent = 873 ; // 559
    public static final int IgnoreUserMessageEvent = 1117 ; // 2097
    public static final int DeleteRoomMessageEvent = 532 ; // 638
    public static final int StartQuestMessageEvent = 3604 ; // 2425
    public static final int GetGiftWrappingConfigurationMessageEvent = 418 ; // 3335
    public static final int UpdateGroupIdentityMessageEvent = 3137 ; // 1885
    public static final int RideHorseMessageEvent = 1036 ; // 45
    public static final int ApplySignMessageEvent = 1975 ; // 1195
    public static final int FindRandomFriendingRoomMessageEvent = 1703 ; // 1275
    public static final int GetModeratorUserChatlogMessageEvent = 1391 ; // 1925
    public static final int GetWardrobeMessageEvent = 2742 ; // 2699
    public static final int MuteUserMessageEvent = 3485 ; // 3753
    public static final int UpdateForumSettingsMessageEvent = 2214 ; // 2339
    public static final int ApplyDecorationMessageEvent = 711 ; // 1388
    public static final int GetBotInventoryMessageEvent = 3848 ; // 1199
    public static final int UseHabboWheelMessageEvent = 2144 ; // 2452
    public static final int EditRoomPromotionMessageEvent = 3991 ; // 22
    public static final int GetModeratorUserInfoMessageEvent = 3295 ; // 1588
    public static final int PlaceBotMessageEvent = 1592 ; // 3391
    public static final int GetCatalogPageMessageEvent = 412 ; // 2148
    public static final int GetThreadsListDataMessageEvent = 436 ; // 276
    public static final int ShoutMessageEvent = 2085 ; // 1795
    public static final int DiceOffMessageEvent = 1533 ; // 3670
    public static final int LetUserInMessageEvent = 1644 ; // 754
    public static final int SetActivatedBadgesMessageEvent = 644 ; // 1429
    public static final int UpdateGroupSettingsMessageEvent = 3435 ; // 164
    public static final int ApproveNameMessageEvent = 2109 ; // 321
    public static final int SubmitNewTicketMessageEvent = 1691 ; // 2586
    public static final int DeleteGroupMessageEvent = 1134 ; // 3593
    public static final int DeleteStickyNoteMessageEvent = 3336 ; // 2506
    public static final int GetGroupInfoMessageEvent = 2991 ; // 1415
    public static final int GetStickyNoteMessageEvent = 3964 ; // 3686
    public static final int DeclineBuddyMessageEvent = 2890 ; // 1591
    public static final int OpenGiftMessageEvent = 3558 ; // 2066
    public static final int GiveRoomScoreMessageEvent = 3582 ; // 337
    public static final int SetGroupFavouriteMessageEvent = 3549 ; // 538
    public static final int SetMannequinNameMessageEvent = 2850 ; // 1301
    public static final int RoomDimmerSavePresetMessageEvent = 1648 ; // 1488
    public static final int UpdateGroupBadgeMessageEvent = 1991 ; // 1088
    public static final int PickTicketMessageEvent = 15 ; // 1001
    public static final int SetTonerMessageEvent = 2880 ; // 2186
    public static final int RespectUserMessageEvent = 2694 ; // 2377
    public static final int DeleteGroupThreadMessageEvent = 1397 ; // 1549
    public static final int DeleteGroupReplyMessageEvent = 286 ; // 1616
    public static final int CreditFurniRedeemMessageEvent = 3115 ; // 1009
    public static final int InitDiffieHandshake = 3110;
    public static final int ModerationMsgMessageEvent = 1840 ; // 975
    public static final int ToggleYouTubeVideoMessageEvent = 2069 ; // 1777
    public static final int UpdateNavigatorSettingsMessageEvent = 1740 ; // 3724
    public static final int ToggleMuteToolMessageEvent = 3637 ; // 36
    public static final int ChatMessageEvent = 1314 ; // 1831
    public static final int SaveRoomSettingsMessageEvent = 1969 ; // 1090
    public static final int PurchaseFromCatalogAsGiftMessageEvent = 1411 ; // 2142
    public static final int GetGroupCreationWindowMessageEvent = 798 ; // 1907
    public static final int GiveAdminRightsMessageEvent = 2894 ; // 1587
    public static final int GetGroupMembersMessageEvent = 312 ; // 1048
    public static final int ModerateRoomMessageEvent = 3260 ; // 2949
    public static final int GetForumStatsMessageEvent = 3149 ; // 2932
    public static final int GetPromoArticlesMessageEvent = 1827 ; // 1293
    public static final int SitMessageEvent = 2235 ; // 1805
    public static final int SetSoundSettingsMessageEvent = 1367 ; // 1718
    public static final int ModerationCautionMessageEvent = 229 ; // 2849
    public static final int InitializeFloorPlanSessionMessageEvent = 3559 ; // 698
    public static final int ModeratorActionMessageEvent = 3842 ; // 1992
    public static final int PostGroupContentMessageEvent = 3529 ; // 324
    public static final int GetModeratorRoomChatlogMessageEvent = 2587 ; // 329
    public static final int GetUserFlatCatsMessageEvent = 3027 ; // 1761
    public static final int RemoveRightsMessageEvent = 2064 ; // 1877
    public static final int ModerationBanMessageEvent = 2766 ; // 265
    public static final int CanCreateRoomMessageEvent = 2128 ; // 3866
    public static final int UseWallItemMessageEvent = 210 ; // 3032
    public static final int PlaceObjectMessageEvent = 1258 ; // 1268
    public static final int OpenBotActionMessageEvent = 1986 ; // 643
    public static final int GetEventCategoriesMessageEvent = 1782 ; // 1735
    public static final int GetRoomEntryDataMessageEvent = 2300 ; // 2195
    public static final int MoveWallItemMessageEvent = 168 ; // 1038
    public static final int UpdateGroupColoursMessageEvent = 1764 ; // 1131
    public static final int HabboSearchMessageEvent = 1210 ; // 1145
    public static final int CommandBotMessageEvent = 2624 ; // 436
    public static final int GetInfobusPollsResultsMessageEvent = 6200;// 2978
    public static final int SetCustomStackingHeightMessageEvent = 3839 ; // 3794
    public static final int UnIgnoreUserMessageEvent = 2061 ; // 2879
    public static final int GetGuestRoomMessageEvent = 2230 ; // 3646
    public static final int SetMannequinFigureMessageEvent = 2209 ; // 190
    public static final int AssignRightsMessageEvent = 808 ; // 2578
    public static final int GetYouTubeTelevisionMessageEvent = 336 ; // 1183
    public static final int SetMessengerInviteStatusMessageEvent = 1086 ; // 2702
    public static final int UpdateFloorPropertiesMessageEvent = 875 ; // 400
    public static final int GetMoodlightConfigMessageEvent = 2813 ; // 2090
    public static final int PurchaseRoomPromotionMessageEvent = 777 ; // 2937
    public static final int SendRoomInviteMessageEvent = 1276 ; // 282
    public static final int ModerationMuteMessageEvent = 1945 ; // 3861
    public static final int SetRelationshipMessageEvent = 3768 ; // 681
    public static final int ChangeMottoMessageEvent = 2228 ; // 3079
    public static final int UnbanUserFromRoomMessageEvent = 992 ; // 451
    public static final int GetRoomRightsMessageEvent = 3385 ; // 551
    public static final int PurchaseGroupMessageEvent = 230 ; // 1934
    public static final int CreateFlatMessageEvent = 2752 ; // 859
    public static final int OpenHelpToolMessageEvent = 3267 ; // 1708
    public static final int ThrowDiceMessageEvent = 1990 ; // 348
    public static final int SaveWiredConditionConfigMessageEvent = 3203 ; // 2001
    public static final int GetCatalogOfferMessageEvent = 2594 ; // 1313
    public static final int PurchaseFromCatalogMessageEvent = 3492 ; // 3250
    public static final int PickupObjectMessageEvent = 3456 ; // 3064
    public static final int CancelQuestMessageEvent = 2397 ; // 3297
    public static final int NavigatorSearchMessageEvent = 249 ; // 946
    public static final int MoveAvatarMessageEvent = 3320 ; // 3802
    public static final int GetClientVersionMessageEvent = 4000 ; // 4000 // IN SWF DANN 6666 // IN PRODUCTION 201611 = 4000
    public static final int InitializeNavigatorMessageEvent = 2110 ; // 3231
    public static final int GetRoomFilterListMessageEvent = 1911 ; // 1973
    public static final int WhisperMessageEvent = 1543 ; // 88
    public static final int InitCryptoMessageEvent = 2688 ; // 3347 // INN SWF DANN 6688 // IN PRODUCTION 201611 = 2688
    public static final int GetPetTrainingPanelMessageEvent = 2161 ; // 2691
    public static final int MoveObjectMessageEvent = 248 ; // 2955
    public static final int StartTypingMessageEvent = 1597 ; // 1266
    public static final int GoToHotelViewMessageEvent = 105 ; // 2644
    public static final int GetExtendedProfileMessageEvent = 3265 ; // 3455
    public static final int SendMsgMessageEvent = 3567 ; // 1750
    public static final int CancelTypingMessageEvent = 1474 ; // 978
    public static final int GetGroupFurniConfigMessageEvent = 367 ; // 2183
    public static final int RemoveGroupFavouriteMessageEvent = 1820 ; // 1332
    public static final int PlacePetMessageEvent = 2647 ; // 2174
    public static final int ModifyWhoCanRideHorseMessageEvent = 1472 ; // 579
    public static final int GetRelationshipsMessageEvent = 2138 ; // 716
    public static final int GetCatalogIndexMessageEvent = 223 ; // 2069
    public static final int ScrGetUserInfoMessageEvent = 3166 ; // 857
    public static final int ConfirmLoveLockMessageEvent = 3775 ; // 357
    public static final int RemoveSaddleFromHorseMessageEvent = 186 ; // 2118
    public static final int AcceptBuddyMessageEvent = 137 ; // 2363
    public static final int GetQuestListMessageEvent = 3333 ; // 3227
    public static final int SaveWardrobeOutfitMessageEvent = 800 ; // 1577
    public static final int BanUserMessageEvent = 1477 ; // 1414
    public static final int GetThreadDataMessageEvent = 232 ; // 1832
    public static final int GetBadgesMessageEvent = 2769 ; // 1723
    public static final int UseFurnitureMessageEvent = 99 ; // 788
    public static final int GoToFlatMessageEvent = 685 ; // 586
    public static final int GetModeratorUserRoomVisitsMessageEvent = 3526 ; // 2828
    public static final int GetSanctionStatusMessageEvent = 2746 ; // 1654
    public static final int SetChatPreferenceMessageEvent = 1262 ; // 3378
    public static final int ResizeNavigatorMessageEvent = 3159 ; // 3072
    public static final int RenderRoomMessageEvent = 3226 ; // 1184
    public static final int SongInventoryMessageEvent = 2304 ; // 1218
    public static final int SongIdMessageEvent = 3189 ; // 37
    public static final int SongDataMessageEvent = 3082 ; // 2675
    public static final int PlaylistMessageEvent = 1435 ; // 1176
    public static final int PlaylistAddMessageEvent = 753 ; // 52
    public static final int PlaylistRemoveMessageEvent = 3050 ; // 1562
    public static final int StaffPickRoomMessageEvent = 1918 ; // 1920
    public static final int SubmitPollAnswerMessageEvent = 3505 ; // 2978
    public static final int GetPollMessageEvent = 109 ; // 1422
    public static final int UpdateSnapshotsMessageEvent = 3373 ; // 290
    public static final int MarkAsReadMessageEvent = 1855 ; // 2073
    public static final int InitTradeMessageEvent = 1481 ; // 3722
    public static final int TradingOfferItemMessageEvent = 3107 ; // 3376
    public static final int TradingOfferItemsMessageEvent = 1263 ; // 3395
    public static final int TradingRemoveItemMessageEvent = 3845 ; // 240
    public static final int TradingAcceptMessageEvent = 3863 ; // 2165
    public static final int TradingCancelMessageEvent = 2551 ; // 1926
    public static final int TradingModifyMessageEvent = 1444 ; // 945
    public static final int TradingConfirmMessageEvent = 2760 ; // 561
    public static final int TradingCancelConfirmMessageEvent = 2341 ; // 1703
    public static final int RedeemVoucherMessageEvent = 339 ; // 1191
    public static final int ChangeNameMessageEvent = 2977 ; // 3124
    public static final int CheckValidNameMessageEvent = 3950 ; // 3946
    public static final int RequestGuideToolMessageEvent = 1922 ; // 2599
    public static final int RequestGuideAssistanceMessageEvent = 3338 ; // 2761
    public static final int GuideUserTypingMessageEvent = 519 ; // 3302
    public static final int GuideReportHelperMessageEvent = 3969 ; // 2581
    public static final int GuideRecommendHelperMessageEvent = 477 ; // 3563
    public static final int GuideUserMessageMessageEvent = 3899 ; // 3062
    public static final int GuideCancelHelpRequestMessageEvent = 291 ; // 903
    public static final int GuideHandleHelpRequestMessageEvent = 1424 ; // 835
    public static final int GuideVisitUserMessageEvent = 1052 ; // 2353
    public static final int GuideInviteUserMessageEvent = 234 ; // 2156
    public static final int GuideCloseHelpRequestMessageEvent = 887 ; // 175
    public static final int GuardianNoUpdatesWantedMessageEvent = 2501 ; // 2267
    public static final int GuardianVoteMessageEvent = 3961 ; // 3625
    public static final int GuardianAcceptRequestMessageEvent = 3365 ; // 706
    public static final int RequestReportUserBullyingMessageEvent = 3786 ; // 2385
    public static final int ReportBullyMessageEvent = 3060 ; // 318
    public static final int GetUserTagsMessageEvent = 17 ; // 1468
    public static final int FindNewFriendsMessageEvent = 516 ; // 488
    public static final int SaveNavigatorSearchMessageEvent = 2226 ; // 3301
    public static final int DeleteNavigatorSavedSearchMessageEvent = 1954 ; // 2235
    public static final int AddFavouriteRoomMessageEvent = 3817 ; // 3523
    public static final int DeleteFavouriteRoomMessageEvent = 309 ; // 1969
    public static final int SaveFootballGateMessageEvent = 924 ; // 177
    public static final int GroupConfirmRemoveMemberMessageEvent = 3593 ; // 423
    public static final int GetGroupPartsMessageEvent = 813 ; // 2047
    public static final int SetRoomCameraFollowMessageEvent = 1461 ; // 3527
    public static final int BreedPetsMessageEvent = 3382 ; // 2162
    public static final int OpenPetPackageMessageEvent = 3698 ; // 455
    public static final int RedeemClothingMessageEvent = 3374 ; // 3162
    public static final int EquipEffectMessageEvent = 2959 ; // 2255
    public static final int ThumbnailMessageEvent = 1982 ; // 2046
    public static final int PurchasePhotoMessageEvent = 2408 ; // 1554
    public static final int PurchasePhotoXXLMessageEvent = 3959;
    public static final int PhotoPricingMessageEvent = 796 ; // 654
    public static final int GetGameListMessageEvent = 2399 ; // 1288
    public static final int GetGameAchievementsMessageEvent = 741 ; // 97
    public static final int GetGameStatusMessageEvent = 2914 ; // 1740
    public static final int JoinGameQueueMessageEvent = 1458 ; // 3654
    public static final int NavigatorSaveViewModeMessageEvent = 1202 ; // 597
    public static final int JavascriptCallbackMessageEvent = 314 ; // 2433
    public static final int UserNuxMessageEvent = 1299 ; // 1025 ProcessNUXMessageEvent
    public static final int CancelPollMessageEvent = 1773 ; // 525
    public static final int RequestTargetOfferMessageEvent = 2487 ; // 2076
    public static final int RequestUserCreditsMessageEvent = 273 ; // 3598
    public static final int RequestCraftingRecipesAvailableMessageEvent = 3086 ; // 3106
    public static final int CraftingCraftItemMessageEvent = 3591 ; // 1556
    public static final int CraftingCraftSecretMessageEvent = 1251 ; // 3359
    public static final int CraftingAddRecipeMessageEvent = 633 ; // 442
    public static final int RequestCraftingRecipesMessageEvent = 1173 ; // 1838
    public static final int UnknowEvent1 = 1371 ; // 1838

    public static final int RequestClubGiftsMessageEvent = 487 ; // 3115
    public static final int GetClubDataEvent = 3285 ; // 1948
    public static final int HotelViewRequestCommunityGoalMessageEvent = 1145 ; // 3634
    public static final int HotelViewRequestBonusRareMessageEvent = 957 ; // 3968
    public static final int HotelViewRequestConcurrentUsersMessageEvent = 1343 ; // 469
    public static final int HotelViewConcurrentUsersButtonMessageEvent = 3872 ; // 2615
    public static final int HotelViewClaimBadgeMessageEvent = 3077 ; // 3169
    public static final int ClientVariablesMessageEvent = 1053 ; // 3638
    public static final int MovePetMessageEvent = 3449 ; // 3160
    public static final int RequestFriendRequestMessageEvent = 2448 ; // 1642
    public static final int RequestRecylerLogicMessageEvent = 398 ; // 624
    public static final int RequestResolutionMessageEvent = 359 ; // 3699
    public static final int RequestCreditsMessageEvent = 2650 ; // 2687
    public static final int ToggleMonsterplantBreedableMessageEvent = 3379 ; // 130
    public static final int RequestUserCitizenshipMessageEvent = 2127 ; // 3719
    public static final int NavigatorCollapseCategoryMessageEvent = 1834 ; // 783
    public static final int NavigatorUncollapseCategoryMessageEvent = 637 ; // 1676
    public static final int AdventCalendarOpenDayMessageEvent = 2257 ; // 3437 CheckCalendarDayMessageEvent
    public static final int AdventCalendarForceOpenMessageEvent = 3889 ; // 3214
    public static final int FloorPlanEditorRequestBlockedTilesMessageEvent = 1687 ; // 3219
    public static final int TestInventoryMessageEvent = 3500 ; // 2716
    public static final int PickNewUserGiftMessageEvent = 1822 ; // 3248
    public static final int JukeBoxRequestPlayListMessageEvent = 1325 ; // 1202
    public static final int AmbassadorAlertMessageEvent = 2996 ; // 2230
    public static final int AmbassadorVisitCommandMessageEvent = 2970 ; // 3890
    public static final int ModToolSanctionTradeLockMessageEvent = 3742 ; // 1722
    public static final int ReloadRecyclerMessageEvent = 1342 ; // 3268
    public static final int SearchRoomsMessageEvent = 3943 ; // 3269
    public static final int SearchRoomsMyFavoriteMessageEvent = 2578 ; // 3448
    public static final int SearchRoomsFriendsOwnMessageEvent = 2266 ; // 866
    public static final int SearchRoomsFriendsNowMessageEvent = 1786 ; // 1484
    public static final int SearchRoomsInGroupMessageEvent = 39 ; // 3093
    public static final int SearchRoomsVisitedMessageEvent = 2264 ; // 3977
    public static final int SearchRoomsWithRightsMessageEvent = 272 ; // 1995
    public static final int BuyItemMessageEvent = 1603 ; // 1222
    public static final int RequestMeMenuSettingsMessageEvent = 2388 ; // 1742
    public static final int AddEntityToGroupWhisperMessageEvent = 1118;
    public static final int GetMarketplaceConfigMessageEvent = 2597 ; // 1231
    public static final int SellItemMessageEvent = 3447 ; // 360
    public static final int TakeBackItemMessageEvent = 434 ; // 756
    public static final int GetClubDataMessageEvent = 3285 ; // 1259
    public static final int RequestCatalogModeMessageEvent = 1195 ; // 263
    public static final int EnableEffectMessageEvent = 1752 ; // 1291
    public static final int GetHabboGuildBadgesMessageEvent = 21 ; // 278
    public static final int CameraPublishToWebMessageEvent = 2068 ; // 1817
    public static final int RequestItemInfoMessageEvent = 3288 ; // 3372
    public static final int GameCenterLoadGameMessageEvent = 1054 ; // 1615
    public static final int GameCenterRequestGameStatusMessageEvent = 11 ; // 2861
    public static final int GameCenterRequestAccountStatusMessageEvent = 3171 ; // 1389
    public static final int RequestSellItemMessageEvent = 848 ; // 308
    public static final int RequestHighestScoreRoomsMessageEvent = 2939 ; // 3613
    public static final int RequestPublicRoomsMessageEvent = 1229 ; // 2312
    public static final int RequestPromotedRoomsMessageEvent = 2908 ; // 888
    public static final int RequestPromotionRoomsMessageEvent = 1075 ; // 2362
    public static final int RequestPopularRoomsMessageEvent = 2758 ; // 2961
    public static final int RequestTagsMessageEvent = 826 ; // 1189
    public static final int RentSpaceMessageEvent = 2946 ; // 490
    public static final int RentSpaceCancelMessageEvent = 1667 ; // 1345
    public static final int RecycleMessageEvent = 2771 ; // 3430
    public static final int RequestFriendsMessageEvent = 1523 ; // 3464
    public static final int CompostMonsterplantMessageEvent = 3835 ; // 1932
    public static final int BreedPets = 1638 ; // 2462
    public static final int RequestCatalogIndexMessageEvent = 2529 ; // 928
    public static final int SavePostItStickyPoleMessageEvent = 3283 ; // 1441
    public static final int RequestOwnItemsMessageEvent = 2105 ; // 940
    public static final int RequestOffersMessageEvent = 2407 ; // 1005
    public static final int RequestTalentTrackMessageEvent = 196 ; // 3568
    public static final int RequestMyRoomsMessageEvent = 2277 ; // 1529
    public static final int RequestDiscountEvent = 223;

    // Recycler
    public static final int OpenRecycleBoxEvent = 9434;
    public static final int ReloadRecyclerEvent = 1342;
    public static final int RequestRecylerLogicEvent = 398;
    public static final int RecycleEvent = 2771;

    //Gamecenter
    public static final int GetWeeklyLeaderboardEvent = 654;// 3195;
    public static final int UnknowLeaderboardEvent = 2565;// 3195;
    public static final int LastWeekLeaderboardWidgetEvent = 6002;// 3195;

    //Subscriptions
    public static final int GetClubPresentMessageEvent = 2276; // 1948
    public static final int GetPresentsPageMessageEvent = 487; // 3115

    //new
    public static final int GetWelcomeGiftMessageEvent = 66;
    public static final int ConfirmWelcomeGiftMessageEvent = 2638;
    public static final int DeleteItemOnInventoryMessageEvent = 9004;
    public static final int UseRandomStateItemMessageEvent = 3617;

    //WhisperGroup
    public static final int WhiperGroupMessageEvent = 1118;

    //Kisses
    public static final int KissesMessageEvent = 9003;
    public static final int NewKissesMessageEvent = 9004;

    //Landing new
    public static final int LTDCountdownMessageEvent = 271; // 3896
    public static final int VerifyEmailMessageEvent = 2721;// 2533;

    //SNOWSTORM
    public static final int UNKNOWN_SNOWSTORM_6009 = 6009;

    public static final int UNKNOWN_SNOWSTORM_6011 = 6011;

    public static final int SnowStormJoinQueueEvent = 6012;

    public static final int UNKNOWN_SNOWSTORM_6015 = 6015;
    public static final int UNKNOWN_SNOWSTORM_6016 = 6016;

    public static final int UNKNOWN_SNOWSTORM_6022 = 6022;
    public static final int UNKNOWN_SNOWSTORM_6023 = 6023;
    public static final int UNKNOWN_SNOWSTORM_6024 = 6024;
    public static final int UNKNOWN_SNOWSTORM_6025 = 6025;
    public static final int UNKNOWN_SNOWSTORM_2502 = 2502;
    public static final int SnowStormUserPickSnowballEvent = 6026;

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