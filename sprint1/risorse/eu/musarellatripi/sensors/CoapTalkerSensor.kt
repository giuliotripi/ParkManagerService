package eu.musarellatripi.sensors

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.CoapResponse



abstract class CoapTalkerSensor : CoapTalker {
    
	constructor(url: String) : super(url)
	
	
	abstract fun requestValue(): Double
	
	fun respToIntValue(value: String): Int {
		return respToValue(value)?.toInt() ?: 0
	}
	fun respToDoubleValue(value: String): Double {
		println("prova ${respToValue(value)}")
		return respToValue(value)?.toDouble() ?: 0.0
	}
	//msg(weightState,reply,weight,weight,weightState(30),40)
	private fun respToValue(value: String): String? {
		return Regex("""msg\(\w+,\w+,\w+,\w+,\w+\((\d+),(\w+)\),\d+\)""").find(value)?.groupValues!![1]
	}
	
}