package eu.musarellatripi.test

import eu.musarellatripi.sensors.CoapTalker
import eu.musarellatripi.sensors.ApplMessage
import eu.musarellatripi.sensors.ApplMessageType
import eu.musarellatripi.sensors.Values
import org.junit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class TestPresentation {
	
}

fun enterRequest(): Int? {
	val coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
	val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
	
	var slotNum: Int

	if(resp != null) {
		val respInfo = coapClient.respToInfo(resp)
		slotNum = respInfo!![3].toInt()
		return slotNum
	}
	return null
}

fun carEnter(slotNum: Int): String? {
	TestUtils.setWeight(Values.weightThreshold + 100);
	
	val coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
	
	val resp2 = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))

	if(resp2 != null) {
		val respInfo2 = coapClient.respToInfo(resp2)
		var token = respInfo2!![3]
		return token
	}
	return null
}

fun pickUp(token: String): Boolean {
	TestUtils.setSonar(Values.sonarThreshold + 100)

	val coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
	
	val resp3 = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest($token)", "0"))
	
	if(resp3 != null) {
		val respInfo3 = coapClient.respToInfo(resp3)
		println(respInfo3)
		return "pickUpReply" == respInfo3!![1]
	}
	return false
}

fun main() {
	println("STARTING test presentation")
	val sdf = SimpleDateFormat("yyyy-MM-dd HH.mm.ss.S")
	TestUtils.setWeight(0);
	val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
	for(i in 1..6) {
		parkingState.update(i, false, "")
	}
	
	parkingState.update(4, true, "") //set slot 4 as booked
	parkingState.update(6, true, "t6testToken") //set slot 6 as busy
	
	println("Wrote status to file. Now run the service and press ENTER to continue")
	
	readLine()
	
	//runBlocking {
		
		Thread() {
			println("[${sdf.format(Date())}] [CLIENT 1] made enterRequest")
			val slotNum = enterRequest()
			println("[${sdf.format(Date())}] [CLIENT 1] received $slotNum as slotNum")
		}.start()
		Thread.sleep(2000)
		Thread() {
			println("[${sdf.format(Date())}] [CLIENT 2] made carEnter in slot 4")
			val token = carEnter(4);
			println("[${sdf.format(Date())}] [CLIENT 2] received $token as token")
		}.start()
		Thread.sleep(2000)
		Thread() {
			println("[${sdf.format(Date())}] [CLIENT 3] made pickUp on slot 6")
			val ok = pickUp("t6testToken")
			println("[${sdf.format(Date())}] [CLIENT 3] received $ok as response")
		}.start()
	//}
}