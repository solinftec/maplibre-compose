package org.maplibre.composehtmlinterop

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateObserver

@Composable
internal fun <T : Any> SnapshotEffect(target: T, effect: (T) -> Unit) {
  val observer = remember { SnapshotStateObserver { it() } }
  val currentTarget by rememberUpdatedState(target)
  val currentEffect by rememberUpdatedState(effect)
  DisposableEffect(observer) {
    observer.start()
    observer.observeReads(Unit, { currentEffect(currentTarget) }) { currentEffect(currentTarget) }
    onDispose {
      observer.stop()
      observer.clear()
    }
  }
}
