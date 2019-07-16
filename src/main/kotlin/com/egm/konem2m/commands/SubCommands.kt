package com.egm.konem2m.commands

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.lightRed
import com.egm.konem2m.utils.generateRI
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

class SubCreateCommands : CliktCommand(name = "sub-create") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")
    private val cntName by argument(help = "Name of the container to be notified about")
    private val subName by argument(help = "Name of the subscription")
    private val subUrl by argument(help = "URL to send subscription updates to")
    private val subType by argument(help = "Subscription type")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val payload = """
            {
                "m2m:sub": {
		            "rn": "$subName",
		            "enc": {
			            "net": ["$subType"]
		            },
		            "nu": ["$subUrl"],
		            "nct": "1"
		        }
	        }""".trimIndent()

        val url = config["HOST"].plus(config["CSEBASE"]).plus("/").plus(cntName)
        val (request, response, result) = url
            .httpPost()
            .body(payload)
            .header(mapOf("X-M2M-Origin" to origin,
                "Content-Type" to "application/json;ty=23",
                "X-M2M-RI" to "sub-create-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(request.body.asString("application/json"))
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val subLocation = response.header("Content-Location").first()
                println("Subscription $subName successfully created under ${subLocation.green()}")
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
            }
        }
    }

}