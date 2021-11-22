package eu.musarellatripi.webapp

import eu.musarellatripi.sensors.ApplMessage
import eu.musarellatripi.sensors.ApplMessageType
import eu.musarellatripi.sensors.CoapTalker
import eu.musarellatripi.sensors.Values
import eu.musarellatripi.test.TestUtils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class ManagerController {
    @GetMapping("/manager")
    fun homePage(model: Model): String {
        return "manager"
    }

    @GetMapping("/api/manager/status")
    @ResponseBody
    fun status(): String {
        val coapClient = CoapTalker("coap://localhost:8050/ctxservice/managerservice")
        val resp = coapClient.request(ApplMessage("readStatus", ApplMessageType.request.toString(), "external", "managerservice", "readStatus(X)", "0"))
        if(resp != null) {
            return CoapTalker.respToJson(resp)!![3]
        } else {
            return "{\"ok\": false}"
        }
    }
    @PostMapping("/api/manager/setTrolley/{state}")
    @ResponseBody
    fun setTrolleyState(@PathVariable(value="state") state: String): String {
        TestUtils.setTemperature(Values.TMAX+10)
        val coapClient = CoapTalker("coap://localhost:8050/ctxservice/managerservice")
        val reqType  = if(state == "stopped") "stopTrolley" else "resumeTrolley"
        val resp = coapClient.request(ApplMessage(reqType, ApplMessageType.request.toString(), "external", "managerservice", "$reqType(_)", "0"))
        if(resp != null) {
            val respInfo = coapClient.respToInfo(resp)
            if(respInfo != null && respInfo.count() == 4) {
                if(respInfo[1] == "ok") {
                    return "{\"ok\": true}"
                } else if(respInfo[1] == "error") {
                    return Json.encodeToString(ErrorResponse(false, respInfo[3]))
                }
            }
        }
        return "{\"ok\": false}"
    }
}