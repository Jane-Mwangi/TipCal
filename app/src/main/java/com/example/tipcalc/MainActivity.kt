package com.example.tipcalc

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalc.Components.InputField
import com.example.tipcalc.ui.theme.TipCalcTheme
import com.example.tipcalc.utls.calculateTotalPerPerson
import com.example.tipcalc.utls.calculateTotalTip
import com.example.tipcalc.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalcTheme {
                MyApp {
                    TopHeader()
                    MainContent()
                }
            }
        }
    }
}

@Preview
@Composable
fun MyApp(content: @Composable () -> Unit = {}) {
// A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,

        ) {
        Column() {
//            TopHeader()
            MainContent()

        }

    }


}


@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(color = 0xFFE9D7F7)

    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }

}


@Preview
@Composable
fun MainContent() {
    BillForm() { billAmt ->
        Log.d("AMT", "MainContent:$billAmt ")
    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf(value = "")
    }
    val validState = remember(totalBillState) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPositionState = remember {
        mutableStateOf(value = 0f)
    }

    val splitByState = remember {
        mutableStateOf(0)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            InputField(
                valueState = totalBillState,
                labelId = "Entr Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    //Todo-onvaluechanged
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                },
                modifier = Modifier.background(color = Color.Cyan)
            )
//            if (validState) {
            Row(
                modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Split",
                    style = MaterialTheme.typography.h5.copy(color = Color.Black),
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )
                Spacer(modifier = Modifier.width(120.dp))
                IncrementDecrementButtons(
                    splitByState,
                    totalPerPersonState,
                    totalBillState,
                    tipPercentage,
                    range
                )
            }
            //Tip Row
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 3.dp,
                        vertical = 12.dp
                    )
            ) {
                Text(
                    text = "Text",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(
                    text = "$ ${tipAmountState.value}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage%")
                Spacer(modifier = Modifier.height(14.dp))

                //slider
                Slider(value = sliderPositionState.value,
                    onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        Log.d(TAG, "BillForm: ${totalBillState.value}")
                        tipAmountState.value =
                            calculateTotalTip(
                                totalBill = if (totalBillState.value.isNotEmpty()) totalBillState.value.toDouble() else 0.0,
                                tipPercentage
                            )
                        totalPerPersonState.value =
                            calculateTotalPerPerson(
                                totalBill = if (totalBillState.value.isNotEmpty()) totalBillState.value.toDouble() else 0.0,
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage
                            )
                    },
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp
                    ),
                    steps = 5,
                    onValueChangeFinished = {
                        //Log.d(TAG, "BillForm: ")
                    }

                )
            }
        }
    }
}


@Composable
private fun IncrementDecrementButtons(
    splitByState: MutableState<Int>,
    totalPerPersonState: MutableState<Double>,
    totalBillState: MutableState<String>,
    tipPercentage: Int,
    range: IntRange
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 3.dp),
        horizontalArrangement = Arrangement.End
    ) {
        RoundIconButton(imageVector = Icons.Default.Remove,
            onClick = {
                splitByState.value =
                    if (splitByState.value > 1) splitByState.value - 1 else 1

                totalPerPersonState.value =
                    calculateTotalPerPerson(
                        totalBill = if (totalBillState.value.isNotEmpty()) totalBillState.value.toDouble() else 0.0,
                        splitBy = splitByState.value,
                        tipPercentage = tipPercentage
                    )
            })

        Text(
            text = "${splitByState.value}",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 9.dp, end = 9.dp)
        )
        RoundIconButton(imageVector = Icons.Default.Add,
            onClick = {
                if (splitByState.value < range.last) {
                    splitByState.value < splitByState.value + 1
                    totalPerPersonState.value =
                        calculateTotalPerPerson(
                            totalBill = if (totalBillState.value.isNotEmpty()) totalBillState.value.toDouble() else 0.0 ,
                            splitBy = splitByState.value,
                            tipPercentage = tipPercentage
                        )
                }

            })
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalcTheme {
        MyApp {

        }
    }
}