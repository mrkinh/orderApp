package com.homemade.ordapp.ui.statistic

import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import com.homemade.ordapp.ui.components.Header
import com.homemade.ordapp.ui.components.NavBar
import com.homemade.ordapp.ui.home.HomeViewModel
import com.homemade.ordapp.ui.theme.backgroundColor
import com.homemade.ordapp.utils.ITEM_CHUNG_CAKE_LARGE
import com.homemade.ordapp.utils.ITEM_CHUNG_CAKE_NORMAL
import com.homemade.ordapp.utils.ITEM_CHUNG_CAKE_SMALL
import com.homemade.ordapp.utils.ITEM_PORK_SAUSAGE
import com.homemade.ordapp.utils.ITEM_PORK_SAUSAGE_FRY
import com.homemade.ordapp.utils.ITEM_PORK_SAUSAGE_LARGE
import com.homemade.ordapp.utils.getCurrentDate
import com.homemade.ordapp.utils.getMoonDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statistic(
    navController: NavHostController,
    navigateToOther: (String) -> Unit,
    viewModel: StatisticViewModel = Graph.statisticVM,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val statisticViewState by viewModel.state.collectAsStateWithLifecycle()

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
            StatisticHeader( navigateToOther, viewModel)
            StatisticContent(viewModel, statisticViewState.statisticItem, statisticViewState.totalPrice) {

            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun StatisticHeader(
    navigateToOther: (String) -> Unit,
    viewModel: StatisticViewModel
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .height((0.075 * Graph.screenHeightDp).dp)
            .fillMaxWidth(1F)
            .background(Color.White),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Thống Kê",
            fontSize = 25.sp,
            textAlign = TextAlign.Left,
            lineHeight = 30.sp,
            maxLines = 1,
            color = Color(0xFF004F36),
            overflow = TextOverflow.Ellipsis,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(0.7F)
                .padding(start = 30.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.White)
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
                        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())
                        val dateString = formatter.format(Date())
                        val fileName = "Orders_$dateString.xlsx"
                        try {
                            viewModel.exportToExcel(context, fileName)
                            Toast.makeText(context, "Saved to ${fileName}", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Log.e("Statistic", "Failed to export data: err= ${e.message}")
                            Toast.makeText(context, "Faild to save to ${fileName}", Toast.LENGTH_LONG).show()
                        }
                    }
            ) {
                Text(
                    text = "Lưu Dữ Liệu",
                    fontSize = 25.sp,
                    color = Color(0xFFE4CA8A),
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticContent(
    viewModel: StatisticViewModel,
    data: List<StatisticViewModel.StatisticItem>,
    totalPrice: String,
    onEditOrder: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        items(data) { item ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
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
                            .background(Color(0x4C9747FF))
                    ) {
                        Text(
                            text = item.date + "-" + getMoonDate(item.date) + "AL",
                            fontSize = 25.sp,
                            lineHeight = 35.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9747FF).copy(alpha = 1.0F),
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
                            text = "${getQuantity(ITEM_CHUNG_CAKE_LARGE, item.items)} cái",
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
                            text = "${getQuantity(ITEM_CHUNG_CAKE_NORMAL, item.items)} cái",
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
                            text = "${getQuantity(ITEM_CHUNG_CAKE_SMALL, item.items)} cái",
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
                            text = "${getQuantity(ITEM_PORK_SAUSAGE_LARGE, item.items)} cái",
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
                            text = "${getQuantity(ITEM_PORK_SAUSAGE, item.items)} cái",
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
                            text = "Số Lượng (0.5kg)",
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 30.dp)
                        )

                        Text(
                            text = "${getQuantity(ITEM_PORK_SAUSAGE_FRY, item.items)} Cái",
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            text = "Tổng Tiền",
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 30.dp)
                        )

                        Text(
                            text = "${item.totalPrice}K",
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 30.dp)
                        )
                    }
                }
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Tổng Tiền: ${totalPrice}K",
                    fontSize = 25.sp,
                    lineHeight = 30.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF0000),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.5F)
                        .background(Color(0x4CFF0000))
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
fun getQuantity(name: String, data: List<StatisticViewModel.StatisticItemDetail>): Int {
    var item: StatisticViewModel.StatisticItemDetail? = data.find { it.name == name }
    if (item != null) {
        return item.quantity
    }
    return 0
}