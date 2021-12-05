package eu.musarellatripi.sensors

import org.eclipse.californium.core.CoapClient

class CoapTalkerSonarSensor: CoapTalkerSensor {
	constructor() : super("coap://${eu.musarellatripi.test.TestUtils.url}/sonar")
	

	override fun requestValue(): Double {
		val resp = request(ApplMessage("getValue", ApplMessageType.request.toString(), "external", "sonar", "getValue(X)", "100"));
		println("resp: $resp")
		if (resp != null)
			return respToDoubleValue(resp)
		else
			return 0.0
	}
}