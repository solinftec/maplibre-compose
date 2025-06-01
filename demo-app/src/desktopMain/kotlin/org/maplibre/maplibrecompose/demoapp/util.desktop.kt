package org.maplibre.maplibrecompose.demoapp

import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.NotSupportedLocator

actual fun getGeolocator() = Geolocator(NotSupportedLocator)
