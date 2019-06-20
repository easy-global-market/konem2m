package com.egm.konem2m.model

import com.google.gson.*
import java.lang.reflect.Type
import com.google.gson.JsonElement

data class LastCiResponse(val con: String)

class LastCiResponseDeserializer : JsonDeserializer<LastCiResponse> {
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LastCiResponse {
        val json = jsonElement as JsonObject
        val con = json.getAsJsonObject("m2m:cin").get("con").asString
        return LastCiResponse(con)
    }
}
