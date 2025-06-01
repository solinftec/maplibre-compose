package org.maplibre.compose.material3.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.*

/**
 * Given an imaginary line drawn from the center of [area] to [target], returns the point and angle
 * at which the line intersects with an ellipsis filling the [area].
 */
internal fun findEllipsisIntersection(
  area: Rect,
  target: Offset,
): org.maplibre.compose.material3.util.Intersection? {
  val delta = target - area.center
  val theta = atan2(delta.y, delta.x) + PI / 2
  val radius =
    _root_ide_package_.org.maplibre.compose.material3.util.ellipsisRadius(
      area.height / 2.0,
      area.width / 2.0,
      theta,
    )
  val ellipsisDelta = Offset((sin(theta) * radius).toFloat(), (-cos(theta) * radius).toFloat())
  if (delta.getDistanceSquared() < ellipsisDelta.getDistanceSquared()) return null

  return _root_ide_package_.org.maplibre.compose.material3.util.Intersection(
    ellipsisDelta + area.center,
    theta,
  )
}

private fun ellipsisRadius(a: Double, b: Double, angle: Double): Double {
  val x = sin(angle)
  val y = cos(angle)
  return a * b / sqrt(a * a * x * x + b * b * y * y)
}

internal data class Intersection(val position: Offset, val angle: Double)
