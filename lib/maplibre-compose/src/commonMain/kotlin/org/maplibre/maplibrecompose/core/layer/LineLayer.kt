package org.maplibre.maplibrecompose.core.layer

import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.expressions.ast.CompiledExpression
import org.maplibre.maplibrecompose.expressions.value.BooleanValue
import org.maplibre.maplibrecompose.expressions.value.ColorValue
import org.maplibre.maplibrecompose.expressions.value.DpOffsetValue
import org.maplibre.maplibrecompose.expressions.value.DpValue
import org.maplibre.maplibrecompose.expressions.value.FloatValue
import org.maplibre.maplibrecompose.expressions.value.ImageValue
import org.maplibre.maplibrecompose.expressions.value.LineCap
import org.maplibre.maplibrecompose.expressions.value.LineJoin
import org.maplibre.maplibrecompose.expressions.value.TranslateAnchor
import org.maplibre.maplibrecompose.expressions.value.VectorValue

internal expect class LineLayer(id: String, source: Source) : FeatureLayer {
  override var sourceLayer: String

  override fun setFilter(filter: CompiledExpression<BooleanValue>)

  fun setLineCap(cap: CompiledExpression<LineCap>)

  fun setLineJoin(join: CompiledExpression<LineJoin>)

  fun setLineMiterLimit(miterLimit: CompiledExpression<FloatValue>)

  fun setLineRoundLimit(roundLimit: CompiledExpression<FloatValue>)

  fun setLineSortKey(sortKey: CompiledExpression<FloatValue>)

  fun setLineOpacity(opacity: CompiledExpression<FloatValue>)

  fun setLineColor(color: CompiledExpression<ColorValue>)

  fun setLineTranslate(translate: CompiledExpression<DpOffsetValue>)

  fun setLineTranslateAnchor(translateAnchor: CompiledExpression<TranslateAnchor>)

  fun setLineWidth(width: CompiledExpression<DpValue>)

  fun setLineGapWidth(gapWidth: CompiledExpression<DpValue>)

  fun setLineOffset(offset: CompiledExpression<DpValue>)

  fun setLineBlur(blur: CompiledExpression<DpValue>)

  fun setLineDasharray(dasharray: CompiledExpression<VectorValue<Number>>)

  fun setLinePattern(pattern: CompiledExpression<ImageValue>)

  fun setLineGradient(gradient: CompiledExpression<ColorValue>)
}
