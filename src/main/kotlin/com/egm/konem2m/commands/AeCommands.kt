package com.egm.konem2m.commands

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.lightRed
import com.egm.konem2m.model.*
import com.egm.konem2m.utils.cseUrl
import com.egm.konem2m.utils.generateRI
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

class AeCreateCommands : CliktCommand(name = "ae-create") {
    private val apiName by argument(help = "api name")
    private val aeName by argument(help = "Name of the application entity to create")
    private val acpi by argument(help = "ACP identifier")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val payload = """
            {
                "m2m:ae": {
	                "api": "$apiName",
                    "rr": "true",
                    "rn": "$aeName",
                    "acpi": ["$acpi"]
                }
            }
        """.trimIndent()

        val (request, response, result) = cseUrl
            .httpPost()
            .body(payload)
            .header(mapOf("X-M2M-Origin" to "",
                "Content-Type" to "application/json;ty=2",
                "X-M2M-RI" to "ae-create-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(request.body.asString("application/json"))
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val aeLocation = response.header("Content-Location").first()
                println("AE $aeName successfully created under $aeLocation".green())
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
            }
        }
    }
}

class AeListCommands : CliktCommand(name = "ae-list") {
    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val (request, response, result) = cseUrl
            .httpGet(listOf("ty" to "2", "fu" to "1"))
            .header(mapOf("X-M2M-Origin" to "admin:admin",
                "Content-Type" to "application/json;ty=2",
                "X-M2M-RI" to "ae-list-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(response)
        }

        val gson = GsonBuilder()
            .registerTypeAdapter(ListResourceReponse::class.java, ListResourceResponseDeserializer())
            .create()

        val listAeResponse = gson.fromJson<ListResourceReponse>(String(result.get()), ListResourceReponse::class.java)

        val table = AsciiTable()
        table.addRule()
        table.addRow("AE name").setTextAlignment(TextAlignment.CENTER)
        table.addRule()
        listAeResponse.uris.forEach { uri ->
            table.addRow(uri).setTextAlignment(TextAlignment.CENTER)
            table.addRule()
        }
        println(table.render())
    }
}

class AeShowCommands : CliktCommand(name = "ae-show") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")
    private val aeLocation by argument(help = "Location of the AE to show")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val url = cseUrl + "/" + aeLocation.substringAfterLast("/")
        val (request, response, result) = url
            .httpGet()
            .header(mapOf("X-M2M-Origin" to origin,
                          "X-M2M-RI" to "ae-show-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonResult = gson.fromJson<Any>(String(result.get()), Any::class.java)
                println(jsonResult)
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
            }
        }
    }
}