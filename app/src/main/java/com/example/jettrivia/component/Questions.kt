package com.example.jettrivia.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.screens.QuestionsViewModel
import com.example.jettrivia.utils.AppColors


@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()

    val questionIndex = remember {
        mutableStateOf(0)
    }

    val correctAnswersTillNow = remember {
        mutableStateOf(0)
    }

    if (viewModel.data.value.loading == true) {
        Surface {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        if (questions != null) {
            QuestionDisplay(
                questions[questionIndex.value],
                questionIndex,
                totalCount = questions.size,
                correctQuestionsTillNow = correctAnswersTillNow
            ) {
                questionIndex.value = questionIndex.value + 1
                if (it == 1) {
                    // Answer Correct
                    correctAnswersTillNow.value = correctAnswersTillNow.value + 1
                }
            }
        }
    }
}

@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    totalCount: Int,
    correctQuestionsTillNow: MutableState<Int>,
    onNextClicked: (Int) -> Unit = {},
) {
    var choices = remember(question) {
        question.choices.toMutableList()
    }

    val isOptionSubmitted = remember {
        mutableStateOf(false)
    }

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .fillMaxSize(),
        color = AppColors.mDarkPurple
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            ShowProgress(score = correctQuestionsTillNow.value)
            QuestionTracker(questionIndex, totalCount)
            DrawDottedLine(pathEffect)
            QuestionBody(question, choices, isOptionSubmitted)
            if (isOptionSubmitted.value) {
                Button(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .align(Alignment.CenterHorizontally), onClick = {
                        onNextClicked(questionIndex.value)
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.mLightBlue)
                ) {
                    Text("Next", color = Color.White, fontSize = 17.sp)
                }
            }
        }
    }
}

@Preview
@Composable
fun ShowProgress(score: Int = 12) {
    val progressFactor = remember(score) {
        mutableStateOf(score * 0.005f)
    }

    val gradient = Brush.linearGradient(colors = listOf(Color(0xFFF95075), Color(0xFFBE6BE5)))
    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppColors.mLightPurple,
                        AppColors.mLightPurple
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            )
            .clip(
                RoundedCornerShape(
                    topStartPercent = 50,
                    topEndPercent = 50,
                    bottomStartPercent = 50,
                    bottomEndPercent = 50
                )
            )
            .background(color = Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {},
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier
                .fillMaxWidth(progressFactor.value)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent
            )
        ) {
            Text(
                text = (score * 10).toString(),
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(6.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuestionBody(
    question: QuestionItem,
    options: MutableList<String>,
    isOptionSubmitted: MutableState<Boolean>
) {
    // The option that is selected
    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }

    // The answer is correct or not
    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }

    // Indicates if radio button enabled or not
    val radioEnabled = remember(question) {
        mutableStateOf<Boolean?>(true)
    }

    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
        }
    }

    Column {
        Text(
            text = question.question,
            modifier = Modifier
                .padding(6.dp)
                .align(Alignment.Start)
                .fillMaxHeight(0.3f),
            fontSize = 17.sp,
            color = AppColors.mOffWhite, fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
        )
        options.forEachIndexed { index, answerText ->
            Row(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth()
                    .height(45.dp)
                    .border(
                        width = 4.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AppColors.mDarkPurple,
                                AppColors.mDarkPurple
                            )
                        ),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clip(
                        RoundedCornerShape(
                            topStartPercent = 50,
                            topEndPercent = 50,
                            bottomEndPercent = 50,
                            bottomStartPercent = 50
                        )
                    )
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = answerState.value == index,
                    onClick = { updateAnswer(index) },
                    enabled = radioEnabled.value!!,
                    modifier = Modifier.padding(start = 16.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = if (correctAnswerState.value == true && index == answerState.value) {
                            Color.Green.copy(alpha = 0.2f)
                        } else {
                            Color.Red.copy(alpha = 0.2f)
                        }
                    )
                )
                Text(
                    answerText,
                    modifier = Modifier.padding(start = 15.dp), color =
                    if (correctAnswerState.value != null && index == answerState.value) {
                        if (correctAnswerState.value == true) {
                            Color.Green.copy(alpha = 0.2f)
                        } else {
                            Color.Red.copy(alpha = 0.2f)
                        }
                    } else {
                        Color.White
                    }
                )
            }
        }
        if (answerState.value != null) {
            Button(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .align(Alignment.CenterHorizontally), onClick = {
                    correctAnswerState.value = options[answerState.value!!] == question.answer
                    radioEnabled.value = false
                    isOptionSubmitted.value = true
                }, colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.mLightBlue)
            ) {
                Text("Submit", color = Color.White, fontSize = 17.sp)
            }
        }
    }
}

@Composable
fun QuestionTracker(counter: MutableState<Int>, totalCount: Int = 100) {
    Text(text = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = AppColors.mLightGray,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp
            )
        ) {
            append("Question ${counter.value + 1}/")
        }
        withStyle(
            style = SpanStyle(
                color = AppColors.mLightGray,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        ) {
            append(totalCount.toString())
        }
    }, modifier = Modifier.padding(20.dp))
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(x = 0f, y = 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}