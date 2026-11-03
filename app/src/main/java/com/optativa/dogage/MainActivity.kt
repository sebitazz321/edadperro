package com.optativa.dogage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.optativa.dogage.ui.CalculatorScreen
import com.optativa.dogage.ui.ChatScreen
import com.optativa.dogage.ui.DogAgeTheme
import com.optativa.dogage.ui.DogsViewModel
import com.optativa.dogage.ui.MyDogsScreen
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DogAgeTheme { DogApp() } }
    }
}

@Composable
fun DogApp(dogsVm: DogsViewModel = viewModel()) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Text("🧮") },
                    label = { Text("Calculadora") }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = {
                        BadgedBox(badge = { if (dogsVm.dogs.isNotEmpty()) Badge { Text("${dogsVm.dogs.size}") } }) { Text("🐶") }
                    },
                    label = { Text("Mis perros") }
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    icon = { Text("💬") },
                    label = { Text("Chatbot") }
                )
            }
        }
    ) { padding ->
        when (tab) {
            0 -> CalculatorScreen(Modifier.padding(padding), dogsVm)
            1 -> MyDogsScreen(dogsVm, Modifier.padding(padding), onOpenCalculator = { tab = 0 })
            else -> ChatScreen(Modifier.padding(padding))
        }
    }
}
