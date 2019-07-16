package com.egm.konem2m.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class KOneM2M : CliktCommand() {
    private val verbose by option("--verbose", "-v", help = "display full traces of request and response").flag(default = false)
    private val config by findObject { mutableMapOf<String, String>() }
    private val host by option(envvar = "MOBIUS_HOST").default("http://127.0.0.1:7579")
    private val cseBase by option(envvar = "MOBIUS_CSEBASE").default("/Mobius")

    override fun run() {
        config["VERBOSE"] = if (verbose) "on" else "off"
        config["HOST"] = host
        config["CSEBASE"] = cseBase
    }
}