package org.maplibre.maplibrecompose.core.layer

import cocoapods.MapLibre.MLNFillExtrusionStyleLayer
import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.core.util.toNSExpression
import org.maplibre.maplibrecompose.core.util.toNSPredicate
import org.maplibre.maplibrecompose.expressions.ast.CompiledExpression
import org.maplibre.maplibrecompose.expressions.ast.NullLiteral
import org.maplibre.maplibrecompose.expressions.value.*

internal actual class FillExtrusionLayer actual constructor(id: String, source: Source) :
  FeatureLayer(source) {

  override val impl = MLNFillExtrusionStyleLayer(id, source.impl)

  actual override var sourceLayer: String
    get() = impl.sourceLayerIdentifier!!
    set(value) {
      impl.sourceLayerIdentifier = value
    }

  actual override fun setFilter(filter: CompiledExpression<BooleanValue>) {
    impl.predicate = filter.toNSPredicate()
  }

  actual fun setFillExtrusionOpacity(opacity: CompiledExpression<FloatValue>) {
    impl.fillExtrusionOpacity = opacity.toNSExpression()
  }

  actual fun setFillExtrusionColor(color: CompiledExpression<ColorValue>) {
    impl.fillExtrusionColor = color.toNSExpression()
  }

  actual fun setFillExtrusionTranslate(translate: CompiledExpression<DpOffsetValue>) {
    impl.fillExtrusionTranslation = translate.toNSExpression()
  }

  actual fun setFillExtrusionTranslateAnchor(anchor: CompiledExpression<TranslateAnchor>) {
    impl.fillExtrusionTranslationAnchor = anchor.toNSExpression()
  }

  actual fun setFillExtrusionPattern(pattern: CompiledExpression<ImageValue>) {
    // TODO figure out how to unset pattern
    if (pattern != NullLiteral) impl.fillExtrusionPattern = pattern.toNSExpression()
  }

  actual fun setFillExtrusionHeight(height: CompiledExpression<FloatValue>) {
    impl.fillExtrusionHeight = height.toNSExpression()
  }

  actual fun setFillExtrusionBase(base: CompiledExpression<FloatValue>) {
    impl.fillExtrusionBase = base.toNSExpression()
  }

  actual fun setFillExtrusionVerticalGradient(verticalGradient: CompiledExpression<BooleanValue>) {
    impl.fillExtrusionHasVerticalGradient = verticalGradient.toNSExpression()
  }
}
