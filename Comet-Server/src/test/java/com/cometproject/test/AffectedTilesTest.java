package com.cometproject.test;

import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.google.common.collect.Lists;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class AffectedTilesTest extends TestCase {
    private static final ArrayList<AffectedTile> tiles4x4 = Lists.newArrayList(
            new AffectedTile(1, 1),
            new AffectedTile(2, 1),
            new AffectedTile(1, 2),
            new AffectedTile(2, 2)
    );

    public void test_make_square_invalid_params_assert(){
        Assert.assertThrows("Length must be >= 1",AssertionError.class, ()-> AffectedTile.makeSquare(-1,1,1,1,0) );
        Assert.assertThrows("Width must be >= 1",AssertionError.class, ()-> AffectedTile.makeSquare(1,-1,1,1,0) );
        Assert.assertThrows("Rotation must be >= 0",AssertionError.class, ()-> AffectedTile.makeSquare(1,1,1,1,-1) );
    }

    public void test_affected_tiles_including_main_tile_at_4x4(int rotation){
        final List<AffectedTile> tiles = AffectedTile.getAffectedBothTilesAt(2,2,1,1,rotation);
        assertEquals(4, tiles.size());
        for (final AffectedTile tile :tiles) {
            assertTrue(tiles4x4.contains(tile));
        }
    }
    public void test_affected_tiles_at_4x4(int rotation){
        final List<AffectedTile> tiles = AffectedTile.getAffectedTilesAt(2,2,1,1,rotation);
        assertEquals(3, tiles.size());
        for (final AffectedTile tile :tiles) {
            assertTrue(tiles4x4.contains(tile));
        }
    }

    public void test_affected_tiles_at_4x4_all_rots(){
        for (int rotation = 0; rotation < 8; rotation+=2) {
            test_affected_tiles_at_4x4(rotation);
        }
    }

    public void test_affected_tiles_including_main_tile_at_4x4_all_rots(){
        for (int rotation = 0; rotation < 8; rotation+=2) {
            test_affected_tiles_including_main_tile_at_4x4(rotation);
        }
    }
}
