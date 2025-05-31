package org.maplibre.maplibrecompose.compose.engine

import androidx.compose.runtime.*
import co.touchlab.kermit.Logger
import kotlinx.coroutines.awaitCancellation
import org.maplibre.maplibrecompose.compose.MaplibreComposable
import org.maplibre.maplibrecompose.compose.StyleState
import org.maplibre.maplibrecompose.core.SafeStyle

@Composable
internal fun rememberStyleComposition(
  styleState: StyleState,
  maybeStyle: SafeStyle?,
  logger: Logger?,
  content: @Composable @MaplibreComposable () -> Unit,
): State<StyleNode?> {
  val nodeState = remember { mutableStateOf<StyleNode?>(null) }
  val compositionContext = rememberCompositionContext()

  LaunchedEffect(styleState, maybeStyle, compositionContext) {
    val style = maybeStyle ?: return@LaunchedEffect
    val rootNode = StyleNode(style, logger).also { nodeState.value = it }
    styleState.attach(rootNode)
    val composition = Composition(MapNodeApplier(rootNode), compositionContext)

    composition.setContent {
      CompositionLocalProvider(LocalStyleNode provides rootNode) { content() }
    }

    try {
      awaitCancellation()
    } finally {
      nodeState.value = null
      composition.dispose()
    }
  }

  return nodeState
}

internal val LocalStyleNode = staticCompositionLocalOf<StyleNode> { throw IllegalStateException() }
