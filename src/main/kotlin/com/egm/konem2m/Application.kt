package com.egm.konem2m

import com.github.ajalt.clikt.core.subcommands
import com.egm.konem2m.commands.*

fun main(args: Array<String>) {
   KOneM2M().subcommands(AcpCreateCommands(),
         AeCreateCommands(), AeListCommands(), AeShowCommands())
      .main(args)
}
