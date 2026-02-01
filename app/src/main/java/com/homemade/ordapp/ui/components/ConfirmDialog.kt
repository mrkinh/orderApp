package com.homemade.ordapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.homemade.ordapp.Graph
import com.homemade.ordapp.ui.theme.textColorSecondary

@Composable
fun ConfirmDialog(
    openDialog: Boolean,
    title: String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    when {
        openDialog -> {
            ConfirmDialogContent(title, onDismissRequest, onConfirmRequest, content)
        }
    }
}

@Composable
fun ConfirmDialogContent(
    title: String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    var buttonLeft = (Graph.screenWidthDp * 0.075).toInt()
    var btnWidth = (Graph.screenWidthDp * 0.25).toInt()

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0EEF2),
                contentColor = Color(0xFF000000)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    textAlign = TextAlign.Center,
                )
                content()

                Spacer(modifier = Modifier.height(15.dp))
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top=10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier
                            .size(btnWidth.dp, 40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = textColorSecondary,
                            contentColor = Color(0xFFFFFFFF)
                        ),
                        onClick = {
                            onDismissRequest()
                        },
                        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
                    ) {
                        Text(
                            text = "Hủy",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                        )
                    }

                    Button(
                        modifier = Modifier
                            .padding(start=buttonLeft.dp)
                            .size(btnWidth.dp, 40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = textColorSecondary,
                            contentColor = Color(0xFFFFFFFF)
                        ),
                        onClick = {
                            onConfirmRequest()
                        }
                    ) {
                        Text(
                            text = "Đồng Ý",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}