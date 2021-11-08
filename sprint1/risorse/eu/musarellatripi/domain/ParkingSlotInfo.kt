package eu.musarellatripi.domain

class ParkingSlotInfo internal constructor(val slotNum: Int, val reserved: Boolean, val token: String) {

    override fun toString(): String {
        return "[" + slotNum + ", " + reserved + ", " + token + "]"
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