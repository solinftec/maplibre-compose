package org.maplibre.compose.demoapp.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.em
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.Position
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.compose.ClickResult
import org.maplibre.compose.compose.MaplibreMap
import org.maplibre.compose.compose.layer.SymbolLayer
import org.maplibre.compose.compose.rememberCameraState
import org.maplibre.compose.compose.rememberStyleState
import org.maplibre.compose.compose.source.rememberGeoJsonSource
import org.maplibre.compose.core.CameraPosition
import org.maplibre.compose.demoapp.*
import org.maplibre.compose.demoapp.generated.Res
import org.maplibre.compose.demoapp.generated.marker
import org.maplibre.compose.expressions.dsl.*
import org.maplibre.compose.expressions.dsl.Feature.get

private val CHICAGO = Position(latitude = 41.878, longitude = -87.626)

object MarkersDemo : Demo {
  override val name = "Markers, images, and formatting"
  override val description = "Add images to the style and intermingle it with text."

  @Composable
  override fun Component(navigateUp: () -> Unit) {
    DemoScaffold(this, navigateUp) {
      val marker = painterResource(Res.drawable.marker)
      val cameraState =
        rememberCameraState(firstPosition = CameraPosition(target = CHICAGO, zoom = 7.0))
      val styleState = rememberStyleState()
      var selectedFeature by remember { mutableStateOf<Feature?>(null) }

      Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
          styleUri = DEFAULT_STYLE,
          cameraState = cameraState,
          styleState = styleState,
          ornamentSettings = DemoOrnamentSettings(),
        ) {
          val amtrakStations =
            rememberGeoJsonSource(
              id = "amtrak-stations",
              uri =
                "https://raw.githubusercontent.com/datanews/amtrak-geojson/refs/heads/master/amtrak-stations.geojson",
            )
          SymbolLayer(
            id = "amtrak-stations",
            source = amtrakStations,
            onClick = { features ->
              selectedFeature = features.firstOrNull()
              ClickResult.Consume
            },
            iconImage = image(marker),
            textField =
              format(
                span(image("railway")),
                span(" "),
                span(get("STNCODE").asString(), textSize = const(1.2f.em)),
              ),
            textFont = const(listOf("Noto Sans Regular")),
            textColor = const(MaterialTheme.colorScheme.onBackground),
            textOffset = offset(0.em, 0.6.em),
          )
        }
        DemoMapControls(cameraState, styleState)
      }

      selectedFeature?.let { feature ->
        AlertDialog(
          onDismissRequest = { selectedFeature = null },
          confirmButton = {},
          title = { Text(feature.getStringProperty("STNNAME") ?: "") },
          text = {
            Column {
              Text("Station Code: ${feature.getStringProperty("STNCODE") ?: ""}")
              Text("Station Type: ${feature.getStringProperty("STNTYPE") ?: ""}")
              Text("Address: ${feature.getStringProperty("ADDRESS1") ?: ""}")
              Text("City: ${feature.getStringProperty("CITY") ?: ""}")
              Text("State: ${feature.getStringProperty("STATE") ?: ""}")
              Text("Zip: ${feature.getStringProperty("ZIP") ?: ""}")
            }
          },
        )
      }
    }
  }
}
