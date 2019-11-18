package com.egm.konem2m.service

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.lightRed
import com.egm.konem2m.model.LastCiResponse
import com.egm.konem2m.model.LastCiResponseDeserializer
import com.egm.konem2m.model.ListResourceReponse
import com.egm.konem2m.model.ListResourceResponseDeserializer
import com.egm.konem2m.utils.generateRI
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder

object OneM2MHttpService {

    fun listCnt(host: String, cseBase: String, verbose: String = "") : Result<ListResourceReponse, Exception> {
        val (request, response, result) = host.plus(cseBase)
            .httpGet(listOf("ty" to "3", "fu" to "1"))
            .header(mapOf("X-M2M-Origin" to "admin:admin",
                "Content-Type" to "application/json;ty=3",
                "X-M2M-RI" to "cnt-list-${generateRI()}"))
            .response()

        if (verbose == "on") {
            println(request)
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val gson = GsonBuilder()
                    .registerTypeAdapter(ListResourceReponse::class.java, ListResourceResponseDeserializer())
                    .create()

                return Result.Companion.of(
                    gson.fromJson<ListResourceReponse>(
                        String(result.get()),
                        ListResourceReponse::class.java
                    )
                )
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
                return Result.error(
                    Exception(result.error.localizedMessage + " - " + response.body().asString("application/json"))
                )
            }
        }
    }

    fun lastCi(host: String, cseBase: String, origin: String, ciLocation: String, verbose: String = "") : Result<LastCiResponse, Exception> {
        val url = host.plus(cseBase) + "/" + ciLocation + "/latest"
        val (request, response, result) = url
            .httpGet()
            .header(mapOf("X-M2M-Origin" to origin,
                "X-M2M-RI" to "ci-last-${generateRI()}"))
            .response()

        if (verbose == "on") {
            println(request)
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val gson = GsonBuilder()
                    .registerTypeAdapter(LastCiResponse::class.java, LastCiResponseDeserializer())
                    .create()
                val lastCiResponse = gson.fromJson<LastCiResponse>(String(result.get()), LastCiResponse::class.java)
                println("Latest value for $ciLocation is : " + lastCiResponse.con.green())

                return Result.success(gson.fromJson<LastCiResponse>(String(result.get()), LastCiResponse::class.java))
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())

                return Result.error(Exception(result.error.localizedMessage + " - " + response.body().asString("application/json")))
            }
        }
    }
}