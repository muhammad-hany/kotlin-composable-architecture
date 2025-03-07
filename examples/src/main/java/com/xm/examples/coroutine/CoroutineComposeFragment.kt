package com.xm.examples.coroutine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xm.examples.coroutine.ui.HomeViewModel
import com.xm.examples.coroutine.ui.LOCAL_NAVIGATOR
import com.xm.examples.coroutine.ui.homeScope
import com.xm.examples.coroutine.ui.navigation.Home
import com.xm.examples.coroutine.ui.navigation.Survey
import com.xm.examples.coroutine.ui.navigation.SurveyAppNavigation
import com.xm.examples.coroutine.ui.screens.HomeScreen
import com.xm.examples.coroutine.ui.screens.SurveyScreen
import com.xm.examples.coroutine.ui.surveyScope

class CoroutineComposeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))
            setContent {
                MaterialTheme {
                    SurveyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun SurveyApp(viewModel: HomeViewModel) {
    val navController = rememberNavController()

    CompositionLocalProvider(
        LOCAL_NAVIGATOR provides navController
    ) {
        SurveyAppNavigation(navController = navController) {
            composable(Home) { HomeScreen(viewModel.coroutineStore.homeScope()) }
            composable(Survey) { SurveyScreen(viewModel.coroutineStore.surveyScope()) }
        }
    }

}