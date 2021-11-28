package eu.musarellatripi.test

import it.unibo.kactor.ActorBasic
import kotlinx.coroutines.channels.Channel
import eu.musarellatripi.sensors.CoapTalker
import org.junit.BeforeClass
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import it.unibo.kactor.QakContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Test
import org.junit.Assert
import eu.musarellatripi.ParkingDevicesStatus
import com.google.gson.Gson
import eu.musarellatripi.sensors.Values
import eu.musarellatripi.sensors.ApplMessage
import eu.musarellatripi.sensors.ApplMessageType
import eu.musarellatripi.TrolleyState
import kotlinx.coroutines.delay
import eu.musarellatripi.FanState
import org.hamcrest.CoreMatchers.*;

class TestPlan2 {
	companion object{
		var myactor : ActorBasic? = null
		val channelSyncStart = Channel<String>()
		lateinit var coapClient: CoapTalker
		
		init {
			
		}
		
		@BeforeClass
		@JvmStatic
		fun setup() {
			println("doing setup")
			GlobalScope.launch{
				it.unibo.ctxservice.main() //keep the control
				
			}
			GlobalScope.launch/*(newSingleThreadContext("MyOwnThread"))*/ {
				//delay(500)
				//it.unibo.ctxsensor.main()
			}
			GlobalScope.launch{
				myactor = QakContext.getActor("parkingservice")
				while(  myactor == null ) {
					println("+++++++++ waiting for system startup ...")
					delay(500)
					myactor = QakContext.getActor("parkingservice")
				}
				delay(1000)
				channelSyncStart.send("starttesting")
			}
			runBlocking{
				println("+++++++++ checkSystemStarted waiting ")
				channelSyncStart.receive()
			    println("+++++++++ checkSystemStarted resumed ")
			}
			coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
			
		}
		
		
		@AfterClass
		@JvmStatic
		fun teardown() {
			println("All tests done")
		}
	}
	fun getStateRequest(): ParkingDevicesStatus {
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/managerservice")
		val resp = coapClient.request(ApplMessage("readStatus", ApplMessageType.request.toString(), "external", "managerservice", "readStatus(X)", "0"))
		var respInfo = CoapTalker.respToJson(resp!!)
		println(resp)
		val state = Gson().fromJson<ParkingDevicesStatus>(respInfo!![3], ParkingDevicesStatus::class.java);
		return state
	}
	@Test
	fun testGetState() {
		getStateRequest()
	}
	@Test
	fun testTemperatureChangeFanOn() {
		TestUtils.setTemperature(Values.TMAX+10)
		val state = getStateRequest()
		Assert.assertEquals(FanState.valueOf("ON"), state.fanState)
	}
	@Test
	fun testTemperatureChangeFanOff() {
		TestUtils.setTemperature(Values.TMAX-10)
		val state = getStateRequest()
		Assert.assertEquals(FanState.valueOf("OFF"), state.fanState)
	}
	@Test
	fun testStopTrolley() {
		TestUtils.setTemperature(Values.TMAX+10)
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/managerservice")
		val resp = coapClient.request(ApplMessage("stopTrolley", ApplMessageType.request.toString(), "external", "managerservice", "stopTrolley(X)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(respInfo?.count(), 4)
			Assert.assertEquals(respInfo!![1], "ok")
		}
		val state = getStateRequest()
		Assert.assertEquals(TrolleyState.valueOf("STOPPED"), state.trolleyState)
	}
	@Test
	fun testStopTrolleyTemperatureError() {
		testResumeTrolley()
		TestUtils.setTemperature(Values.TMAX-10)
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/managerservice")
		val resp = coapClient.request(ApplMessage("stopTrolley", ApplMessageType.request.toString(), "external", "managerservice", "stopTrolley(X)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(respInfo?.count(), 4)
			Assert.assertEquals(respInfo!![1], "error")
		}
		val state = getStateRequest()
		Assert.assertFalse(state.trolleyState.equals(TrolleyState.valueOf("STOPPED")))
	}
	@Test
	fun testResumeTrolley() {
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/managerservice")
		val resp = coapClient.request(ApplMessage("resumeTrolley", ApplMessageType.request.toString(), "external", "managerservice", "resumeTrolley(X)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(respInfo?.count(), 4)
			Assert.assertEquals(respInfo!![1], "ok")
		}
		val state = getStateRequest()
		Assert.assertFalse(state.trolleyState.equals(TrolleyState.valueOf("STOPPED")))
	}
}