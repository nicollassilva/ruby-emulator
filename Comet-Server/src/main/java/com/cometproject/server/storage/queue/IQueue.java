package com.cometproject.server.storage.queue;

public interface IQueue {
    void init();

    void stop();

    void run();
}
