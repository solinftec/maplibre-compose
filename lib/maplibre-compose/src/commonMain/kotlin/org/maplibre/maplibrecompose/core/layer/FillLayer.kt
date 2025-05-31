package org.maplibre.maplibrecompose.core.layer

import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.expressions.ast.CompiledExpression
import org.maplibre.maplibrecompose.expressions.value.*

internal expect class FillLayer(id: String, source: Source) : FeatureLayer {
  override var sourceLayer: String

  override fun setFilter(filter: CompiledExpression<BooleanValue>)

  fun setFillSortKey(sortKey: CompiledExpression<FloatValue>)

  fun setFillAntialias(antialias: CompiledExpression<BooleanValue>)

  fun setFillOpacity(opacity: CompiledExpression<FloatValue>)

  fun setFillColor(color: CompiledExpression<ColorValue>)

  fun setFillOutlineColor(outlineColor: CompiledExpression<ColorValue>)

  fun setFillTranslate(translate: CompiledExpression<DpOffsetValue>)

  fun setFillTranslateAnchor(translateAnchor: CompiledExpression<TranslateAnchor>)

  fun setFillPattern(pattern: CompiledExpression<ImageValue>)
}
