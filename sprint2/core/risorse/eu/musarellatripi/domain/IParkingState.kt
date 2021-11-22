package eu.musarellatripi.domain
interface IParkingState {
    fun getAll(): HashMap<Int, ParkingSlotInfo>
    operator fun get(slotNum: Int): ParkingSlotInfo
    fun update(slotNum: Int, reserved: Boolean, token: String)
}