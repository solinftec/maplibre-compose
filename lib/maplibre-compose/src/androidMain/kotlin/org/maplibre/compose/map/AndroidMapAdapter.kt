package org.maplibre.compose.map

import android.graphics.PointF
import android.graphics.RectF
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.dellisd.spatialk.geojson.BoundingBox
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.Position
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import org.maplibre.android.camera.CameraPosition as MLNCameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.VisibleRegion as MLNVisibleRegion
import org.maplibre.android.gestures.MoveGestureDetector
import org.maplibre.android.gestures.RotateGestureDetector
import org.maplibre.android.gestures.ShoveGestureDetector
import org.maplibre.android.gestures.StandardScaleGestureDetector
import org.maplibre.android.log.Logger as MLNLogger
import org.maplibre.android.maps.MapLibreMap as MLNMap
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMap.OnCameraMoveStartedListener
import org.maplibre.android.maps.MapLibreMap.OnMoveListener
import org.maplibre.android.maps.MapLibreMap.OnScaleListener
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style as MlnStyle
import org.maplibre.android.style.expressions.Expression as MLNExpression
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.ast.CompiledExpression
import org.maplibre.compose.expressions.value.BooleanValue
import org.maplibre.compose.style.AndroidStyle
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.KermitLoggerDefinition
import org.maplibre.compose.util.VisibleRegion
import org.maplibre.compose.util.correctedAndroidUri
import org.maplibre.compose.util.getSystemRefreshRate
import org.maplibre.compose.util.toBoundingBox
import org.maplibre.compose.util.toGravity
import org.maplibre.compose.util.toLatLng
import org.maplibre.compose.util.toLatLngBounds
import org.maplibre.compose.util.toMLNExpression
import org.maplibre.compose.util.toOffset
import org.maplibre.compose.util.toPointF
import org.maplibre.compose.util.toPosition
import org.maplibre.compose.util.toRectF
import org.maplibre.geojson.Feature as MLNFeature

internal class AndroidMapAdapter(
  private val mapView: MapView,
  private val map: MapLibreMap,
  private val scaleBar: AndroidScaleBar,
  layoutDir: LayoutDirection,
  density: Density,
  internal var callbacks: MapAdapter.Callbacks,
  logger: Logger?,
  baseStyle: BaseStyle,
) : StandardMapAdapter {

  internal var layoutDir: LayoutDirection = layoutDir
    set(value) {
      field = value
      scaleBar.layoutDir = value
      scaleBar.updateLayout()
    }

  internal var density: Density = density
    set(value) {
      field = value
      scaleBar.density = value
      scaleBar.updateLayout()
    }

  internal var logger: Logger? = logger
    set(value) {
      if (value != field) {
        MLNLogger.setLoggerDefinition(KermitLoggerDefinition(value))
        field = value
      }
    }

  private var lastBaseStyle: BaseStyle? = null

  override fun setBaseStyle(style: BaseStyle) {
    if (style == lastBaseStyle) return
    lastBaseStyle = style
    logger?.i { "Setting style URI" }
    callbacks.onStyleChanged(this, null)

    val builder =
      when (style) {
        is BaseStyle.Uri -> MlnStyle.Builder().fromUri(style.uri.correctedAndroidUri())
        is BaseStyle.Json -> MlnStyle.Builder().fromJson(style.json)
      }

    map.setStyle(builder) { style ->
      logger?.i { "Style finished loading" }
      callbacks.onStyleChanged(this, AndroidStyle(style))
    }
  }

  init {
    mapView.addOnDidFinishLoadingMapListener { callbacks.onMapFinishedLoading(this) }
    mapView.addOnDidFailLoadingMapListener { callbacks.onMapFailLoading(it) }

    map.addOnCameraMoveStartedListener { reason ->
      // MapLibre doesn't have docs on these reasons, and even though they're named like Google's:
      // https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnCameraMoveStartedListener#constants
      // they don't quite work the way the Google ones are documented. In particular,
      // REASON_DEVELOPER_ANIMATION is never used, and REASON_API_ANIMATION is used when the
      // animation was from the developer or from the API.
      callbacks.onCameraMoveStarted(
        map = this,
        reason =
          when (reason) {
            OnCameraMoveStartedListener.REASON_API_GESTURE -> CameraMoveReason.GESTURE
            OnCameraMoveStartedListener.REASON_API_ANIMATION -> CameraMoveReason.PROGRAMMATIC
            else -> {
              logger?.w { "Unknown camera move reason: $reason" }
              CameraMoveReason.UNKNOWN
            }
          },
      )
    }
    map.addOnCameraMoveListener { callbacks.onCameraMoved(this) }
    map.addOnCameraIdleListener { callbacks.onCameraMoveEnded(this) }

    // TODO: Support double tap below.
    // This is a bit of a hack since the OnCameraMoveStartedListener above doesn't always fire when
    // gestures are simultaneous with animations.
    map.addOnMoveListener(
      object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
          callbacks.onCameraMoveStarted(this@AndroidMapAdapter, CameraMoveReason.GESTURE)
        }

        override fun onMove(detector: MoveGestureDetector) {}

        override fun onMoveEnd(detector: MoveGestureDetector) {}
      }
    )
    map.addOnScaleListener(
      object : OnScaleListener {
        override fun onScaleBegin(detector: StandardScaleGestureDetector) {
          callbacks.onCameraMoveStarted(this@AndroidMapAdapter, CameraMoveReason.GESTURE)
        }

        override fun onScale(detector: StandardScaleGestureDetector) {}

        override fun onScaleEnd(detector: StandardScaleGestureDetector) {}
      }
    )
    map.addOnShoveListener(
      object : MLNMap.OnShoveListener {
        override fun onShoveBegin(detector: ShoveGestureDetector) {
          callbacks.onCameraMoveStarted(this@AndroidMapAdapter, CameraMoveReason.GESTURE)
        }

        override fun onShove(detector: ShoveGestureDetector) {}

        override fun onShoveEnd(detector: ShoveGestureDetector) {}
      }
    )
    map.addOnRotateListener(
      object : MLNMap.OnRotateListener {
        override fun onRotateBegin(detector: RotateGestureDetector) {
          callbacks.onCameraMoveStarted(this@AndroidMapAdapter, CameraMoveReason.GESTURE)
        }

        override fun onRotate(detector: RotateGestureDetector) {}

        override fun onRotateEnd(detector: RotateGestureDetector) {}
      }
    )

    map.addOnMapClickListener { coords ->
      val pos = coords.toPosition()
      callbacks.onClick(this, pos, screenLocationFromPosition(pos))
      true
    }

    map.addOnMapLongClickListener { coords ->
      val pos = coords.toPosition()
      callbacks.onLongClick(this, pos, screenLocationFromPosition(pos))
      true
    }

    map.setOnFpsChangedListener { fps -> callbacks.onFrame(fps) }

    setBaseStyle(baseStyle)
  }

  override fun setMinPitch(minPitch: Double) {
    map.setMinPitchPreference(minPitch)
  }

  override fun setMaxPitch(maxPitch: Double) {
    map.setMaxPitchPreference(maxPitch)
  }

  override fun setMinZoom(minZoom: Double) {
    map.setMinZoomPreference(minZoom)
  }

  override fun setMaxZoom(maxZoom: Double) {
    map.setMaxZoomPreference(maxZoom)
  }

  private var lastBoundingBox: BoundingBox? = null

  override fun setCameraBoundingBox(boundingBox: BoundingBox?) {
    if (boundingBox == lastBoundingBox) return
    lastBoundingBox = boundingBox
    map.setLatLngBoundsForCameraTarget(boundingBox?.toLatLngBounds())
  }

  override fun getVisibleBoundingBox(): BoundingBox {
    return map.projection.visibleRegion.latLngBounds.toBoundingBox()
  }

  override fun getVisibleRegion(): VisibleRegion {
    return map.projection.visibleRegion.toVisibleRegion()
  }

  override fun setRenderSettings(value: RenderOptions) {
    mapView.setMaximumFps(value.maximumFps ?: getSystemRefreshRate(mapView.context).roundToInt())
    map.isDebugActive = value.isDebugEnabled
  }

  override fun setGestureSettings(value: GestureOptions) {
    map.uiSettings.isRotateGesturesEnabled = value.isRotateEnabled
    map.uiSettings.isScrollGesturesEnabled = value.isScrollEnabled
    map.uiSettings.isTiltGesturesEnabled = value.isTiltEnabled
    map.uiSettings.isZoomGesturesEnabled = value.isZoomEnabled
    map.uiSettings.isQuickZoomGesturesEnabled = value.isQuickZoomEnabled
    map.uiSettings.isDoubleTapGesturesEnabled = value.isDoubleTapEnabled
  }

  override fun setOrnamentSettings(value: OrnamentOptions) {
    map.uiSettings.isLogoEnabled = value.isLogoEnabled
    map.uiSettings.logoGravity = value.logoAlignment.toGravity(layoutDir)

    map.uiSettings.isAttributionEnabled = value.isAttributionEnabled
    map.uiSettings.attributionGravity = value.attributionAlignment.toGravity(layoutDir)

    map.uiSettings.isCompassEnabled = value.isCompassEnabled
    map.uiSettings.compassGravity = value.compassAlignment.toGravity(layoutDir)

    scaleBar.enabled = value.isScaleBarEnabled
    scaleBar.alignment = value.scaleBarAlignment
    scaleBar.padding = value.padding
    scaleBar.updateLayout()

    with(density) {
      val left =
        (value.padding.calculateLeftPadding(layoutDir).coerceAtLeast(0.dp) + 8.dp).roundToPx()
      val top = (value.padding.calculateTopPadding().coerceAtLeast(0.dp) + 8.dp).roundToPx()
      val right =
        (value.padding.calculateRightPadding(layoutDir).coerceAtLeast(0.dp) + 8.dp).roundToPx()
      val bottom = (value.padding.calculateBottomPadding().coerceAtLeast(0.dp) + 8.dp).roundToPx()
      map.uiSettings.setAttributionMargins(left, top, right, bottom)
      map.uiSettings.setLogoMargins(left, top, right, bottom)
      map.uiSettings.setCompassMargins(left, top, right, bottom)
    }
  }

  private fun MLNCameraPosition.toCameraPosition(): CameraPosition =
    with(density) {
      CameraPosition(
        target = target?.toPosition() ?: Position(0.0, 0.0),
        zoom = zoom,
        bearing = bearing,
        tilt = tilt,
        padding =
          padding?.let {
            PaddingValues.Absolute(
              left = it[0].toInt().toDp(),
              top = it[1].toInt().toDp(),
              right = it[2].toInt().toDp(),
              bottom = it[3].toInt().toDp(),
            )
          } ?: PaddingValues.Absolute(0.dp),
      )
    }

  private fun CameraPosition.toMLNCameraPosition(): MLNCameraPosition =
    with(density) {
      MLNCameraPosition.Builder()
        .target(target.toLatLng())
        .zoom(zoom)
        .tilt(tilt)
        .bearing(bearing)
        .padding(
          left = padding.calculateLeftPadding(layoutDir).toPx().toDouble(),
          top = padding.calculateTopPadding().toPx().toDouble(),
          right = padding.calculateRightPadding(layoutDir).toPx().toDouble(),
          bottom = padding.calculateBottomPadding().toPx().toDouble(),
        )
        .build()
    }

  override fun getCameraPosition(): CameraPosition {
    return map.cameraPosition.toCameraPosition()
  }

  override fun setCameraPosition(cameraPosition: CameraPosition) {
    map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.toMLNCameraPosition()))
  }

  private class CancelableCoroutineCallback(private val cont: Continuation<Unit>) :
    MLNMap.CancelableCallback {
    override fun onCancel() = cont.resume(Unit)

    override fun onFinish() = cont.resume(Unit)
  }

  override suspend fun animateCameraPosition(finalPosition: CameraPosition, duration: Duration) =
    suspendCoroutine { cont ->
      map.animateCamera(
        CameraUpdateFactory.newCameraPosition(finalPosition.toMLNCameraPosition()),
        duration.toInt(DurationUnit.MILLISECONDS),
        CancelableCoroutineCallback(cont),
      )
    }

  override suspend fun animateCameraPosition(
    boundingBox: BoundingBox,
    bearing: Double,
    tilt: Double,
    padding: PaddingValues,
    duration: Duration,
  ) = suspendCoroutine { cont ->
    with(density) {
      map.animateCamera(
        CameraUpdateFactory.newLatLngBounds(
          bounds = boundingBox.toLatLngBounds(),
          bearing = bearing,
          tilt = tilt,
          paddingLeft = padding.calculateLeftPadding(layoutDir).roundToPx(),
          paddingTop = padding.calculateTopPadding().roundToPx(),
          paddingRight = padding.calculateRightPadding(layoutDir).roundToPx(),
          paddingBottom = padding.calculateBottomPadding().roundToPx(),
        ),
        duration.toInt(DurationUnit.MILLISECONDS),
        CancelableCoroutineCallback(cont),
      )
    }
  }

  override fun positionFromScreenLocation(offset: DpOffset): Position =
    map.projection.fromScreenLocation(offset.toPointF(density)).toPosition()

  override fun screenLocationFromPosition(position: Position): DpOffset =
    map.projection.toScreenLocation(position.toLatLng()).toOffset(density)

  override fun queryRenderedFeatures(
    offset: DpOffset,
    layerIds: Set<String>?,
    predicate: CompiledExpression<BooleanValue>?,
  ): List<Feature> {
    // Kotlin hack to pass null to a java nullable varargs
    val query: (PointF, MLNExpression?, Array<String>?) -> List<MLNFeature> =
      map::queryRenderedFeatures
    return query(offset.toPointF(density), predicate?.toMLNExpression(), layerIds?.toTypedArray())
      .map { Feature.fromJson(it.toJson()) }
  }

  override fun queryRenderedFeatures(
    rect: DpRect,
    layerIds: Set<String>?,
    predicate: CompiledExpression<BooleanValue>?,
  ): List<Feature> {
    // Kotlin hack to pass null to a java nullable varargs
    val query: (RectF, MLNExpression?, Array<String>?) -> List<MLNFeature> =
      map::queryRenderedFeatures
    return query(rect.toRectF(density), predicate?.toMLNExpression(), layerIds?.toTypedArray())
      .map { Feature.fromJson(it.toJson()) }
  }

  override fun metersPerDpAtLatitude(latitude: Double) =
    map.projection.getMetersPerPixelAtLatitude(latitude)
}

private fun MLNVisibleRegion.toVisibleRegion() =
  VisibleRegion(
    farLeft = farLeft!!.toPosition(),
    farRight = farRight!!.toPosition(),
    nearLeft = nearLeft!!.toPosition(),
    nearRight = nearRight!!.toPosition(),
  )
