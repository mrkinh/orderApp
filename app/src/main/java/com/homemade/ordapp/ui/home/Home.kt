package com.homemade.ordapp.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.homemade.ordapp.Graph
import com.homemade.ordapp.ui.components.NavBar
import com.homemade.ordapp.ui.home.HomeViewModel.HomeUIItem
import com.homemade.ordapp.ui.theme.backgroundColor
import com.homemade.ordapp.utils.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavHostController,
    navigateToOther: (String) -> Unit,
    viewModel: HomeViewModel = Graph.homeVM,
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
    viewModel: HomeViewModel
) {
    val homeViewState by viewModel.state.collectAsStateWithLifecycle()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(navigateToOther, viewModel)
        DataContent(navigateToOther, "Số Lượng Cần Giao", 0x4CFF0000,homeViewState.orderList)
        DataContent(navigateToOther, "Số Lượng Thực Tế", 0x4C9747FF,homeViewState.warehouseList)
        DataContent(navigateToOther, "Số Lượng Đã Giao", 0x4CE4CA8A, homeViewState.deliveredList)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Header(
    navigateToOther: (String) -> Unit,
    viewModel: HomeViewModel
) {
    Row(
        modifier = Modifier
            .height((0.1 * Graph.screenHeightDp).dp)
            .fillMaxWidth(1F)
            .background(Color(0xFF75B974)),
        horizontalArrangement = Arrangement.Center,
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(0.7F)
                .padding(20.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = "Ngày ${getCurrentDate()}",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color(0xFF004F36),
                overflow = TextOverflow.Ellipsis,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Âm Lịch: ${getMoonDate(getCurrentDate())}",
                fontSize = 25.sp,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                maxLines = 1,
                color = Color(0xFF004F36),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color(0xFF75B974))
                .fillMaxSize()
                .clickable(onClick = {

                })
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.9F)
                    .fillMaxHeight(0.5F)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF274F31))
                    .clickable {
                        navigateToOther("CreateOrder")
                    }
            ) {
                Text(
                    text = "Nhận Đơn",
                    fontSize = 25.sp,
                    color = Color(0xFFE4CA8A),
                )
            }
        }
    }
}

@Composable
fun DataContent(
    navigateToOther: (String) -> Unit,
    title: String,
    titleColor: Long,
    data: MutableList<HomeUIItem>
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column (
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(titleColor))
            ) {
                Text(
                    text = title,
                    fontSize = 25.sp,
                    lineHeight = 35.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = Color(titleColor).copy(alpha = 1.0F),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "# Bánh Chưng",
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Lớn",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )

                Text(
                    text = "${getQuantity(ITEM_CHUNG_CAKE_LARGE, data)} cái",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Trung Bình",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )

                Text(
                    text = "${getQuantity(ITEM_CHUNG_CAKE_NORMAL, data)} cái",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Nhỏ",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )

                Text(
                    text = "${getQuantity(ITEM_CHUNG_CAKE_SMALL, data)} cái",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "# Giò (Chả Lụa)",
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Lớn (1kg)",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )

                Text(
                    text = "${getQuantity(ITEM_PORK_SAUSAGE_LARGE, data)} cái",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Nhỏ (0.5kg)",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )

                Text(
                    text = "${getQuantity(ITEM_PORK_SAUSAGE, data)} cái",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "# Chả Chiên",
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Số Lượng (0.5Kg)",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )

                Text(
                    text = "${getQuantity(ITEM_PORK_SAUSAGE_FRY, data)} cái",
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 30.dp)
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

fun getQuantity(name: String, data: MutableList<HomeUIItem>): Int {
    var item: HomeUIItem? = data.find { it.name == name }
    if (item != null) {
        return item.quantity
    }
    return 0
}