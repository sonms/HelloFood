package com.purang.hellofood

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.purang.hellofood.utils.FontUtils
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.purang.hellofood.ui.theme.HelloFoodTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.purang.hellofood.ui.theme.mintColor4
import com.purang.hellofood.ui.theme.mintColor5
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.utils.FontSize
import com.purang.hellofood.utils.PreferenceDataStore
import com.purang.hellofood.viewmodels.FoodLogViewModel
import com.purang.hellofood.viewmodels.GeminiViewModel
import com.purang.hellofood.views.account.AccountScreen
import com.purang.hellofood.views.calendar.CalendarDataListScreen
import com.purang.hellofood.views.camera.CameraScreen
import com.purang.hellofood.views.camera.analysis.AnalysisScreen
import com.purang.hellofood.views.home.HomeScreen
import com.purang.hellofood.views.loading.GlobalLoadingScreen
import com.purang.hellofood.views.login.LoginScreen
import com.purang.hellofood.views.saved.SavedScreen
import com.purang.hellofood.views.saved.food.shopping.ShoppingScreen
import com.purang.hellofood.views.saved.recipe.list.RecipeListScreen
import com.purang.hellofood.views.schedule.ScheduleScreen
import com.purang.hellofood.views.schedule.edit.EditScreen
import com.purang.hellofood.views.search.SearchScreen
import com.purang.hellofood.views.statistics.exercise.ExerciseScreen
import com.purang.hellofood.views.statistics.food_manage.FoodManageScreen
import com.purang.hellofood.views.statistics.personal.PersonalScreen
import com.purang.hellofood.views.statistics.rest.RestScreen

sealed class BottomNavItem(
    val title: Int, val icon: Int, val screenRoute: String
) {
    data object Home : BottomNavItem(R.string.home, R.drawable.baseline_home_filled_24, "home")
    data object Calendar : BottomNavItem(R.string.calendar, R.drawable.baseline_calendar_month_24, "calendar")
    data object Camera : BottomNavItem(R.string.camera, R.drawable.baseline_camera_24, "camera")
    data object Saved : BottomNavItem(R.string.saved, R.drawable.baseline_bookmark_24, "saved")
    data object Account : BottomNavItem(R.string.account, R.drawable.account_icon, "account")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            HelloFoodTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                    GlobalLoadingScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainContent() {
    val navController = rememberNavController()
    // 현재 라우트를 가져옵니다.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            // 특정 라우트에서는 FloatingActionButton 숨깁니다.
            /*if (currentRoute == BottomNavItem.Home.screenRoute || currentRoute == BottomNavItem.Calendar.screenRoute) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("edit_financial")
                    },
                    modifier = Modifier.padding(end = 10.dp),
                ) {
                    Icon(Icons.Default.Create, contentDescription = "CreateFinancialData")
                }
            }*/
        },
        bottomBar = {
            // 특정 라우트에서는 BottomNavigation을 숨깁니다.
            if (currentRoute !in listOf(
                    "login",
                    "analysis"
                )
            ) {
                BottomNavigation(navController = navController)
            }
        },
        topBar = {
            if (currentRoute !in listOf(
                    BottomNavItem.Saved.screenRoute,
                    "edit?type={typeName}&scheduleId={id}",
                    "analysis",
                    "login",
                    "personal?type={type}",
                    "search",
                    "exercise?type={type}",
                    "food",
                    "rest?type={type}",
                    "list?date={selectDate}",
                    BottomNavItem.Account.screenRoute,
                    BottomNavItem.Calendar.screenRoute
                )
            ) {
                Column {
                    TopAppBar(navController, fontSize)
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    ) {
        Box(Modifier.padding(it)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calendar,
        BottomNavItem.Camera,
        BottomNavItem.Saved,
        BottomNavItem.Account
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val path = Path().apply {
                val width = size.width
                val height = size.height

                // Start point
                moveTo(0f, 0f)

                // Left flat section
                lineTo(width * 0.35f, 0f)

                // Upper curve for central button
                cubicTo(
                    width * 0.4f, 0f,
                    width * 0.45f, height * 0.3f,
                    width * 0.5f, height * 0.3f
                )
                cubicTo(
                    width * 0.55f, height * 0.3f,
                    width * 0.6f, 0f,
                    width * 0.65f, 0f
                )

                // Right flat section
                lineTo(width, 0f)

                // 바텀 경계선
                lineTo(width, height)
                lineTo(0f, height)

                close()
            }
            drawPath(
                path = path,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            //간격의 시작 타이밍이 다름 - evenly는 맨앞부터 간격시작
            //between은 item부터
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEachIndexed { index, item ->
                if (index == 2) {
                    /*Column (
                        modifier = Modifier.navigationBarsPadding().padding(bottom = 5.dp),
                    ) {
                        Spacer(modifier = Modifier.weight(1f, true))
                        Text(
                            text = stringResource(id = R.string.bottom_center), // 버튼 아래 이름
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                        )
                    }*/
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(BottomNavItem.Camera.screenRoute) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true // 중복 스택 방지
                                    restoreState = true
                                }
                            },
                            containerColor = mintColor4,
                            modifier = Modifier
                                .wrapContentSize()
                                .offset(y = (-10).dp),
                            shape = RoundedCornerShape(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_camera_24),
                                contentDescription = "Center Button",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.camera), // 버튼 아래 이름
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                } else {
                    NavigationBarItem(
                        //그림자 파장효과 제거
                        interactionSource = NoRippleInteractionSource,
                        selected = currentRoute == item.screenRoute,
                        onClick = {
                            /*navController.navigate(item.screenRoute) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                restoreState = true
                                launchSingleTop = true
                            }*/
                            navController.navigate(item.screenRoute) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true // 현재 스크린 제거
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(id = item.icon),
                                contentDescription = item.title.toString(),
                                modifier = Modifier.wrapContentSize()
                            )
                        },
                        label = {
                            Text(
                                text = LocalContext.current.getString(item.title),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = mintColor5,
                            selectedTextColor = mintColor5
                        )
                    )
                }
            }
        }

        // 가운데 버튼
        /*FloatingActionButton(
            onClick = {
                navController.navigate(MainActivity.BottomNavItem.PillPredictionCamera.screenRoute)
            },
            containerColor = blueColor5,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_camera_24),
                contentDescription = "Center Button",
                tint = Color.White
            )
        }*/
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController : NavController,
    fontSize : FontSize
) {
    androidx.compose.material3.TopAppBar(
        modifier = Modifier.wrapContentSize(),
        title = {
            Column (
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Title: what2c
                    Text(
                        text = "HelloFood",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = 5.dp),
                        style = FontUtils.getTextStyle(fontSize.size + 2f)
                    )
                }
                //Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray)
            }
        },
        actions = {
            IconButton(onClick = {
                //Log.e("currentUser", FirebaseUserManager.currentUser.toString())
                navController.navigate("search") }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )

    /*androidx.compose.material3.TopAppBar (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "질병 예측", modifier = Modifier.padding(start = 10.dp), style = FontUtils.getTextStyle(fontSize.size + 4f))

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {
            navController.navigate("search")
        }) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }*/
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavigationGraph(navController: NavHostController) {
    val context = LocalContext.current
    val isLoggedIn by PreferenceDataStore.getLoginState(context).collectAsState(initial = false)
    val geminiViewModel: GeminiViewModel = hiltViewModel()
    val foodLogViewModel: FoodLogViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        //startDestination = BottomNavItem.Home.screenRoute
        startDestination = if (isLoggedIn) BottomNavItem.Home.screenRoute else "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("search") {
            SearchScreen(navController)
        }
        composable("analysis") {
            AnalysisScreen(navController = navController, geminiViewModel)
        }
        composable(
            route = "rest?type={type}",
            arguments = listOf(navArgument("type") {
                defaultValue = "default"
            })
        ) {
            val type = it.arguments?.getString("type") ?: "default"
            RestScreen(navController, type)
        }

        composable(
            route = "personal?type={type}",
            arguments = listOf(navArgument("type") {
                defaultValue = "default"
                //type = NavType.StringType
            })
        ) {
            val type = it.arguments?.getString("type") ?: "default"
            PersonalScreen(navController, type)
        }

        composable(
            route = "exercise?type={type}",
            arguments = listOf(navArgument("type") {
                defaultValue = "default"
                //type = NavType.StringType
            })
        ) {
            val type = it.arguments?.getString("type") ?: "default"
            ExerciseScreen(navController, type)
        }

        composable("food") {
            FoodManageScreen(navController, geminiViewModel)
        }
        composable("shopping") {
            ShoppingScreen(navController, foodLogViewModel)
        }
        composable("recipe") {
            RecipeListScreen(navController, foodLogViewModel)
        }
        /*composable("rest") {
            PersonalScreen(navController)
        }*/

        composable(BottomNavItem.Home.screenRoute) {
            HomeScreen(navController)
        }
        composable(BottomNavItem.Calendar.screenRoute) {
            ScheduleScreen(navController)
        }
        composable(BottomNavItem.Saved.screenRoute) {
            SavedScreen(navController, foodLogViewModel)
        }
        composable(BottomNavItem.Camera.screenRoute) {
            CameraScreen(navController, geminiViewModel)
        }
        composable(BottomNavItem.Account.screenRoute) {
            //InterestSelectionScreen(navController)
            AccountScreen(navController)
        }
        /*composable(
            route = "detail?schedule={id}",
            arguments = listOf(
                navArgument("id") { defaultValue = "-1" }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "-1"

            DetailScreen(navController, id = id)
        }*/
        composable(
            route = "edit?type={typeName}&scheduleId={id}",
            arguments = listOf(
                navArgument("typeName") { defaultValue = "default" },
                navArgument("id") { defaultValue = "-1" }
            )
        ) { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString("typeName") ?: "default"
            val scheduleId = backStackEntry.arguments?.getString("id") ?: "-1"

            EditScreen(navController, type = typeName, scheduleId = scheduleId)
        }
        composable(
            route = "list?date={selectDate}",
            arguments = listOf(
                navArgument("selectDate") { defaultValue = "" },
            )
        ) { backStackEntry ->
            val selectDate = backStackEntry.arguments?.getString("selectDate") ?: "default"
            Log.e("selectedDate", selectDate)
            CalendarDataListScreen(navController, selectedDate = selectDate)
        }






        /*composable(
            route = "edit_financial?type={type}&id={id}",
            arguments = listOf(
                navArgument("type") { defaultValue = "default" },
                navArgument("id") { defaultValue = "-1" }
            )
        ) { backStackEntry ->

            val type = backStackEntry.arguments?.getString("type") ?: "default"
            val id = backStackEntry.arguments?.getString("id") ?: "-1"

            EditFinancialScreen(navController, type = type, id = id, homeViewModel)
        }

        composable(
            route = "search?text={searchText}",
            arguments = listOf(
                navArgument("searchText") { defaultValue = "" } // 키 수정
            )
        ) { backStackEntry ->
            val searchText = backStackEntry.arguments?.getString("searchText") ?: ""
            SearchScreen(navController, searchText)
        }*/
    }
}


private object NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true
}