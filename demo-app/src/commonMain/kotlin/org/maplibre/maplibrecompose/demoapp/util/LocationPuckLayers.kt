package org.maplibre.maplibrecompose.demoapp.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.maplibre.maplibrecompose.compose.layer.CircleLayer
import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.expressions.dsl.const
import org.maplibre.maplibrecompose.expressions.dsl.offset

@Composable
fun LocationPuckLayers(idPrefix: String, locationSource: Source) {
  CircleLayer(
    id = "${idPrefix}-shadow",
    source = locationSource,
    radius = const(13.dp),
    color = const(Color.Black),
    blur = const(1f),
    translate = offset(0.dp, 1.dp),
  )
  CircleLayer(
    id = "${idPrefix}-circle",
    source = locationSource,
    radius = const(7.dp),
    color = const(MaterialTheme.colorScheme.primary),
    strokeColor = const(Color.White),
    strokeWidth = const(3.dp),
  )
}
