package com.xm.tka.coroutine

import kotlinx.coroutines.CoroutineScope

fun interface StoreScopeProvider {
    fun getStoreScope(): CoroutineScope

}