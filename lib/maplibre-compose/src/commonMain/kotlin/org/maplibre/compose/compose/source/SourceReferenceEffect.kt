package org.maplibre.compose.compose.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.maplibre.compose.compose.engine.LocalStyleNode
import org.maplibre.compose.core.source.Source

@Composable
internal fun SourceReferenceEffect(source: Source) {
  val node = LocalStyleNode.current
  DisposableEffect(source) {
    node.sourceManager.addReference(source)
    onDispose { node.sourceManager.removeReference(source) }
  }
}
