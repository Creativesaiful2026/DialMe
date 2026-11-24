package com.saiful.dialme.ui.screens

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.saiful.dialme.BuildConfig
import com.saiful.dialme.R
import com.saiful.dialme.core.model.theme.ThemeMode
import com.saiful.dialme.core.model.theme.ThemeSpec
import kotlin.math.roundToInt

private val AccentChoices = listOf(
    0xFF1B6B4FL, // green (default)
    0xFF1A5FB4L, // blue
    0xFF8E2DE2L, // purple
    0xFFC2185BL, // pink
    0xFFE65100L, // orange
)

private val ModeChoices = listOf(
    ThemeMode.SYSTEM to R.string.theme_mode_system,
    ThemeMode.LIGHT to R.string.theme_mode_light,
    ThemeMode.DARK to R.string.theme_mode_dark,
    ThemeMode.AMOLED to R.string.theme_mode_amoled,
)

@Composable
fun SettingsScreen(
    themeSpec: ThemeSpec,
    onThemeChange: ((ThemeSpec) -> ThemeSpec) -> Unit,
    onThemeReset: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        SectionTitle(R.string.settings_section_appearance)

        Text(
            text = stringResource(R.string.settings_theme_mode),
            style = MaterialTheme.typography.titleSmall,
        )
        ModeChoices.forEach { (mode, labelRes) ->
            val selected = themeSpec.mode == mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selected,
                        onClick = { onThemeChange { it.copy(mode = mode) } },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected, onClick = null)
                Text(
                    text = stringResource(labelRes),
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }

        val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.settings_dynamic_color))
                Text(
                    text = stringResource(R.string.settings_dynamic_color_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = themeSpec.useDynamicColor,
                onCheckedChange = { checked -> onThemeChange { it.copy(useDynamicColor = checked) } },
                enabled = dynamicSupported,
            )
        }

        Text(
            text = stringResource(R.string.settings_accent_color),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 8.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccentChoices.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(color), CircleShape)
                        .clickable { onThemeChange { it.copy(seedColor = color) } },
                )
            }
        }

        Text(
            text = stringResource(R.string.settings_corner_radius),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 8.dp),
        )
        Slider(
            value = themeSpec.cornerRadiusDp.toFloat(),
            onValueChange = { value ->
                onThemeChange { it.copy(cornerRadiusDp = value.roundToInt()) }
            },
            valueRange = ThemeSpec.MIN_CORNER_RADIUS_DP.toFloat()..ThemeSpec.MAX_CORNER_RADIUS_DP.toFloat(),
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.settings_preview_card),
                modifier = Modifier.padding(16.dp),
            )
        }

        OutlinedButton(onClick = onThemeReset) {
            Text(stringResource(R.string.settings_reset_theme))
        }
        Text(
            text = stringResource(R.string.settings_not_saved),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle(R.string.settings_section_diagnostics)
        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_open_log)) },
            modifier = Modifier.clickable(onClick = onOpenDiagnostics),
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle(R.string.settings_section_about)
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(
                R.string.settings_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionTitle(@StringRes textRes: Int) {
    Text(
        text = stringResource(textRes),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp),
    )
}
