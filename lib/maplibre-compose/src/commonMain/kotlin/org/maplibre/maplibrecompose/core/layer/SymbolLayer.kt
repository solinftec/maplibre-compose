package org.maplibre.maplibrecompose.core.layer

import org.maplibre.maplibrecompose.core.source.Source
import org.maplibre.maplibrecompose.expressions.ast.CompiledExpression
import org.maplibre.maplibrecompose.expressions.value.BooleanValue
import org.maplibre.maplibrecompose.expressions.value.ColorValue
import org.maplibre.maplibrecompose.expressions.value.DpOffsetValue
import org.maplibre.maplibrecompose.expressions.value.DpPaddingValue
import org.maplibre.maplibrecompose.expressions.value.DpValue
import org.maplibre.maplibrecompose.expressions.value.FloatOffsetValue
import org.maplibre.maplibrecompose.expressions.value.FloatValue
import org.maplibre.maplibrecompose.expressions.value.FormattedValue
import org.maplibre.maplibrecompose.expressions.value.IconPitchAlignment
import org.maplibre.maplibrecompose.expressions.value.IconRotationAlignment
import org.maplibre.maplibrecompose.expressions.value.IconTextFit
import org.maplibre.maplibrecompose.expressions.value.ImageValue
import org.maplibre.maplibrecompose.expressions.value.ListValue
import org.maplibre.maplibrecompose.expressions.value.StringValue
import org.maplibre.maplibrecompose.expressions.value.SymbolAnchor
import org.maplibre.maplibrecompose.expressions.value.SymbolOverlap
import org.maplibre.maplibrecompose.expressions.value.SymbolPlacement
import org.maplibre.maplibrecompose.expressions.value.SymbolZOrder
import org.maplibre.maplibrecompose.expressions.value.TextJustify
import org.maplibre.maplibrecompose.expressions.value.TextPitchAlignment
import org.maplibre.maplibrecompose.expressions.value.TextRotationAlignment
import org.maplibre.maplibrecompose.expressions.value.TextTransform
import org.maplibre.maplibrecompose.expressions.value.TextVariableAnchorOffsetValue
import org.maplibre.maplibrecompose.expressions.value.TextWritingMode
import org.maplibre.maplibrecompose.expressions.value.TranslateAnchor

internal expect class SymbolLayer(id: String, source: Source) : FeatureLayer {
  override var sourceLayer: String

  override fun setFilter(filter: CompiledExpression<BooleanValue>)

  fun setSymbolPlacement(placement: CompiledExpression<SymbolPlacement>)

  fun setSymbolSpacing(spacing: CompiledExpression<DpValue>)

  fun setSymbolAvoidEdges(avoidEdges: CompiledExpression<BooleanValue>)

  fun setSymbolSortKey(sortKey: CompiledExpression<FloatValue>)

  fun setSymbolZOrder(zOrder: CompiledExpression<SymbolZOrder>)

  fun setIconAllowOverlap(allowOverlap: CompiledExpression<BooleanValue>)

  fun setIconOverlap(overlap: CompiledExpression<StringValue>)

  fun setIconIgnorePlacement(ignorePlacement: CompiledExpression<BooleanValue>)

  fun setIconOptional(optional: CompiledExpression<BooleanValue>)

  fun setIconRotationAlignment(rotationAlignment: CompiledExpression<IconRotationAlignment>)

  fun setIconSize(size: CompiledExpression<FloatValue>)

  fun setIconTextFit(textFit: CompiledExpression<IconTextFit>)

  fun setIconTextFitPadding(textFitPadding: CompiledExpression<DpPaddingValue>)

  fun setIconImage(image: CompiledExpression<ImageValue>)

  fun setIconRotate(rotate: CompiledExpression<FloatValue>)

  fun setIconPadding(padding: CompiledExpression<DpValue>)

  fun setIconKeepUpright(keepUpright: CompiledExpression<BooleanValue>)

  fun setIconOffset(offset: CompiledExpression<DpOffsetValue>)

  fun setIconAnchor(anchor: CompiledExpression<SymbolAnchor>)

  fun setIconPitchAlignment(pitchAlignment: CompiledExpression<IconPitchAlignment>)

  fun setIconOpacity(opacity: CompiledExpression<FloatValue>)

  fun setIconColor(color: CompiledExpression<ColorValue>)

  fun setIconHaloColor(haloColor: CompiledExpression<ColorValue>)

  fun setIconHaloWidth(haloWidth: CompiledExpression<DpValue>)

  fun setIconHaloBlur(haloBlur: CompiledExpression<DpValue>)

  fun setIconTranslate(translate: CompiledExpression<DpOffsetValue>)

  fun setIconTranslateAnchor(translateAnchor: CompiledExpression<TranslateAnchor>)

  fun setTextPitchAlignment(pitchAlignment: CompiledExpression<TextPitchAlignment>)

  fun setTextRotationAlignment(rotationAlignment: CompiledExpression<TextRotationAlignment>)

  fun setTextField(field: CompiledExpression<FormattedValue>)

  fun setTextFont(font: CompiledExpression<ListValue<StringValue>>)

  fun setTextSize(size: CompiledExpression<DpValue>)

  fun setTextMaxWidth(maxWidth: CompiledExpression<FloatValue>)

  fun setTextLineHeight(lineHeight: CompiledExpression<FloatValue>)

  fun setTextLetterSpacing(letterSpacing: CompiledExpression<FloatValue>)

  fun setTextJustify(justify: CompiledExpression<TextJustify>)

  fun setTextRadialOffset(radialOffset: CompiledExpression<FloatValue>)

  fun setTextVariableAnchor(variableAnchor: CompiledExpression<ListValue<SymbolAnchor>>)

  fun setTextVariableAnchorOffset(
    variableAnchorOffset: CompiledExpression<TextVariableAnchorOffsetValue>
  )

  fun setTextAnchor(anchor: CompiledExpression<SymbolAnchor>)

  fun setTextMaxAngle(maxAngle: CompiledExpression<FloatValue>)

  fun setTextWritingMode(writingMode: CompiledExpression<ListValue<TextWritingMode>>)

  fun setTextRotate(rotate: CompiledExpression<FloatValue>)

  fun setTextPadding(padding: CompiledExpression<DpValue>)

  fun setTextKeepUpright(keepUpright: CompiledExpression<BooleanValue>)

  fun setTextTransform(transform: CompiledExpression<TextTransform>)

  fun setTextOffset(offset: CompiledExpression<FloatOffsetValue>)

  fun setTextAllowOverlap(allowOverlap: CompiledExpression<BooleanValue>)

  fun setTextOverlap(overlap: CompiledExpression<SymbolOverlap>)

  fun setTextIgnorePlacement(ignorePlacement: CompiledExpression<BooleanValue>)

  fun setTextOptional(optional: CompiledExpression<BooleanValue>)

  fun setTextOpacity(opacity: CompiledExpression<FloatValue>)

  fun setTextColor(color: CompiledExpression<ColorValue>)

  fun setTextHaloColor(haloColor: CompiledExpression<ColorValue>)

  fun setTextHaloWidth(haloWidth: CompiledExpression<DpValue>)

  fun setTextHaloBlur(haloBlur: CompiledExpression<DpValue>)

  fun setTextTranslate(translate: CompiledExpression<DpOffsetValue>)

  fun setTextTranslateAnchor(translateAnchor: CompiledExpression<TranslateAnchor>)
}
