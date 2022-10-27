package com.cometproject.server.game.catalog.types;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.types.CatalogPageType;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.bundles.RoomBundleManager;
import com.cometproject.server.game.rooms.bundles.types.RoomBundle;
import com.cometproject.api.game.catalog.types.bundles.RoomBundleItem;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class CatalogPage implements ICatalogPage {
    private static final Type listType = new TypeToken<List<String>>() {
    }.getType();

    private final int id;
    private final CatalogPageType type;
    private final String caption;
    private final int icon;
    private final int minRank;
    private final String template;
    private final int parentId;
    private final String linkName;
    private int order;

    private final boolean enabled;
    private final boolean vipOnly;

    private final List<String> images;
    private final List<String> texts;

    private final Map<Integer, ICatalogItem> items;
    private final String extraData;

    private final List<ICatalogPage> children = Lists.newArrayList();
    private boolean sorted = false;

    public CatalogPage(ResultSet data, Map<Integer, ICatalogItem> items) throws SQLException {

        this.id = data.getInt("id");
        this.caption = data.getString("caption");
        this.icon = data.getInt("icon_image");
        this.minRank = data.getInt("min_rank");
        this.template = data.getString("page_layout");
        this.parentId = data.getInt("parent_id");
        this.linkName = data.getString("link");
        this.type = CatalogPageType.valueOf(data.getString("type"));
        this.extraData = data.getString("extra_data");
        this.order = data.getInt("order_num");
        this.vipOnly = data.getString("vip_only").equals("1");

        //if (data.getString("page_images") == null || data.getString("page_images").isEmpty()) {
            this.images = new ArrayList<>();
            this.images.add(data.getString("page_headline"));
            this.images.add(data.getString("page_teaser"));
            this.images.add(data.getString("page_special"));

        /*} else {
            this.images = JsonUtil.getInstance().fromJson(data.getString("page_images"), listType);
        }*/

            //if (data.getString("page_texts") == null || data.getString("page_texts").isEmpty()) {
            this.texts = new ArrayList<>();
            this.texts.add(data.getString("page_text_1"));
            this.texts.add(data.getString("page_text_2"));
            this.texts.add(data.getString("page_text_details"));
        /*} else {
            this.texts = JsonUtil.getInstance().fromJson(data.getString("page_texts"), listType);
        }*/

            this.enabled = data.getString("enabled").equals("1");

        if (this.type == CatalogPageType.BUNDLE) {
            final RoomBundle roomBundle = RoomBundleManager.getInstance().getBundle(this.extraData);

            if (roomBundle != null) {
                final List<ICatalogBundledItem> bundledItems = new ArrayList<>();
                final Map<Integer, List<RoomBundleItem>> bundleItems = new HashMap<>();

                for (final RoomBundleItem bundleItem : roomBundle.getRoomBundleData()) {
                    if (bundleItems.containsKey(bundleItem.getItemId())) {
                        bundleItems.get(bundleItem.getItemId()).add(bundleItem);
                    } else {
                        bundleItems.put(bundleItem.getItemId(), Lists.newArrayList(bundleItem));
                    }
                }

                for (final Map.Entry<Integer, List<RoomBundleItem>> bundledItem : bundleItems.entrySet()) {
                    bundledItems.add(new CatalogBundledItem("0", bundledItem.getValue().size(), bundledItem.getKey()));
                }

                final ICatalogItem catalogItem = new CatalogItem(roomBundle.getId(), "-1", bundledItems, "single_bundle",
                        roomBundle.getCostCredits(), roomBundle.getCostActivityPoints(), roomBundle.getCostVip(), roomBundle.getCostSeasonal(),1, false, 0, 0, false, "", "", this.id, 0);

                this.items = new HashMap<>();
                this.items.put(catalogItem.getId(), catalogItem);
            } else {
                this.items = new HashMap<>();
            }
        } else {
            this.items = items;
        }
    }

    public List<ICatalogPage> getChildren() {
        if (!sorted) {
            this.children.sort(Comparator.comparing(ICatalogPage::getCaption));
            sorted = true;
        }

        return this.children;
    }

    @Override
    public int getOfferSize() {
        int size = 0;

        for (ICatalogItem item : this.items.values()) {
            if (item.getItemId().equals("-1")) continue;

            if (ItemManager.getInstance().getDefinition(item.getItems().get(0).getItemId()) != null) {
                if (ItemManager.getInstance().getDefinition(item.getItems().get(0).getItemId()).getOfferId() != -1 && ItemManager.getInstance().getDefinition(item.getItems().get(0).getItemId()).getOfferId() != 0) {
                    size++;
                }
            }
        }

        return size;
    }

    @Override
    public int getOfferSizeByRank(int rank) {
        int size = 0;

        for (ICatalogItem item : this.items.values()) {
            if(this.isVipOnly() && (rank != CometSettings.vipRank && rank < CometSettings.rankCanSeeVipContent)) {
                continue;
            }

            if (rank >= this.getMinRank() && this.getTemplate().equals("default_3x3")) {
                size++;
            }
        }

        return size;
    }
  
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public int getIcon() {
        return icon;
    }

    @Override
    public int getMinRank() {
        return minRank;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public int getParentId() {
        return parentId;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isVipOnly() {
        return vipOnly;
    }

    @Override
    public Map<Integer, ICatalogItem> getItems() {
        return items;
    }

    @Override
    public List<String> getImages() {
        return images;
    }

    @Override
    public List<String> getTexts() {
        return texts;
    }

    @Override
    public String getLinkName() {
        return linkName;
    }

    @Override
    public String getExtraData() {
        return extraData;
    }

    @Override
    public CatalogPageType getType() {
        return type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
