package org.maplibre.compose.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import org.maplibre.compose.core.MapOptions
import org.maplibre.compose.core.MaplibreMap
import org.maplibre.compose.core.SafeStyle

@Composable
internal expect fun ComposableMapView(
  modifier: Modifier,
  styleUri: String,
  rememberedStyle: SafeStyle?,
  update: (map: MaplibreMap) -> Unit,
  onReset: () -> Unit,
  logger: Logger?,
  callbacks: MaplibreMap.Callbacks,
  options: MapOptions,
)
