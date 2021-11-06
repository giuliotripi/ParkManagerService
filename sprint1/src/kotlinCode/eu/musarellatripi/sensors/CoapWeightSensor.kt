package eu.musarellatripi.sensors

import org.eclipse.californium.core.CoapClient

class CoapWeightSensor: CoapSensor {
	constructor() : super("coap://localhost:8050/ctxservice/weight")
	

	override fun requestValue(): Double {
		val resp = request(ApplMessage("getValue", ApplMessageType.request.toString(), "external", "weight", "getValue(X)", "100"));
		println("resp: $resp")
		if (resp != null)
			return respToDoubleValue(resp)
		else
			return 0.0
	}
}