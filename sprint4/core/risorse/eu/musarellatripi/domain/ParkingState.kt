package eu.musarellatripi.domain

import java.util.HashMap
import kotlinx.serialization.Serializable
import java.io.IOException
import java.io.File

@Serializable data class ParkingStateStore(var status: HashMap<Int, ParkingSlotInfo>)


class ParkingState() : IParkingState {
    companion object {
        //@JvmStatic var status : HashMap<Int, eu.musarellatripi.domain.ParkingSlotInfo> = hashMapOf(1 to eu.musarellatripi.domain.ParkingSlotInfo(1, false, ""));
        @JvmStatic var status = HashMap<Int, ParkingSlotInfo>();
    }

    init {
        if(status.size != 6) { //executed only when not populated
			try {
				val json = File("status.json").readText(Charsets.UTF_8)
				status = com.google.gson.Gson().fromJson(json, ParkingStateStore::class.java).status
				System.out.println("Init DB from saved state");
				for(i in 1..6) {
	                println(status.get(i))
	            }
			} catch(e: IOException) {
	            for(i in 1..6) {
	                status.put(i, ParkingSlotInfo(i, false, ""));
	            }
	            System.out.println("Init DB from 0");
			}
        }
    }
    public override fun getAll(): HashMap<Int, ParkingSlotInfo> {
		//we copy the map and all the objects inside so we cannot edit it outside and compare it later
        val all = HashMap<Int, ParkingSlotInfo>();
        
		for((key, value) in status) {
			all.put(key, ParkingSlotInfo(value.slotNum, value.reserved, value.token))
		}
		
		return all
        //System.out.println("Current DB: " + status);
        //return list;
        //return status;
    }
    public override fun get(slotNum: Int) : ParkingSlotInfo {
        //return eu.musarellatripi.domain.ParkingSlotInfo(1, false, "");
        //System.out.println("Current DB: " + status);
        val info = status.get(slotNum);
        if(info == null)
            throw IllegalStateException("Status has not been initialized");
        return info;
    }
    public override fun update(slotNum: Int, reserved: Boolean, token: String) {
		status.put(slotNum, ParkingSlotInfo(slotNum, reserved, token));
		val Gson = com.google.gson.Gson();
		val currentState = Gson.toJson(ParkingStateStore(status))
		java.io.File("status.json").writeText(currentState)
    }
}

fun main() {
    val ps = ParkingState()
    println("ciao")
    println(ps.getAll())
    ps.update(3, true, "ciao");
    println(ps.getAll())
}