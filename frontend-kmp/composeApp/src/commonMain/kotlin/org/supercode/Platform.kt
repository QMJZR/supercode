package org.supercode

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform