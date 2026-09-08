package com.example.composemultiplatform.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemultiplatform.data.createKSafe
import com.example.composemultiplatform.ui.viewmodel.SecureViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SecureForKSafeScreen() {
    val viewModel: SecureViewModel = koinViewModel()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Button(
                onClick = viewModel::incrementCounter
            ) {
                Text(
                    text = "Counter ${viewModel.counter}"
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            TextField(
                value = viewModel.userProfile.email,
                onValueChange = {
                    viewModel.updateProfile(
                        email = it,
                        password = viewModel.userProfile.password
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label =  { Text("Email")  }
            )

            TextField(
                value = viewModel.userProfile.password,
                onValueChange = {
                    viewModel.updateProfile(
                        email = viewModel.userProfile.email,
                        password = it
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label =  { Text("Password")  }
            )
        }
    }
}