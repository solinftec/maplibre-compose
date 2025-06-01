package org.maplibre.compose.compose

import androidx.compose.runtime.*
import dev.datlag.kcef.KCEF
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.maplibre.compose.core.CustomCefAppHandler

@Composable
public fun KcefProvider(loading: @Composable () -> Unit = {}, content: @Composable () -> Unit) {
  var initialized by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    withContext(Dispatchers.IO) {
      KCEF.init({
        // TODO https://github.com/harawata/appdirs
        installDir(File("kcef-bundle"))
        appHandler(CustomCefAppHandler)
      })
    }
    initialized = true
  }

  if (initialized) content() else loading()

  DisposableEffect(Unit) { onDispose { KCEF.disposeBlocking() } }
}
