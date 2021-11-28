package eu.musarellatripi.webapp

import eu.musarellatripi.sensors.ApplMessage
import eu.musarellatripi.sensors.ApplMessageType
import eu.musarellatripi.sensors.CoapTalker
import eu.musarellatripi.sensors.Values
import eu.musarellatripi.test.TestUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.springframework.web.bind.annotation.*

@Controller
class ClientController {
    @Value("\${spring.application.name}")
    var appName: String? = null
    @GetMapping("/")
    fun homePage(model: Model): String {
        println("------------------- ClientController homePage $model")
        model.addAttribute("arg", appName)
        return "index"
    }

    @PostMapping("/api/enterRequest")
    @ResponseBody
    fun enterRequest(): String {
        TestUtils.setWeight(0)
        val coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
        val resp = coapClient.request(ApplMessage("enterRequest", ApplMessageType.request.toString(), "external", "parkingservice", "enterRequest(X)", "0"))
        if(resp != null) {
            val respInfo = coapClient.respToInfo(resp)
            if(respInfo != null && respInfo.count() == 4) {
                if(respInfo[1] == "slotNum") {
                    return Json.encodeToString(EnterRequestResponse(true, respInfo[3].toInt()))
                } else if(respInfo[1] == "error") {
                    return Json.encodeToString(ErrorResponse(false, respInfo[3]))
                }
            }
        }
        return Json.encodeToString(ErrorResponse(false, "connectionError"))
    }

    @PostMapping("/api/carEnter")
    @ResponseBody
    fun carEnter(@RequestParam(name="SLOTNUM") slotNum: Int): String {
        TestUtils.setWeight(Values.weightThreshold + 100)
        val coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
        val resp = coapClient.request(ApplMessage("carEnter", ApplMessageType.request.toString(), "external", "parkingservice", "carEnter(${slotNum})", "0"))
        if(resp != null) {
            val respInfo = coapClient.respToInfo(resp)
            if(respInfo != null && respInfo.count() == 4) {
                if(respInfo[1] == "token") {
                    val token = respInfo[3]
                    return Json.encodeToString(CarEnterResponse(true, token))
                } else if(respInfo[1] == "error") {
                    return Json.encodeToString(ErrorResponse(false, respInfo[3]))
                }
            }
        }
        return Json.encodeToString(ErrorResponse(false, "connectionError"))
    }

    @PostMapping("/api/pickUp")
    @ResponseBody
    fun pickUp(@RequestParam(name="TOKENID") token: String): String {
        TestUtils.setSonar(Values.sonarThreshold + 100)
        val coapClient = CoapTalker("coap://localhost:8050/ctxservice/parkingservice")
        val resp = coapClient.request(ApplMessage("pickUpRequest", ApplMessageType.request.toString(), "external", "parkingservice", "pickUpRequest($token)", "0"))
        if(resp != null) {
            val respInfo = coapClient.respToInfo(resp)
            if(respInfo != null && respInfo.count() == 4) {
                if(respInfo[1] == "pickUpReply") {
                    return "{\"ok\": true}"
                } else if(respInfo[1] == "error") {
                    return Json.encodeToString(ErrorResponse(false, respInfo[3]))
                }
            }
        }
        return Json.encodeToString(ErrorResponse(false, "connectionError"))
    }

    @ExceptionHandler
    fun handle(ex: Exception): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        return ResponseEntity(
            "BaseController ERROR ${ex.message}",
            responseHeaders, HttpStatus.CREATED
        )
    }
}