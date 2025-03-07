package com.xm.examples.coroutine.store

import io.reactivex.rxjava3.core.Scheduler

interface SchedulerProvider {
    fun mainThread(): Scheduler
    fun ioThread(): Scheduler
    fun computationThread(): Scheduler
}