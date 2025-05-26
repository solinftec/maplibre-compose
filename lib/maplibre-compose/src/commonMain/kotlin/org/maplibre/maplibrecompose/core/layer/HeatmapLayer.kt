package org.maplibre.maplibrecompose.core.layer

import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.expressions.ast.CompiledExpression
import org.maplibre.maplibrecompose.expressions.value.BooleanValue
import org.maplibre.maplibrecompose.expressions.value.ColorValue
import org.maplibre.maplibrecompose.expressions.value.DpValue
import org.maplibre.maplibrecompose.expressions.value.FloatValue

internal expect class HeatmapLayer(id: String, source: Source) : FeatureLayer {
  override var sourceLayer: String

  override fun setFilter(filter: CompiledExpression<BooleanValue>)

  fun setHeatmapRadius(radius: CompiledExpression<DpValue>)

  fun setHeatmapWeight(weight: CompiledExpression<FloatValue>)

  fun setHeatmapIntensity(intensity: CompiledExpression<FloatValue>)

  fun setHeatmapColor(color: CompiledExpression<ColorValue>)

  fun setHeatmapOpacity(opacity: CompiledExpression<FloatValue>)
}
