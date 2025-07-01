package org.maplibre.compose.core.source

import org.maplibre.compose.expressions.ast.Expression
import org.maplibre.compose.expressions.value.BooleanValue
import io.github.dellisd.spatialk.geojson.Feature

public actual class VectorSource : Source {
  public actual constructor(id: String, uri: String) : super() {
    this.impl = TODO()
  }

  public actual constructor(id: String, tiles: List<String>, options: TileSetOptions) : super() {
    this.impl = TODO()
  }

  override val impl: Nothing

  public actual fun querySourceFeatures(
    sourceLayerIds: Set<String>,
    predicate: Expression<BooleanValue>,
  ): List<Feature> {
    TODO()
  }
}
