package org.maplibre.compose.demoapp.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.compose.MaplibreMap
import org.maplibre.compose.compose.rememberCameraState
import org.maplibre.compose.compose.rememberStyleState
import org.maplibre.compose.core.CameraPosition
import org.maplibre.compose.demoapp.*

private val PORTLAND = Position(latitude = 45.521, longitude = -122.675)

object EdgeToEdgeDemo : Demo {
  override val name = "Edge-to-edge"
  override val description =
    "Fill the entire screen with a map and pad ornaments to position them correctly."

  @Composable
  override fun Component(navigateUp: () -> Unit) {
    val cameraState = rememberCameraState(CameraPosition(target = PORTLAND, zoom = 13.0))
    val styleState = rememberStyleState()

    Scaffold(topBar = { DemoAppBar(this, navigateUp, alpha = 0.5f) }) { padding ->
      Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
          styleUri = DEFAULT_STYLE,
          cameraState = cameraState,
          styleState = styleState,
          options = DemoMapOptions(padding),
        )
        DemoMapControls(
          cameraState,
          styleState,
          modifier = Modifier.padding(padding).consumeWindowInsets(padding),
        )
      }
    }
  }
}
