package org.maplibre.compose.core.util

import androidx.compose.runtime.Composable
import org.maplibre.compose.compose.LocalMaplibreContext

public actual object PlatformUtils {
  @Composable
  public actual fun getSystemRefreshRate(): Float =
    LocalMaplibreContext.current.refreshRate.toFloat()
}
