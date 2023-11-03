package com.ignfab.minalac.generator.minetest;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class TestMTVoxelWorld {

    private Method getGetPosValueMethod() throws NoSuchMethodException {
        Method method = MTVoxelWorld.class.getDeclaredMethod("getPosValue", int.class, int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private Method getGetNodeRelativePositionMethod() throws NoSuchMethodException {
        Method method = MTVoxelWorld.class.getDeclaredMethod("getNodeRelativePosition", int.class);
        method.setAccessible(true);
        return method;
    }

    private Method getGetBlockPositionMethod() throws NoSuchMethodException {
        Method method = MTVoxelWorld.class.getDeclaredMethod("getBlockPosition", int.class);
        method.setAccessible(true);
        return method;
    }

    @Test
    public void testGetPosValue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MTVoxelWorld world = new MTVoxelWorld();

        assertEquals(0, getGetPosValueMethod().invoke(world, 0, 0, 0));

        assertEquals(16777216, getGetPosValueMethod().invoke(world, 0, 0, 1));
        assertEquals(-16777216, getGetPosValueMethod().invoke(world, 0, 0, -1));

        assertEquals(1, getGetPosValueMethod().invoke(world, 1, 0, 0));
        assertEquals(-1, getGetPosValueMethod().invoke(world, -1, 0, 0));

        assertEquals(4096, getGetPosValueMethod().invoke(world, 0, 1, 0));
        assertEquals(-4096, getGetPosValueMethod().invoke(world, 0, -1, 0));

        assertEquals(-2, getGetPosValueMethod().invoke(world, -2, 0, 0));
        assertEquals(-3, getGetPosValueMethod().invoke(world, -3, 0, 0));

        assertEquals(-4098, getGetPosValueMethod().invoke(world, -2, -1, 0));
        assertEquals(-4099, getGetPosValueMethod().invoke(world, -3, -1, 0));
    }

    @Test
    public void testGetNodeRelativePosition() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MTVoxelWorld world = new MTVoxelWorld();

        assertEquals(0, getGetNodeRelativePositionMethod().invoke(world, 0));
        assertEquals(10, getGetNodeRelativePositionMethod().invoke(world, 10));
        assertEquals(15, getGetNodeRelativePositionMethod().invoke(world, 15));

        assertEquals(0, getGetNodeRelativePositionMethod().invoke(world, 16));
        assertEquals(1, getGetNodeRelativePositionMethod().invoke(world, 17));
        assertEquals(15, getGetNodeRelativePositionMethod().invoke(world, 31));

        assertEquals(0, getGetNodeRelativePositionMethod().invoke(world, -16));
        assertEquals(2, getGetNodeRelativePositionMethod().invoke(world, -14));
        assertEquals(15, getGetNodeRelativePositionMethod().invoke(world, -1));

        assertEquals(0, getGetNodeRelativePositionMethod().invoke(world, -32));
        assertEquals(14, getGetNodeRelativePositionMethod().invoke(world, -18));
        assertEquals(15, getGetNodeRelativePositionMethod().invoke(world, -17));
    }

    @Test
    public void testGetBlockPosition() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MTVoxelWorld world = new MTVoxelWorld();

        assertEquals(0, getGetBlockPositionMethod().invoke(world, 0));
        assertEquals(0, getGetBlockPositionMethod().invoke(world, 10));
        assertEquals(0, getGetBlockPositionMethod().invoke(world, 15));

        assertEquals(2, getGetBlockPositionMethod().invoke(world, 32));
        assertEquals(2, getGetBlockPositionMethod().invoke(world, 40));
        assertEquals(2, getGetBlockPositionMethod().invoke(world, 47));

        assertEquals(-1, getGetBlockPositionMethod().invoke(world, -16));
        assertEquals(-1, getGetBlockPositionMethod().invoke(world, -10));
        assertEquals(-1, getGetBlockPositionMethod().invoke(world, -1));

        assertEquals(-2, getGetBlockPositionMethod().invoke(world, -32));
        assertEquals(-2, getGetBlockPositionMethod().invoke(world, -20));
        assertEquals(-2, getGetBlockPositionMethod().invoke(world, -17));
    }
}