package com.otawilma.mobileclient

class InvalidTokenNetworkException(override val message: String?) : Exception()
class OtaWilmaDownException(override val message: String) : Exception()
class WilmaDownException(override val message: String?) : Exception()
class NoStoredTokenException(override val message: String?) : Exception()