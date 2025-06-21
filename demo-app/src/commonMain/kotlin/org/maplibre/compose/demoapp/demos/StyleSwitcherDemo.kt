package org.maplibre.compose.demoapp.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.compose.MaplibreMap
import org.maplibre.compose.compose.rememberCameraState
import org.maplibre.compose.compose.rememberStyleState
import org.maplibre.compose.core.CameraPosition
import org.maplibre.compose.demoapp.*

private val NEW_YORK = Position(latitude = 40.744, longitude = -73.981)

object StyleSwitcherDemo : Demo {
  override val name = "Style switcher"
  override val description = "Switch between different map styles at runtime."

  @Composable
  override fun Component(navigateUp: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }

    MaterialTheme(colorScheme = getDefaultColorScheme(isDark = ALL_STYLES[selectedIndex].isDark)) {
      DemoScaffold(this, navigateUp) {
        Column {
          val cameraState =
            rememberCameraState(CameraPosition(target = NEW_YORK, zoom = 15.0, tilt = 30.0))
          val styleState = rememberStyleState()

          Box(modifier = Modifier.weight(1f)) {
            MaplibreMap(
              baseStyle = ALL_STYLES[selectedIndex].style,
              cameraState = cameraState,
              styleState = styleState,
              options = DemoMapOptions(),
            )
            DemoMapControls(cameraState, styleState)
          }

          SecondaryScrollableTabRow(selectedTabIndex = selectedIndex) {
            ALL_STYLES.forEachIndexed { index, style ->
              Tab(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                text = {
                  Text(
                    text = style.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                  )
                },
              )
            }
          }
        }
      }
    }
  }
}
