package com.homemade.ordapp.ui.order

import android.os.Build
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.ui.components.ConfirmDialog
import com.homemade.ordapp.ui.components.Header
import com.homemade.ordapp.ui.components.NavBar
import com.homemade.ordapp.ui.theme.backgroundColor
import com.homemade.ordapp.ui.theme.lineGrey
import com.homemade.ordapp.utils.ORDER_STATUS_CANCELED
import com.homemade.ordapp.utils.ORDER_STATUS_DELIVERED
import com.homemade.ordapp.utils.ORDER_STATUS_ORDERED
import com.homemade.ordapp.utils.getMoonDate
import com.homemade.ordapp.utils.getOrderStatusBGColor
import com.homemade.ordapp.utils.getOrderStatusColor
import com.homemade.ordapp.utils.getOrderStatusName
import com.homemade.ordapp.utils.getQuantityTypeName
import com.homemade.ordapp.utils.getReadableItemName
import com.homemade.ordapp.utils.getTotalPrice

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderList(
    navController: NavHostController,
    navigateToOther: (String) -> Unit,
    viewModel: OrderViewModel = Graph.orderVM,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val orderViewState by viewModel.state.collectAsStateWithLifecycle()
    var openConfirmDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            NavBar(navController, navigateToOther)
        },
        containerColor = backgroundColor
    ) {  innerPadding ->
        Column(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding(),
            )
        ) {
            Header( true, "Danh Sách Đơn Hàng") {
                navigateToOther("home")
            }
            SearchContent(viewModel, orderViewState.searchStatus)
            ListContentData(viewModel, orderViewState.displayOrderList, navigateToOther) {
                openConfirmDialog.value = true
            }
            ConfirmDialog(openDialog = openConfirmDialog.value,
                title = "Sửa Đơn",
                onDismissRequest= {
                    openConfirmDialog.value = false
                },
                onConfirmRequest = {
                    viewModel.updateOrder()
                    openConfirmDialog.value = false
                }
            ) {
                EditingOrderContent(orderViewState.editingOrder)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    viewModel: OrderViewModel,
    searchStatus: OrderViewModel.SearchStatus,
) {
    val searchStr = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val dateExpanded = remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        BasicTextField(
            value = searchStr.value,
            onValueChange = {
                searchStr.value = it
                viewModel.filterByKeyword(searchStr.value)
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
                .padding(start = 20.dp, end = 20.dp, top = 5.dp)
                .border(width = 1.dp, color = Color(0x33C4C4C4), shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .focusRequester(focusRequester),
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = searchStr.value,
                innerTextField = {
                    innerTextField()
                },
                enabled = true,
                singleLine = true,
                placeholder = {
                    Text("Nhập tên, số điện thoại", color = Color.LightGray)
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
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 15.dp)
                .fillMaxWidth(0.3F)
                .wrapContentHeight()
                .border(width = 1.dp, color = Color(0xFF9747FF), shape = RoundedCornerShape(4.dp))
                .background(color = Color(0x199747FF))
                .clickable {
                    dateExpanded.value = !dateExpanded.value
                }
        ) {
            Text(
                text = searchStatus.date,
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color(0xFF9747FF),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, top = 10.dp)
            )
        }
        DateList(
            isExpanded = dateExpanded.value,
            dataList = viewModel.getSearchDateList(),
            onDismissRequest = { ->
                dateExpanded.value = false
            }
        ) { selectedDate ->
            dateExpanded.value = false
            viewModel.filterByDate(viewModel.getSearchDateList()[selectedDate])
        }
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 15.dp)
                .fillMaxWidth(0.3F)
                .wrapContentHeight()
                .thenIf (searchStatus.orderStatus == ORDER_STATUS_ORDERED) {
                    border(width = 1.dp,  color = Color(0xFF9747FF), shape = RoundedCornerShape(4.dp))
                }
                .background(color = if (searchStatus.orderStatus == ORDER_STATUS_ORDERED) Color(0x199747FF) else Color(0xC4C4C4C4))
                .clickable {
                    viewModel.filterByOrderStatus(ORDER_STATUS_ORDERED)
                }
        ) {
            Text(
                text = "Chưa Giao",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = if (searchStatus.orderStatus == ORDER_STATUS_ORDERED) Color(0xFF9747FF) else Color(0xFFB5B5AF),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, top = 10.dp)
            )
        }
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 15.dp)
                .fillMaxWidth(0.45F)
                .wrapContentHeight()
                .thenIf (searchStatus.orderStatus == ORDER_STATUS_DELIVERED) {
                    border(width = 1.dp, color = Color(0xFF9747FF), shape = RoundedCornerShape(4.dp))
                }
                .background(color = if (searchStatus.orderStatus == ORDER_STATUS_DELIVERED) Color(0x199747FF) else Color(0xC4C4C4C4))
                .clickable {
                    viewModel.filterByOrderStatus(ORDER_STATUS_DELIVERED)
                }
        ) {
            Text(
                text = "Đã Giao",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = if (searchStatus.orderStatus == ORDER_STATUS_DELIVERED) Color(0xFF9747FF) else Color(0xFFB5B5AF),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, top = 10.dp)
            )
        }
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 15.dp, end = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .thenIf (searchStatus.orderStatus == ORDER_STATUS_CANCELED) {
                    border(width = 1.dp, color = Color(0xFF9747FF), shape = RoundedCornerShape(4.dp))
                }
                .background(color = if (searchStatus.orderStatus == ORDER_STATUS_CANCELED) Color(0x199747FF) else Color(0xC4C4C4C4))
                .clickable {
                    viewModel.filterByOrderStatus(ORDER_STATUS_CANCELED)
                }
        ) {
            Text(
                text = "Hủy",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = if (searchStatus.orderStatus == ORDER_STATUS_CANCELED) Color(0xFF9747FF) else Color(0xFFB5B5AF),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, top = 10.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContentData(
    viewModel: OrderViewModel,
    data: List<OrderWithItem>,
    navigateToOther: (String) -> Unit,
    onEditOrder: () -> Unit
) {
    LazyColumn (
        modifier = Modifier.padding(start= 20.dp, end=20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        items(data) { item ->
            var statusListExpanded = remember { mutableStateOf(false) }
            Column (
                modifier = Modifier.padding(top=20.dp)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        text = item.order.pickupTime + " - " + getMoonDate(item.order.pickupTime) + "AL",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 30.sp,
                        maxLines = 1,
                        color = Color(0xFF61615F),
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Đã Cọc (" + item.order.depositMoney + "K)",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 30.sp,
                        maxLines = 1,
                        color = Color(0xFFFF5900),
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = item.order.customerName,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 30.sp,
                        maxLines = 1,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.order.customerPhone,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 30.sp,
                        maxLines = 1,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                item.items.forEachIndexed { index, detailItem ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    ) {
                        Text(
                            text = getReadableItemName(detailItem.itemName),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Left,
                            lineHeight = 30.sp,
                            maxLines = 1,
                            color = Color(0xFF61615F),
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = detailItem.quantity.toString() + " " + getQuantityTypeName(detailItem.itemName),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Left,
                            lineHeight = 30.sp,
                            maxLines = 1,
                            color = Color(0xFF61615F),
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color(0x42C4C4C4))
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = "Ghi chú: ${item.order.description}",
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 30.sp,
                        color = Color.Black,
                        overflow = TextOverflow.Visible,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Action List
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 20.dp)
                ) {
                    Text(
                        text = "Tổng: ${getTotalPrice(item.items)}K",
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 30.sp,
                        maxLines = 1,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .width(200.dp)
                            .wrapContentHeight()
                            .background(Color(0x199747FF))
                            .border(width = 1.dp, color = Color(0xFF9747FF), shape = RoundedCornerShape(4.dp))
                            .padding(top =10.dp, bottom = 10.dp, end=20.dp)
                            .clickable {
                                viewModel.setUpdatingFullOrder(item)
                                navigateToOther("UpdateOrder")
                            }
                    ) {
                        Text(
                            text = "Sửa Đơn",
                            fontSize = 25.sp,
                            color = Color(0xFF9747FF),
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .width(200.dp)
                            .wrapContentHeight()
                            .background(getOrderStatusBGColor(item.order.status))
                            .border(width = 1.dp, color = Color(0x33C4C4C4), shape = RoundedCornerShape(4.dp))
                            .padding(top =10.dp, bottom = 10.dp, end=20.dp)
                            .clickable {
                                statusListExpanded.value = !statusListExpanded.value
                            }
                    ) {
                        Text(
                            text = getOrderStatusName(item.order.status),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp,
                            maxLines = 1,
                            color = getOrderStatusColor(item.order.status),
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                        )
                        OrderStatusList(
                            isExpanded = statusListExpanded.value,
                            dataList = viewModel.getOrderStatusList(),
                            onDismissRequest = { ->
                                statusListExpanded.value = false
                            }
                        ) { selectedIndex ->
                            statusListExpanded.value = false
                            val newOrder = item.order.copy(status = viewModel.getOrderStatusList()[selectedIndex].first)
                            viewModel.setUpdatingOrder(newOrder)
                            onEditOrder()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun OrderStatusList(
    isExpanded: Boolean,
    dataList: List<Pair<String, String>>,
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
                        text = item.second,
                        color = Color(0xFF51AF58),
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
fun EditingOrderContent(
    data: OrderWithItem,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 5.dp)
            .background(Color.White)
    ) {
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
                text = "Trạng Thái",
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
                text = getOrderStatusName(data.order.status),
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
    }
}

fun Modifier.thenIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        this.then(modifier(Modifier))
    } else {
        this
    }
}