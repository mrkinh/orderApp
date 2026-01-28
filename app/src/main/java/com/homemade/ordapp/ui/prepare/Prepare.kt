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
import com.homemade.ordapp.data.room.entities.Warehouse
import com.homemade.ordapp.ui.order.DateList
import com.homemade.ordapp.utils.*


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
    val prepareViewState by viewModel.state.collectAsStateWithLifecycle()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header(false, "Chuẩn Bị Hàng"){
        }
        PrepareContent(navigateToOther, prepareViewState, viewModel)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PrepareContent(
        navigateToOther: (String) -> Unit,
        viewState: PrepareViewModel.PrepareViewState,
        viewModel: PrepareViewModel
    ) {
        var showDateList by remember { mutableStateOf(false) }
        var pickupDateExpanded = remember { mutableStateOf(false) }
        var pickupDate by remember { mutableStateOf("16/02/2026") }
        var showDialog by remember { mutableStateOf(false) }
        var quantityCakeLarge by remember { mutableStateOf(0) }
        var quantityCakeNormal by remember { mutableStateOf(0) }
        var quantityCakeSmall by remember { mutableStateOf(0) }
        var quantityPorkSaurageL by remember { mutableStateOf(0) }
        var quantityPorkSaurageM by remember { mutableStateOf(0) }
        var quantityPorkSaurageFry by remember { mutableStateOf(0) }

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
                            .fillMaxWidth(0.4F)
                            .wrapContentHeight()
                            .border(width = 1.dp, color = Color(0xFF9747FF), shape = RoundedCornerShape(4.dp))
                            .background(color = Color(0x199747FF))
                            .clickable {
                                pickupDateExpanded.value = !pickupDateExpanded.value
                            }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = viewState.creatingItem.date + "-" + getMoonDate(viewState.creatingItem.date) + "AL",
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
                        viewModel.updateCreatingItemDate(pickupDate)
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
                                InputQuantityField(
                                    getQuantityByName(viewState.creatingItem, ITEM_CHUNG_CAKE_LARGE)
                                ) { quantity ->
                                    viewModel.updateCreatingItem(ITEM_CHUNG_CAKE_LARGE, quantity)
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
                                text = "Vừa",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField (
                                    getQuantityByName(viewState.creatingItem, ITEM_CHUNG_CAKE_NORMAL)
                                ) { quantity ->
                                    viewModel.updateCreatingItem(ITEM_CHUNG_CAKE_NORMAL, quantity)
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
                                InputQuantityField (
                                    getQuantityByName(viewState.creatingItem, ITEM_CHUNG_CAKE_SMALL)
                                ) { quantity ->
                                    viewModel.updateCreatingItem(ITEM_CHUNG_CAKE_SMALL, quantity)
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
                                text = "Lớn (1 Kg)",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField (
                                    getQuantityByName(viewState.creatingItem, ITEM_PORK_SAUSAGE_LARGE)
                                ) { quantity ->
                                    viewModel.updateCreatingItem(ITEM_PORK_SAUSAGE_LARGE, quantity)
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
                                text = "Nhỏ (0.5 Kg)",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField (
                                    getQuantityByName(viewState.creatingItem, ITEM_PORK_SAUSAGE)
                                ) { quantity ->
                                    viewModel.updateCreatingItem(ITEM_PORK_SAUSAGE, quantity)
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
                                text = "Số Lượng",
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InputQuantityField (
                                    getQuantityByName(viewState.creatingItem, ITEM_PORK_SAUSAGE_FRY)
                                ) { quantity ->
                                    viewModel.updateCreatingItem(ITEM_PORK_SAUSAGE_FRY, quantity)
                                }
                                Text(
                                    text = "Kg",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black,
                                )
                            }
                        }
                    }

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
                            viewState,
                            onDismiss = {
                                showDialog = false
                            }
                        ) {
                            viewModel.updateItem()
                            showDialog = false
                        }
                    }
                }
            }
        }
    }


@Composable
fun UpdateQuantityDialog(
    viewState: PrepareViewModel.PrepareViewState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
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
                            text = "${getQuantityByName(viewState.creatingItem, ITEM_CHUNG_CAKE_LARGE)} Cái"
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "#Bánh Chưng (Vừa)"
                        )
                        Text(
                            text = "${getQuantityByName(viewState.creatingItem, ITEM_CHUNG_CAKE_NORMAL)} Cái"
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
                            text = "${getQuantityByName(viewState.creatingItem, ITEM_CHUNG_CAKE_SMALL)} Cái"
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
                            text = "${getQuantityByName(viewState.creatingItem, ITEM_PORK_SAUSAGE_LARGE)} Cái"
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
                            text = "${getQuantityByName(viewState.creatingItem, ITEM_PORK_SAUSAGE)} Cái"
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
                            text = "${getQuantityByName(viewState.creatingItem, ITEM_PORK_SAUSAGE_FRY)} Cái"
                        )
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton( modifier = Modifier.fillMaxWidth(0.5F),
                        onClick = onDismiss) {
                        Text("Huỷ")
                    }
                    TextButton( modifier = Modifier.fillMaxWidth(),
                        onClick = onConfirm) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputQuantityField(
    value: Int,
    onQuantityChange: (Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = value.toString(),
        onValueChange = {
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
            value = value.toString(),
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

fun getQuantityByName(warehouse: Warehouse, name: String): Int {
    return when (name) {
        warehouse.item1.name -> warehouse.item1.quantity
        warehouse.item2.name -> warehouse.item2.quantity
        warehouse.item3.name -> warehouse.item3.quantity
        warehouse.item4.name -> warehouse.item4.quantity
        warehouse.item5.name -> warehouse.item5.quantity
        warehouse.item6.name -> warehouse.item6.quantity
        warehouse.item7.name -> warehouse.item7.quantity
        else -> 0
    }
}