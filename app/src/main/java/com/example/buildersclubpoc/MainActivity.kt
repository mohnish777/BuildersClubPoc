package com.example.buildersclubpoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.buildersclubpoc.planner.presentation.PlannerApp
import com.example.buildersclubpoc.ui.theme.BuildersClubPocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildersClubPocTheme {
                PlannerApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlannerAppPreview() {
    BuildersClubPocTheme {
        PlannerApp()
    }
}
