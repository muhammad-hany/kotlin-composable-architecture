package com.xm.examples.coroutine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xm.examples.coroutine.store.SchedulerProviderImpl
import com.xm.examples.coroutine.store.SurveyState
import com.xm.examples.coroutine.api.SurveyApi
import com.xm.examples.coroutine.data.client.RetrofitClient
import com.xm.examples.coroutine.data.repository.RepositoryImpl
import com.xm.examples.coroutine.store.GlobalAction
import com.xm.examples.coroutine.store.GlobalState
import com.xm.examples.coroutine.store.HomeAction
import com.xm.examples.coroutine.store.HomeState
import com.xm.examples.coroutine.store.SurveyAction
import com.xm.examples.coroutine.store.SurveyEnvironment
import com.xm.examples.coroutine.store.reduced
import com.xm.tka.ActionPrism
import com.xm.tka.StateLens
import com.xm.tka.coroutine.CoroutineStore
import com.xm.tka.coroutine.DispatcherProvider
import com.xm.tka.coroutine.FlowReducer
import com.xm.tka.coroutine.StoreScopeProvider

class HomeViewModel() : ViewModel() {
//    val store = Store(
//        initialState = GlobalState(),
//        reducer = globalReducer,
//        environment = environment
//    )

    val client = RetrofitClient()
    val moshi = client.buildMoshiInstance()
    val retrofitClient = client.buildRetrofitInstance(moshi)
    val api = retrofitClient.create(SurveyApi::class.java)
    val repository = RepositoryImpl(api)
    val environment = SurveyEnvironment(
        repository = repository,
        schedulerProvider = SchedulerProviderImpl()
    )

    val storeScopeProvider = StoreScopeProvider { viewModelScope }


    val coroutineStore = CoroutineStore.create(
        initialState = GlobalState(),
        reducer = globalReducer,
        storeScopeProvider = storeScopeProvider,
        dispatcherProvider = DispatcherProvider(
            main = kotlinx.coroutines.Dispatchers.Main,
            io = kotlinx.coroutines.Dispatchers.IO,
            computation = kotlinx.coroutines.Dispatchers.Default
        ),
        environment = environment
    )

}

fun CoroutineStore<GlobalState, GlobalAction>.homeScope(): CoroutineStore<HomeState, HomeAction> =
    this.scope(
        toLocalState = { it.home },
        fromLocalAction = { GlobalAction.Home(it) }
    )

fun CoroutineStore<GlobalState, GlobalAction>.surveyScope(): CoroutineStore<SurveyState, SurveyAction> =
    this.scope(
        toLocalState = { it.survey },
        fromLocalAction = { GlobalAction.Survey(it) }
    )

private val homeReducer = FlowReducer<HomeState, HomeAction, SurveyEnvironment> { state, action, env ->
    when (action) {
        is HomeAction.QuestionsLoaded -> action.reduced(this, state)
        is HomeAction.GetQuestions -> action.reduced(this, state, env)
    }
}

private val surveyReducer = FlowReducer<SurveyState, SurveyAction, SurveyEnvironment> { state, action, env ->
    when (action) {
        is SurveyAction.CreateNewSurvey -> action.reduced(this, state, env)
        is SurveyAction.SurveyLoaded -> action.reduced(this, state)
        is SurveyAction.ResetQuestion -> action.reduced(this, state)
        is SurveyAction.SubmitAnswer -> action.reduced(this, state, env)
        is SurveyAction.AnswerSubmitted -> action.reduced(this, state)
    }
}

val globalReducer: FlowReducer<GlobalState, GlobalAction, SurveyEnvironment> = FlowReducer.combine(
    homeReducer.pullback(
        toLocalState = StateLens(
            set = { state, update -> state.copy(home = update) },
            get = { it.home }
        ),
        toLocalAction = ActionPrism(
            get = {
                (it as? GlobalAction.Home)?.action
                  },
            reverseGet = { GlobalAction.Home(it) }
        ),
        toLocalEnvironment = { it }
    ),
    surveyReducer.pullback(
        toLocalState = StateLens(
            set = { state, update -> state.copy(survey = update) },
            get = { it.survey }
        ),
        toLocalAction = ActionPrism(
            get = { (it as? GlobalAction.Survey)?.action },
            reverseGet = { GlobalAction.Survey(it) }
        ),
        toLocalEnvironment = { it }
    )
)


object QuestionLoading
object AnswerSubmitting


