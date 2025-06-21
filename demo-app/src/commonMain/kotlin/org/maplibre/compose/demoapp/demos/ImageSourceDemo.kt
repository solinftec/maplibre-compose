package org.maplibre.compose.demoapp.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.maplibre.compose.compose.MaplibreMap
import org.maplibre.compose.compose.layer.RasterLayer
import org.maplibre.compose.compose.rememberCameraState
import org.maplibre.compose.compose.rememberStyleState
import org.maplibre.compose.compose.source.rememberImageSource
import org.maplibre.compose.core.CameraPosition
import org.maplibre.compose.core.util.PositionQuad
import org.maplibre.compose.demoapp.DEFAULT_STYLE
import org.maplibre.compose.demoapp.Demo
import org.maplibre.compose.demoapp.DemoAppBar
import org.maplibre.compose.demoapp.DemoMapControls
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.value.RasterResampling
import io.github.dellisd.spatialk.geojson.Position

private val SWITZERLAND = Position(latitude = 47.0, longitude = 8.0)

object ImageSourceDemo : Demo {
  override val name = "Image Source"
  override val description = "Display an image at specific bounds"

  @Composable
  override fun Component(navigateUp: () -> Unit) {
    val cameraState = rememberCameraState(CameraPosition(target = SWITZERLAND, zoom = 5.0))
    val styleState = rememberStyleState()

    Scaffold(topBar = { DemoAppBar(this, navigateUp) }) { padding ->
      Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(baseStyle = DEFAULT_STYLE, cameraState = cameraState, styleState = styleState) {
          val imageSource =
            rememberImageSource(
              id = "demo-image",
              uri =
                "https://opengraph.githubassets.com/5a529cd541b838b1de685019e0b85365dc6a32ed378abc1a77af477fd0d257a2/maplibre/maplibre-compose",
              position =
                PositionQuad(
                  topLeft = Position(latitude = 48.5, longitude = 5.0),
                  topRight = Position(latitude = 48.5, longitude = 11.0),
                  bottomRight = Position(latitude = 45.5, longitude = 11.0),
                  bottomLeft = Position(latitude = 45.5, longitude = 5.0),
                ),
            )

          RasterLayer(
            id = "demo-image-layer",
            source = imageSource,
            resampling = const(RasterResampling.Nearest),
          )
        }

        DemoMapControls(
          cameraState,
          styleState,
          modifier = Modifier.padding(padding).consumeWindowInsets(padding),
        )
      }
    }
  }
}
