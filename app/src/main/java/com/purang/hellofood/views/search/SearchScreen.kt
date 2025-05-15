package com.purang.hellofood.views.search

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.purang.hellofood.ui.theme.mintColor5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    navController : NavController
) {

    var searchValue by remember {
        mutableStateOf("")
    }

    //false가 검색창으로 변경, true = 최근검색어창
    var isRecentQuery by remember {
        mutableStateOf(true)
    }

    val focusManager = LocalFocusManager.current
    // FocusRequester 생성
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically, // 수직 정렬: 가운데 정렬
            horizontalArrangement = Arrangement.Start // 가로 방향 정렬: 시작 정렬
        ) {
            // 뒤로가기 버튼
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp) // 아이콘 크기 조정
                )
            }

            SearchEditText(
                searchValue = searchValue,
                focusRequester = focusRequester,
                onTextChange = {
                    searchValue = it
                },
                onDone = {
                    isRecentQuery = false
                },
                onClear = {
                    isRecentQuery = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEditText(
    searchValue: String,
    focusRequester: FocusRequester,
    onTextChange: (String) -> Unit,
    onDone : (Boolean) -> Unit,
    onClear : () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = searchValue,
        onValueChange = { onTextChange(it) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp) // 뒤로가기 버튼과의 간격 조정
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .focusRequester(focusRequester)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done // 완료 버튼 표시
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onDone(true)
            }
        ),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = searchValue,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                isError = false,
                label = null,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = {
                    Text(
                        text = "Please enter your search term",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 8.sp
                    )
                },
                leadingIcon = null,
                trailingIcon = {
                    if (searchValue.isNotEmpty()) {
                        IconButton(onClick = {
                            onTextChange("")
                            onClear()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            )
        }
    )
}