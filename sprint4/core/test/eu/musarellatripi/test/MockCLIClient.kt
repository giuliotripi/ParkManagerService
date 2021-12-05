package eu.musarellatripi.test

import eu.musarellatripi.sensors.CoapTalkerSensor
import eu.musarellatripi.sensors.CoapTalkerSonarSensor
import eu.musarellatripi.sensors.CoapTalkerWeightSensor
import eu.musarellatripi.sensors.CoapTalkerTemperatureSensor
import eu.musarellatripi.sensors.Values

class MockCLIClient {
	
}

fun main() {
	val banner = "Insert: \nl to list the updated sensor readings\ne to edit a sensor value\nq to quit";
	println(banner)
	var line = readLine()
	while(line != null && line != "q") {
		
		if(line == "l") {
			
			val sonar = CoapTalkerSonarSensor().requestValue()
			val weight = CoapTalkerWeightSensor().requestValue()
			val temperature = CoapTalkerTemperatureSensor().requestValue()
			
			println("\nCurrent readings are:\nweight: $weight (threshold: ${Values.weightThreshold})\nsonar: $sonar (threshold: ${Values.sonarThreshold})\ntemperature: $temperature (threshold: ${Values.TMAX})\n")
			
		} else if(line == "e") {
			print("Insert: \nw for weight sensor\ns for sonar sensor\nt for temperature sensor: ")
			val sensor = readLine()
			print("Insert the new value: ")
			val newValue = readLine()!!.toInt()
			
			if(sensor == "w")
				TestUtils.setWeight(newValue)
			else if(sensor == "t")
				TestUtils.setTemperature(newValue)
			else if(sensor == "s")
				TestUtils.setSonar(newValue)
			else
				println("Invalid sensor name")
			
		} else {
			println("Invalid option")
		}
		
		println(banner)
		line = readLine();
	}
}