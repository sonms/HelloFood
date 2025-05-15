package com.purang.hellofood.viewmodels

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.purang.hellofood.BuildConfig
import com.purang.hellofood.views.camera.analysis.GeminiUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.type.FinishReason
import com.purang.hellofood.models.FoodLog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*@HiltViewModel
class GeminiViewModel @Inject constructor(

) {
    val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-2.0-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.GEMINI_API
    )

    suspend fun generateContent(prompt: String): GenerateContentResponse =
        generateContent(content { text(prompt) }.toString())


    *//*suspend fun generateContent(vararg prompt: Content): GenerateContentResponse =
                try {
                    controller.generateContent(constructRequest(*prompt)).toPublic().validate()
                } catch (e: Throwable) {
                    throw GoogleGenerativeAIException.from(e)
                }*//*

}*/
@HiltViewModel
class GeminiViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow<GeminiUiState>(GeminiUiState.Initial)
    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val _responseText = MutableStateFlow("")
    val responseText = _responseText.asStateFlow()

    private val _responseRecipeText = MutableStateFlow("")
    val responseRecipeText = _responseRecipeText.asStateFlow()

    private val _responseFoodLog = MutableStateFlow<FoodLog?>(null)
    val responseFoodLog = _responseFoodLog.asStateFlow()

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun updateSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun clearResponse() {
        _responseText.value = ""
    }
    fun fetchFoodLog(data : FoodLog?) {
        _responseFoodLog.value = data
    }
    fun fetchUIState(state : GeminiUiState) {
        _uiState.value = state
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageWithImage(context: Context, imageUri: Uri?, prompt: String) {
        if (imageUri == null) {
            _responseText.value = "Please select an image"
            return
        }

        _uiState.value = GeminiUiState.Loading

        viewModelScope.launch {
            try {
                val geminiApiKey = BuildConfig.GEMINI_API // API 키 불러오기

                // 안전 설정
                val safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE ),
                    SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
                )

                // Gemini Pro Vision 모델 설정
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = geminiApiKey,
                    safetySettings = safetySettings
                )

                // 이미지를 비트맵으로 변환
                val inputStream: java.io.InputStream = context.contentResolver.openInputStream(imageUri)
                    ?: throw Exception("Unable to load image")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                // 비트맵을 MIME 타입과 바이트 배열로 변환
                /*val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()*/

                // Gemini에 전송할 컨텐츠 생성
                val content = content {
                    image(bitmap)
                    text(prompt)
                }

                // 응답 생성
                val response = generativeModel.generateContent(content)
                val candidate = response.candidates.firstOrNull()
                val finishReason = candidate?.finishReason

                when (finishReason) {
                    FinishReason.STOP -> {
                        _responseText.value = response.text ?: "No response received"

                        /*if (response.text?.isNotEmpty() == true) {
                            _responseFoodLog.value = parseFoodLogFromNutritionText(response.text!!, FirebaseUserManager.userId.toString(),
                                imageUri.toString()
                            )
                        }*/
                        _selectedImageUri.value = imageUri
                        _uiState.value = GeminiUiState.Success
                    }
                    FinishReason.MAX_TOKENS -> {
                        _responseText.value = (response.text ?: "") + "\n(The response was truncated because the maximum number of tokens was reached)"
//                        if (response.text?.isNotEmpty() == true) {
//                            _responseFoodLog.value = parseFoodLogFromNutritionText(response.text!!, FirebaseUserManager.userId.toString(),
//                                imageUri.toString()
//                            )
//                        }
                        _selectedImageUri.value = imageUri
                        _uiState.value = GeminiUiState.Success
                    }
                    FinishReason.SAFETY -> {
                        _responseText.value = "Your response was blocked by a security filter"
                        /*if (response.text?.isNotEmpty() == true) {
                            _responseFoodLog.value = parseFoodLogFromNutritionText(response.text!!, FirebaseUserManager.userId.toString(),
                                imageUri.toString()
                            )
                        }*/
                        _selectedImageUri.value = imageUri
                        _uiState.value = GeminiUiState.Error("Your response was blocked by a security filter")
                    }
                    FinishReason.RECITATION -> {
                        _responseText.value = (response.text ?: "") + "\n(Response stopped due to citation policy)"
                       /* if (response.text?.isNotEmpty() == true) {
                            _responseFoodLog.value = parseFoodLogFromNutritionText(response.text!!, FirebaseUserManager.userId.toString(),
                                imageUri.toString()
                            )
                        }*/
                        _selectedImageUri.value = imageUri
                        _uiState.value = GeminiUiState.Success
                    }
                    FinishReason.OTHER -> {
                        _responseText.value = response.text ?: "Response stopped for unknown reasons"
                        /*if (response.text?.isNotEmpty() == true) {
                            _responseFoodLog.value = parseFoodLogFromNutritionText(response.text!!, FirebaseUserManager.userId.toString(),
                                imageUri.toString()
                            )
                        }*/
                        _selectedImageUri.value = imageUri
                        _uiState.value = GeminiUiState.Success
                    }
                    else -> {
                        _responseText.value = response.text ?: "No response received"
                        /*if (response.text?.isNotEmpty() == true) {
                            _responseFoodLog.value = parseFoodLogFromNutritionText(response.text!!, FirebaseUserManager.userId.toString(),
                                imageUri.toString()
                            )
                        }*/
                        _selectedImageUri.value = imageUri
                        _uiState.value = GeminiUiState.Success
                    }
                }
            } catch (e: Exception) {
                _uiState.value = GeminiUiState.Error(e.localizedMessage ?: "An error occurred")
                _responseText.value = "Error: ${e.localizedMessage}"
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageWithText(prompt: String) {
        _uiState.value = GeminiUiState.Loading

        viewModelScope.launch {
            try {
                val geminiApiKey = BuildConfig.GEMINI_API

                val safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
                )

                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash", // or gemini-1.0-pro, depending on need
                    apiKey = geminiApiKey,
                    safetySettings = safetySettings
                )

                val content = content {
                    text(prompt)
                }

                val response = generativeModel.generateContent(content)
                val candidate = response.candidates.firstOrNull()
                val finishReason = candidate?.finishReason

                when (finishReason) {
                    FinishReason.STOP,
                    FinishReason.MAX_TOKENS,
                    FinishReason.RECITATION,
                    FinishReason.OTHER,
                    null -> {
                        _responseText.value = response.text ?: "No response received"
                        _uiState.value = GeminiUiState.Success
                        Log.e("_responseText n", _responseText.value)
                    }
                    FinishReason.SAFETY -> {
                        _responseText.value = "Your response was blocked by a security filter"
                        _uiState.value = GeminiUiState.Error("Blocked by a security filter")
                        //Log.e("_responseText s", _responseText.value)
                    }

                    else -> {

                    }
                }

            } catch (e: Exception) {
                _responseText.value = "Error: ${e.localizedMessage}"
                //Log.e("_responseText e", _responseText.value)
                _uiState.value = GeminiUiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageWithTextRecipe(prompt: String) {
        _uiState.value = GeminiUiState.Loading

        viewModelScope.launch {
            try {
                val geminiApiKey = BuildConfig.GEMINI_API

                val safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
                )

                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash", // or gemini-1.0-pro, depending on need
                    apiKey = geminiApiKey,
                    safetySettings = safetySettings
                )

                val content = content {
                    text(prompt)
                }

                val response = generativeModel.generateContent(content)
                val candidate = response.candidates.firstOrNull()
                val finishReason = candidate?.finishReason

                when (finishReason) {
                    FinishReason.STOP,
                    FinishReason.MAX_TOKENS,
                    FinishReason.RECITATION,
                    FinishReason.OTHER,
                    null -> {
                        _responseRecipeText.value = response.text ?: "No response received"
                        _uiState.value = GeminiUiState.Success
                        Log.e("_responseRecipeText n", _responseRecipeText.value)
                    }
                    FinishReason.SAFETY -> {
                        _responseRecipeText.value = "Your response was blocked by a security filter"
                        _uiState.value = GeminiUiState.Error("Blocked by a security filter")
                        //Log.e("_responseText s", _responseText.value)
                    }

                    else -> {

                    }
                }

            } catch (e: Exception) {
                _responseRecipeText.value = "Error: ${e.localizedMessage}"
                //Log.e("_responseText e", _responseText.value)
                _uiState.value = GeminiUiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }
}

