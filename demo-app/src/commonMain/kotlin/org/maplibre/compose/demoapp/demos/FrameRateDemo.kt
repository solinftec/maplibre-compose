package org.maplibre.compose.demoapp.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import org.maplibre.compose.compose.MaplibreMap
import org.maplibre.compose.compose.rememberCameraState
import org.maplibre.compose.compose.rememberStyleState
import org.maplibre.compose.core.RenderOptions
import org.maplibre.compose.demoapp.*

object FrameRateDemo : Demo {
  override val name = "Frame rate"
  override val description = "Change the frame rate of the map."

  @Composable
  override fun Component(navigateUp: () -> Unit) {
    DemoScaffold(this, navigateUp) {
      Column {
        var maximumFps by remember { mutableStateOf(120) }
        val fpsState = remember { FrameRateState() }

        val cameraState = rememberCameraState()
        val styleState = rememberStyleState()

        Box(modifier = Modifier.weight(1f)) {
          MaplibreMap(
            styleUri = DEFAULT_STYLE,
            onFrame = fpsState::recordFps,
            cameraState = cameraState,
            styleState = styleState,
            options =
              DemoMapOptions().copy(renderOptions = RenderOptions.Standard.withMaxFps(maximumFps)),
          )
          DemoMapControls(cameraState, styleState)
        }

        Column(modifier = Modifier.padding(16.dp)) {
          if (Platform.usesMaplibreNative) {
            Slider(
              value = maximumFps.toFloat(),
              onValueChange = { maximumFps = it.roundToInt() },
              valueRange = 15f..120f,
              enabled = Platform.usesMaplibreNative,
            )
            Text(
              "Target: $maximumFps ${fpsState.spinChar} Actual: ${fpsState.avgFps}",
              style = MaterialTheme.typography.labelMedium,
            )
          } else {
            Text(
              "${fpsState.spinChar} ${fpsState.avgFps}",
              style = MaterialTheme.typography.labelMedium,
            )
          }
        }
      }
    }
  }
}
