package com.saiful.dialme.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saiful.dialme.R
import com.saiful.dialme.core.common.logging.DialMeLog

/** Shows the in-app log so it can be copied or shared without a PC. */
@Composable
fun DiagnosticsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var logText by remember { mutableStateOf(DialMeLog.readLog()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.diagnostics_back))
            }
        }
        Text(
            text = stringResource(R.string.diagnostics_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { logText = DialMeLog.readLog() }) {
                Text(stringResource(R.string.diagnostics_refresh))
            }
            Button(onClick = { shareLog(context, logText) }) {
                Text(stringResource(R.string.diagnostics_share))
            }
            OutlinedButton(onClick = {
                DialMeLog.clear()
                logText = ""
            }) {
                Text(stringResource(R.string.diagnostics_clear))
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            SelectionContainer {
                Text(
                    text = logText.ifEmpty { stringResource(R.string.diagnostics_empty) },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

private fun shareLog(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.diagnostics_share_subject))
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, null))
}
