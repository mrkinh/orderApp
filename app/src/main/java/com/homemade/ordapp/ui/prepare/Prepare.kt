package com.homemade.ordapp.ui.prepare

import android.os.Build
import android.preference.PreferenceActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.homemade.ordapp.Graph
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.ui.components.Header
import com.homemade.ordapp.ui.components.NavBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import com.homemade.ordapp.ui.home.HomeViewModel
import com.homemade.ordapp.ui.theme.backgroundColor
import com.homemade.ordapp.ui.theme.lineGrey
import com.homemade.ordapp.utils.getMoonDate
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.homemade.ordapp.ui.order.DateList


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Prepare(
    navController: NavHostController,
    navigateToOther: (String) -> Unit,
    viewModel: PrepareViewModel = Graph.prepareVM) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            NavBar(navController, navigateToOther)
        },
        containerColor = backgroundColor
    ) {  innerPadding ->
        LazyColumn(modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            bottom = innerPadding.calculateBottomPadding(),
        )) {
            item {
                Content(navigateToOther, viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Content(
    navigateToOther: (String) -> Unit,
    viewModel: PrepareViewModel
) {
    val orderViewState by viewModel.state.collectAsStateWithLifecycle()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header(false, "Chuẩn Bị Hàng"){
        }
        PrepareContent(navigateToOther, viewModel)


    }
}
@OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PrepareContent(
        navigateToOther: (String) -> Unit,
        viewModel: PrepareViewModel

    ) {
        var showDateList by remember { mutableStateOf(false) }
        var pickupDateExpanded = remember { mutableStateOf(false) }
        var pickupDate by remember { mutableStateOf("16/02/2026") }
        var showDialog by remember { mutableStateOf(false) }
        val keyboardController = LocalSoftwareKeyboardController.current
        var note by remember { mutableStateOf("") }
        var quantityCakeLarge by remember { mutableStateOf(0) }
        var quantityCakeSmall by remember { mutableStateOf(0) }
        var quantityPorkSaurageL by remember { mutableStateOf(0) }
        var quantityPorkSaurageM by remember { mutableStateOf(0) }
        var quantityPorkSaurageFry by remember { mutableStateOf(0) }

        val focusRequester = remember { FocusRequester() }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)

        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row() {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(
                                width = 1.dp,
                                color = Color(0x33C4C4C4),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                pickupDateExpanded.value = !pickupDateExpanded.value
                            }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = pickupDate + "-" + getMoonDate(pickupDate) + "AL",
                            fontSize = 25.sp,
                            textAlign = TextAlign.Left,
                            lineHeight = 30.sp,
                            maxLines = 1,
                            color = Color.Black,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = if (pickupDateExpanded.value)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    DateList(
                        isExpanded = pickupDateExpanded.value,
                        dataList = viewModel.getDateList(),
                        onDismissRequest = {
                            pickupDateExpanded.value= false
                        }
                    ) { selectedDate ->
                        pickupDateExpanded.value = false
                        pickupDate = viewModel.getDateList()[selectedDate]
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(20.dp)

                ) {
                    Text(
                        text = "Tổng Số Lượng",
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold

                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = "# Bánh Chưng",
                            fontSize = 18.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Text(
                                text = "Lớn",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField{
                                    quantityCakeLarge = it
                                }
                                Text(
                                    text = "Cái",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black,
                                )
                            }

                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Text(
                                text = "Nhỏ",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField{
                                    quantityCakeSmall = it
                                }
                                Text(
                                    text = "Cái",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = "# Giò (Chả lụa)",
                            fontSize = 18.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,

                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Text(
                                text = "1 Kg",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField{
                                    quantityPorkSaurageL = it
                                }
                                Text(
                                    text = "Cái",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black,
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,

                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Text(
                                text = "0.5 Kg",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField{
                                    quantityPorkSaurageM = it
                                }
                                Text(
                                    text = "Cái",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = "# Chả Chiên",
                            fontSize = 18.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,

                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Text(
                                text = "0.5 Kg",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField{
                                    quantityPorkSaurageFry = it
                                }
                                Text(
                                    text = "Cái",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black,
                                )
                            }
                        }
                    }
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(5.dp),
//                    ) {
//                        Text(
//                            text = "Ghi Chú",
//                            fontSize = 18.sp,
//                            fontStyle = FontStyle.Italic,
//                            fontWeight = FontWeight.Bold,
//                        )
//                        NoteContentField{
//                            note = it
//                        }
//
//                    }


                    //Dialog area
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF75B974))
                            .clickable(onClick = {
                                showDialog = true
                            })

                    ) {
                        Text(
                            text = "Cập Nhật",
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),

                            )
                    }
                    if (showDialog) {
                        UpdateQuantityDialog (
                            quantityCakeLarge = quantityCakeLarge,
                            quantityCakeSmall = quantityCakeSmall,
                            quantityPorkSaurageL = quantityPorkSaurageL,
                            quantityPorkSaurageM = quantityPorkSaurageM,
                            quantityPorkSaurageFry,
                            note = note,
                            onDismiss = { showDialog = false }

                        )
                    }
                }
            }
        }
    }



//Dialog Area
@Composable
fun UpdateQuantityDialog(
    quantityCakeLarge: Int,
    quantityCakeSmall: Int,
    quantityPorkSaurageL: Int,
    quantityPorkSaurageM: Int,
    quantityPorkSausageFry: Int,
    note: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Xác Nhận Số Lượng",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                    )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "#Bánh Chưng (Lớn)"
                    )
                    Text(
                        text = "$quantityCakeLarge Cái"
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "#Bánh Chưng (Nhỏ)"
                    )
                    Text(
                        text = "$quantityCakeSmall Cái"
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "#Chả Lụa (1 Kg)"
                    )
                    Text(
                        text = "$quantityPorkSaurageL Cái"
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "#Chả Lụa(0.5 Kg)"
                    )
                    Text(
                        text = "$quantityPorkSaurageM Cái"
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "#Chả Chiên"
                    )
                    Text(
                        text = "$quantityPorkSausageFry Cái"
                    )
                }
//                Column(
//                    verticalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ) {
//                    Text(
//                        text = "Ghi Chú",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 21.sp
//                    )
//                    Text(
//                        text = "$note"
//                    )
//                }
            }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Huỷ")
                    }
                    TextButton(onClick = onDismiss) {
                        Text(
                            "OK",
                            color = Color(0xFF75B974)
                        )
                    }
                }

            }
        }
    }
}



//Input Field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputQuantityField(
    onQuantityChange: (Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var quantityText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = quantityText,
        onValueChange = {
            quantityText = it
            onQuantityChange(it.toIntOrNull() ?: 0)
        },
        textStyle = TextStyle(fontSize = 25.sp, color = Color.Black),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        modifier = Modifier
            .width(80.dp)
            .height(45.dp)
            .border(1.dp, Color(0x33C4C4C4), RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .focusRequester(focusRequester)
            .padding(horizontal = 10.dp)
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = quantityText,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = remember { MutableInteractionSource() },
            contentPadding = PaddingValues(0.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(4.dp)
        )
    }
}



//NoteField
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteContentField(
    onNoteChange: (String) -> Unit
){
    var contentText by remember { mutableStateOf(TextFieldValue("", selection = TextRange(0))) }
    BasicTextField(
        value = contentText,
        onValueChange = {
            contentText = it
            onNoteChange(it.text)
        },
        textStyle = TextStyle(
            fontSize = 21.sp,
            color = Color.Black
        ),
        maxLines = Int.MAX_VALUE,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Default
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .border(
                width = 1.dp,
                color = Color(0x33C4C4C4),
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = contentText.text,
            innerTextField = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    innerTextField()
                }
            },
            enabled = true,
            singleLine = false,
            visualTransformation = VisualTransformation.None,
            interactionSource = remember { MutableInteractionSource() },
            contentPadding = PaddingValues(0.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(4.dp)
        )

    }
}
