package com.egm.konem2m.commands

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.red
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import com.google.gson.GsonBuilder
import com.egm.konem2m.model.*
import com.egm.konem2m.utils.cseUrl
import com.egm.konem2m.utils.generateRI
import com.github.ajalt.clikt.core.requireObject
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

class AcpCreateCommands : CliktCommand(name = "acp-create") {

    private val acpName by argument(help = "name of the access control policy")
    private val acor by argument(help = "request originator to authorize (by convention, it starts with a `C`)")
    private val acop: Int by argument(help = "allowed operations indicator").int()

    private val config by requireObject<Map<String, String>>()

    override fun run() {
        val payload = """
            { "m2m:acp": {
                "rn": "$acpName",
                "pv": {
                    "acr": [{
                        "acor": ["$acor"],
                        "acop": "$acop"
                    }]
                },
                "pvs": {
                    "acr": [{
                        "acor": ["$acor"],
                        "acop": "$acop"
                    }]
                }
            }}
        """.trimIndent()

        val (request, response, result) = cseUrl
            .httpPost()
            .body(payload)
            .header(mapOf("X-M2M-Origin" to "admin:admin",
                          "Content-Type" to "application/json;ty=1",
                          "X-M2M-RI" to "create-acp-${generateRI()}"))
            .response()

        if (config["VERBOSE"] == "on") {
            println(request)
            println(response)
        }

        when (result) {
            is Result.Success -> {
                val gson = GsonBuilder()
                    .registerTypeAdapter(CreateAcpResponse::class.java, CreateAcpResponseDeserializer())
                    .create()
                val createAcpResponse = gson.fromJson<CreateAcpResponse>(String(result.get()), CreateAcpResponse::class.java)
                println("ACP $acpName successfully created".green())
                println("")
                println("Here is your generated RI : " + createAcpResponse.ri.red())
            }
            is Result.Failure -> {
                println(result.error.localizedMessage + " - " + response.body().asString("application/json").red())
            }
        }
    }
}
