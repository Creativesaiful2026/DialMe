package com.saiful.dialme.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.saiful.dialme.core.model.theme.ButtonShape

/** Material shapes scaled from the user's chosen corner radius. */
internal fun shapesFor(cornerRadiusDp: Int): Shapes {
    val r = cornerRadiusDp.toFloat()
    return Shapes(
        extraSmall = RoundedCornerShape((r * 0.25f).dp),
        small = RoundedCornerShape((r * 0.5f).dp),
        medium = RoundedCornerShape(r.dp),
        large = RoundedCornerShape((r * 1.5f).dp),
        extraLarge = RoundedCornerShape((r * 2f).dp),
    )
}

/** Shape to use for buttons such as dialpad keys and call buttons. */
fun ButtonShape.toShape(cornerRadiusDp: Int): Shape = when (this) {
    ButtonShape.ROUND -> CircleShape
    ButtonShape.ROUNDED_SQUARE -> RoundedCornerShape(cornerRadiusDp.dp)
    ButtonShape.SQUARE -> RectangleShape
}
