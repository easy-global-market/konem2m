package com.egm.konem2m.model

import com.google.gson.*
import java.lang.reflect.Type
import com.google.gson.JsonElement

data class CreateAcpResponse(val ri: String)

class CreateAcpResponseDeserializer : JsonDeserializer<CreateAcpResponse> {
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CreateAcpResponse {
        val json = jsonElement as JsonObject
        val ri = json.getAsJsonObject("m2m:acp").get("ri").asString
        return CreateAcpResponse(ri)
    }
}
