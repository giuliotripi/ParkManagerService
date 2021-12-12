package eu.musarellatripi.webapp

import com.google.gson.Gson
import eu.musarellatripi.ParkingDevicesStatus
import eu.musarellatripi.TrolleyState
import eu.musarellatripi.sensors.CoapTalker
import eu.musarellatripi.sensors.Values
import eu.musarellatripi.test.TestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebappApplicationTests(@Autowired val restTemplate: TestRestTemplate) {

	@Test
	fun homePageLoads() {
		val entity = restTemplate.getForEntity<String>("/")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(entity.body).contains("CARENTER") //can check HTML code
	}
	@Test
	fun clientRequests() {
		val entity = restTemplate.postForEntity<String>("/api/enterRequest")
		//assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		TestUtils.setWeight(0)
		assertThat(entity.body).contains("SLOTNUM") //can check HTML code
		val SLOTNUM = Gson().fromJson(entity.body, EnterRequestResponse::class.java).SLOTNUM;
		println(entity.body)
		println("SLOTNUM: $SLOTNUM")
		val map: MultiValueMap<String, String> = LinkedMultiValueMap()
		map.add("SLOTNUM", SLOTNUM.toString())
		val request = HttpEntity(map, HttpHeaders())
		TestUtils.setWeight(Values.weightThreshold+10)
		val entity2 = restTemplate.postForEntity<String>("/api/carEnter", request)
		//assertThat(entity2.statusCode).isEqualTo(HttpStatus.OK)
		//assertThat(entity2.body).contains("TOKENID")
		println(entity2.body)
		val TOKENID = Gson().fromJson(entity2.body, CarEnterResponse::class.java).TOKENID;
		println("TOKENID: $TOKENID")

		val map2: MultiValueMap<String, String> = LinkedMultiValueMap()
		map2.add("TOKENID", TOKENID)
		val request2 = HttpEntity(map2, HttpHeaders())
		TestUtils.setSonar(Values.sonarThreshold + 10)
		val entity3 = restTemplate.postForEntity<String>("/api/pickUp", request2)
		//assertThat(entity3.statusCode).isEqualTo(HttpStatus.OK)
		println(entity3.body)
		assertThat(entity3.body).contains("true")
	}
	@Test
	fun trolleySet() {
		val entity1 = restTemplate.postForEntity<String>("/api/manager/setTrolley/working")
		assertThat(entity1.body).contains("true")

		val state1JSON = restTemplate.getForEntity<String>("/api/manager/status")
		val state1 = Gson().fromJson(state1JSON.body!!, ParkingDevicesStatus::class.java).trolleyState;

		assertThat(state1).isNotEqualTo(TrolleyState.valueOf("STOPPED"))

		TestUtils.setTemperature(Values.TMAX+10)
		val entity2 = restTemplate.postForEntity<String>("/api/manager/setTrolley/stopped")

		assertThat(entity2.body).contains("true")

		val state2JSON = restTemplate.getForEntity<String>("/api/manager/status")
		val state2 = Gson().fromJson(state2JSON.body!!, ParkingDevicesStatus::class.java).trolleyState;

		assertThat(state2).isEqualTo(TrolleyState.valueOf("STOPPED"))

		restTemplate.postForEntity<String>("/api/manager/setTrolley/working") //to let other tests run
	}
}
