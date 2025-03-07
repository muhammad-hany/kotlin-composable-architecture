package com.xm.tka.coroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoroutineStore<STATE : Any, ACTION : Any> private constructor(
    val sendAction: (ACTION) -> Unit,
    val state: Flow<STATE> = emptyFlow()
) {
    
    fun send(action: ACTION) = sendAction(action)

    fun <LOCAL_STATE : Any, LOCAL_ACTION : Any> scope(
        toLocalState: (STATE) -> LOCAL_STATE,
        fromLocalAction: (LOCAL_ACTION) -> ACTION,
    ): CoroutineStore<LOCAL_STATE, LOCAL_ACTION> = CoroutineStore(
        state = state.map { toLocalState(it) }.distinctUntilChanged(),
        sendAction = { action ->
            sendAction(fromLocalAction(action))
        },
    )

    companion object {
        fun <STATE : Any, ACTION : Any, ENVIRONMENT : Any> create(
            initialState: STATE,
            reducer: FlowReducer<STATE, ACTION, ENVIRONMENT>,
            environment: ENVIRONMENT,
            storeScopeProvider: StoreScopeProvider,
            dispatcherProvider: DispatcherProvider,
        ): CoroutineStore<STATE, ACTION> {
            val storeScope = storeScopeProvider.getStoreScope()
            val state = MutableStateFlow(initialState)

            lateinit var sendAction: (ACTION) -> Unit
            sendAction = { action: ACTION ->
                storeScope.launch(context = dispatcherProvider.main) {
                    val (nextState, effect) = reducer.reduce(state.value, action, environment)
                    state.update { nextState }
                    effect.collectLatest {
                        sendAction(it)
                    }
                }
            }

            return CoroutineStore(
                sendAction = sendAction,
                state = state
            )
        }

    }


}