package com.xm.examples.coroutine.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xm.examples.coroutine.store.HomeState
import com.xm.examples.coroutine.store.HomeAction
import com.xm.examples.coroutine.ui.LOCAL_NAVIGATOR
import com.xm.examples.coroutine.ui.navigation.Survey
import com.xm.tka.coroutine.CoroutineStore

@Composable
fun HomeScreen(viewStore: CoroutineStore<HomeState, HomeAction>) {
    val surveyState by viewStore.state.collectAsStateWithLifecycle(HomeState())
    val navController = LOCAL_NAVIGATOR.current


    LaunchedEffect(Unit) {
        viewStore.send(HomeAction.GetQuestions)
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            if (surveyState.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text(
                "Welcome", modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(12.dp)
            )
            Spacer(Modifier.weight(1F))
            Button(
                enabled = surveyState.questions.isNotEmpty(),
                onClick = {
                    navController.navigate(Survey)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(12.dp)
            ) {
                Text("Start Survey", fontSize = 20.sp)
            }
            if (!surveyState.error.isNullOrBlank()) {
                Button(
                    onClick = { viewStore.send(HomeAction.GetQuestions) },
                    enabled = !surveyState.isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Retry")
                        Text(text = "Retry")
                    }

                }
                Text(text = surveyState.error!!)
            }

            Spacer(Modifier.weight(1F))
        }
    }
}
