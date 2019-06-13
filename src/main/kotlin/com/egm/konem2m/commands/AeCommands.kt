package com.egm.konem2m.commands

import com.egm.konem2m.model.*
import com.egm.konem2m.utils.cseUrl
import com.egm.konem2m.utils.generateRI
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.kittinunf.fuel.httpGet
import com.google.gson.GsonBuilder
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

class AeListCommands : CliktCommand(name = "ae-list") {
    override fun run() {
        val (request, response, result) = cseUrl
            .httpGet(listOf("ty" to "2", "fu" to "1"))
            .header(mapOf("X-M2M-Origin" to "admin:admin",
                "Content-Type" to "application/json;ty=2",
                "X-M2M-RI" to "ae-list-${generateRI()}"))
            .response()

        println(request)
        println(response)

        val gson = GsonBuilder()
            .registerTypeAdapter(ListAeReponse::class.java, ListAeResponseDeserializer())
            .create()

        val listAeResponse = gson.fromJson<ListAeReponse>(String(result.get()), ListAeReponse::class.java)

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

class AeCreateCommands : CliktCommand(name = "ae-create") {
    val aeName by argument(help = "name of the application entity")

    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}