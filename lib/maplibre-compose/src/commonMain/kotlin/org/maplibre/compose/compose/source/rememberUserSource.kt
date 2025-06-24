package org.maplibre.compose.compose.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import org.maplibre.compose.compose.engine.LocalStyleNode
import org.maplibre.compose.core.source.Source

@Composable
internal fun <T : Source> rememberUserSource(factory: () -> T, update: T.() -> Unit): T {
  val node = LocalStyleNode.current
  val source = remember(node) { factory().also { node.sourceManager.addReference(it) } }
  LaunchedEffect(source, update, node.style.isUnloaded) {
    if (!node.style.isUnloaded) source.update()
  }
  DisposableEffect(node, source) { onDispose { node.sourceManager.removeReference(source) } }
  return source
}
