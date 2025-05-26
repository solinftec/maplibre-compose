package org.maplibre.maplibrecompose.core.source

import cocoapods.MapLibre.MLNFeatureProtocol
import cocoapods.MapLibre.MLNTileCoordinateSystemTMS
import cocoapods.MapLibre.MLNTileCoordinateSystemXYZ
import cocoapods.MapLibre.MLNTileSourceOptionAttributionHTMLString
import cocoapods.MapLibre.MLNTileSourceOptionCoordinateBounds
import cocoapods.MapLibre.MLNTileSourceOptionMaximumZoomLevel
import cocoapods.MapLibre.MLNTileSourceOptionMinimumZoomLevel
import cocoapods.MapLibre.MLNTileSourceOptionTileCoordinateSystem
import cocoapods.MapLibre.MLNVectorTileSource
import org.maplibre.maplibrecompose.core.util.toFeature
import org.maplibre.maplibrecompose.core.util.toMLNCoordinateBounds
import org.maplibre.maplibrecompose.core.util.toNSPredicate
import org.maplibre.maplibrecompose.expressions.ExpressionContext
import org.maplibre.maplibrecompose.expressions.ast.Expression
import org.maplibre.maplibrecompose.expressions.dsl.const
import org.maplibre.maplibrecompose.expressions.value.BooleanValue
import io.github.dellisd.spatialk.geojson.Feature
import platform.Foundation.NSURL

public actual class VectorSource : Source {
  override val impl: MLNVectorTileSource

  internal constructor(source: MLNVectorTileSource) {
    impl = source
  }

  public actual constructor(id: String, uri: String) : super() {
    this.impl = MLNVectorTileSource(id, NSURL(string = uri))
  }

  public actual constructor(id: String, tiles: List<String>, options: TileSetOptions) : super() {
    this.impl =
      MLNVectorTileSource(
        identifier = id,
        tileURLTemplates = tiles,
        options =
          mapOf(
            MLNTileSourceOptionMinimumZoomLevel to options.minZoom.toDouble(),
            MLNTileSourceOptionMaximumZoomLevel to options.maxZoom.toDouble(),
            MLNTileSourceOptionTileCoordinateSystem to
              when (options.tileCoordinateSystem) {
                TileCoordinateSystem.XYZ -> MLNTileCoordinateSystemXYZ
                TileCoordinateSystem.TMS -> MLNTileCoordinateSystemTMS
              },
            MLNTileSourceOptionCoordinateBounds to options.boundingBox?.toMLNCoordinateBounds(),
            MLNTileSourceOptionAttributionHTMLString to options.attributionHtml,
          ),
      )
  }

  public actual fun querySourceFeatures(
    sourceLayerIds: Set<String>,
    predicate: Expression<BooleanValue>,
  ): List<Feature> {
    return impl
      .featuresInSourceLayersWithIdentifiers(
        sourceLayerIdentifiers = sourceLayerIds,
        predicate =
          predicate
            .takeUnless { it == const(true) }
            ?.compile(ExpressionContext.None)
            ?.toNSPredicate(),
      )
      .map { (it as MLNFeatureProtocol).toFeature() }
  }
}
