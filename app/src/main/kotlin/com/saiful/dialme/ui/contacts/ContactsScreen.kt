package com.saiful.dialme.ui.contacts

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saiful.dialme.R
import com.saiful.dialme.core.data.contacts.Contact
import com.saiful.dialme.core.data.search.ContactSearch
import com.saiful.dialme.ui.common.CallLauncher
import com.saiful.dialme.ui.common.CallLauncherHost
import com.saiful.dialme.ui.common.ContactAvatar
import com.saiful.dialme.ui.common.PermissionGate
import com.saiful.dialme.ui.common.rememberPhoneFormatter

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshPermission() }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { viewModel.refreshPermission() }

    CallLauncherHost { launcher ->
        if (!state.hasPermission) {
            PermissionGate(
                message = stringResource(R.string.permission_contacts_message),
                onGrant = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
                modifier = modifier,
            )
        } else {
            ContactsContent(
                contacts = state.contacts,
                loading = state.loading,
                launcher = launcher,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ContactsContent(
    contacts: List<Contact>,
    loading: Boolean,
    launcher: CallLauncher,
    modifier: Modifier = Modifier,
) {
    val formatter = rememberPhoneFormatter()
    var query by rememberSaveable { mutableStateOf("") }
    var selected by remember { mutableStateOf<Contact?>(null) }
    val filtered = remember(contacts, query) { ContactSearch.filter(contacts, query) }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            label = { Text(stringResource(R.string.contacts_search_label)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (!loading) {
                    Text(
                        text = stringResource(
                            if (contacts.isEmpty()) R.string.contacts_empty else R.string.contacts_no_match,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { contact ->
                    ListItem(
                        headlineContent = { Text(contact.displayName) },
                        supportingContent = {
                            contact.numbers.firstOrNull()?.let { Text(formatter(it.number)) }
                        },
                        leadingContent = {
                            ContactAvatar(name = contact.displayName, photoUri = contact.photoThumbnailUri)
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    if (contact.numbers.size == 1) {
                                        launcher.call(contact.numbers[0].number)
                                    } else {
                                        selected = contact
                                    }
                                },
                            ) {
                                Icon(Icons.Filled.Call, contentDescription = stringResource(R.string.contact_call))
                            }
                        },
                        modifier = Modifier.clickable { selected = contact },
                    )
                }
            }
        }
    }

    selected?.let { contact ->
        ContactDetailsDialog(
            contact = contact,
            onDismiss = { selected = null },
            onCall = { number -> launcher.call(number) },
        )
    }
}
