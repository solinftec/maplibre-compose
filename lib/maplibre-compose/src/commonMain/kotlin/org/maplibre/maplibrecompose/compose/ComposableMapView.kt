package org.maplibre.maplibrecompose.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import org.maplibre.maplibrecompose.core.MapOptions
import org.maplibre.maplibrecompose.core.MaplibreMap
import org.maplibre.maplibrecompose.core.SafeStyle

@Composable
internal expect fun ComposableMapView(
  modifier: Modifier,
  styleUri: String,
  rememberedStyle: SafeStyle?,
  update: (map: MaplibreMap) -> Unit,
  onReset: () -> Unit,
  logger: Logger?,
  callbacks: MaplibreMap.Callbacks,
  platformOptions: MapOptions,
)
