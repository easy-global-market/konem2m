package com.egm.konem2m.commands

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.lightRed
import com.egm.konem2m.model.LastCiResponse
import com.egm.konem2m.model.LastCiResponseDeserializer
import com.egm.konem2m.utils.cseBase
import com.egm.konem2m.utils.generateRI
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder

class CiCreateCommands : CliktCommand(name = "ci-create") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")
    private val cntName by argument(help = "Name of the container in which to create the content instance")
    private val value by argument(help = "Value to create")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val payload = """
            {
	            "m2m:cin": {
                    "con": "$value"
	            }
	        }
        """.trimIndent()

        val url = config["HOST"].plus(cseBase).plus("/").plus(cntName)
        val (request, response, result) = url
            .httpPost()
            .body(payload)
            .header(mapOf("X-M2M-Origin" to origin,
                "Content-Type" to "application/json;ty=4",
                "X-M2M-RI" to "ci-create-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(request.body.asString("application/json"))
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val ciLocation = response.header("Content-Location").first()
                println("CI $ciLocation successfully created under $cntName".green())
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
            }
        }
    }
}


class CiLastCommands : CliktCommand(name = "ci-last") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")
    private val ciLocation by argument(help = "Location of the CI to show the last value")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val url = config["HOST"].plus(cseBase) + "/" + ciLocation.substringAfter("/") + "/latest"
        val (request, response, result) = url
            .httpGet()
            .header(mapOf("X-M2M-Origin" to origin,
                "X-M2M-RI" to "ci-last-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
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
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
            }
        }
    }
}
