package com.pocketimps.testbandlab.wavetrimmer.core.wave


class BadWavePointException(line: String, cause: Throwable? = null)
    : RuntimeException("Failed to parse wave point: \"$line\"", cause)
