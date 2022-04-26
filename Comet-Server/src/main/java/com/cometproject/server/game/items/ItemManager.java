package com.cometproject.server.game.items;

import com.cometproject.api.game.furniture.IFurnitureService;
import com.cometproject.api.game.furniture.types.CrackableReward;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.IMusicData;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.game.items.crafting.CraftingMachine;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.storage.queries.crafting.CraftingDao;
import com.cometproject.server.storage.queries.items.ItemDao;
import com.cometproject.server.storage.queries.items.MusicDao;
import com.cometproject.server.storage.queries.items.TeleporterDao;
import com.cometproject.storage.api.StorageContext;
import com.cometproject.storage.api.data.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ItemManager implements IFurnitureService {
    private static ItemManager itemManagerInstance;

    private final Logger log = LogManager.getLogger(ItemManager.class.getName());

    private Map<Integer, FurnitureDefinition> itemDefinitions;

    private Map<Integer, Integer> itemSpriteIdToDefinitionId;
    private Map<Integer, IMusicData> musicData;

    private Map<Long, Integer> itemIdToVirtualId;
    private Map<Integer, Long> virtualIdToItemId;
    private Map<Integer, CrackableReward> crackableRewards;
    private List<CraftingMachine> craftingMachines;

    private AtomicInteger itemIdCounter;
    private Integer saddleId;

    public ItemManager() {

    }

    public static ItemManager getInstance() {
        if (itemManagerInstance == null) {
            itemManagerInstance = new ItemManager();
        }

        return itemManagerInstance;
    }

    @Override
    public void initialize() {
        this.itemDefinitions = new HashMap<>();
        this.musicData = new HashMap<>();

        this.itemIdToVirtualId = new ConcurrentHashMap<>();
        this.virtualIdToItemId = new ConcurrentHashMap<>();
        this.crackableRewards = new ConcurrentHashMap<>();
        this.craftingMachines = new ArrayList<>();

        this.itemIdCounter = new AtomicInteger(1);

        this.loadItemDefinitions();
        this.loadMusicData();
        this.loadCraftingMachines();

        log.info("ItemManager initialized");
    }

    public void loadCraftingMachines() {
        if (!this.craftingMachines.isEmpty()) {
            this.craftingMachines.clear();
        }

        for(FurnitureDefinition item : this.itemDefinitions.values()) {
            if(item.getInteraction().equals("crafting")) {
                final CraftingMachine machine = new CraftingMachine(item.getId());

                CraftingDao.loadAllowedItems(machine);
                CraftingDao.loadRecipes(machine);

                this.craftingMachines.add(machine);
            }
        }
    }

    public CraftingMachine getCraftingMachine(int itemId) {
        final CraftingMachine[] machine = {null};

        for(final CraftingMachine machineX : this.craftingMachines){
            if(machineX.getBaseId() == itemId) { machine[0] = machineX; }
        }

        return machine[0];
    }

    @Override
    public void loadItemDefinitions() {
        Map<Integer, FurnitureDefinition> tempMap = this.itemDefinitions;
        Map<Integer, Integer> tempSpriteIdItemMap = this.itemSpriteIdToDefinitionId;

        try {
            this.crackableRewards.clear();

            this.itemDefinitions = ItemDao.getDefinitions();
            this.crackableRewards = ItemDao.getCrackableRewards();
            this.itemSpriteIdToDefinitionId = new HashMap<>();

            log.info("Loaded " + this.crackableRewards.size() + " crackable rewards");
        } catch (Exception e) {
            log.error("Error while loading item definitions", e);
        }

        if (tempMap.size() >= 1) {
            tempMap.clear();
            tempSpriteIdItemMap.clear();
        }

        if (this.itemDefinitions != null) {
            for (FurnitureDefinition itemDefinition : this.itemDefinitions.values()) {
                if (itemDefinition.getItemName().equals("horse_saddle1")) {
                    this.saddleId = itemDefinition.getId();
                }

                this.itemSpriteIdToDefinitionId.put(itemDefinition.getSpriteId(), itemDefinition.getId());
            }
        }

        log.info("Loaded " + this.getItemDefinitions().size() + " item definitions");
    }

    public FurnitureDefinition getByBaseId(int baseId) {
        return this.itemDefinitions.get(baseId);
    }

    @Override
    public void loadMusicData() {
        if (!this.musicData.isEmpty()) {
            this.musicData.clear();
        }

        MusicDao.getMusicData(this.musicData);
        log.info("Loaded " + this.musicData.size() + " songs");
    }

    @Override
    public int getItemVirtualId(long itemId) {
        if (this.itemIdToVirtualId.containsKey(itemId)) {
            return this.itemIdToVirtualId.get(itemId);
        }

        final int virtualId = this.itemIdCounter.getAndIncrement();

        this.itemIdToVirtualId.put(itemId, virtualId);
        this.virtualIdToItemId.put(virtualId, itemId);

        return virtualId;
    }

    @Override
    public void disposeItemVirtualId(long itemId) {
        int virtualId = this.getItemVirtualId(itemId);

        this.itemIdToVirtualId.remove(itemId);
        this.virtualIdToItemId.remove(virtualId);
    }

    @Override
    public Long getItemIdByVirtualId(int virtualId) {
        return this.virtualIdToItemId.get(virtualId);
    }

    @Override
    public long getTeleportPartner(long itemId) {
        return TeleporterDao.getPairId(itemId);
    }

    @Override
    public int roomIdByItemId(long itemId) {
        final Data<Integer> data = Data.createEmpty();

        StorageContext.getCurrentContext().getRoomItemRepository().getRoomIdByItemId(itemId, data::set);

        if (data.has()) {
            return data.get();
        }

        return 0;
    }

    @Override
    public FurnitureDefinition getDefinition(int itemId) {
        if (this.getItemDefinitions().containsKey(itemId)) {
            return this.getItemDefinitions().get(itemId);
        }

        return null;
    }

    @Override
    public IMusicData getMusicData(int songId) {
        if (this.musicData.containsKey(songId)) {
            return this.musicData.get(songId);
        }

        return null;
    }

    @Override
    public IMusicData getMusicDataByName(String name) {
        for (IMusicData musicData : this.musicData.values()) {
            if (musicData.getName().equals(name)) {
                return musicData;
            }
        }

        return null;
    }

    @Override
    public Map<Long, Integer> getItemIdToVirtualIds() {
        return itemIdToVirtualId;
    }

    @Override
    public FurnitureDefinition getBySpriteId(int spriteId) {
        return this.itemDefinitions.get(this.itemSpriteIdToDefinitionId.get(spriteId));
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Map<Integer, FurnitureDefinition> getItemDefinitions() {
        return this.itemDefinitions;
    }

    @Override
    public Integer getSaddleId() {
        return saddleId;
    }

    public Map<Integer, CrackableReward> getCrackableRewards() {
        return crackableRewards;
    }

    public PlayerItem handleRecycle(Player player, String itemId) {
        String extradata = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + Calendar.getInstance().get(Calendar.YEAR);

        PlayerItem item = null;

        return item;
    }
}
