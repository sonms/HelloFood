package com.purang.hellofood.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.purang.hellofood.R
import com.purang.hellofood.models.HealthTipData
import com.purang.hellofood.models.HealthTips
import com.purang.hellofood.utils.FontSize
import com.purang.hellofood.utils.PreferenceDataStore
import com.purang.hellofood.utils.FontUtils

@Composable
fun HomeScreen(
    navController : NavController,
) {
    val context = LocalContext.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    var healthTipData by remember {
        mutableStateOf<HealthTipData?>(null)
    }

    LaunchedEffect(Unit) {
        healthTipData =  HealthTips.getAll().shuffled()[0]
    }

    //- 최근 측정 건강 통계
    //- 건강 팁
    //- 오늘 할일 스케줄

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn (
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HealthTip(healthTipData,fontSize)
            }
        }
    }
}

@Composable
fun HealthTip(healthTipData: HealthTipData?, fontSize: FontSize) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.shiny_light_bulb_icon),
            contentDescription = "bulb"
        )

        Column (
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp)
        ) {
            Text(
                text = healthTipData?.tipTitle.toString(),
                fontWeight = FontWeight.Bold,
                style = FontUtils.getTextStyle(fontSize.size + 4f)
            )
            Text(
                text = healthTipData?.tipDescription.toString(),
                fontWeight = FontWeight.Medium,
                style = FontUtils.getTextStyle(fontSize.size)
            )
        }
    }
}