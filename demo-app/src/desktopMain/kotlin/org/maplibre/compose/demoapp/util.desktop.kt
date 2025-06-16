package org.maplibre.compose.demoapp

import androidx.compose.foundation.layout.PaddingValues
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.NotSupportedLocator
import org.maplibre.compose.core.OrnamentOptions
import org.maplibre.compose.core.RenderOptions

actual fun getGeolocator() = Geolocator(NotSupportedLocator)

actual fun RenderOptions.withMaxFps(maxFps: Int) = this

actual fun OrnamentOptions.withPadding(padding: PaddingValues) = this
