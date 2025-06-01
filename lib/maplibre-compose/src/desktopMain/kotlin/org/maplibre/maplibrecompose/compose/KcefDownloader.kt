package org.maplibre.maplibrecompose.compose

import androidx.compose.runtime.*
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.maplibre.maplibrecompose.core.CustomCefAppHandler
import java.io.File

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
