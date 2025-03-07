package com.xm.examples.coroutine.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LOCAL_NAVIGATOR =
    staticCompositionLocalOf<NavController> { error("navController must be provided") }