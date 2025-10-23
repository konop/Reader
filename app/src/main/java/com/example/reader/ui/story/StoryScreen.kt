package com.example.reader.ui.story

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryScreen() {
    val context = LocalContext.current
    val storyViewModel: StoryViewModel = viewModel(
        factory = StoryViewModelFactory(context.applicationContext as Application)
    )
    val story by storyViewModel.story.collectAsState()
    val currentPage by storyViewModel.currentPage.collectAsState()
    var isListening by remember { mutableStateOf(false) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    story?.let {
        val page = it.pages[currentPage]
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = page.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                page.words.forEach { word ->
                    Text(
                        text = word.text,
                        modifier = Modifier
                            .padding(4.dp)
                            .background(if (word.isRead) Color.Yellow else Color.Transparent)
                            .clickable { storyViewModel.speakWord(word) }
                            .padding(4.dp)
                    )
                }
            }
            Button(
                onClick = {
                    if (hasPermission) {
                        if (isListening) {
                            storyViewModel.stopListening()
                        } else {
                            storyViewModel.startListening()
                        }
                        isListening = !isListening
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = if (hasPermission) {
                    if (isListening) "Stop Listening" else "Start Listening"
                } else "Request Permission")
            }
        }
    }
}
