package eu.musarellatripi

import kotlinx.serialization.Serializable
import com.fasterxml.jackson.annotation.JsonValue

enum class TrolleyState(@JsonValue val state: String) {
    IDLE("idle"),
	WORKING("working"),
	STOPPED("stopped")
}

enum class FanState(@JsonValue val state: String) {
    ON("on"),
	OFF("off"),
}

@Serializable
data class SensorMeasure(var measure: Float, var time: Int, val name: String) {
	
}

@Serializable
data class ParkingDevicesStatus(val sensors: HashMap<String, SensorMeasure> = HashMap<String, eu.musarellatripi.SensorMeasure>(),
								var fanState: FanState = FanState.OFF,
								var trolleyState: TrolleyState = TrolleyState.IDLE,
								var outdoorAlarm: Boolean = false) {
}