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

internal actual class CircleLayer actual constructor(id: String, source: Source) :
  FeatureLayer(source) {
  override val impl = TODO()

  actual override var sourceLayer: String = TODO()

  actual override fun setFilter(filter: CompiledExpression<BooleanValue>) {
    TODO()
  }

  actual fun setCircleSortKey(sortKey: CompiledExpression<FloatValue>) {
    TODO()
  }

  actual fun setCircleRadius(radius: CompiledExpression<DpValue>) {
    TODO()
  }

  actual fun setCircleColor(color: CompiledExpression<ColorValue>) {
    TODO()
  }

  actual fun setCircleBlur(blur: CompiledExpression<FloatValue>) {
    TODO()
  }

  actual fun setCircleOpacity(opacity: CompiledExpression<FloatValue>) {
    TODO()
  }

  actual fun setCircleTranslate(translate: CompiledExpression<DpOffsetValue>) {
    TODO()
  }

  actual fun setCircleTranslateAnchor(translateAnchor: CompiledExpression<TranslateAnchor>) {
    TODO()
  }

  actual fun setCirclePitchScale(pitchScale: CompiledExpression<CirclePitchScale>) {
    TODO()
  }

  actual fun setCirclePitchAlignment(pitchAlignment: CompiledExpression<CirclePitchAlignment>) {
    TODO()
  }

  actual fun setCircleStrokeWidth(strokeWidth: CompiledExpression<DpValue>) {
    TODO()
  }

  actual fun setCircleStrokeColor(strokeColor: CompiledExpression<ColorValue>) {
    TODO()
  }

  actual fun setCircleStrokeOpacity(strokeOpacity: CompiledExpression<FloatValue>) {
    TODO()
  }
}
