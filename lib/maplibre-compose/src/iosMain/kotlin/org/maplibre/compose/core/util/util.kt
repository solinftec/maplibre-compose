package org.maplibre.compose.core.util

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.unit.*
import cocoapods.MapLibre.*
import io.github.dellisd.spatialk.geojson.BoundingBox
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.GeoJson
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.cinterop.CValue
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.serialization.json.*
import org.jetbrains.skia.Image
import org.maplibre.compose.expressions.ast.*
import org.maplibre.compose.expressions.value.BooleanValue
import platform.CoreGraphics.*
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.*
import platform.UIKit.*
import platform.posix.memcpy

internal fun ByteArray.toNSData(): NSData {
  return if (isEmpty()) NSData()
  else usePinned { NSData.dataWithBytes(it.addressOf(0), it.get().size.toULong()) }
}

internal fun NSData.toByteArray(): ByteArray {
  return if (length.toInt() == 0) ByteArray(0)
  else ByteArray(length.toInt()).apply { usePinned { memcpy(it.addressOf(0), bytes, length) } }
}

internal fun MLNFeatureProtocol.toFeature(): Feature {
  return Feature.fromJson(JsonElement.convert(geoJSONDictionary()) as JsonObject)
}

internal fun JsonElement.Companion.convert(any: Any?): JsonElement {
  return when (any) {
    null -> JsonNull
    is Boolean -> JsonPrimitive(any)
    is Number -> JsonPrimitive(any)
    is String -> JsonPrimitive(any)
    is List<*> -> JsonArray(any.map { convert(it) })
    is Map<*, *> -> JsonObject(any.entries.associate { (k, v) -> k as String to convert(v) })
    else -> error("Unsupported type: ${any::class.simpleName}")
  }
}

internal fun CValue<CGPoint>.toDpOffset(): DpOffset = useContents { DpOffset(x = x.dp, y = y.dp) }

internal fun DpOffset.toCGPoint(): CValue<CGPoint> =
  CGPointMake(x = x.value.toDouble(), y = y.value.toDouble())

internal fun DpRect.toCGRect(): CValue<CGRect> =
  CGRectMake(
    x = left.value.toDouble(),
    y = top.value.toDouble(),
    width = (right - left).value.toDouble(),
    height = (bottom - top).value.toDouble(),
  )

internal fun CValue<CLLocationCoordinate2D>.toPosition(): Position = useContents { toPosition() }

internal fun CLLocationCoordinate2D.toPosition(): Position =
  Position(longitude = longitude, latitude = latitude)

internal fun Position.toCLLocationCoordinate2D(): CValue<CLLocationCoordinate2D> =
  CLLocationCoordinate2DMake(latitude = latitude, longitude = longitude)

internal fun CValue<MLNCoordinateBounds>.toBoundingBox(): BoundingBox = useContents {
  BoundingBox(northeast = ne.toPosition(), southwest = sw.toPosition())
}

internal fun BoundingBox.toMLNCoordinateBounds(): CValue<MLNCoordinateBounds> =
  MLNCoordinateBoundsMake(
    ne = northeast.toCLLocationCoordinate2D(),
    sw = southwest.toCLLocationCoordinate2D(),
  )

internal fun GeoJson.toMLNShape(): MLNShape {
  return MLNShape.shapeWithData(
    data = json().encodeToByteArray().toNSData(),
    encoding = NSUTF8StringEncoding,
    error = null,
  )!!
}

internal fun String.toMLNShape(): MLNShape {
  return MLNShape.shapeWithData(
    data = encodeToByteArray().toNSData(),
    encoding = NSUTF8StringEncoding,
    error = null,
  )!!
}

internal fun CompiledExpression<*>.toNSExpression(): NSExpression =
  if (this == NullLiteral) NSExpression.expressionForConstantValue(null)
  else NSExpression.expressionWithMLNJSONObject(normalizeJsonLike(false)!!)

internal fun CompiledExpression<BooleanValue>.toNSPredicate(): NSPredicate? =
  if (this == NullLiteral) null
  else NSPredicate.predicateWithMLNJSONObject(normalizeJsonLike(false)!!)

private fun buildLiteralList(inLiteral: Boolean, block: MutableList<Any?>.() -> Unit): List<Any?> {
  return if (inLiteral) {
    buildList { block() }
  } else {
    buildList {
      add("literal")
      add(buildList { block() })
    }
  }
}

private fun buildLiteralMap(
  inLiteral: Boolean,
  block: MutableMap<String, Any?>.() -> Unit,
): Map<String, *> {
  return if (inLiteral) {
    buildMap { block() }
  } else {
    buildMap { put("literal", buildMap { block() }) }
  }
}

private fun CompiledExpression<*>.normalizeJsonLike(inLiteral: Boolean): Any? =
  when (this) {
    NullLiteral -> null
    is BooleanLiteral -> value
    is FloatLiteral -> value
    is StringLiteral -> value
    is OffsetLiteral ->
      NSValue.valueWithCGVector(CGVectorMake(value.x.toDouble(), value.y.toDouble()))

    is ColorLiteral ->
      UIColor.colorWithRed(
        red = value.red.toDouble(),
        green = value.green.toDouble(),
        blue = value.blue.toDouble(),
        alpha = value.alpha.toDouble(),
      )

    is DpPaddingLiteral ->
      NSValue.valueWithUIEdgeInsets(
        UIEdgeInsetsMake(
          top = value.calculateTopPadding().value.toDouble(),
          left = value.calculateLeftPadding(LayoutDirection.Ltr).value.toDouble(),
          bottom = value.calculateBottomPadding().value.toDouble(),
          right = value.calculateRightPadding(LayoutDirection.Ltr).value.toDouble(),
        )
      )

    is CompiledFunctionCall ->
      buildList {
        add(name)
        args.forEachIndexed { i, v -> add(v.normalizeJsonLike(inLiteral || isLiteralArg(i))) }
      }

    is CompiledListLiteral<*> ->
      buildLiteralList(inLiteral) { value.forEach { add(it.normalizeJsonLike(true)) } }

    is CompiledMapLiteral<*> ->
      buildLiteralMap(inLiteral) { value.forEach { (k, v) -> put(k, v.normalizeJsonLike(true)) } }

    is CompiledOptions<*> ->
      buildMap { value.forEach { (k, v) -> put(k, v.normalizeJsonLike(inLiteral)) } }
  }

internal fun Alignment.toMLNOrnamentPosition(layoutDir: LayoutDirection): MLNOrnamentPosition {
  return when (align(IntSize(1, 1), IntSize(2, 2), layoutDir)) {
    IntOffset(0, 0) -> MLNOrnamentPositionTopLeft
    IntOffset(1, 0) -> MLNOrnamentPositionTopRight
    IntOffset(0, 1) -> MLNOrnamentPositionBottomLeft
    IntOffset(1, 1) -> MLNOrnamentPositionBottomRight
    else -> error("Invalid alignment")
  }
}

internal fun ImageBitmap.toUIImage(scale: Float, sdf: Boolean) =
  UIImage(
      data = Image.makeFromBitmap(this.asSkiaBitmap()).encodeToData()!!.bytes.toNSData(),
      scale = scale.toDouble(),
    )
    .imageWithRenderingMode(
      if (sdf) UIImageRenderingMode.UIImageRenderingModeAlwaysTemplate
      else UIImageRenderingMode.UIImageRenderingModeAutomatic
    )
