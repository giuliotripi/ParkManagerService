package eu.musarellatripi.sensors

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapObserveRelation
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.eclipse.californium.core.coap.MediaTypeRegistry

object ch : CoapHandler {
    override fun onLoad(response: CoapResponse) {
        println("CoapHandler | GET RESP-CODE= " + response.code + " content:" + response.responseText)
    }
    override fun onError() {
        println("CoapHandler | FAILED")
    }
} 

class ResponseObserver(val name : String, val channel : Channel<String>,
					val expected:String?=null): CoapHandler {
	override fun onLoad(response: CoapResponse) {
		val content = response.responseText
		println("responseHandler $name | received: $content")
		if( content.contains("START") || content.contains("created")) return
		if( expected == null || content.contains(expected) )
			runBlocking{ channel.send(content)
				println("sent info")}
	}
	override fun onError() {
		println("responseHandler | ERROR")
	}
}

open class CoapTalker(url: String) {
	private val client: CoapClient = CoapClient(url)
	private lateinit var relation: CoapObserveRelation
	
	init {
		println("connQakCoap | createConnection hostIP=${url}")
		//val url = "coap://$robothostAddr:$robotPort/${ctxqakdest}/${qakdestination}"
		client.setTimeout( 20000L )
	    val respGet  = client.get( ) //CoapResponse
		if( respGet != null )
			println("connQakCoap | createConnection doing  get | CODE=  ${respGet.code}")
		else
			println("connQakCoap | url=  ${url} FAILURE")
		
		//observeResource(ch);
	}
	
	fun request( msg: ApplMessage ): String? {
 		val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
		if( respPut != null )
			println("connQakCoap | answer= ${respPut.getResponseText()}")
		else
			println("connQakCoap | answer is null")
		return respPut?.getResponseText()
	}
	
	fun respToInfo(value: String): List<String>? {
		//works only if prolog response contains \w+, no multiple values
		return Regex("""msg\((\w+),\w+,\w+,\w+,(\w+)\((\w+)\),\d+\)""").find(value)?.groupValues
	}
	
	fun removeObserve() {
        relation.proactiveCancel()
    }

    fun observeResource(handler: CoapHandler) {
        relation = client.observe(handler)
    }
	
	fun shutdown() {
		client.shutdown()
	}
}