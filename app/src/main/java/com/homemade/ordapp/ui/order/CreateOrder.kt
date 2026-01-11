package com.homemade.ordapp.ui.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.homemade.ordapp.data.room.model.OrderWithItem
import com.homemade.ordapp.ui.components.Header
import com.homemade.ordapp.ui.components.NavBar
import com.homemade.ordapp.ui.theme.backgroundColor
import com.homemade.ordapp.ui.theme.lineGrey
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
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header( true, "Nhận Đơn") {
            navigateToOther("home")
        }
        NewOrderContent (viewModel) { newOrder ->

        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderContent(
    viewModel: OrderViewModel,
    onNewOrder: (OrderWithItem) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var userName by remember { mutableStateOf(TextFieldValue("", selection = TextRange(0))) }
    var userPhone by remember { mutableStateOf(TextFieldValue("", selection = TextRange(0))) }
    var pickupDate by remember { mutableStateOf("16/02/2026") }
    var pickupDateExpanded = remember { mutableStateOf(false) }
    var userDeposit by remember { mutableStateOf(TextFieldValue("", selection = TextRange(0))) }
    var userNote by remember { mutableStateOf(TextFieldValue("", selection = TextRange(0))) }

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
            value = userName,
            onValueChange = {
                userName = it
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
                value = userName.text,
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
            value = userPhone,
            onValueChange = {
                userPhone = it
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
                value = userPhone.text,
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
                text = pickupDate + "-" + getMoonDate(pickupDate) + "AL",
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
            pickupDate = viewModel.getDateList()[selectedDate]
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
            value = userDeposit,
            onValueChange = {
                userDeposit = it
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
                value = userDeposit.text,
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
    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp)
            .background(Color.White)
    ) {

    }


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
        modifier = Modifier.padding(start = 50.dp, top= 20.dp)
    )
    BasicTextField(
        value = userNote,
        onValueChange = {
            userNote = it
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
            value = userNote.text,
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