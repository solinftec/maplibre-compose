package org.maplibre.compose.compose.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key as composeKey
import org.maplibre.compose.core.source.TileSetOptions
import org.maplibre.compose.core.source.VectorSource

/**
 * Remember a new [VectorSource] with the given [id] from the given [uri].
 *
 * @throws IllegalArgumentException if a layer with the given [id] already exists.
 */
@Composable
public fun rememberVectorSource(id: String, uri: String): VectorSource =
  composeKey(id, uri) {
    rememberUserSource(factory = { VectorSource(id = id, uri = uri) }, update = {})
  }

@Composable
public fun rememberVectorSource(
  id: String,
  tiles: List<String>,
  options: TileSetOptions = TileSetOptions(),
): VectorSource =
  composeKey(id, tiles, options) {
    rememberUserSource(
      factory = { VectorSource(id = id, tiles = tiles, options = options) },
      update = {},
    )
  }
