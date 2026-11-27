package com.saiful.dialme.ui.recents

import android.Manifest
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saiful.dialme.R
import com.saiful.dialme.core.data.calllog.CallLogGroup
import com.saiful.dialme.core.data.calllog.CallType
import com.saiful.dialme.ui.common.CallLauncher
import com.saiful.dialme.ui.common.CallLauncherHost
import com.saiful.dialme.ui.common.ContactAvatar
import com.saiful.dialme.ui.common.PermissionGate
import com.saiful.dialme.ui.common.rememberPhoneFormatter
import java.util.Locale

@StringRes
private fun typeLabelRes(type: CallType): Int = when (type) {
    CallType.INCOMING -> R.string.call_type_incoming
    CallType.OUTGOING -> R.string.call_type_outgoing
    CallType.MISSED -> R.string.call_type_missed
    CallType.REJECTED -> R.string.call_type_rejected
    CallType.BLOCKED -> R.string.call_type_blocked
    CallType.VOICEMAIL -> R.string.call_type_voicemail
    CallType.OTHER -> R.string.call_type_other
}

@Composable
private fun typeColor(type: CallType): Color = when (type) {
    CallType.MISSED, CallType.REJECTED, CallType.BLOCKED -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun formatDuration(seconds: Long): String {
    val total = if (seconds < 0) 0 else seconds
    return String.format(Locale.getDefault(), "%d:%02d", total / 60, total % 60)
}

@Composable
fun RecentsScreen(
    modifier: Modifier = Modifier,
    viewModel: RecentsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshPermission() }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { viewModel.refreshPermission() }

    CallLauncherHost { launcher ->
        if (!state.hasPermission) {
            PermissionGate(
                message = stringResource(R.string.permission_call_log_message),
                onGrant = { permissionLauncher.launch(Manifest.permission.READ_CALL_LOG) },
                modifier = modifier,
            )
        } else {
            RecentsContent(
                groups = state.groups,
                loading = state.loading,
                launcher = launcher,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun RecentsContent(
    groups: List<CallLogGroup>,
    loading: Boolean,
    launcher: CallLauncher,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val formatter = rememberPhoneFormatter()
    var missedOnly by rememberSaveable { mutableStateOf(false) }
    var selected by remember { mutableStateOf<CallLogGroup?>(null) }
    val shown = remember(groups, missedOnly) {
        if (missedOnly) groups.filter { it.type == CallType.MISSED } else groups
    }
    val unknown = stringResource(R.string.recents_unknown_number)

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = !missedOnly,
                onClick = { missedOnly = false },
                label = { Text(stringResource(R.string.recents_filter_all)) },
            )
            FilterChip(
                selected = missedOnly,
                onClick = { missedOnly = true },
                label = { Text(stringResource(R.string.recents_filter_missed)) },
            )
        }
        if (shown.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (!loading) {
                    Text(
                        text = stringResource(
                            if (missedOnly) R.string.recents_empty_missed else R.string.recents_empty,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(shown, key = { _, group -> group.latest.id }) { _, group ->
                    val title = group.displayName
                        ?: if (group.number.isEmpty()) unknown else formatter(group.number)
                    val timeText = DateUtils.getRelativeTimeSpanString(
                        group.latest.dateMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE,
                    ).toString()
                    ListItem(
                        headlineContent = {
                            Text(if (group.count > 1) "$title (${group.count})" else title)
                        },
                        supportingContent = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = stringResource(typeLabelRes(group.type)),
                                    color = typeColor(group.type),
                                )
                                Text(text = timeText)
                            }
                        },
                        leadingContent = {
                            ContactAvatar(name = group.displayName ?: group.number, photoUri = null)
                        },
                        trailingContent = {
                            if (group.number.isNotEmpty()) {
                                IconButton(onClick = { launcher.call(group.number) }) {
                                    Icon(Icons.Filled.Call, contentDescription = stringResource(R.string.contact_call))
                                }
                            }
                        },
                        modifier = Modifier.clickable { selected = group },
                    )
                }
            }
        }
    }

    selected?.let { group ->
        val title = group.displayName
            ?: if (group.number.isEmpty()) unknown else formatter(group.number)
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(title) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    group.entries.take(MAX_DETAIL_ENTRIES).forEach { entry ->
                        Column {
                            Text(
                                text = stringResource(typeLabelRes(entry.type)),
                                color = typeColor(entry.type),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = DateUtils.formatDateTime(
                                    context,
                                    entry.dateMillis,
                                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or
                                        DateUtils.FORMAT_ABBREV_ALL,
                                ) + " · " + formatDuration(entry.durationSeconds),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (group.number.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            selected = null
                            launcher.call(group.number)
                        },
                    ) { Text(stringResource(R.string.contact_call)) }
                }
            },
            dismissButton = {
                TextButton(onClick = { selected = null }) { Text(stringResource(R.string.contact_close)) }
            },
        )
    }
}

private const val MAX_DETAIL_ENTRIES = 20
