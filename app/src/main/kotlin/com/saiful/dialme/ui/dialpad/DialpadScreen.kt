package com.saiful.dialme.ui.dialpad

import android.Manifest
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saiful.dialme.R
import com.saiful.dialme.core.data.search.T9
import com.saiful.dialme.core.designsystem.theme.LocalThemeSpec
import com.saiful.dialme.core.designsystem.theme.toShape
import com.saiful.dialme.ui.DialRequest
import com.saiful.dialme.ui.common.CallLauncherHost
import com.saiful.dialme.ui.common.ContactAvatar
import com.saiful.dialme.ui.common.rememberPhoneFormatter

private val KEY_ROWS = listOf(
    listOf('1', '2', '3'),
    listOf('4', '5', '6'),
    listOf('7', '8', '9'),
    listOf('*', '0', '#'),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialpadScreen(
    dialRequest: DialRequest?,
    modifier: Modifier = Modifier,
    viewModel: DialpadViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val number by viewModel.number.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val formatter = rememberPhoneFormatter()
    val spec = LocalThemeSpec.current
    val keyShape = spec.buttonShape.toShape(spec.cornerRadiusDp)

    var permissionRefresh by remember { mutableIntStateOf(0) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { permissionRefresh++ }
    val hasContactsPermission = remember(permissionRefresh) {
        context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }
    val contactsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        permissionRefresh++
        viewModel.onContactsPermissionChanged()
    }

    // A tel: link or DIAL intent from another app only fills in the number; it never dials.
    LaunchedEffect(dialRequest?.token) {
        if (dialRequest != null && dialRequest.number.isNotBlank()) {
            viewModel.setNumber(dialRequest.number)
        }
    }

    CallLauncherHost { launcher ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                if (!hasContactsPermission) {
                    TextButton(
                        onClick = { contactsLauncher.launch(Manifest.permission.READ_CONTACTS) },
                    ) { Text(stringResource(R.string.dialpad_allow_contacts)) }
                }
                suggestions.forEach { suggestion ->
                    ListItem(
                        headlineContent = { Text(suggestion.contact.displayName) },
                        supportingContent = { Text(formatter(suggestion.number.number)) },
                        leadingContent = {
                            ContactAvatar(
                                name = suggestion.contact.displayName,
                                photoUri = suggestion.contact.photoThumbnailUri,
                            )
                        },
                        modifier = Modifier.clickable { viewModel.setNumber(suggestion.number.number) },
                    )
                }
                Text(
                    text = number.ifEmpty { stringResource(R.string.dialpad_hint) },
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (number.isEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                )
                TextButton(
                    onClick = {
                        val clip = context.getSystemService(ClipboardManager::class.java)?.primaryClip
                        val text = if (clip != null && clip.itemCount > 0) {
                            clip.getItemAt(0).coerceToText(context)?.toString()
                        } else {
                            null
                        }
                        if (text == null || !viewModel.setNumber(text)) {
                            Toast.makeText(context, R.string.dialpad_clipboard_invalid, Toast.LENGTH_SHORT).show()
                        }
                    },
                ) { Text(stringResource(R.string.dialpad_paste)) }
            }

            KEY_ROWS.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    row.forEach { key ->
                        DialKey(
                            label = key.toString(),
                            letters = if (key == '0') "+" else T9.lettersFor(key),
                            shape = keyShape,
                            onClick = { viewModel.append(key) },
                            onLongClick = if (key == '0') ({ viewModel.append('+') }) else null,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.5f),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { launcher.call(number) },
                    enabled = number.isNotEmpty(),
                    shape = keyShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                ) {
                    Icon(Icons.Filled.Call, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.dialpad_call))
                }
                val backspaceDescription = stringResource(R.string.dialpad_backspace)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(keyShape)
                        .combinedClickable(
                            role = Role.Button,
                            onClick = { viewModel.backspace() },
                            onLongClick = { viewModel.clear() },
                        )
                        .semantics { contentDescription = backspaceDescription },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\u232B", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DialKey(
    label: String,
    letters: String,
    shape: Shape,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .combinedClickable(
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.headlineMedium)
        if (letters.isNotEmpty()) {
            Text(
                text = letters,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
