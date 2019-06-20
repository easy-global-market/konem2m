package com.egm.konem2m.utils

import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

const val cseBase = "/Mobius"

private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun generateRI(): String =
    ThreadLocalRandom.current()
        .ints(12.toLong(), 0, charPool.size)
        .asSequence()
        .map(charPool::get)
        .joinToString("")