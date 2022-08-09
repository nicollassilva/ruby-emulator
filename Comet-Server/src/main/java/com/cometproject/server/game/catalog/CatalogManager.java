package com.cometproject.server.game.catalog;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.ICatalogService;
import com.cometproject.api.game.catalog.ITargetOffer;
import com.cometproject.api.game.catalog.types.*;
import com.cometproject.api.game.catalog.types.purchase.ICatalogPurchaseHandler;
import com.cometproject.api.game.catalog.types.subscriptions.IClubOffer;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.composers.catalog.data.CatalogOfferConfigMessageComposer;
import com.cometproject.server.game.catalog.purchase.CatalogPurchaseHandler;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.nuxs.NuxGift;
import com.cometproject.server.storage.queries.catalog.CatalogDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Stream;


public class CatalogManager implements ICatalogService {
    private static CatalogManager catalogManagerInstance;
    /**
     * Maps the offer ID of an item to the page ID.
     */
    private final Map<Integer, ICatalogOffer> catalogOffers = new HashMap<>();
    /**
     * The new style of gift boxes
     */
    private final List<Integer> giftBoxesNew = Lists.newArrayList();
    /**
     * The old style of gift boxes
     */
    private final List<Integer> giftBoxesOld = Lists.newArrayList();
    /**
     * Featured catalog pages (they are displayed on the front page)
     */
    private final List<ICatalogFrontPageEntry> frontPageEntries = new ArrayList<>();
    /**
     * Redeemable clothing items
     */
    private final Map<String, IClothingItem> clothingItems = Maps.newConcurrentMap();
    /**
     * The pages within the catalog
     */
    private Map<Integer, ICatalogPage> pages;
    /**
     * The items within the catalog
     */
    private Map<Integer, ICatalogItem> items;
    /**
     * The club offers items
     */
    public final THashMap<Integer, IClubOffer> clubOfferItems;
    /**
     * The club offers items
     */
    public final THashMap<Integer, ITargetOffer> targetOffers;
    /**
     * The catalog item IDs to page IDs map
     */
    private Map<Integer, Integer> catalogItemIdToPageId;
    /**
     * The handler of everything catalog-purchase related
     */
    private ICatalogPurchaseHandler purchaseHandler;
    /**
     * The logger for the catalog manager
     */
    public final Logger log = LogManager.getLogger(CatalogManager.class.getName());

    /**
     * Parent pages
     */
    private final List<ICatalogPage> parentPages = Lists.newCopyOnWriteArrayList();

    /**
     * Furniture recycler
     */
    public final THashMap<Integer, THashSet<FurnitureDefinition>> prizes;

    /**
     * Capture ecotron_box
     */
    public final FurnitureDefinition ecotronItem;

    /**
     * Initialize the catalog
     */
    public CatalogManager() {
        this.clubOfferItems = new THashMap<>();
        this.targetOffers = new THashMap<>();
        this.prizes = new THashMap<>();

        this.ecotronItem = ItemManager.getInstance().getBySpriteId(CometSettings.ITEM_ID_ECOTRON_BOX);
    }

    public static CatalogManager getInstance() {
        if (catalogManagerInstance == null) {
            catalogManagerInstance = new CatalogManager();
        }

        return catalogManagerInstance;
    }

    @Override
    public void initialize() {
        this.pages = new ListOrderedMap<>();
        this.items = new ListOrderedMap<>();

        this.catalogItemIdToPageId = new HashMap<>();

        this.purchaseHandler = new CatalogPurchaseHandler();

        this.loadItemsAndPages();
        this.loadGiftBoxes();
        this.loadClothingItems();
        this.loadClubOffers();
        this.loadTargetOffers();
        this.loadRecycler();

        log.info("CatalogManager initialized");
    }

    /**
     * Load all catalog pages
     */
    @Override
    public void loadItemsAndPages() {
        if (this.items.size() >= 1) {
            this.items.clear();
        }

        if (this.getPages().size() >= 1) {
            this.getPages().clear();
        }

        if (this.frontPageEntries.size() >= 1) {
            this.frontPageEntries.clear();
        }

        if (getCatalogOffers().size() >= 1) {
            getCatalogOffers().clear();
        }

        if (this.catalogItemIdToPageId.size() >= 1) {
            this.catalogItemIdToPageId.clear();
        }

        try {
            CatalogDao.getItems(this.items);
            CatalogDao.getPages(this.pages);
            CatalogDao.getFeaturedPages(this.frontPageEntries);
            CatalogDao.getClubOffers(this.clubOfferItems);
        } catch (Exception e) {
            log.error("Error while loading catalog pages/items", e);
        }

        for (ICatalogPage page : this.pages.values()) {
            for (Integer item : page.getItems().keySet()) {
                this.catalogItemIdToPageId.put(item, page.getId());
            }
        }

        this.sortCatalogChildren();

        log.info("Loaded " + this.getPages().size() + " catalog pages and " + this.items.size() + " catalog items");
    }

    public void loadRecycler() {
        if(this.prizes.size() >= 1) {
            this.prizes.clear();
        }

        CatalogDao.loadAllRecycler();

        log.info("Loaded " + this.prizes.size() + " recyclers");
    }

    /**
     * Get pages for a specific player rank
     *
     * @return A list of pages that are accessible by the specified rank
     */
    public List<ICatalogPage> getPagesByRank(int rank, int parentId) {
        final List<ICatalogPage> pages = new ArrayList<>();

        for (final ICatalogPage page : this.getPages().values()) {
            if(page.isVipOnly() && rank != CometSettings.vipRank && rank < CometSettings.rankCanSeeVipContent) continue;

            if (rank >= page.getMinRank() && page.getParentId() == parentId) {
                pages.add(page);
            }
        }

        return pages;
    }

    @Override
    public void loadGiftBoxes() {
        if (this.giftBoxesNew.size() >= 1) {
            this.giftBoxesNew.clear();
        }

        if (this.giftBoxesOld.size() >= 1) {
            this.giftBoxesOld.clear();
        }

        CatalogDao.loadGiftBoxes(this.giftBoxesOld, this.giftBoxesNew);
        log.info("Loaded " + (this.giftBoxesNew.size() + this.giftBoxesOld.size()) + " gift wrappings");
    }

    @Override
    public void loadClothingItems() {
        if (this.clothingItems.size() >= 1) {
            this.clothingItems.clear();
        }

        CatalogDao.getClothing(this.clothingItems);
        log.info("Loaded " + clothingItems.size() + " clothing items");
    }

    @Override
    public void loadClubOffers() {
        if (this.clubOfferItems.size() > 0) {
            this.clubOfferItems.clear();
        }

        CatalogDao.getClubOffers(this.clubOfferItems);
        log.info("Loaded " + clubOfferItems.size() + " club offers");
    }

    @Override
    public void loadTargetOffers() {
        synchronized (this.targetOffers) {
            this.targetOffers.clear();

            CatalogDao.getTargetOffers(this.targetOffers);

            log.info("Loaded " + this.targetOffers.size() + " target offers!");
        }
    }

    public ITargetOffer getTargetOffer(int offerId) {
        return this.targetOffers.get(offerId);
    }

    public List<IClubOffer> getClubOfferItems() {
        final List<IClubOffer> offers = new ArrayList<>();

        for (final Map.Entry<Integer, IClubOffer> entry : this.clubOfferItems.entrySet()) {
            if (! entry.getValue().isDeal()) {
                offers.add(entry.getValue());
            }
        }

        return offers;
    }

    /**
     * Get pages for a specific player rank
     *
     * @param rank Player rank
     * @return A list of pages that are accessible by the specified rank
     */
    @Override
    public List<ICatalogPage> getPagesForRank(int rank) {
        final List<ICatalogPage> pages = new ArrayList<>();

        for (final ICatalogPage page : this.getPages().values()) {
            if (rank >= page.getMinRank()) {
                pages.add(page);
            }
        }

        return pages;
    }

    public void sortCatalogChildren() {
        this.parentPages.clear();

        for (final ICatalogPage catalogPage : this.pages.values()) {
            if (catalogPage.getParentId() != -1) {
                final ICatalogPage parentPage = this.getPage(catalogPage.getParentId());

                if(parentPage == null) {
                    log.warn("Page " + catalogPage.getId() + " with invalid parent id: " + catalogPage.getParentId());
                } else {
                    parentPage.getChildren().add(catalogPage);
                }
            } else {
                this.parentPages.add(catalogPage);
            }
        }

        this.parentPages.sort(Comparator.comparing(ICatalogPage::getOrder));
    }

    @Override
    public ICatalogItem getCatalogItemByOfferId(int offerId) {
        final ICatalogOffer offer = getCatalogOffers().get(offerId);

        if (offer == null)
            return null;

        final ICatalogPage page = this.getPage(offer.getCatalogPageId());

        if (page == null)
            return null;

        return page.getItems().get(offer.getCatalogItemId());
    }

    @Override
    public Map<Integer, ICatalogItem> getItemsForPage(int pageId) {
        final Map<Integer, ICatalogItem> items = Maps.newHashMap();

        for (final Map.Entry<Integer, ICatalogItem> catalogItem : this.items.entrySet()) {
            if (catalogItem.getValue().getPageId() == pageId) {
                items.put(catalogItem.getKey(), catalogItem.getValue());
            }
        }

        return items;
    }

    /**
     * Get a catalog page by its ID
     *
     * @param id Catalog Page ID
     * @return Catalog Page object with the specified ID
     */
    @Override
    public ICatalogPage getPage(int id) {
        if (this.pageExists(id)) {
            return this.getPages().get(id);
        }

        return null;
    }

    @Override
    public ICatalogPage getCatalogPageByCatalogItemId(final int id) {
        if (!this.catalogItemIdToPageId.containsKey(id)) {
            return null;
        }

        return this.pages.get(this.catalogItemIdToPageId.get(id));
    }

    /**
     * Get a catalog item by its ID
     *
     * @param catalogItemId The ID of the catalog item
     * @return CatalogItem object with specified ID
     */
    @Override
    public ICatalogItem getCatalogItem(final int catalogItemId) {
        return this.items.get(catalogItemId);
    }

    /**
     * Does a page with a specific ID exist?
     *
     * @param id The ID of the page we want to check that exists
     * @return Whether or not the page with the specified ID exists
     */
    @Override
    public boolean pageExists(int id) {
        return this.getPages().containsKey(id);
    }

    /**
     * Get all catalog pages
     *
     * @return All catalog pages in-memory
     */
    @Override
    public Map<Integer, ICatalogPage> getPages() {
        return this.pages;
    }

    /**
     * Get the catalog page handler
     *
     * @return The catalog page handler
     */
    @Override
    public ICatalogPurchaseHandler getPurchaseHandler() {
        return purchaseHandler;
    }

    /**
     * Gift wrappings new
     *
     * @return The new style of gift wrapping boxes
     */
    @Override
    public List<Integer> getGiftBoxesNew() {
        return giftBoxesNew;
    }

    /**
     * Gift wrappings old
     *
     * @return The old style of gift wrapping boxes
     */
    @Override
    public List<Integer> getGiftBoxesOld() {
        return giftBoxesOld;
    }

    /**
     * List all front page entries
     *
     * @return List of all front page entries
     */
    @Override
    public List<ICatalogFrontPageEntry> getFrontPageEntries() {
        return this.frontPageEntries;
    }

    @Override
    public Map<String, IClothingItem> getClothingItems() {
        return this.clothingItems;
    }

    @Override
    public Map<Integer, ICatalogOffer> getCatalogOffers() {
        return catalogOffers;
    }

    @Override
    public List<ICatalogPage> getParentPages() {
        return parentPages;
    }

    private List<NuxGift> nuxGiftsData = new ArrayList<>();

    public List<NuxGift> getNuxGifts() { return this.nuxGiftsData; }
    public List<NuxGift> getNuxGiftsSelectionView(int type) {
        List<NuxGift> nuxTypeGifts = new ArrayList<>();

        for(NuxGift nuxGift : this.nuxGiftsData){
            if(nuxGift.getPageType() == type){
                nuxTypeGifts.add(nuxGift);
            }
        }


        return nuxTypeGifts;
    }

    public void loadNuxGifts() {
        if(this.nuxGiftsData != null) {
            this.nuxGiftsData.clear();
        }

        this.nuxGiftsData = CatalogDao.getNuxGiftsSelectionView();
    }

    public int calculateDiscountedPrice(int originalPrice, int amount, ICatalogItem item) {
        if(!item.allowOffer()) return  originalPrice * amount;

        int basicDiscount = amount / CatalogOfferConfigMessageComposer.DISCOUNT_BATCH_SIZE;

        int bonusDiscount = 0;
        if(basicDiscount >= CatalogOfferConfigMessageComposer.MINIMUM_DISCOUNTS_FOR_BONUS) {
            if(amount % CatalogOfferConfigMessageComposer.DISCOUNT_BATCH_SIZE == CatalogOfferConfigMessageComposer.DISCOUNT_BATCH_SIZE -1) {
                bonusDiscount = 1;
            }

            bonusDiscount += basicDiscount - CatalogOfferConfigMessageComposer.MINIMUM_DISCOUNTS_FOR_BONUS;
        }

        int additionalDiscounts = 0;
        for(int threshold : CatalogOfferConfigMessageComposer.ADDITIONAL_DISCOUNT_THRESHOLDS) {
            if (amount >= threshold) additionalDiscounts++;
        }

        int totalDiscountedItems = (basicDiscount * CatalogOfferConfigMessageComposer.DISCOUNT_AMOUNT_PER_BATCH) + bonusDiscount + additionalDiscounts;

        return Math.max(0, originalPrice * (amount - totalDiscountedItems));
    }

    public FurnitureDefinition getRandomRecyclerPrize() {
        int level = 1;

        if(Comet.getRandom().nextInt(CometSettings.ECOTRON_RARITY_CHANCE_5) + 1 == CometSettings.ECOTRON_RARITY_CHANCE_5) {
            level = 5;
        } else if (Comet.getRandom().nextInt(CometSettings.ECOTRON_RARITY_CHANCE_4) + 1 == CometSettings.ECOTRON_RARITY_CHANCE_4) {
            level = 4;
        } else if (Comet.getRandom().nextInt(CometSettings.ECOTRON_RARITY_CHANCE_3) + 1 == CometSettings.ECOTRON_RARITY_CHANCE_3) {
            level = 3;
        } else if (Comet.getRandom().nextInt(CometSettings.ECOTRON_RARITY_CHANCE_2) + 1 == CometSettings.ECOTRON_RARITY_CHANCE_2) {
            level = 2;
        }

        if(this.prizes.containsKey(level) && !this.prizes.get(level).isEmpty()) {
            return (FurnitureDefinition) this.prizes.get(level).toArray()[Comet.getRandom().nextInt(this.prizes.get(level).size())];
        } else {
            log.error("No rewards specified por rarity level" + level);
        }

        return null;
    }
}
