package com.purang.hellofood.viewmodels

import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class GeminiViewModel @Inject constructor(

) {
    suspend fun generateContent(prompt: String): GenerateContentResponse =
        generateContent(content { text(prompt) }.toString())


    /*suspend fun generateContent(vararg prompt: Content): GenerateContentResponse =
        try {
            controller.generateContent(constructRequest(*prompt)).toPublic().validate()
        } catch (e: Throwable) {
            throw GoogleGenerativeAIException.from(e)
        }*/
}