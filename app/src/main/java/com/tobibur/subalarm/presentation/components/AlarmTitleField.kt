package com.tobibur.subalarm.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun AlarmTitleField(label: String = "ALARM TITLE", title: String = "", onTitleChanged: (String) -> Unit) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            CustomIconButton(
                onClick = {},
                text = "OPTIONS",
                icon = Icons.Default.Settings
            )
        }
        Spacer(Modifier.height(8.dp))

        BasicTextField(
            value = title,
            onValueChange = { newText ->
                onTitleChanged(newText)
            },
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {

                    // Hint / Placeholder — visible only when empty, disappears on typing
                    if (title.isEmpty()) {
                        Text(
                            text = "e.g. Work Morning",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}
