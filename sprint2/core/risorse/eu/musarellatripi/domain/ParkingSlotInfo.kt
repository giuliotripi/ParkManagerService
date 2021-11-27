package eu.musarellatripi.domain

import kotlinx.serialization.Serializable

@Serializable
data class ParkingSlotInfo(val slotNum: Int, val reserved: Boolean, val token: String) {

    override fun toString(): String {
        return "[" + slotNum + ", " + reserved + ", \"" + token + "\"]"
    }

    /*fun getSlotNum() : Int {
        return slotNum
    }
    fun getReserved(): Boolean {
        return reserved
    }
    fun getToken(): String {
        return token
    }*/
}