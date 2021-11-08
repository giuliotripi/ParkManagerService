package eu.musarellatripi.test

import eu.musarellatripi.sensors.CoapTalker
import eu.musarellatripi.sensors.ApplMessageType
import eu.musarellatripi.sensors.ApplMessage

class CLIClient {
}

fun main() {
	println("Premi enter per una enterRequest")
	readLine()
	TestUtils.setWeight(0)
	val coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
	val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
	val respInfo = coapClient.respToInfo(resp!!)
	val slotNum = respInfo!![3]
	println("Ti è stato assegnato lo slot numero: " + slotNum)
	println("Premi enter per una carEnter")
	readLine()

	TestUtils.setWeight(100)
	
	val resp2 = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))
	val respInfo2 = coapClient.respToInfo(resp2!!)
	var token = respInfo2!![3];
	println("Ti è stato assegnato il seguente token: " + token)
	println("Premi enter per una pickUp")
	readLine()
	print("Inserisci il token: ")
	val token2 = readLine()
	
	TestUtils.setSonar(100)
	
	val resp3 = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest(${token2})", "0"))
	val respInfo3 = coapClient.respToInfo(resp3!!)
	println(respInfo3)
	println("Terminato")
}