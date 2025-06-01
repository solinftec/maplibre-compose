package org.maplibre.maplibrecompose.demoapp.demos

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.TrackingStatus
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.vectorResource
import org.maplibre.maplibrecompose.compose.CameraState
import org.maplibre.maplibrecompose.compose.MaplibreMap
import org.maplibre.maplibrecompose.compose.rememberCameraState
import org.maplibre.maplibrecompose.compose.rememberStyleState
import org.maplibre.maplibrecompose.compose.source.rememberGeoJsonSource
import org.maplibre.maplibrecompose.core.CameraMoveReason
import org.maplibre.maplibrecompose.demoapp.*
import org.maplibre.maplibrecompose.demoapp.generated.Res
import org.maplibre.maplibrecompose.demoapp.generated.location_searching
import org.maplibre.maplibrecompose.demoapp.generated.my_location_filled
import org.maplibre.maplibrecompose.demoapp.generated.navigation_filled
import org.maplibre.maplibrecompose.demoapp.util.LocationPuckLayers
import org.maplibre.maplibrecompose.material3.controls.PointerPinButton
import kotlin.math.max
import kotlin.time.Duration.Companion.seconds

object UserLocationDemo : Demo {
  override val name = "User location"
  override val description =
    "Display and track the user's location using the Compass Geolocation library."

  @Composable
  override fun Component(navigateUp: () -> Unit) {
    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()

    val locationStatus by rememberLocationStatusState()
    val location by derivedStateOf { locationStatus.toLocationOrNull() }

    var mode by remember { mutableStateOf(LocationTrackingMode.Free) }

    MapGestureEffect(cameraState) { mode = LocationTrackingMode.Free }

    // disengage automatically if lacking data
    LaunchedEffect(location, mode) {
      val location = location
      when {
        location == null -> mode = LocationTrackingMode.Free
        location.azimuth == null && mode == LocationTrackingMode.FollowPositionAndBearing ->
          mode = LocationTrackingMode.FollowPosition
      }
    }

    // follow location
    LaunchedEffect(mode, location, cameraState) {
      val location = location
      if (location == null || mode == LocationTrackingMode.Free) return@LaunchedEffect
      val azimuth = location.azimuth

      val target =
        cameraState.position.copy(
          target = location.position,
          zoom = max(cameraState.position.zoom, 12.0),
          bearing =
            // TODO azimuth doesn't seem to work very well for bearing
            // perhaps we should look into other device sensors for bearing?
            if (mode == LocationTrackingMode.FollowPositionAndBearing && azimuth != null)
              azimuth.degrees.toDouble()
            else cameraState.position.bearing,
        )

      cameraState.animateTo(target)
    }

    DemoScaffold(this, navigateUp) {
      Column {
        Box(modifier = Modifier.weight(1f)) {
          MaplibreMap(
            styleUri = DEFAULT_STYLE,
            cameraState = cameraState,
            styleState = styleState,
            ornamentSettings = DemoOrnamentSettings(),
          ) {
            location?.let { LocationPuck(it) }
          }

          location?.let {
            PointerPinButton(
              cameraState,
              it.position,
              onClick = {
                if (mode == LocationTrackingMode.Free) mode = LocationTrackingMode.FollowPosition
              },
            ) {
              Text("📍", fontSize = 28.sp)
            }
          }

          DemoMapControls(
            cameraState,
            styleState,
            onCompassClick = {
              if (mode == LocationTrackingMode.FollowPositionAndBearing)
                mode = LocationTrackingMode.FollowPosition
            },
          ) {
            ElevatedButton(
              onClick = { mode = mode.nextMode },
              shape = CircleShape,
              modifier = Modifier.requiredSize(48.dp).aspectRatio(1f),
              contentPadding = PaddingValues(8.dp),
            ) {
              AnimatedContent(mode) { mode ->
                when (mode) {
                  LocationTrackingMode.Free ->
                    Icon(
                      vectorResource(Res.drawable.location_searching),
                      contentDescription = "Free camera",
                    )
                  LocationTrackingMode.FollowPosition ->
                    Icon(
                      vectorResource(Res.drawable.my_location_filled),
                      contentDescription = "Camera following position",
                    )
                  LocationTrackingMode.FollowPositionAndBearing ->
                    Icon(
                      vectorResource(Res.drawable.navigation_filled),
                      contentDescription = "Camera following position and bearing",
                    )
                }
              }
            }
          }
        }
      }
    }
  }
}

enum class LocationTrackingMode {
  Free,
  FollowPosition,
  FollowPositionAndBearing;

  val nextMode
    get() = entries[(ordinal + 1) % entries.size]
}

@Composable
private fun MapGestureEffect(cameraState: CameraState, block: CoroutineScope.() -> Unit) {
  LaunchedEffect(cameraState.isCameraMoving, cameraState.moveReason) {
    if (cameraState.isCameraMoving && cameraState.moveReason == CameraMoveReason.GESTURE) block()
  }
}

@Composable
private fun rememberLocationStatusState(): State<TrackingStatus> {
  return remember { getGeolocator() }
    .track(
      LocationRequest(priority = Priority.HighAccuracy, interval = 0.2.seconds.inWholeMilliseconds)
    )
    .collectAsState(TrackingStatus.Idle)
}

@Composable
private fun LocationPuck(location: Location) {
  val locationSource = rememberGeoJsonSource(id = "location", data = Point(location.position))
  LocationPuckLayers(idPrefix = "user-location", locationSource = locationSource)
}

private fun TrackingStatus.toLocationOrNull(): Location? {
  return when (this) {
    is TrackingStatus.Update -> location
    else -> null
  }
}

private val Location.position
  get() = Position(latitude = coordinates.latitude, longitude = coordinates.longitude)
