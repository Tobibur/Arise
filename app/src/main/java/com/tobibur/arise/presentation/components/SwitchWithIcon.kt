package com.tobibur.arise.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

@Composable
fun SwitchWithIcon(modifier: Modifier = Modifier, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {

    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        },
        thumbContent = if (checked) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        }
    )
}