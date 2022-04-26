package com.cometproject.server.storage.queue.pets;

import com.cometproject.api.game.pets.IPetData;
import com.cometproject.server.game.pets.data.PetData;
import com.cometproject.server.storage.queries.pets.PetDao;
import com.cometproject.server.storage.queue.IQueue;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SavePetsQueue implements CometTask, IQueue {

    private ConcurrentLinkedQueue<PetData> petsQueue;
    private ScheduledFuture periodic;

    public void init() {
        this.petsQueue = new ConcurrentLinkedQueue<>();
        this.periodic = CometThreadManager.getInstance().executePeriodic(this, 1500, 5000, TimeUnit.MILLISECONDS);
    }

    public void savePet(PetData item) {
        this.petsQueue.remove(item);
        this.petsQueue.add(item);
    }

    public void stop() {
        this.periodic.cancel(false);
    }

    @Override
    public void run() {
        Set<IPetData> pets = Sets.newHashSet();
        for (int i = 0; i < this.petsQueue.size(); i++) {
            pets.add(this.petsQueue.poll());
        }
        if (pets.size() > 0) {
            PetDao.savePetsBatch(pets);
        }
    }
}
