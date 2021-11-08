package eu.musarellatripi.test

import org.eclipse.californium.core.CoapClient
import eu.musarellatripi.sensors.ApplMessage
import eu.musarellatripi.sensors.ApplMessageType
import org.eclipse.californium.core.coap.MediaTypeRegistry

class TestUtils {
	companion object {
		
		fun setWeight(weight: Int) {
			val client: CoapClient = CoapClient("coap://localhost:8050/ctxservice/weight")
			val msg = ApplMessage("setValue", ApplMessageType.dispatch.toString(), "external", "weight", "sensorValue(${weight})", "0")
			val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
			println(respPut.getResponseText())
			println(respPut.getCode())
		}
		fun setSonar(value: Int) {
			val client: CoapClient = CoapClient("coap://localhost:8050/ctxservice/sonar")
			val msg = ApplMessage("setValue", ApplMessageType.dispatch.toString(), "external", "sonar", "sensorValue(${value})", "0")
			val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
			println(respPut.getResponseText())
			println(respPut.getCode())
		}
	}
}