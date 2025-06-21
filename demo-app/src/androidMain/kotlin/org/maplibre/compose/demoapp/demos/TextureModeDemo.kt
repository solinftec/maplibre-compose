package org.maplibre.compose.demoapp.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.maplibre.compose.compose.MaplibreMap
import org.maplibre.compose.compose.rememberCameraState
import org.maplibre.compose.compose.rememberStyleState
import org.maplibre.compose.core.RenderOptions
import org.maplibre.compose.demoapp.*

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
          baseStyle = DEFAULT_STYLE,
          cameraState = cameraState,
          styleState = styleState,
          options =
            DemoMapOptions()
              .copy(
                renderOptions = RenderOptions(renderMode = RenderOptions.RenderMode.TextureView)
              ),
        )
        DemoMapControls(cameraState, styleState)
      }
    }
  }
}
