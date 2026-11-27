package com.saiful.dialme.ui.contacts

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saiful.dialme.R
import com.saiful.dialme.core.data.contacts.Contact
import com.saiful.dialme.ui.common.ContactAvatar
import com.saiful.dialme.ui.common.rememberPhoneFormatter

/** Shows every number of [contact] with call and message buttons. */
@Composable
fun ContactDetailsDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onCall: (String) -> Unit,
) {
    val context = LocalContext.current
    val formatter = rememberPhoneFormatter()
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { ContactAvatar(name = contact.displayName, photoUri = contact.photoThumbnailUri, size = 56.dp) },
        title = { Text(contact.displayName) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (contact.isStarred) {
                    Text(
                        text = stringResource(R.string.contact_favorite_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                contact.numbers.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(formatter(item.number), style = MaterialTheme.typography.bodyLarge)
                            val label = item.typeLabel
                            if (!label.isNullOrBlank()) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                try {
                                    context.startActivity(
                                        Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(item.number))),
                                    )
                                } catch (e: ActivityNotFoundException) {
                                    // No messaging app installed: nothing to do.
                                }
                            },
                        ) {
                            Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.contact_message))
                        }
                        IconButton(onClick = { onDismiss(); onCall(item.number) }) {
                            Icon(Icons.Filled.Call, contentDescription = stringResource(R.string.contact_call))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.padding(0.dp)) {
                Text(stringResource(R.string.contact_close))
            }
        },
    )
}
