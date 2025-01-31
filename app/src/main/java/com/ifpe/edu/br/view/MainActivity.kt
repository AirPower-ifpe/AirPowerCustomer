package com.ifpe.edu.br.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ifpe.edu.br.common.components.Card
import com.ifpe.edu.br.common.components.CustomColumn

import com.ifpe.edu.br.common.ui.theme.AirPowerCostumerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AirPowerCostumerTheme {
                CustomColumn(layouts = listOf {
                    Card(layouts = listOf{

                    })
                })
            }
        }
    }
}
