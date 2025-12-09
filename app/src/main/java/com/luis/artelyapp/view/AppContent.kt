package com.luis.artelyapp.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luis.artelyapp.view.common.PhoneContainer
import com.luis.artelyapp.view.navigation.NavigationHost
import com.luis.artelyapp.viewmodel.MainViewModel

@Composable
fun AppContent(viewModel: MainViewModel = viewModel()) {
    Surface(modifier = Modifier.fillMaxSize()) {
        PhoneContainer {
            NavigationHost(viewModel = viewModel)
        }
    }
}

