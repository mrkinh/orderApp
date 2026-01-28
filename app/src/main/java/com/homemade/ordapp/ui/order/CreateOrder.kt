package com.homemade.ordapp.ui.order

import android.os.Build
import android.text.InputFilter
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.homemade.ordapp.Graph
import com.homemade.ordapp.data.room.entities.Order
import com.homemade.ordapp.data.room.entities.OrderItem
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.ui.components.ConfirmDialog
import com.homemade.ordapp.ui.components.Header
import com.homemade.ordapp.ui.components.NavBar
import com.homemade.ordapp.ui.theme.backgroundColor
import com.homemade.ordapp.ui.theme.lineGrey
import com.homemade.ordapp.utils.*
import com.homemade.ordapp.utils.getMoonDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrder(
    navController: NavHostController,
    navigateToOther: (String) -> Unit,
    viewModel: OrderViewModel = Graph.orderVM,
) {
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
    viewModel: OrderViewModel
) {
    val orderViewState by viewModel.state.collectAsStateWithLifecycle()
    var openConfirmDialog = remember { mutableStateOf(false) }
    var notificationText = remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header( true, "Nhận Đơn") {
            navigateToOther("home")
        }
        NewOrderContent (viewModel, orderViewState.creatingOrder) { ->
            notificationText.value = ""
            openConfirmDialog.value = true
        }
        ConfirmDialog(openDialog = openConfirmDialog.value,
            title = "Nhận Đơn",
            onDismissRequest= {
                openConfirmDialog.value = false
            },
            onConfirmRequest = {
                if (!viewModel.isCreatingOrderValid()) {
                    notificationText.value = "Thông tin khách hàng không đầy đủ"
                } else {
                    viewModel.addNewOder()
                    openConfirmDialog.value = false
                    notificationText.value = ""
                    navigateToOther("Home")
                }
            }
        ) {
            OrderConfirmContent(notificationText.value,orderViewState.creatingOrder)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderContent(
    viewModel: OrderViewModel,
    creatingOrder: OrderWithItem,
    onNewOrder: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var pickupDateExpanded = remember { mutableStateOf(false) }

    Text(
        text = "Thông Tin Khách Hàng",
        fontSize = 22.sp,
        textAlign = TextAlign.Left,
        lineHeight = 30.sp,
        maxLines = 1,
        color = Color.Black,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 50.dp, top= 20.dp)
    )
    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp)
            .background(Color.White)
    ) {
        // UserName Area
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
        ) {
            Text(
                text = "Tên",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,

            )
            Text(
                text = "(*)",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Red,
                overflow = TextOverflow.Ellipsis,
            )
        }
        BasicTextField(
            value = creatingOrder.order.customerName,
            onValueChange = {
                val order = creatingOrder.order.copy(customerName = it)
                viewModel.updateOrderInfo(order)
            },
            textStyle = TextStyle(fontSize = 25.sp, color = Color.Black),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(start = 20.dp, end=20.dp, top = 5.dp)
                .border(width = 1.dp, color = Color(0x33C4C4C4), shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .focusRequester(focusRequester),
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = creatingOrder.order.customerName,
                innerTextField = {
                    innerTextField()
                },
                enabled = true,
                singleLine = true,
                placeholder = {
                    Text("Nhập tên khách hàng", color = Color.LightGray)
                },
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(start=10.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor= Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
            )
        }

        // Phone Area
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
        ) {
            Text(
                text = "SĐT",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,

                )
            Text(
                text = "(*)",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Red,
                overflow = TextOverflow.Ellipsis,
            )
        }
        BasicTextField(
            value = creatingOrder.order.customerPhone,
            onValueChange = {
                val order = creatingOrder.order.copy(customerPhone = it)
                viewModel.updateOrderInfo(order)
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
                .fillMaxWidth()
                .height(55.dp)
                .padding(start = 20.dp, end=20.dp, top = 5.dp)
                .border(width = 1.dp, color = Color(0x33C4C4C4), shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .focusRequester(focusRequester),
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = creatingOrder.order.customerPhone,
                innerTextField = {
                    innerTextField()
                },
                enabled = true,
                singleLine = true,
                placeholder = {
                    Text("Nhập số điện thoại", color = Color.LightGray)
                },
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(start=10.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor= Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
            )
        }

        // Pickup date Area
        Row(
            modifier = Modifier
                .padding(start = 20.dp, top = 5.dp)
        ) {
            Text(
                text = "Ngày Lấy",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,

                )
            Text(
                text = "(*)",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Red,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 15.dp, end=20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .border(width = 1.dp, color = Color(0x33C4C4C4), shape = RoundedCornerShape(4.dp))
                .clickable {
                    pickupDateExpanded.value = !pickupDateExpanded.value
                }
        ) {
            Text(
                text = creatingOrder.order.pickupTime + "-" + getMoonDate(creatingOrder.order.pickupTime) + "AL",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
            )
        }
        DateList(
            isExpanded = pickupDateExpanded.value,
            dataList = viewModel.getDateList(),
            onDismissRequest = { ->
            }
        ) { selectedDate ->
            pickupDateExpanded.value = false
            var pickupDate = viewModel.getDateList()[selectedDate]
            val order = creatingOrder.order.copy(pickupTime = pickupDate)
            viewModel.updateOrderInfo(order)
        }

        // Deposit area
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
        ) {
            Text(
                text = "Cọc",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,

                )
            Text(
                text = "(*)",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Red,
                overflow = TextOverflow.Ellipsis,
            )
        }
        BasicTextField(
            value = creatingOrder.order.depositMoney,
            onValueChange = {
                val order = creatingOrder.order.copy(depositMoney = it)
                viewModel.updateOrderInfo(order)
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
                .fillMaxWidth()
                .height(55.dp)
                .padding(start = 20.dp, end=20.dp, top = 5.dp)
                .border(width = 1.dp, color = Color(0x33C4C4C4), shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .focusRequester(focusRequester),
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = creatingOrder.order.depositMoney,
                innerTextField = {
                    innerTextField()
                },
                enabled = true,
                singleLine = true,
                placeholder = {
                    Text("Nhập số tiền cọc", color = Color.LightGray)
                },
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(start=10.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor= Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
            )
        }
    }

    Text(
        text = "Chi Tiết Đơn Hàng",
        fontSize = 22.sp,
        textAlign = TextAlign.Left,
        lineHeight = 30.sp,
        maxLines = 1,
        color = Color.Black,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 50.dp, top= 20.dp)
    )

    // Detail Order
    DetailOrder(viewModel, creatingOrder)

    // Note
    Text(
        text = "Ghi Chú",
        fontSize = 22.sp,
        textAlign = TextAlign.Left,
        lineHeight = 30.sp,
        maxLines = 1,
        color = Color.Black,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 50.dp, top= 5.dp)
    )
    BasicTextField(
        value = creatingOrder.order.description,
        onValueChange = {
            val order = creatingOrder.order.copy(description = it)
            viewModel.updateOrderInfo(order)
        },
        minLines = 3,
        maxLines = 10,
        textStyle = TextStyle(fontSize = 25.sp, color = Color.Black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(start = 40.dp, end=20.dp, top = 5.dp)
            .border(width = 1.dp, color = Color(0x33C4C4C4), shape = RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .focusRequester(focusRequester),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = creatingOrder.order.description,
            innerTextField = {
                innerTextField()
            },
            enabled = true,
            singleLine = true,
            placeholder = {
                Text("Nhập ghi chú", color = Color.LightGray)
            },
            visualTransformation = VisualTransformation.None,
            interactionSource = remember { MutableInteractionSource() },
            contentPadding = PaddingValues(start=10.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor= Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
        )
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF75B974))
                .width(200.dp)
                .height(70.dp)
                .clickable(onClick = {
                    onNewOrder()
                })
        ) {
            Text(
                text = "Lưu",
                fontSize = 25.sp,
                color = Color(0xFF004F36),
            )
        }
    }
}

@Composable
fun DateList(
    isExpanded: Boolean,
    dataList: List<String>,
    onDismissRequest: () -> Unit,
    onClickRequest: (Int) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {
            onDismissRequest()
        },
        modifier = Modifier
            .wrapContentSize()
            .border(1.dp, Color.White)
            .background(Color.White)
            .clip(RoundedCornerShape(50.dp)),
        offset = DpOffset(x = 5.dp, y = 8.dp)
    ) {
        dataList.forEachIndexed { index, item ->
            DropdownMenuItem(
                text = {
                    Text(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        text = item + "-" + getMoonDate(item) + "AL",
                        fontSize = 25.sp
                    )
                },
                onClick = {
                    onClickRequest(index)
                }
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                color = lineGrey,
                thickness = 1.dp
            )
            if (index == dataList.count() - 1 && dataList.count() > 0) {
                Divider(color = Color.White, thickness = 10.dp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailOrder(
    viewModel: OrderViewModel,
    creatingOrder: OrderWithItem,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 5.dp)
            .background(Color.White)
    ) {
        // Pork Large
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Giò (Loại 1kg)",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )

            BasicTextField(
                value = getQuantityByItemName(creatingOrder,ITEM_PORK_SAUSAGE_LARGE),
                onValueChange = {
                    val quantity = it.toIntOrNull() ?: 0
                    var orderItem = OrderItem (itemName = ITEM_PORK_SAUSAGE_LARGE, quantity = quantity)
                    viewModel.addOrUpdateItem(orderItem)
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
                    .width(55.dp)
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0x33C4C4C4),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .focusRequester(focusRequester),
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = getQuantityByItemName(creatingOrder,ITEM_PORK_SAUSAGE_LARGE),
                    innerTextField = {
                        innerTextField()
                    },
                    enabled = true,
                    singleLine = true,
                    placeholder = {
                        Text("0", color = Color.Black, fontSize = 25.sp)
                    },
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(start = 10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }
            Text(
                text = "Cái",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp)
            )
        }

        // Pork Small
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Giò (Loại 0.5kg)",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )

            BasicTextField(
                value = getQuantityByItemName(creatingOrder,ITEM_PORK_SAUSAGE),
                onValueChange = {
                    val quantity = it.toIntOrNull() ?: 0
                    var orderItem = OrderItem (itemName = ITEM_PORK_SAUSAGE, quantity = quantity)
                    viewModel.addOrUpdateItem(orderItem)
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
                    .width(55.dp)
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0x33C4C4C4),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .focusRequester(focusRequester),
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = getQuantityByItemName(creatingOrder,ITEM_PORK_SAUSAGE),
                    innerTextField = {
                        innerTextField()
                    },
                    enabled = true,
                    singleLine = true,
                    placeholder = {
                        Text("0", color = Color.Black, fontSize = 25.sp)
                    },
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(start = 10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }
            Text(
                text = "Cái",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp)
            )
        }

        // Pork Dry
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Chả Chiên",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )

            BasicTextField(
                value = getQuantityByItemName(creatingOrder,ITEM_PORK_SAUSAGE_FRY),
                onValueChange = {
                    val quantity = it.toIntOrNull() ?: 0
                    var orderItem = OrderItem (itemName = ITEM_PORK_SAUSAGE_FRY, quantity = quantity)
                    viewModel.addOrUpdateItem(orderItem)
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
                    .width(55.dp)
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0x33C4C4C4),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .focusRequester(focusRequester),
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = getQuantityByItemName(creatingOrder,ITEM_PORK_SAUSAGE_FRY),
                    innerTextField = {
                        innerTextField()
                    },
                    enabled = true,
                    singleLine = true,
                    placeholder = {
                        Text("0", color = Color.Black, fontSize = 25.sp)
                    },
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(start = 10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }
            Text(
                text = "Kg",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp)
            )
        }

        // Cake Large
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Bánh Chưng (To)",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )

            BasicTextField(
                value = getQuantityByItemName(creatingOrder,ITEM_CHUNG_CAKE_LARGE),
                onValueChange = {
                    val quantity = it.toIntOrNull() ?: 0
                    var orderItem = OrderItem (itemName = ITEM_CHUNG_CAKE_LARGE, quantity = quantity)
                    viewModel.addOrUpdateItem(orderItem)
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
                    .width(55.dp)
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0x33C4C4C4),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .focusRequester(focusRequester),
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = getQuantityByItemName(creatingOrder,ITEM_CHUNG_CAKE_LARGE),
                    innerTextField = {
                        innerTextField()
                    },
                    enabled = true,
                    singleLine = true,
                    placeholder = {
                        Text("0", color = Color.Black, fontSize = 25.sp)
                    },
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(start = 10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }
            Text(
                text = "Cái",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp)
            )
        }

        // Cake Normal
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Bánh Chưng (Vừa)",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )
            BasicTextField(
                value = getQuantityByItemName(creatingOrder,ITEM_CHUNG_CAKE_NORMAL),
                onValueChange = {
                    val quantity = it.toIntOrNull() ?: 0
                    var orderItem = OrderItem (itemName = ITEM_CHUNG_CAKE_NORMAL, quantity = quantity)
                    viewModel.addOrUpdateItem(orderItem)
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
                    .width(55.dp)
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0x33C4C4C4),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .focusRequester(focusRequester),
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = getQuantityByItemName(creatingOrder,ITEM_CHUNG_CAKE_LARGE),
                    innerTextField = {
                        innerTextField()
                    },
                    enabled = true,
                    singleLine = true,
                    placeholder = {
                        Text("0", color = Color.Black, fontSize = 25.sp)
                    },
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(start = 10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }
            Text(
                text = "Cái",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp)
            )
        }

        // Cake Small
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Bánh Chưng (Nhỏ)",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )

            BasicTextField(
                value = getQuantityByItemName(creatingOrder,ITEM_CHUNG_CAKE_SMALL),
                onValueChange = {
                    val quantity = it.toIntOrNull() ?: 0
                    var orderItem = OrderItem (itemName = ITEM_CHUNG_CAKE_SMALL, quantity = quantity)
                    viewModel.addOrUpdateItem(orderItem)
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
                    .width(55.dp)
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0x33C4C4C4),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .focusRequester(focusRequester),
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = getQuantityByItemName(creatingOrder,ITEM_CHUNG_CAKE_SMALL),
                    innerTextField = {
                        innerTextField()
                    },
                    enabled = true,
                    singleLine = true,
                    placeholder = {
                        Text("0", color = Color.Black, fontSize = 25.sp)
                    },
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(start = 10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }
            Text(
                text = "Cái",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp)
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmContent(
    errorMessage: String,
    data: OrderWithItem,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 5.dp)
            .background(Color.White)
    ) {
        if (!errorMessage.isEmpty()) {
            Text(
                text = errorMessage,
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Red,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 50.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Tên",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )
            Text(
                text = data.order.customerName,
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 50.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Số Điện Thoại",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )
            Text(
                text = data.order.customerPhone,
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 50.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Ngày Lấy",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )
            Text(
                text = data.order.pickupTime +  "-" + getMoonDate(data.order.pickupTime) + "AL",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 50.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "Tiền Cọc",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.5F)
                    .padding(start = 50.dp)
            )
            Text(
                text = data.order.depositMoney +  "K",
                fontSize = 22.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 50.dp)
            )
        }
        data.items.forEachIndexed { index, detailItem ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = getReadableItemName(detailItem.itemName),
                    fontSize = 22.sp,
                    textAlign = TextAlign.Left,
                    lineHeight = 30.sp,
                    maxLines = 1,
                    color = Color.Black,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.5F)
                        .padding(start = 50.dp)
                )
                Text(
                    text = detailItem.quantity.toString() + " " + getQuantityTypeName(detailItem.itemName),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Left,
                    lineHeight = 30.sp,
                    maxLines = 1,
                    color = Color.Black,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 50.dp)
                )
            }
        }
    }
}
fun getQuantityByItemName(order: OrderWithItem, itemName: String): String {
    val itemsList = order.items
    itemsList.map { item ->
        if (item.itemName == itemName) {
            return item.quantity.toString()
        }
    }
    return "0"
}