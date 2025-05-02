package me.francis.audioplayerwithequalizer.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EqualizerView(
    navController: NavController,
//    equalizerViewModel: EqualizerViewModel,
) {

//    val equalizer by remember { mutableStateOf(value = Equalizer()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Equalizador",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    )
                }
            )
        },
        content = { padding ->
            GraphicEqualizer(
                padding = padding,
//                equalizer = equalizer,
                updateFrequency = { column, frequency ->
//                    equalizer.gains[column] = frequency
//                    equalizerViewModel.equalize(equalizer = equalizer)
                }
            )
        }
    )
}

@Composable
internal fun GraphicEqualizer(
    padding: PaddingValues,
//    equalizer: Equalizer,
    updateFrequency: (column: Int, frequency: Int) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(padding)
            .fillMaxWidth(),
        content = {
//            items(equalizer.frequencies.size) { index ->
            items(5) { index ->
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        SliderCustom(
                            index = index,
//                            frequency = equalizer.frequencies[index],
                            frequency = 0,
                            updateFrequency = updateFrequency,
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .fillMaxHeight()
                                .width(45.dp)
                                .zIndex(-1f)
                        )
                    }
                )
            }
        }
    )
}

@Composable
internal fun SliderCustom(
    frequency: Short,
    index: Int,
    modifier: Modifier = Modifier,
    updateFrequency: (column: Int, frequency: Int) -> Unit,
) {
    var sliderValue by remember(frequency) { mutableStateOf(frequency) }
    var gainFrequencies = listOf(32f, 125f, 250f, 1000f, 4000f, 16000f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                Text(
                    text = "${sliderValue.toInt()}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "dB",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        )

        //slider
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1F)
                .background(Color.Transparent),
        ) {
            Slider(
                value = sliderValue.toFloat(),
                onValueChange = {
                    sliderValue = it.toInt().toShort()
                },
                valueRange = -12f..12f,
                onValueChangeFinished = { updateFrequency(index, sliderValue.toInt()) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    disabledThumbColor = MaterialTheme.colorScheme.onBackground,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = 270f
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxHeight,
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.place(-placeable.width, 0)
                        }
                    },
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                Text(
                    text = if (gainFrequencies[index] >= 1000) {
                        "${gainFrequencies[index] / 1000}k"
                    } else {
                        "${gainFrequencies[index]}"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Hz",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        )
    }
}
