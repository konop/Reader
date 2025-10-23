package com.example.reader.ui.storylist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reader.data.Story

@Composable
fun StoryListScreen(
    onStoryClick: (Story) -> Unit
) {
    val storyListViewModel: StoryListViewModel = viewModel()
    val stories by storyListViewModel.stories.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Choose a Story",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(stories) { story ->
                StoryListItem(story = story) {
                    onStoryClick(story)
                }
            }
        }
    }
}

@Composable
fun StoryListItem(story: Story, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = story.title, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
