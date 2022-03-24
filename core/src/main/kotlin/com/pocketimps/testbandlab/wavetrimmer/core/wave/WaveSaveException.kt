package com.pocketimps.testbandlab.wavetrimmer.core.wave


class WaveSaveException(cause: Throwable)
    : RuntimeException("Failed to save wave data", cause)
