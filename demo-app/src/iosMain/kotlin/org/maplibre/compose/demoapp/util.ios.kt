package org.maplibre.compose.demoapp

import org.maplibre.compose.core.RenderOptions

actual fun RenderOptions.withMaxFps(maxFps: Int) = copy(maximumFps = maxFps)
