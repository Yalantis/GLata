package com.yalantis.glata.util

import timber.log.Timber

class Logger {

    fun log(message: String) {
        Timber.e(message)
    }

}