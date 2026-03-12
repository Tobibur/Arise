package com.tobibur.arise.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tobibur.arise.presentation.utils.convertTime
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubAlarmBottomSheet(
    currentTime: Long,
    sheetState: SheetState,
    showBottomSheet: (Boolean) -> Unit,
    onComplete: (time: Long, title: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet(false)
        },
        sheetState = sheetState
    ) {
        // Sheet content

        val subAlarmTitle = rememberSaveable { mutableStateOf("") }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = false,
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimeInput(
                state = timePickerState,
            )
            Spacer(modifier = Modifier.height(16.dp))

            AlarmTitleField(label = "SUB ALARM TITLE", title = subAlarmTitle.value) {
                subAlarmTitle.value = it
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = {
                    //dismiss the bottom sheet
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet(false)
                        }
                    }
                }) {
                    Text("Dismiss")
                }
                Button(onClick = {
                    onComplete(
                        convertTime(timePickerState.hour, timePickerState.minute),
                        subAlarmTitle.value
                    )
                    //dismiss the bottom sheet
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet(false)
                        }
                    }
                }) {
                    Text("Confirm selection")
                }
            }
        }

    }
}