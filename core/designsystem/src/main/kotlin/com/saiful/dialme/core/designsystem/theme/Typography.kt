package com.saiful.dialme.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

private fun TextStyle.scaled(factor: Float): TextStyle = copy(
    fontSize = if (fontSize.isSp) fontSize * factor else fontSize,
    lineHeight = if (lineHeight.isSp) lineHeight * factor else lineHeight,
)

/** Material 3 default typography with every size multiplied by [factor]. */
internal fun scaledTypography(factor: Float): Typography {
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.scaled(factor),
        displayMedium = base.displayMedium.scaled(factor),
        displaySmall = base.displaySmall.scaled(factor),
        headlineLarge = base.headlineLarge.scaled(factor),
        headlineMedium = base.headlineMedium.scaled(factor),
        headlineSmall = base.headlineSmall.scaled(factor),
        titleLarge = base.titleLarge.scaled(factor),
        titleMedium = base.titleMedium.scaled(factor),
        titleSmall = base.titleSmall.scaled(factor),
        bodyLarge = base.bodyLarge.scaled(factor),
        bodyMedium = base.bodyMedium.scaled(factor),
        bodySmall = base.bodySmall.scaled(factor),
        labelLarge = base.labelLarge.scaled(factor),
        labelMedium = base.labelMedium.scaled(factor),
        labelSmall = base.labelSmall.scaled(factor),
    )
}
