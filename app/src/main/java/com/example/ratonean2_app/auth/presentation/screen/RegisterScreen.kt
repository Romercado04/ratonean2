package com.example.ratonean2_app.auth.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ratonean2_app.auth.presentation.viemwodel.RegisterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen() {
    val viewModel = koinViewModel<RegisterViewModel>()

    val state by viewModel.uiState.collectAsState()


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register")

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.registerUser() },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Create Account")
            }
        }

        if (state.error != null) {
            Text(text = "Error: ${state.error}", color = Color.Red)
        }

        if (state.success) {
            Text(text = "User created successfully!", color = Color.Green)
        }
    }
}
