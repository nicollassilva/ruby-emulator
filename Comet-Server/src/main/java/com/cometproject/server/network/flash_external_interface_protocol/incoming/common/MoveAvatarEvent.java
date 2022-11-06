package com.cometproject.server.network.flash_external_interface_protocol.incoming.common;

import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;

import java.util.LinkedList;
import java.util.List;

public class MoveAvatarEvent extends IncomingExternalInterfaceMessage<MoveAvatarEvent.JSONMoveAvatarEvent> {
    private static final short DEFAULT_WALK_AMOUNT = 1;

    public MoveAvatarEvent() {
        super(JSONMoveAvatarEvent.class);
    }
    @Override
    public void handle(Session client, JSONMoveAvatarEvent message) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if(room == null) {
            return;
        }

        try {
            if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().hasAttribute("warp")) {
                return;
            }

            final PlayerEntity entity = client.getPlayer().getEntity();

            if (!entity.isVisible()) return;

            if (room.getModel().getDoorX() == entity.getPosition().getX() && room.getModel().getDoorY() == entity.getPosition().getY()) return;
            
            final Position goal = entity.getWalkingGoal();

            switch (message.direction) {
                case "stop":
                    entity.cancelWalk();
                    entity.setWalkingGoal(entity.getPosition().getX(), entity.getPosition().getY());
                    //entity.removeStatus(RoomEntityStatus.MOVE);
                    return;
                case "left":
                    goal.setY(goal.getY() + DEFAULT_WALK_AMOUNT);
                    break;
                case "right":
                    goal.setY(goal.getY() - DEFAULT_WALK_AMOUNT);
                    break;
                case "up":
                    goal.setX(goal.getX() - DEFAULT_WALK_AMOUNT);
                    break;
                case "down":
                    goal.setX(goal.getX() + DEFAULT_WALK_AMOUNT);
                    break;
                default: return;
            }

            if (goal.getX() == entity.getPosition().getX() && goal.getY() == entity.getPosition().getY()) {
                return;
            }

            if (entity.hasAttribute("teleport")) {
                final List<Square> squares = new LinkedList<>();
                squares.add(new Square(goal.getX(), goal.getY()));

                entity.unIdle();
                entity.resetAfkTimer();

                if (entity.getMountedEntity() != null) {
                    entity.getMountedEntity().setWalkingPath(squares);
                    entity.getMountedEntity().setWalkingGoal(goal.getX(), goal.getY());
                }

                entity.setWalkingPath(squares);
                entity.setWalkingGoal(goal.getX(), goal.getY());
                return;
            }

            if (!entity.sendUpdateMessage()) {
                entity.setSendUpdateMessage(true);
            }

            if (entity.canWalk() && !entity.isOverriden() && entity.isVisible()) {
                entity.moveTo(goal);
            }
        } catch (Exception e) {
            // do nothing
        }
    }
    static class JSONMoveAvatarEvent {
        String direction;
    }
}
