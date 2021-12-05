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
import eu.musarellatripi.sensors.ApplMessage
import org.junit.After
import org.junit.Before
import eu.musarellatripi.sensors.ApplMessageType
import org.junit.Assert
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.coap.MediaTypeRegistry
import eu.musarellatripi.sensors.Values
import eu.musarellatripi.sensors.CoapTalkerWeightSensor
import eu.musarellatripi.sensors.CoapTalkerSonarSensor

class TestPlan0 {
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
	//c'è anche BeforeAll
	@Before
	fun prepareSystem() {
		
	}
	@After
	fun closeSystm() {
		
	}
	@Test
	fun checkMockWorking() {
		println("STARTING checkMockWorking")
		TestUtils.setWeight(30);
		val nw = CoapTalkerWeightSensor().requestValue()
		Assert.assertEquals(30, nw.toInt())
		TestUtils.setSonar(0);
		val ns = CoapTalkerSonarSensor().requestValue()
		Assert.assertEquals(0, ns.toInt())
	}
	@Test
	fun testClientEnterRequest() {
		println("STARTING testClientEnterRequest")
		TestUtils.setWeight(0);
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		val oldState = parkingState.getAll()
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
		
		var slotNum: Int
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("slotNum", respInfo!![1])
			slotNum = respInfo[3].toInt()
			assert(slotNum >= 1)
			assert(slotNum <= 6)
			Assert.assertEquals(false, oldState.get(slotNum)?.reserved)//before it was available
			Assert.assertEquals(true, parkingState.get(slotNum).reserved)//and now it is not
		}
	}
	@Test
	fun testClientEnterRequestNoSlotsFree() {
		println("STARTING testClientEnterRequestNoSlotsFree")
		TestUtils.setWeight(0);
		
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		for(i in 1..6) {
			parkingState.update(i, true, "ciao")
		}
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
		
		var slotNum: Int
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(respInfo?.count(), 4)
			Assert.assertEquals(respInfo!![1], "slotNum")
			slotNum = respInfo[3].toInt()
			Assert.assertEquals(0, slotNum)
		}
	}
	@Test
	fun testClientEnterRequestIndoorOccupied() {
		println("STARTING testClientEnterRequestIndoorOccupied")
		
		TestUtils.setWeight(Values.weightThreshold + 100);
		
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1])
			Assert.assertEquals("weighttoohigh", respInfo[3])
		}
	}
	@Test
	fun testClientEnterRequestStoppedTrolley() {
		println("STARTING testClientEnterRequestStoppedTrolley")
		
		TestUtils.setWeight(0);
		TestUtils.setTrolley("stopped")
		
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1])
			Assert.assertEquals("trolleystopped", respInfo[3])
		}
		TestUtils.setTrolley("working")
	}
	@Test
	fun testClientCarEnter() {
		println("STARTING testClientCarEnter")
		TestUtils.setWeight(Values.weightThreshold + 100);
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		parkingState.update(1, true, "")
		
		var slotNum = 1
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertNotNull(respInfo) //maybe regex error from message
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("token", respInfo!![1])
			var token = respInfo[3]
			assert(token.length > 0)
			Assert.assertEquals(true, parkingState.get(slotNum).reserved)
			Assert.assertEquals(token, parkingState.get(slotNum).token)
		}
	}
	@Test
	fun testClientCarEnterStoppedTrolley() {
		println("STARTING testClientCarEnterStoppedTrolley")
		TestUtils.setWeight(Values.weightThreshold + 100);
		TestUtils.setTrolley("stopped")
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		parkingState.update(1, true, "")
		
		var slotNum = 1
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertNotNull(respInfo) //maybe regex error from message
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1])
			Assert.assertEquals("trolleystopped", respInfo[3])
		}
		TestUtils.setTrolley("working")
	}
	@Test
	fun testClientCarEnterIndoorNotOccupied() {
		println("STARTING testClientCarEnterIndoorNotOccupied")
		TestUtils.setWeight(0);
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		parkingState.update(1, true, "")
		
		var slotNum = 1
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertNotNull(respInfo) //maybe regex error from message
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1])
			Assert.assertEquals("indoorempty", respInfo[3])
		}
	}
	@Test
	fun testClientCarEnterNoSlotsFree() {
		println("STARTING testClientCarEnterNoSlotsFree")
		
		TestUtils.setWeight(Values.weightThreshold + 100);
		
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		for(i in 1..6) {
			parkingState.update(i, true, "ciao")
		}
		
		val slotNum = 0 //received during enterRequest
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1])
			Assert.assertEquals("valuesnotvalid", respInfo[3])
		}
	}
	@Test
	fun testClientPickup() {
		println("STARTING testClientPickup")
		
		TestUtils.setSonar(Values.sonarThreshold + 100)
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		parkingState.update(5, true, "tokenTest")
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest(tokenTest)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("pickUpReply", respInfo!![1])
			Assert.assertEquals(false, parkingState.get(5).reserved)
			Assert.assertEquals("", parkingState.get(5).token)
		}
	}
	@Test
	fun testClientPickupOutdoorOccupied() {
		println("STARTING testClientPickupOutdoorOccupied")
		
		TestUtils.setSonar(0)
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		parkingState.update(5, true, "tokenTest")
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest(tokenTest)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1]) //expected (1) but was (2)
			Assert.assertEquals("outdoorbusy", respInfo[3])
		}
	}
	@Test
	fun testClientPickupWrongToken() {
		println("STARTING testClientPickupWrongToken")
		
		TestUtils.setSonar(Values.sonarThreshold + 100)
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		parkingState.update(5, true, "tokenTest")
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest(altroToken)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1])
			Assert.assertEquals("wrongtoken", respInfo[3])
		}
	}
	@Test
	fun testClientPickupStoppedTrolley() {
		println("STARTING testClientPickupStoppedTrolley")
		
		TestUtils.setSonar(Values.sonarThreshold + 100)
		TestUtils.setTrolley("stopped")
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		parkingState.update(5, true, "tokenTest")
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest(tokenTest)", "0"))
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("error", respInfo!![1]) //expected (1) but was (2)
			Assert.assertEquals("trolleystopped", respInfo[3])
		}
		TestUtils.setTrolley("working")
	}
	@Test
	fun testClientWorkflow() {
		println("STARTING testClientWorkflow")
		TestUtils.setWeight(0);
		val parkingState: eu.musarellatripi.domain.ParkingState = eu.musarellatripi.domain.ParkingState()
		
		for(i in 1..6) {
			parkingState.update(i, false, "")
		}
		
		val oldState = parkingState.getAll()
		
		coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
		val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
		
		var slotNum: Int
		
		Assert.assertNotNull(resp)
		if(resp != null) {
			val respInfo = coapClient.respToInfo(resp)
			println(respInfo)
			Assert.assertEquals(4, respInfo?.count())
			Assert.assertEquals("slotNum", respInfo!![1])
			slotNum = respInfo[3].toInt()
			assert(slotNum >= 1)
			assert(slotNum <= 6)
			Assert.assertEquals(false, oldState.get(slotNum)?.reserved)//before it was available
			Assert.assertEquals(true, parkingState.get(slotNum).reserved)//and now it is not
			
			//NOW I START WITH carEnter
			
			TestUtils.setWeight(Values.weightThreshold + 100);
			
			coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
			val resp2 = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))
			Assert.assertNotNull(resp2)
			if(resp2 != null) {
				val respInfo2 = coapClient.respToInfo(resp2)
				println(respInfo2)
				Assert.assertNotNull(respInfo2) //maybe regex error from message
				Assert.assertEquals(4, respInfo2?.count())
				Assert.assertEquals("token", respInfo2!![1])
				var token = respInfo2[3]
				assert(token.length > 0)
				Assert.assertEquals(true, parkingState.get(slotNum).reserved)
				Assert.assertEquals(token, parkingState.get(slotNum).token)
				
				//now I start with pickUp
				
				TestUtils.setSonar(Values.sonarThreshold + 100)
				
				coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
				val resp3 = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest(${token})", "0"))
				
				Assert.assertNotNull(resp3)
				if(resp3 != null) {
					val respInfo3 = coapClient.respToInfo(resp3)
					println(respInfo3)
					Assert.assertEquals(4, respInfo3?.count())
					Assert.assertEquals("pickUpReply", respInfo3!![1])
					Assert.assertEquals(false, parkingState.get(slotNum).reserved)
					Assert.assertEquals("", parkingState.get(slotNum).token)
				}
			}
		}
	}
}