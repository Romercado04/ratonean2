package com.example.ratonean2_app.auth.presentation.screen

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ratonean2_app.auth.presentation.viemwodel.LoginViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val viewModel = koinViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = Identity.getSignInClient(context)
                .getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (!idToken.isNullOrEmpty()) {
                viewModel.loginWithGoogle(idToken)
            }
        }
    }

    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("368774461247-9v49te82pb20ndpli048fccambrleu8n.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(false)
        .build()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            val oneTapClient = Identity.getSignInClient(context)
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    launcher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                }
                .addOnFailureListener { e ->
                    Log.e("GIS Login", "Error: ${e.message}")
                }
        }) {
            Text("Login con Google")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(state)
    }
}