package com.egm.konem2m.commands

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.lightRed
import com.egm.konem2m.model.LastCiResponse
import com.egm.konem2m.model.LastCiResponseDeserializer
import com.egm.konem2m.service.OneM2MHttpService
import com.egm.konem2m.utils.generateRI
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.long
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import com.google.gson.GsonBuilder
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import java.util.concurrent.ThreadLocalRandom

class CiCreateCommands : CliktCommand(name = "ci-create") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")
    private val cntName by argument(help = "Name of the container in which to create the content instance")
    private val value by argument(help = "Value to create").double()
    private val type by argument(help = "Type of the measure (eg CO2)")
    private val unit by argument(help = "Unit of the value (eg ppm)")
    private val repeatInterval: Long by option(help = "Time to wait before sending a new measure").long().default(-1)
    private val minValue: Double by option(help = "Minimal value for a new measure").double().default(0.0)
    private val maxValue: Double by option(help = "Maximal value for a new measure").double().default(100.0)

    private val config by requireObject<Map<String, String>>()

    override fun run() {

        if (repeatInterval == -1L)
            sendNewCi(listOf(value, type, unit).joinToString(separator = ";"))
        else {
            while (true) {
                val newValue = ThreadLocalRandom.current().nextDouble(minValue, maxValue)
                sendNewCi(listOf(newValue, type, unit).joinToString(separator = ";"))
                Thread.sleep(repeatInterval)
            }
        }
    }

    private fun sendNewCi(value: String) {
        val payload = """
            {
	            "m2m:cin": {
                    "con": "$value"
	            }
	        }
        """.trimIndent()

        val url = config["HOST"].plus(config["CSEBASE"]).plus("/").plus(cntName)
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
                println("CI $ciLocation with value $value created under $cntName".green())
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
        val url = config["HOST"].plus(config["CSEBASE"]) + "/" + ciLocation.substringAfter("/") + "/latest"
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

class CiLastListCommands : CliktCommand(name = "ci-last-list") {
    private val origin by argument(help = "Originator of the request (prefixed with 'C' for CSEs or 'S' for AEs)")

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val listCntResult = OneM2MHttpService.listCnt(config["HOST"]!!, config["CSEBASE"]!!, "on")
        listCntResult.fold({ response ->
            val allLastCis = response.uris.map { cnt ->
                OneM2MHttpService.lastCi(config["HOST"]!!, config["CSEBASE"]!!, origin, cnt.substringAfter("/"), "on")
                    .map { Triple(cnt, it.con, it.ct) }
                    .component1()
            }.toList()
            .filterNotNull()
            .sortedByDescending { it.third }

            val table = AsciiTable()
            table.addRule()
            table.addRow("CNT name", "Last value", "Creation time").setTextAlignment(TextAlignment.CENTER)
            table.addRule()
            allLastCis.forEach { lastCiResponse ->
                table.addRow(lastCiResponse.first, lastCiResponse.second, lastCiResponse.third)
                    .setTextAlignment(TextAlignment.CENTER)
                table.addRule()
            }
            println(table.render(170))
        },{
            println(it.message?.lightRed())
        })
    }
}
