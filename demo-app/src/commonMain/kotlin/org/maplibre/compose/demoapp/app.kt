package org.maplibre.compose.demoapp

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.vectorResource
import org.maplibre.compose.compose.CameraState
import org.maplibre.compose.compose.StyleState
import org.maplibre.compose.core.MapOptions
import org.maplibre.compose.core.OrnamentOptions
import org.maplibre.compose.demoapp.demos.*
import org.maplibre.compose.demoapp.generated.Res
import org.maplibre.compose.demoapp.generated.arrow_back
import org.maplibre.compose.demoapp.generated.info
import org.maplibre.compose.material3.controls.DisappearingCompassButton
import org.maplibre.compose.material3.controls.DisappearingScaleBar
import org.maplibre.compose.material3.controls.ExpandingAttributionButton
import org.maplibre.compose.material3.controls.ScaleBarMeasures

private val DEMOS = buildList {
  add(StyleSwitcherDemo)
  if (Platform.supportsBlending) add(EdgeToEdgeDemo)
  if (Platform.supportsLayers) {
    add(UserLocationDemo)
    add(MarkersDemo)
    add(ClusteredPointsDemo)
    add(AnimatedLayerDemo)
    add(LocalTilesDemo)
  }
  if (!Platform.isDesktop) add(CameraStateDemo)
  if (Platform.usesMaplibreNative) add(CameraFollowDemo)
  if (!Platform.isDesktop) add(FrameRateDemo)
  addAll(platformDemos)
}

@Composable
fun DemoApp(navController: NavHostController = rememberNavController()) {
  MaterialTheme(colorScheme = getDefaultColorScheme()) {
    NavHost(
      navController = navController,
      startDestination = "start",
      enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
      exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
      popEnterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End)
      },
      popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    ) {
      composable("start") { DemoMenu { demo -> navController.navigate(demo.name) } }
      DEMOS.forEach { demo ->
        composable(demo.name) { demo.Component { navController.popBackStack() } }
      }
    }
  }
}

@Composable
private fun DemoMenu(navigate: (demo: Demo) -> Unit) {
  Scaffold(topBar = { TopAppBar(title = { Text("MapLibre Compose Demos") }) }) { padding ->
    Column(
      modifier =
        Modifier.consumeWindowInsets(padding).padding(padding).verticalScroll(rememberScrollState())
    ) {
      DEMOS.forEach { demo ->
        Column {
          ListItem(
            modifier = Modifier.clickable(onClick = { navigate(demo) }),
            headlineContent = { Text(text = demo.name) },
            supportingContent = { Text(text = demo.description) },
          )
          HorizontalDivider()
        }
      }
    }
  }
}

@Composable
fun DemoAppBar(demo: Demo, navigateUp: () -> Unit, alpha: Float = 1f) {
  var showInfo by remember { mutableStateOf(false) }

  TopAppBar(
    colors =
      TopAppBarDefaults.mediumTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)
      ),
    title = { Text(demo.name) },
    navigationIcon = {
      IconButton(onClick = navigateUp) {
        Icon(imageVector = vectorResource(Res.drawable.arrow_back), contentDescription = "Back")
      }
    },
    actions = {
      if (Platform.supportsBlending) {
        IconButton(onClick = { showInfo = true }) {
          Icon(imageVector = vectorResource(Res.drawable.info), contentDescription = "Info")
        }
      }
    },
  )

  if (showInfo) {
    AlertDialog(
      onDismissRequest = { showInfo = false },
      title = { Text(text = demo.name) },
      text = { Text(text = demo.description) },
      confirmButton = { TextButton(onClick = { showInfo = false }) { Text("OK") } },
    )
  }
}

@Composable
fun DemoScaffold(demo: Demo, navigateUp: () -> Unit, content: @Composable () -> Unit) {
  Scaffold(topBar = { DemoAppBar(demo, navigateUp) }) { padding ->
    Box(modifier = Modifier.consumeWindowInsets(padding).padding(padding).safeDrawingPadding()) {
      content()
    }
  }
}

@Composable
fun DemoMapControls(
  cameraState: CameraState,
  styleState: StyleState,
  modifier: Modifier = Modifier,
  onCompassClick: () -> Unit = {},
  scaleBarMeasures: ScaleBarMeasures =
    _root_ide_package_.org.maplibre.compose.material3.util.defaultScaleBarMeasures(),
  extraButtons: @Composable ColumnScope.() -> Unit = {},
) {
  if (Platform.supportsBlending) {
    Box(modifier = modifier.fillMaxSize().padding(8.dp)) {
      DisappearingScaleBar(
        metersPerDp = cameraState.metersPerDpAtTarget,
        zoom = cameraState.position.zoom,
        modifier = Modifier.align(Alignment.TopStart),
        measures = scaleBarMeasures,
      )
      Column(
        modifier = Modifier.align(Alignment.TopEnd),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        extraButtons()
        DisappearingCompassButton(cameraState, onClick = onCompassClick)
      }
      ExpandingAttributionButton(
        cameraState = cameraState,
        styleState = styleState,
        modifier = Modifier.align(Alignment.BottomEnd),
        contentAlignment = Alignment.BottomEnd,
      )
    }
  }
}

fun DemoMapOptions(padding: PaddingValues = PaddingValues(0.dp)): MapOptions {
  return if (Platform.supportsBlending) {
    MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo.withPadding(padding))
  } else {
    MapOptions(ornamentOptions = OrnamentOptions.AllEnabled.withPadding(padding))
  }
}
