package com.cometproject.api.networking.messages;

public interface IMessageEvent {


    short readShort();

    int readInt();

    boolean readBoolean();

    String readString();

    byte[] readBytes(int length);

    byte[] toRawBytes();

    int getId();

    int getLength();
}
