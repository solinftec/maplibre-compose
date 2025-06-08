package org.maplibre.compose.compose.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import org.maplibre.compose.core.source.GeoJsonData
import org.maplibre.compose.core.source.GeoJsonOptions
import org.maplibre.compose.core.source.GeoJsonSource

/**
 * Remember a new [GeoJsonSource] with the given [id] and [options] from the given [GeoJsonData].
 *
 * @throws IllegalArgumentException if a source with the given [id] already exists.
 */
@Composable
public fun rememberGeoJsonSource(
  id: String,
  data: GeoJsonData,
  options: GeoJsonOptions = GeoJsonOptions(),
): GeoJsonSource =
  key(id, options) {
    rememberUserSource(
      factory = { GeoJsonSource(id = id, data = data, options = options) },
      update = { setData(data) },
    )
  }
