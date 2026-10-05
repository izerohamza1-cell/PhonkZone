package com.phonkzone.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

data class Track(
    val title: String,
    val artist: String,
    val category: String,
    val audioResId: Int
)

private val tracks = listOf(
    Track("Neon Drift", "PHONK ZONE", "Drift Phonk", R.raw.neon_drift),
    Track("Night Ride", "PHONK ZONE", "Chill Phonk", R.raw.night_ride),
    Track("Brazil Night", "PHONK ZONE", "Brazilian Phonk", R.raw.brazil_night),
    Track("Dark Engine", "PHONK ZONE", "Aggressive Phonk", R.raw.dark_engine),
    Track("Retro Bass", "PHONK ZONE", "Classic Phonk", R.raw.retro_bass)
)

class MainActivity : ComponentActivity() {

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()

        setContent {
            PhonkZoneApp(player)
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}

@Composable
fun PhonkZoneApp(player: ExoPlayer) {

    var selectedTrack by remember { mutableStateOf<Track?>(null) }
    var searchText by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }

    val filteredTracks = tracks.filter {
        it.title.contains(searchText, ignoreCase = true) ||
        it.artist.contains(searchText, ignoreCase = true) ||
        it.category.contains(searchText, ignoreCase = true)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF080808),
            surface = Color(0xFF111111),
            primary = Color(0xFFFF3B30),
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {

        Scaffold(
            containerColor = Color(0xFF080808)
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 18.dp)
            ) {

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "PHONK ZONE",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = "ONLY PHONK. NOTHING ELSE.",
                    fontSize = 12.sp,
                    color = Color(0xFFFF3B30),
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(18.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search phonk...")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "PHONK CATEGORIES",
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    listOf(
                        "Drift",
                        "Brazilian",
                        "Aggressive"
                    ).forEach { category ->

                        AssistChip(
                            onClick = {
                                searchText = category
                            },
                            label = {
                                Text(category)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                Text(
                    "TRACKS",
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(filteredTracks) { track ->

                        TrackCard(
                            track = track,
                            onClick = {

                                selectedTrack = track

                                val uri = Uri.parse(
                                    "android.resource://${player.currentMediaItem?.mediaId ?: "com.phonkzone.app"}/${track.audioResId}"
                                )

                                val mediaItem = MediaItem.fromUri(
                                    "android.resource://com.phonkzone.app/${track.audioResId}"
                                )

                                player.setMediaItem(mediaItem)
                                player.prepare()
                                player.play()

                                isPlaying = true
                            }
                        )
                    }
                }

                selectedTrack?.let { track ->

                    PlayerBar(
                        track = track,
                        isPlaying = isPlaying,
                        onPlayPause = {

                            if (player.isPlaying) {
                                player.pause()
                                isPlaying = false
                            } else {
                                player.play()
                                isPlaying = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrackCard(
    track: Track,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF151515),
                RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    Color(0xFF252525),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                "♫",
                fontSize = 25.sp,
                color = Color(0xFFFF3B30)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                track.title,
                fontWeight = FontWeight.Bold
            )

            Text(
                track.artist,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                track.category,
                color = Color(0xFFFF3B30),
                fontSize = 11.sp
            )
        }

        Text(
            "▶",
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun PlayerBar(
    track: Track,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1B1B1B),
                RoundedCornerShape(18.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                track.title,
                fontWeight = FontWeight.Bold
            )

            Text(
                track.artist,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        Text(
            if (isPlaying) "⏸" else "▶",
            fontSize = 22.sp,
            modifier = Modifier
                .clickable {
                    onPlayPause()
                }
                .padding(8.dp)
        )
    }

    Spacer(Modifier.height(8.dp))
}
