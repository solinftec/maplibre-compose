package org.maplibre.compose.compose

import MapLibre.MLNMapView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import co.touchlab.kermit.Logger
import org.maplibre.compose.core.IosMap
import org.maplibre.compose.core.MapOptions
import org.maplibre.compose.core.MaplibreMap
import org.maplibre.compose.core.SafeStyle
import org.maplibre.compose.core.util.afterConsuming
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSURL

@Composable
internal actual fun ComposableMapView(
  modifier: Modifier,
  styleUri: String,
  rememberedStyle: SafeStyle?,
  update: (map: MaplibreMap) -> Unit,
  onReset: () -> Unit,
  logger: Logger?,
  callbacks: MaplibreMap.Callbacks,
  options: MapOptions,
) {
  IosMapView(
    modifier = modifier,
    styleUri = styleUri,
    update = update,
    onReset = onReset,
    logger = logger,
    callbacks = callbacks,
  )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun IosMapView(
  modifier: Modifier,
  styleUri: String,
  update: (map: MaplibreMap) -> Unit,
  onReset: () -> Unit,
  logger: Logger?,
  callbacks: MaplibreMap.Callbacks,
) {
  var consumedInsets by remember { mutableStateOf(WindowInsets(0, 0, 0, 0)) }
  val insetPadding = WindowInsets.safeDrawing.afterConsuming(consumedInsets).asPaddingValues()
  MeasuredBox(
    modifier = modifier.fillMaxSize().onConsumedWindowInsetsChanged { consumedInsets = it }
  ) { x, y, width, height ->
    val layoutDir = LocalLayoutDirection.current
    val density = LocalDensity.current
    val currentOnReset by rememberUpdatedState(onReset)
    var currentMap by remember { mutableStateOf<IosMap?>(null) }

    UIKitView(
      modifier = modifier.fillMaxSize(),
      properties =
        UIKitInteropProperties(interactionMode = UIKitInteropInteractionMode.NonCooperative),
      factory = {
        MLNMapView(
            frame =
              CGRectMake(
                x = x.value.toDouble(),
                y = y.value.toDouble(),
                width = width.value.toDouble(),
                height = height.value.toDouble(),
              ),
            styleURL = NSURL(string = styleUri),
          )
          .also { mapView ->
            currentMap =
              IosMap(
                mapView = mapView,
                size = CGSizeMake(width.value.toDouble(), height.value.toDouble()),
                layoutDir = layoutDir,
                density = density,
                insetPadding = insetPadding,
                callbacks = callbacks,
                logger = logger,
              )
          }
      },
      update = { _ ->
        val map = currentMap ?: return@UIKitView
        map.size = CGSizeMake(width.value.toDouble(), height.value.toDouble())
        map.layoutDir = layoutDir
        map.density = density
        map.insetPadding = insetPadding
        map.callbacks = callbacks
        map.logger = logger
        map.setStyleUri(styleUri)
        update(map)
      },
      onReset = {
        currentOnReset()
        currentMap = null
      },
    )
  }
}
