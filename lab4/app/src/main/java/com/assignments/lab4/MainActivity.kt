package com.assignments.lab4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.assignments.lab4.ui.theme.Lab4Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        player = ExoPlayer.Builder(this).build()
        setContent {
            Lab4Theme {
                MediaPlayerScreen(player = player!!)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}

enum class MediaType(val label: String) {
    AUDIO("Audio"), VIDEO("Video")
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun MediaPlayerScreen(player: ExoPlayer) {
    val context = LocalContext.current
    var mediaType by remember { mutableStateOf(MediaType.VIDEO) }
    var currentSource by remember { mutableStateOf("No media selected") }
    var urlText by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var hasMedia by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Not all providers grant persistable permissions; playback still works
            }

            val mime = context.contentResolver.getType(uri)
            if (mime != null) {
                mediaType = if (mime.startsWith("audio")) MediaType.AUDIO else MediaType.VIDEO
            }

            currentSource = uri.lastPathSegment ?: "Local file"
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.play()
            hasMedia = true
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) isPlaying = false
            }

            override fun onPlayerError(error: PlaybackException) {
                val msg = when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Network error: check your connection"

                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "File not found"

                    PlaybackException.ERROR_CODE_DECODER_INIT_FAILED, PlaybackException.ERROR_CODE_DECODING_FAILED -> "Unsupported media format"

                    else -> error.localizedMessage ?: "Playback error"
                }
                scope.launch { snackbarHostState.showSnackbar(msg) }
                hasMedia = false
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        CenterAlignedTopAppBar(title = { Text("Media Player") })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                MediaType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = mediaType == type,
                        onClick = { mediaType = type },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index, count = MediaType.entries.size
                        )
                    ) { Text(type.label) }
                }
            }

            if (mediaType == MediaType.VIDEO) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black), contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                this.player = player
                                useController = false
                            }
                        },
                        update = { view -> view.player = player },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isPlaying) "♪ Playing Audio..." else "♪ Audio Mode",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Source: $currentSource",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }

            Button(
                onClick = { filePicker.launch(arrayOf("audio/*", "video/*")) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Pick File from Device") }

            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                label = { Text("Media URL") },
                placeholder = { Text("https://example.com/media.mp4") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val url = urlText.trim()
                    if (url.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Please enter a URL") }
                        return@Button
                    }
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        scope.launch {
                            snackbarHostState.showSnackbar("URL must start with http:// or https://")
                        }
                        return@Button
                    }
                    currentSource = url
                    player.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
                    player.prepare()
                    player.play()
                    hasMedia = true
                }, modifier = Modifier.fillMaxWidth()
            ) { Text("Load from URL") }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(onClick = {
                    if (!hasMedia) {
                        scope.launch { snackbarHostState.showSnackbar("No media selected") }
                        return@FilledTonalButton
                    }
                    if (player.playbackState == Player.STATE_IDLE) {
                        player.prepare()
                    }
                    player.play()
                }) { Text("Play") }

                FilledTonalButton(onClick = {
                    player.pause()
                }) { Text("Pause") }

                FilledTonalButton(onClick = {
                    player.stop()
                    isPlaying = false
                }) { Text("Stop") }
            }
        }
    }
}
