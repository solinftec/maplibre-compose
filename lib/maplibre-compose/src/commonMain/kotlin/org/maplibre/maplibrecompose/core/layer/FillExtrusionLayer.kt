package org.maplibre.maplibrecompose.core.layer

import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.expressions.ast.CompiledExpression
import org.maplibre.maplibrecompose.expressions.value.BooleanValue
import org.maplibre.maplibrecompose.expressions.value.ColorValue
import org.maplibre.maplibrecompose.expressions.value.DpOffsetValue
import org.maplibre.maplibrecompose.expressions.value.FloatValue
import org.maplibre.maplibrecompose.expressions.value.ImageValue
import org.maplibre.maplibrecompose.expressions.value.TranslateAnchor

internal expect class FillExtrusionLayer(id: String, source: Source) : FeatureLayer {
  override var sourceLayer: String

  override fun setFilter(filter: CompiledExpression<BooleanValue>)

  fun setFillExtrusionOpacity(opacity: CompiledExpression<FloatValue>)

  fun setFillExtrusionColor(color: CompiledExpression<ColorValue>)

  fun setFillExtrusionTranslate(translate: CompiledExpression<DpOffsetValue>)

  fun setFillExtrusionTranslateAnchor(anchor: CompiledExpression<TranslateAnchor>)

  fun setFillExtrusionPattern(pattern: CompiledExpression<ImageValue>)

  fun setFillExtrusionHeight(height: CompiledExpression<FloatValue>)

  fun setFillExtrusionBase(base: CompiledExpression<FloatValue>)

  fun setFillExtrusionVerticalGradient(verticalGradient: CompiledExpression<BooleanValue>)
}
