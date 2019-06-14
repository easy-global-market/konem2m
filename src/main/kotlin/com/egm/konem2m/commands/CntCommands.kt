package com.egm.konem2m.commands

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.lightRed
import com.egm.konem2m.model.ListResourceReponse
import com.egm.konem2m.model.ListResourceResponseDeserializer
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

class CntCreateCommands : CliktCommand(name = "cnt-create") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")
    private val aeName by argument(help = "Name of the AE this container will belong to")
    private val cntName by argument(help = "Name of the container to create")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val payload = """
            {
	            "m2m:cnt": {
                    "rn": "$cntName"
	            }
	        }
        """.trimIndent()

        val url = "$cseUrl/$aeName"
        val (request, response, result) = url
            .httpPost()
            .body(payload)
            .header(mapOf("X-M2M-Origin" to origin,
                "Content-Type" to "application/json;ty=3",
                "X-M2M-RI" to "cnt-create-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(request.body.asString("application/json"))
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val cntLocation = response.header("Content-Location").first()
                println("AE $cntName successfully created under $cntLocation".green())
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
            }
        }
    }
}

class CntListCommands : CliktCommand(name = "cnt-list") {
    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val (request, response, result) = cseUrl
            .httpGet(listOf("ty" to "3", "fu" to "1"))
            .header(mapOf("X-M2M-Origin" to "admin:admin",
                "Content-Type" to "application/json;ty=3",
                "X-M2M-RI" to "cnt-list-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val gson = GsonBuilder()
                    .registerTypeAdapter(ListResourceReponse::class.java, ListResourceResponseDeserializer())
                    .create()

                val listCntResponse = gson.fromJson<ListResourceReponse>(String(result.get()), ListResourceReponse::class.java)

                val table = AsciiTable()
                table.addRule()
                table.addRow("CNT name").setTextAlignment(TextAlignment.CENTER)
                table.addRule()
                listCntResponse.uris.forEach { uri ->
                    table.addRow(uri).setTextAlignment(TextAlignment.CENTER)
                    table.addRule()
                }
                println(table.render())
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").lightRed())
            }
        }
    }
}

class CntShowCommands : CliktCommand(name = "cnt-show") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")
    private val cntLocation by argument(help = "Location of the CNT to show")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val url = cseUrl + "/" + cntLocation.substringAfter("/")
        val (request, response, result) = url
            .httpGet()
            .header(mapOf("X-M2M-Origin" to origin,
                "X-M2M-RI" to "cnt-show-${generateRI()}"))
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
