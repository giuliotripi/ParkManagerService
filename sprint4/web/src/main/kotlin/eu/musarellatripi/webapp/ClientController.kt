package eu.musarellatripi.webapp

import eu.musarellatripi.sensors.ApplMessage
import eu.musarellatripi.sensors.ApplMessageType
import eu.musarellatripi.sensors.CoapTalker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
class ClientController {
    @Value("\${spring.application.name}")
    var appName: String? = null
    var logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/")
    fun homePage(model: Model, request: HttpServletRequest): String {
        logger.info("${request.remoteAddr} ${request.method} ${request.requestURI}")
        model.addAttribute("arg", appName)
        return "index"
    }

    @PostMapping("/api/enterRequest")
    @ResponseBody
    fun enterRequest(request: HttpServletRequest): String {
        logger.info("${request.remoteAddr} ${request.method} ${request.requestURI}")
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
    fun carEnter(@RequestParam(name="SLOTNUM") slotNum: Int, request: HttpServletRequest): String {
        logger.info("${request.remoteAddr} ${request.method} ${request.requestURI} slotnum=$slotNum")
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
    fun pickUp(@RequestParam(name="TOKENID") token: String, request: HttpServletRequest): String {
        logger.info("${request.remoteAddr} ${request.method} ${request.requestURI} token=$token")
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