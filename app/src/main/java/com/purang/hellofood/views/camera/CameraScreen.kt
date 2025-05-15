package com.purang.hellofood.views.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.purang.hellofood.R
import com.purang.hellofood.models.FoodLog
import com.purang.hellofood.ui.theme.TextColorGray
import com.purang.hellofood.ui.theme.blueExercise2
import com.purang.hellofood.ui.theme.greenFoodColor1
import com.purang.hellofood.ui.theme.greenFoodColor3
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.GeminiViewModel
import com.purang.hellofood.views.camera.analysis.GeminiUiState
import com.purang.hellofood.views.loading.LoadingState
import java.io.File
import java.time.LocalDateTime

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CameraScreen(
    navController: NavController,
    geminiViewModel: GeminiViewModel
) {
    val context = LocalContext.current

    var hasPermissions by remember { mutableStateOf(false) }

    val permissions = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES
    )

    val permissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    hasPermissions = when {
        permissionState.allPermissionsGranted -> {
            true
        }
        permissionState.shouldShowRationale -> {
            false
        }
        else -> {
            false
        }
    }

    var isImageSelected by remember {
        mutableStateOf(false)
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    //gemini
    val responseText by geminiViewModel.responseText.collectAsState()
    val uiState by geminiViewModel.uiState.collectAsState()

    when (uiState) {
        is GeminiUiState.Loading -> LoadingState.show()
        is GeminiUiState.Success -> {
            //Text(text = responseText)
            Log.e("Success", responseText.toString())
            geminiViewModel.fetchFoodLog(parseCondensedFoodLogFormat(responseText, FirebaseUserManager.userId.toString()))
            navController.navigate("analysis")
        }

        is GeminiUiState.Error -> Text("Error : $responseText")
        else -> {
            GeminiUiState.Initial
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),  // 스크롤 가능하게 유지
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 이미지 표시 영역
        Box(
            modifier = Modifier
                .background(Color.LightGray, RoundedCornerShape(24.dp))
                .fillMaxWidth()
                .aspectRatio(16f / 9f)  // 비율 유지
                .clip(RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isImageSelected && selectedImage != null) {
                AsyncImage(
                    model = selectedImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_image_24),
                        tint = TextColorGray,
                        contentDescription = "select image"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No image selected",
                        color = TextColorGray,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 이미지 선택 버튼
        OpenCameraOrAlbum { uri ->
            isImageSelected = true
            selectedImage = uri
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 분석 버튼
        ButtonUI(R.drawable.baseline_search_24, "Analyze Meal", Color.White, greenFoodColor1) {
            if (isImageSelected) {
                geminiViewModel.updateSelectedImageUri(selectedImage)
                geminiViewModel.sendMessageWithImage(
                    context = context,
                    imageUri = selectedImage,
                    prompt = "Command: Photo Analysis\n" +
                            "\n" +
                            "Task : When answering by command, please follow the following\n" +
                            "\n" +
                            "Food name:\n" +
                            "Calories, ?/\n" +
                            "Protein, ?/\n" +
                            "Fat, ?/\n" +
                            "Vitamins, ?/\n" +
                            "Improvements for this food.\n" +
                            "\n" +
                            "Never change the above answering method. In the ?, indicate the number of nutrients in one serving of the food analyzed, and add / to indicate the following, and do not use any symbols other than those I have mentioned. Please follow this order."
                )
            } else {
                isImageSelected = false
            }
        }

        if (!isImageSelected) {
            Text(
                "Please select an image first",
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 추가 정보 제공
        PhotoTip()
    }
}

@Composable
fun ButtonUI(
    image : Int,
    description : String,
    innerColor: Color,
    outerColor:Color,
    onClick : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(outerColor, RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(image),
                contentDescription = "button icon",
                tint = innerColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = description,
                color = innerColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PhotoTip() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(greenFoodColor3, RoundedCornerShape(8.dp))
    ) {
        Row (
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.shiny_light_bulb_icon),
                contentDescription = "tip icon",
                tint = greenFoodColor1,
                modifier = Modifier.padding(end = 5.dp)
            )

            Column  {
                Text(
                    text = "Tips for better analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = greenFoodColor1
                )

                Text(
                    text = "•Ensure good lighting when taking photos",
                    fontSize = 14.sp,
                    color = greenFoodColor1
                )

                Text(
                    text = "•Center the meal in the frame",
                    fontSize = 14.sp,
                    color = greenFoodColor1
                )

                Text(
                    text = "•Take photo from above for best results",
                    fontSize = 14.sp,
                    color = greenFoodColor1
                )
            }
        }
    }
}

@Composable
fun OpenCameraOrAlbum( //카메라, 앨범 버튼
    onImageSelected: (Uri?) -> Unit,
) {
    val context = LocalContext.current

    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = {
            it?.let {
                val uri = saveBitmapToUri(context, it)
                onImageSelected(uri)
            }
        }
    )

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonUI(
            image = R.drawable.baseline_photo_camera_24,
            description = "Take Photo",
            innerColor = Color.White,
            outerColor = blueExercise2
        ) {
            launcherCamera.launch(null) // 카메라 실행
        }

        Spacer(modifier = Modifier.height(16.dp))

        ButtonUI(
            image = R.drawable.baseline_image_24,
            description = "Choose from Gallery",
            innerColor = blueExercise2,
            outerColor = Color.White
        ) {
            launcherGallery.launch("image/*") // 갤러리 실행
        }
    }
}

// Bitmap을 임시 파일로 저장 후 Uri를 반환
fun saveBitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

/*
@RequiresApi(Build.VERSION_CODES.O)
fun parseFoodLogFromText(
    text: String,
    userId: String,
    photoUrl: String? = null,
    foodId: Int = 0
): FoodLog {
    fun extractFloatFromRange(line: String): Float? {
        val match = Regex("""(\d+)[-–](\d+)\s*(kcal|g)?""").find(line)
        return match?.let {
            val (min, max) = it.groupValues[1].toFloat() to it.groupValues[2].toFloat()
            ((min + max) / 2f)
        }
    }

    fun extractSingleFloat(line: String): Float? {
        val match = Regex("""(\d+(\.\d+)?)\s*(kcal|g|mg)?""").find(line)
        return match?.groupValues?.get(1)?.toFloat()
    }

    val lines = text.lines()

    var foodName: String? = null
    var calories: Float? = null
    var carbohydrates: Float? = null
    var proteins: Float? = null
    var fats: Float? = null
    var vitamin: Float? = 0f // default 0
    var water: Int? = null
    var description = ""

    for (line in lines) {
        when {
            line.contains("Nutritional Value", ignoreCase = true) -> {
                foodName = line.substringBefore(".").removePrefix("Alright, let's analyze this ").trim()
            }

            line.contains("Calories", ignoreCase = true) -> {
                calories = extractFloatFromRange(line) ?: extractSingleFloat(line)
            }

            line.contains("Protein", ignoreCase = true) -> {
                proteins = extractFloatFromRange(line) ?: extractSingleFloat(line)
            }

            line.contains("Fat", ignoreCase = true) && !line.contains("Saturated", ignoreCase = true) -> {
                fats = extractFloatFromRange(line) ?: extractSingleFloat(line)
            }

            line.contains("Carbohydrates", ignoreCase = true) -> {
                carbohydrates = extractFloatFromRange(line) ?: extractSingleFloat(line)
            }

            line.contains("Vitamin", ignoreCase = true) -> {
                // 이 부분은 실제 값이 없으므로 0.0f 유지
                vitamin = 0f
            }

            line.contains("Sodium", ignoreCase = true) || line.contains("Processed", ignoreCase = true) -> {
                description += line.trim() + " "
            }

            line.contains("Overall:", ignoreCase = true) -> {
                // 전체 설명 부분을 description에 추가
                description += line.trim() + " "
            }
        }
    }

    return FoodLog(
        foodId = foodId,
        userId = userId,
        photoUrl = photoUrl,
        foodName = foodName ?: "Unknown Food",
        foodDescription = description.trim().ifEmpty { null },
        calories = calories,
        carbohydrates = carbohydrates,
        proteins = proteins,
        fats = fats,
        vitamin = vitamin,
        water = water,
        createdAt = LocalDateTime.now()
    )
}*/

@RequiresApi(Build.VERSION_CODES.O)
fun parseFoodLogFromText(text: String, userId: String, photoUrl: String? = null): FoodLog {
    // 1. 정규식 정의
    val regexMap = mapOf(
        "calories" to Regex("""Calories[:：]?\s*(\d+(?:\.\d+)?)[^\d]*(?:-|to|~|–|—)?[^\d]*(\d+(?:\.\d+)?)?""", RegexOption.IGNORE_CASE),
        "proteins" to Regex("""Protein[:：]?\s*(\d+(?:\.\d+)?)[^\d]*(?:g|gram|grams)?(?:-|to|~|–|—)?[^\d]*(\d+(?:\.\d+)?)?""", RegexOption.IGNORE_CASE),
        "fats" to Regex("""Fat[:：]?\s*(\d+(?:\.\d+)?)[^\d]*(?:g|gram|grams)?(?:-|to|~|–|—)?[^\d]*(\d+(?:\.\d+)?)?""", RegexOption.IGNORE_CASE),
        "vitaminC" to Regex("""Vitamin\s*C[:：]?\s*(\d+(?:\.\d+)?)[^\d]*(?:mg|milligram|milligrams)?""", RegexOption.IGNORE_CASE)
    )

    fun parseAverage(match: MatchResult): Float {
        val first = match.groups[1]?.value?.toFloatOrNull() ?: 0f
        val second = match.groups[2]?.value?.toFloatOrNull()
        return if (second != null) (first + second) / 2 else first
    }

    fun avgOrNull(matches: List<MatchResult>): Float? {
        return if (matches.isEmpty()) null
        else matches.map(::parseAverage).average().toFloat()
    }

    val calories = avgOrNull(regexMap["calories"]?.findAll(text)?.toList() ?: emptyList())
    val proteins = avgOrNull(regexMap["proteins"]?.findAll(text)?.toList() ?: emptyList())
    val fats = avgOrNull(regexMap["fats"]?.findAll(text)?.toList() ?: emptyList())
    val vitaminC = regexMap["vitaminC"]
        ?.findAll(text)
        ?.mapNotNull { it.groups[1]?.value?.toFloatOrNull() }
        ?.average()
        ?.toFloat()

    // 2. 음식 이름 파싱
    val foodName = Regex("""\*\*Food[:：]?\*\*\s*(.*?)(?:\n|$)""").find(text)?.groups?.get(1)?.value?.trim()
        ?: Regex("""Food[:：]?\s*(.*?)(?:\n|$)""").find(text)?.groups?.get(1)?.value?.trim()
        ?: "Unnamed Food"

    // 3. 설명 파싱 (Improvement 또는 Notes 등)
    val improvementSection = Regex("""(?i)(Improvement[s]?:|Suggestion[s]?:)\s*\n(.*?)(\n\n|\z)""", RegexOption.DOT_MATCHES_ALL)
        .find(text)?.groups?.get(2)?.value

    val foodDescription = improvementSection
        ?.lines()
        ?.filter { it.isNotBlank() }
        ?.joinToString("\n") { it.replace(Regex("""^[\*\-\d\.]+\s*"""), "").trim() }
        ?: "No suggestions found"

    return FoodLog(
        foodId = "0",
        userId = userId,
        photoUrl = photoUrl,
        foodName = foodName,
        foodDescription = foodDescription,
        calories = calories,
        carbohydrates = null, // 없음
        proteins = proteins,
        fats = fats,
        vitamin = vitaminC,
        water = null,
        createdAt = LocalDateTime.now().toString()
    )
}

/*
@RequiresApi(Build.VERSION_CODES.O)
fun parseCondensedFoodLogFormat(text: String, userId: String, photoUrl: String? = null): FoodLog {
    val foodName = text.split("-").first()
    val calories = text.split("-").last().split(",").first()
    val caloriesFormat = Regex("""\d+(\.\d+)?""").find(calories)?.value?.toFloatOrNull()

    val proteins = text.split("-").last().split(",").getOrNull(1)
    val proteinsFormat = Regex("""\d+(\.\d+)?""").find(proteins ?: "")?.value?.toFloatOrNull()

    val fats = text.split("-").last().split(",").getOrNull(2)
    val fatsFormat = Regex("""\d+(\.\d+)?""").find(fats ?: "")?.value?.toFloatOrNull()

    val vitaminC = text.split("-").last().split(",").getOrNull(3)
    val vitaminCFormat = Regex("""\d+(\.\d+)?""").find(vitaminC ?: "")?.value?.toFloatOrNull()

    val description = text.split("-").last()

    Log.e("calories", caloriesFormat.toString())
    Log.e("proteins", proteinsFormat.toString())
    Log.e("fats", fatsFormat.toString())
    Log.e("vitaminC", vitaminCFormat.toString())
    Log.e("description", description)
    Log.e("foodName", foodName)

    return FoodLog(
        foodId = "0",
        userId = userId,
        photoUrl = photoUrl,
        foodName = foodName,
        foodDescription = description,
        calories = caloriesFormat,
        carbohydrates = null,
        proteins = proteinsFormat,
        fats = fatsFormat,
        vitamin = vitaminCFormat,
        water = null,
        createdAt = LocalDateTime.now().toString()
    )
}*/

@RequiresApi(Build.VERSION_CODES.O)
fun parseCondensedFoodLogFormat(text: String, userId: String, photoUrl: String? = null): FoodLog {
    val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

    val foodName = lines.firstOrNull { it.startsWith("Food name:", true) }
        ?.substringAfter("Food name:", "")
        ?.trim()
        ?: "Unknown"

    val calories = lines.firstOrNull { it.startsWith("Calories", true) }
        ?.let { Regex("""\d+(\.\d+)?""").find(it)?.value?.toFloatOrNull() }

    val proteins = lines.firstOrNull { it.startsWith("Protein", true) }
        ?.let { Regex("""\d+(\.\d+)?""").find(it)?.value?.toFloatOrNull() }

    val fats = lines.firstOrNull { it.startsWith("Fat", true) }
        ?.let { Regex("""\d+(\.\d+)?""").find(it)?.value?.toFloatOrNull() }

    val vitamins = lines.firstOrNull { it.startsWith("Vitamins", true) }
        ?.let {
            val value = Regex("""\d+(\.\d+)?""").find(it)?.value?.toFloatOrNull()
            if (value != null) value else if (it.lowercase().contains("negligible")) 0f else null
        }

    val improvementDescription = lines.firstOrNull { it.startsWith("Improvements", ignoreCase = true) }
        ?.substringAfter(":")
        ?.trim()
        ?: ""

    val fullDescription = buildString {
        append("Calories: ${calories ?: "N/A"}, ")
        append("Protein: ${proteins ?: "N/A"}, ")
        append("Fat: ${fats ?: "N/A"}, ")
        append("Vitamins: ${vitamins ?: "N/A"}.\n")
        append("Improvement: $improvementDescription")
    }

    return FoodLog(
        foodId = "0",
        userId = userId,
        photoUrl = photoUrl,
        foodName = foodName,
        foodDescription = fullDescription,
        calories = calories,
        carbohydrates = null,
        proteins = proteins,
        fats = fats,
        vitamin = vitamins,
        water = null,
        createdAt = LocalDateTime.now().toString()
    )
}
