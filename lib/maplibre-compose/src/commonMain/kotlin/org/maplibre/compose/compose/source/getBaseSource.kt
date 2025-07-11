package org.maplibre.compose.compose.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.maplibre.compose.compose.engine.LocalStyleNode
import org.maplibre.compose.core.source.Source

/**
 * Get the source with the given [id] from the base style specified via the `baseStyle` parameter in
 * [MaplibreMap][org.maplibre.compose.compose.MaplibreMap].
 *
 * @throws IllegalStateException if the source does not exist
 */
@Composable
public fun getBaseSource(id: String): Source? {
  val node = LocalStyleNode.current
  return remember(node, id) { node.sourceManager.getBaseSource(id) }
}
