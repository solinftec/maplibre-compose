package org.maplibre.maplibrecompose.material3.offline

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.saket.bytesize.binaryBytes
import org.jetbrains.compose.resources.vectorResource
import org.maplibre.maplibrecompose.compose.offline.OfflinePack
import org.maplibre.maplibrecompose.compose.offline.rememberOfflineManager
import org.maplibre.maplibrecompose.material3.generated.*

/**
 * A [ListItem] to manage an [org.maplibre.maplibrecompose.compose.offline.OfflinePack].
 *
 * By default, it includes controls to pause, resume, invalidate, and delete the pack, and a
 * [CircularProgressIndicator] for download progress.
 *
 * You must supply a [headlineContent] for the list item. Typically, this will be a suitable name
 * for the pack, parsed from [org.maplibre.maplibrecompose.compose.offline.OfflinePack.metadata].
 *
 * You can customize each part of the [ListItem] by supplying alternate [leadingContent],
 * [supportingContent], and [trailingContent].
 */
@Composable
public fun OfflinePackListItem(
  pack: OfflinePack,
  modifier: Modifier = Modifier,
  offlineManager: org.maplibre.maplibrecompose.compose.offline.OfflineManager =
    rememberOfflineManager(),
  leadingContent: @Composable () -> Unit = {
    _root_ide_package_.org.maplibre.maplibrecompose.material3.offline.OfflinePackListItemDefaults
      .LeadingContent(pack, offlineManager)
  },
  supportingContent: @Composable () -> Unit = {
    _root_ide_package_.org.maplibre.maplibrecompose.material3.offline.OfflinePackListItemDefaults
      .SupportingContent(pack.downloadProgress)
  },
  trailingContent: @Composable () -> Unit = {
    _root_ide_package_.org.maplibre.maplibrecompose.material3.offline.OfflinePackListItemDefaults
      .TrailingContent(pack, offlineManager)
  },
  headlineContent: @Composable () -> Unit,
) {
  // TODO swipe to delete? confirmation to delete?
  ListItem(
    modifier = modifier,
    leadingContent = leadingContent,
    headlineContent = headlineContent,
    supportingContent = supportingContent,
    trailingContent = trailingContent,
  )
}

public object OfflinePackListItemDefaults {
  /**
   * The default leading content for an
   * [org.maplibre.maplibrecompose.material3.offline.OfflinePackListItem]. It includes a
   * [CircularProgressIndicator] for in-progress downloads, and otherwise an [Icon] representing the
   * status of the pack.
   */
  @Composable
  public fun LeadingContent(
    pack: OfflinePack,
    offlineManager: org.maplibre.maplibrecompose.compose.offline.OfflineManager =
      rememberOfflineManager(),
    completedIcon: @Composable () -> Unit = {
      Icon(
        imageVector = vectorResource(Res.drawable.check_circle_filled),
        contentDescription = "Complete",
      )
    },
    pausedIcon: @Composable () -> Unit = {
      Icon(
        imageVector = vectorResource(Res.drawable.pause_circle_filled),
        contentDescription = "Paused",
      )
    },
    downloadingIcon: @Composable () -> Unit = {
      _root_ide_package_.org.maplibre.maplibrecompose.material3.offline.DownloadProgressCircle(pack)
    },
    errorIcon: @Composable () -> Unit = {
      Icon(
        imageVector = vectorResource(Res.drawable.error_filled),
        contentDescription = "Error",
        tint = MaterialTheme.colorScheme.error,
      )
    },
    warningIcon: @Composable () -> Unit = {
      Icon(
        imageVector = vectorResource(Res.drawable.warning_filled),
        contentDescription = "Warning",
      )
    },
  ) {
    val icon by derivedStateOf {
      val progress = pack.downloadProgress
      when (progress) {
        is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy ->
          when (progress.status) {
            _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus
              .Complete -> completedIcon
            _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Paused ->
              pausedIcon
            _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus
              .Downloading -> downloadingIcon
          }
        is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Error -> errorIcon
        is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.TileLimitExceeded,
        is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Unknown -> warningIcon
      }
    }
    AnimatedContent(icon) { icon -> icon() }
  }

  /**
   * The default trailing content for an
   * [org.maplibre.maplibrecompose.material3.offline.OfflinePackListItem]. It includes a button to
   * pause, resume, or update the pack, depending on the pack's current status. It also includes a
   * delete button.
   */
  @Composable
  public fun TrailingContent(
    pack: OfflinePack,
    offlineManager: org.maplibre.maplibrecompose.compose.offline.OfflineManager =
      rememberOfflineManager(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
  ): Unit = Row {
    _root_ide_package_.org.maplibre.maplibrecompose.material3.offline.PauseResumeUpdateButton(
      pack,
      offlineManager,
    )
    _root_ide_package_.org.maplibre.maplibrecompose.material3.offline.DeleteButton(
      pack,
      offlineManager,
    )
  }

  /**
   * The default supporting content for an
   * [org.maplibre.maplibrecompose.material3.offline.OfflinePackListItem]. It includes a [Text]
   * describing the status of the pack; typically the download status and size. If the pack is in an
   * error or other unhealthy state, it'll be indicated here.
   */
  @Composable
  public fun SupportingContent(
    progress: org.maplibre.maplibrecompose.compose.offline.DownloadProgress,
    completedContent:
      @Composable
      (org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy) -> Unit =
      {
        Text(it.completedBytesString())
      },
    downloadingContent:
      @Composable
      (org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy) -> Unit =
      {
        Text("Downloading: ${it.completedBytesString()}")
      },
    pausedContent:
      @Composable
      (org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy) -> Unit =
      {
        Text("Paused: ${it.completedBytesString()}")
      },
    errorContent:
      @Composable
      (org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Error) -> Unit =
      {
        Text("Error: ${it.message}")
      },
    tileLimitExceededContent:
      @Composable
      (org.maplibre.maplibrecompose.compose.offline.DownloadProgress.TileLimitExceeded) -> Unit =
      {
        Text("Tile limit exceeded: ${it.limit} tiles")
      },
    unknownContent:
      @Composable
      (org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Unknown) -> Unit =
      {
        Text("Unknown status")
      },
  ) {
    when (progress) {
      is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy ->
        when (progress.status) {
          _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Complete ->
            completedContent(progress)
          _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus
            .Downloading -> downloadingContent(progress)
          _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Paused ->
            pausedContent(progress)
        }
      is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Error ->
        errorContent(progress)
      is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.TileLimitExceeded ->
        tileLimitExceededContent(progress)
      is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Unknown ->
        unknownContent(progress)
    }
  }
}

@Composable
private fun DeleteButton(
  pack: OfflinePack,
  offlineManager: org.maplibre.maplibrecompose.compose.offline.OfflineManager,
) {
  val coroutineScope = rememberCoroutineScope()
  var deleting by remember { mutableStateOf(false) }

  fun onDelete() {
    deleting = true
    coroutineScope.launch { offlineManager.delete(pack) }.invokeOnCompletion { deleting = false }
  }

  IconButton(onClick = ::onDelete, enabled = !deleting) {
    Icon(vectorResource(Res.drawable.delete), "Delete", tint = MaterialTheme.colorScheme.error)
  }
}

@Composable
private fun DownloadProgressCircle(pack: OfflinePack) {
  val progressRatio by derivedStateOf {
    val progress = pack.downloadProgress
    if (
      progress is org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy &&
        progress.requiredResourceCount != 0L
    )
      progress.completedResourceCount.toFloat() / progress.requiredResourceCount
    else 0f
  }

  val animatedProgressRatio by
    animateFloatAsState(
      targetValue = progressRatio,
      animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

  CircularProgressIndicator(progress = { animatedProgressRatio })
}

@Composable
private fun PauseResumeUpdateButton(
  pack: OfflinePack,
  offlineManager: org.maplibre.maplibrecompose.compose.offline.OfflineManager,
) {
  val status =
    (pack.downloadProgress
        as? org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy)
      ?.status ?: return
  val coroutineScope = rememberCoroutineScope()

  fun onClick() {
    when (status) {
      _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Paused ->
        offlineManager.resume(pack)
      _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Downloading ->
        offlineManager.pause(pack)
      _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Complete ->
        coroutineScope.launch { offlineManager.invalidate(pack) }
    }
  }

  IconButton(::onClick) {
    AnimatedContent(status) { status ->
      when (status) {
        _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Paused ->
          Icon(vectorResource(Res.drawable.resume), "Resume")
        _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus
          .Downloading -> Icon(vectorResource(Res.drawable.pause), "Pause")
        _root_ide_package_.org.maplibre.maplibrecompose.compose.offline.DownloadStatus.Complete ->
          Icon(vectorResource(Res.drawable.sync), "Update")
      }
    }
  }
}

private fun org.maplibre.maplibrecompose.compose.offline.DownloadProgress.Healthy
  .completedBytesString() = completedResourceBytes.binaryBytes.toString()
