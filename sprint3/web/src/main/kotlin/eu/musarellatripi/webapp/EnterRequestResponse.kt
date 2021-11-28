package eu.musarellatripi.webapp

import kotlinx.serialization.Serializable


@Serializable
data class EnterRequestResponse(val ok: Boolean, val SLOTNUM: Int) {
}

@Serializable
data class ErrorResponse(val ok: Boolean, val errorMessage: String) {
}

@Serializable
data class CarEnterResponse(val ok: Boolean, val TOKENID: String) {
}