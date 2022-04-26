package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.gameevents.*;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGameStatus {
    public static void parse(final IComposer msg, final SnowWarRoom arena, boolean isFull) {
        msg.writeInt(arena.Turn);
        msg.writeInt(seed(arena.Turn) + arena.checksum);

        msg.writeInt(1);
        {
            msg.writeInt(arena.gameEvents.size());
            for (Event evt : arena.gameEvents) {
                msg.writeInt(evt.EventType); // Event Type
                if (evt.EventType == Event.PLAYERLEFT) {
                    SerializeGame2EventPlayerLeft.parse(msg, (PlayerLeft) evt);
                } else if (evt.EventType == Event.MOVE) {
                    SerializeGame2EventMove.parse(msg, (UserMove) evt);
                } else if (evt.EventType == Event.MAKENOWBALL) {
                    SerializeGame2EventPickSnowBall.parse(msg, (MakeSnowBall) evt);
                } else if (evt.EventType == Event.CREATESNOWBALL) {
                    SerializeGame2EventCreateSnowBall.parse(msg, (CreateSnowBall) evt);
                } else if (evt.EventType == Event.BALLTHROWPOSITION) {
                    SerializeGame2EventBallThrowToPosition.parse(msg, (BallThrowToPosition) evt);
                } else if (evt.EventType == Event.BALLTHROWHUMAN) {
                    SerializeGame2EventBallThrowToHuman.parse(msg, (BallThrowToHuman) evt);
                } else if (evt.EventType == Event.PICKBALLFROMGAMEITEM) {
                    SerializeGame2EventPickBallFromGameItem.parse(msg, (PickBallFromGameItem) evt);
                } else if (evt.EventType == Event.ADDBALLTOMACHINE) {
                    SerializeGame2EventAddBallToMachine.parse(msg, (AddBallToMachine) evt);
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }
                if (!isFull) {
                    evt.apply();
                }
            }
        }
    }

    public static void parseNew(final MessageWriter ClientMessage, final SnowWarRoom arena, boolean isFull) {
        int i = 0;

        ComposerShit.add(arena.Turn, ClientMessage);
        ComposerShit.add(seed(arena.Turn) + arena.checksum, ClientMessage);

        ComposerShit.add(1, ClientMessage);
        {
            ComposerShit.add(ClientMessage.setSaved(0), ClientMessage);
            for(final Event evt : arena.gameEvents) {
                ComposerShit.add(evt.EventType, ClientMessage); // Event Type
                if (evt.EventType == Event.PLAYERLEFT) {
                    SerializeGame2EventPlayerLeft.parse(ClientMessage, (PlayerLeft) evt);
                } else if (evt.EventType == Event.MOVE) {
                    SerializeGame2EventMove.parse(ClientMessage, (UserMove) evt);
                } else if (evt.EventType == Event.MAKENOWBALL) {
                    SerializeGame2EventPickSnowBall.parse(ClientMessage, (MakeSnowBall) evt);
                } else if (evt.EventType == Event.CREATESNOWBALL) {
                    SerializeGame2EventCreateSnowBall.parse(ClientMessage, (CreateSnowBall) evt);
                } else if (evt.EventType == Event.BALLTHROWPOSITION) {
                    SerializeGame2EventBallThrowToPosition.parse(ClientMessage, (BallThrowToPosition) evt);
                } else if (evt.EventType == Event.BALLTHROWHUMAN) {
                    SerializeGame2EventBallThrowToHuman.parse(ClientMessage, (BallThrowToHuman) evt);
                } else if (evt.EventType == Event.PICKBALLFROMGAMEITEM) {
                    SerializeGame2EventPickBallFromGameItem.parse(ClientMessage, (PickBallFromGameItem) evt);
                } else if (evt.EventType == Event.ADDBALLTOMACHINE) {
                    SerializeGame2EventAddBallToMachine.parse(ClientMessage, (AddBallToMachine) evt);
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }

                if(!isFull) {
                    evt.apply();
                }

                i++;
            }
            ClientMessage.writeSaved(i);
        }
    }

    public static int seed(int Turn) {
        int k;
        if (Turn == 0) {
            Turn = -1;
        }
        k = Turn << 13;
        Turn = Turn ^ k;
        k = Turn >> 17;
        Turn = Turn ^ k;
        k = Turn << 5;
        Turn = Turn ^ k;
        return Turn;
    }
}
