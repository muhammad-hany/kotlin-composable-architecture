package com.xm.examples.coroutine.store

import com.xm.examples.coroutine.data.repository.Repository

data class SurveyEnvironment(
    val repository: Repository,
    val schedulerProvider: SchedulerProvider
)