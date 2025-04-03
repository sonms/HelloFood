package com.purang.hellofood.views.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.purang.hellofood.ui.theme.TextColorGray
import com.purang.hellofood.ui.theme.blueExercise2
import com.purang.hellofood.ui.theme.greenFoodColor1
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.ui.theme.greenFoodColor3
import com.purang.hellofood.ui.theme.mintColor3
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CameraScreen(
    navController: NavController
) {
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
                // TODO: 분석 기능 연결
                navController.navigate("analysis?imageUri=$selectedImage")
            }
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