package com.egm.konem2m.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

data class ListResourceReponse(val uris: List<String>)

class ListResourceResponseDeserializer : JsonDeserializer<ListResourceReponse> {
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ListResourceReponse {
        val json = jsonElement as JsonObject
        val jsonUris = json.get("m2m:uril").asJsonArray
        val uris = jsonUris.map { uri -> uri.asString }

        return ListResourceReponse(uris)
    }
}
