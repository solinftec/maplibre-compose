package org.maplibre.maplibrecompose.demoapp.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.maplibre.maplibrecompose.compose.MaplibreMap
import org.maplibre.maplibrecompose.compose.rememberCameraState
import org.maplibre.maplibrecompose.compose.rememberStyleState
import org.maplibre.maplibrecompose.core.MapOptions
import org.maplibre.maplibrecompose.demoapp.DEFAULT_STYLE
import org.maplibre.maplibrecompose.demoapp.Demo
import org.maplibre.maplibrecompose.demoapp.DemoMapControls
import org.maplibre.maplibrecompose.demoapp.DemoOrnamentSettings
import org.maplibre.maplibrecompose.demoapp.DemoScaffold
import org.jetbrains.compose.resources.ExperimentalResourceApi

object TextureModeDemo : Demo {
  override val name = "Texture Mode"
  override val description = "Render the map to a TextureView instead of a GLSurfaceView."

  @Composable
  @OptIn(ExperimentalResourceApi::class)
  override fun Component(navigateUp: () -> Unit) {
    DemoScaffold(this, navigateUp) {
      val cameraState = rememberCameraState()
      val styleState = rememberStyleState()

      Box(modifier = Modifier.Companion.fillMaxSize()) {
        MaplibreMap(
          styleUri = DEFAULT_STYLE,
          cameraState = cameraState,
          styleState = styleState,
          ornamentSettings = DemoOrnamentSettings(),
          platformOptions = MapOptions(textureMode = true),
        )
        DemoMapControls(cameraState, styleState)
      }
    }
  }
}
