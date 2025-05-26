package org.maplibre.maplibrecompose.compose.engine

import org.maplibre.maplibrecompose.compose.FeaturesClickHandler
import org.maplibre.maplibrecompose.compose.layer.Anchor
import org.maplibre.maplibrecompose.core.layer.Layer

internal class LayerNode<T : Layer>(val layer: T, val anchor: Anchor) : MapNode() {
  internal var added: Boolean = false

  override fun allowsChild(node: MapNode) = false

  internal var onClick: FeaturesClickHandler? = null
  internal var onLongClick: FeaturesClickHandler? = null

  override fun toString(): String {
    return "LayerNode(layer=${layer.id}, anchor=$anchor, added=$added)"
  }
}
