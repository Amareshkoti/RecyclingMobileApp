package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.data.database.AppDatabase
import com.example.data.repository.RecyclingRepository
import com.example.ui.screens.MainAppContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RecyclingViewModel
import com.example.ui.viewmodel.RecyclingViewModelFactory

class MainActivity : ComponentActivity() {

    // Lazy initialization of database and repository to promote memory safety
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "recycling_kids_champion.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val repository by lazy {
        RecyclingRepository(database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Instantiate the ViewModel using our custom Factory
                val homeViewModel: RecyclingViewModel = viewModel(
                    factory = RecyclingViewModelFactory(repository)
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainAppContainer(
                        viewModel = homeViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
