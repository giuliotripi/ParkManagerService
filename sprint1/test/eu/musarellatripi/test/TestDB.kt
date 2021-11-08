package eu.musarellatripi.test

import eu.musarellatripi.domain.ParkingState
import org.junit.Assert
import org.junit.Test


class TestDB {
    @Test
    fun testCreate() {
        val ps = ParkingState()
    }
    @Test
    fun testGetAll() {
        val ps = ParkingState()
        Assert.assertEquals(6, ps.getAll().size)
    }
    @Test
    fun testEdit() {
        val ps = ParkingState()
        Assert.assertEquals(1, ps.get(1).slotNum)
        Assert.assertEquals(false, ps[1].reserved)
        Assert.assertEquals("", ps[1].token)
        ps.update(1, true, "ciao")
        Assert.assertEquals(1, ps.get(1).slotNum)
        Assert.assertEquals(true, ps[1].reserved)
        Assert.assertEquals("ciao", ps[1].token)
    }
}