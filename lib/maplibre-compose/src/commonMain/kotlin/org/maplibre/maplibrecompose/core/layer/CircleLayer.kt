package org.maplibre.maplibrecompose.core.layer

import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.expressions.ast.CompiledExpression
import org.maplibre.maplibrecompose.expressions.value.BooleanValue
import org.maplibre.maplibrecompose.expressions.value.CirclePitchAlignment
import org.maplibre.maplibrecompose.expressions.value.CirclePitchScale
import org.maplibre.maplibrecompose.expressions.value.ColorValue
import org.maplibre.maplibrecompose.expressions.value.DpOffsetValue
import org.maplibre.maplibrecompose.expressions.value.DpValue
import org.maplibre.maplibrecompose.expressions.value.FloatValue
import org.maplibre.maplibrecompose.expressions.value.TranslateAnchor

internal expect class CircleLayer(id: String, source: Source) : FeatureLayer {
  override var sourceLayer: String

  override fun setFilter(filter: CompiledExpression<BooleanValue>)

  fun setCircleSortKey(sortKey: CompiledExpression<FloatValue>)

  fun setCircleRadius(radius: CompiledExpression<DpValue>)

  fun setCircleColor(color: CompiledExpression<ColorValue>)

  fun setCircleBlur(blur: CompiledExpression<FloatValue>)

  fun setCircleOpacity(opacity: CompiledExpression<FloatValue>)

  fun setCircleTranslate(translate: CompiledExpression<DpOffsetValue>)

  fun setCircleTranslateAnchor(translateAnchor: CompiledExpression<TranslateAnchor>)

  fun setCirclePitchScale(pitchScale: CompiledExpression<CirclePitchScale>)

  fun setCirclePitchAlignment(pitchAlignment: CompiledExpression<CirclePitchAlignment>)

  fun setCircleStrokeWidth(strokeWidth: CompiledExpression<DpValue>)

  fun setCircleStrokeColor(strokeColor: CompiledExpression<ColorValue>)

  fun setCircleStrokeOpacity(strokeOpacity: CompiledExpression<FloatValue>)
}
