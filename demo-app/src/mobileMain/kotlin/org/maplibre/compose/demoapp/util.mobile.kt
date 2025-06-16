package org.maplibre.compose.demoapp

import androidx.compose.foundation.layout.PaddingValues
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile
import org.maplibre.compose.core.OrnamentOptions

actual fun getGeolocator() = Geolocator.mobile()

actual fun OrnamentOptions.withPadding(padding: PaddingValues) = copy(padding = padding)
