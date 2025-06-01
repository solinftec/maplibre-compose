package org.maplibre.compose.core.util

import androidx.compose.runtime.Composable

public expect object PlatformUtils {
  @Composable public fun getSystemRefreshRate(): Float
}
