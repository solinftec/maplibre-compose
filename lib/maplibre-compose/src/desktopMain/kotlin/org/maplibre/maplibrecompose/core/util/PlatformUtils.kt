package org.maplibre.maplibrecompose.core.util

import androidx.compose.runtime.Composable
import org.maplibre.maplibrecompose.compose.LocalMaplibreContext

public actual object PlatformUtils {
  @Composable
  public actual fun getSystemRefreshRate(): Float =
    LocalMaplibreContext.current.refreshRate.toFloat()
}
