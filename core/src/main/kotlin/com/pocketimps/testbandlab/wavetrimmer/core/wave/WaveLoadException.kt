package com.pocketimps.testbandlab.wavetrimmer.core.wave


class WaveLoadException(cause: Throwable)
    : RuntimeException("Failed to load wave data", cause)
