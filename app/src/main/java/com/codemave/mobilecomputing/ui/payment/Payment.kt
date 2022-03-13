package com.codemave.mobilecomputing.ui.payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.codemave.mobilecomputing.data.entity.Category
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


@Composable
fun Payment(
    onBackPress: () -> Unit,
    viewModel: PaymentViewModel = viewModel(),
    navController: NavController
) {
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val title = rememberSaveable { mutableStateOf("") }
    val category = rememberSaveable { mutableStateOf("") }
    val date = rememberSaveable { mutableStateOf("") }
    val notifications = rememberSaveable { mutableStateOf("") }
    val latlng = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<LatLng>("location_data")
        ?.value

    val flatlng = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<LatLng>("flocation_data")
        ?.value


    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar {
                IconButton(
                    onClick = onBackPress
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(text = "Reminder")
                Spacer(modifier = Modifier.width(180.dp))
                IconButton(
                    onClick = {
                        navController.navigate("mapF")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = null
                    )
                }

            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text(text = "Reminder name")},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                CategoryListDropdown(
                    viewState = viewState,
                    category = category
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = date.value,
                    onValueChange = { date.value = it },
                    label = { Text(text = "Date")},
                    placeholder =  { Text(text = "yyyy-MM-dd--hh-mm")},
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = notifications.value,
                    onValueChange = { notifications.value = it },
                    label = { Text(text = "Number of Notifications")},
                    placeholder =  { Text(text = "0-1-2")},
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (latlng == null) {
                    OutlinedButton(
                        onClick = { navController.navigate("map") },
                        modifier = Modifier.height(55.dp)
                    ) {
                        Text(text = "Reminder location")
                    }
                } else {
                    Text(
                        text = "Lat: ${latlng.latitude}, \nLng: ${latlng.longitude}"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                val dateformat = SimpleDateFormat("yyyy-MM-dd--hh-mm")
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    enabled = true,
                    onClick = {
                        coroutineScope.launch {
                            if (latlng != null && flatlng != null) {
                                viewModel.savePayment(
                                    com.codemave.mobilecomputing.data.entity.Payment(
                                        paymentTitle = title.value,
                                        paymentAmount = 0.0, // amount.value.toDouble(),
                                        paymentDate = dateformat.parse(date.value).getTime(),
                                        paymentCategoryId = getCategoryId(viewState.categories, category.value),
                                        paymentActive = false,
                                        paymentHowManyNotifications = notifications.value.toInt(),
                                        paymentLocationX = latlng.latitude,
                                        paymentLocationY = latlng.longitude,
                                        locationX = flatlng.latitude,
                                        locationY = flatlng.longitude
                                    )
                                )
                            }
                            else{
                                viewModel.savePayment(
                                    com.codemave.mobilecomputing.data.entity.Payment(
                                        paymentTitle = title.value,
                                        paymentAmount = 0.0, // amount.value.toDouble(),
                                        paymentDate = dateformat.parse(date.value).getTime(),
                                        paymentCategoryId = getCategoryId(viewState.categories, category.value),
                                        paymentActive = false,
                                        paymentHowManyNotifications = notifications.value.toInt(),
                                        paymentLocationX = 0.0,
                                        paymentLocationY = 0.0,
                                        locationX= 100.0,
                                        locationY= 100.0
                                    )
                                )
                            }
                        }
                        onBackPress()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp)
                ) {
                    Text("Save reminder")
                }
            }
        }
    }
}

private fun getCategoryId(categories: List<Category>, categoryName: String): Long {
    return categories.first { category -> category.name == categoryName }.id
}

@Composable
private fun CategoryListDropdown(
    viewState: PaymentViewState,
    category: MutableState<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) {
        Icons.Filled.ArrowDropUp // requires androidx.compose.material:material-icons-extended dependency
    } else {
        Icons.Filled.ArrowDropDown
    }

    Column {
        OutlinedTextField(
            value = category.value,
            onValueChange = { category.value = it},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Category") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            viewState.categories.forEach { dropDownOption ->
                DropdownMenuItem(
                    onClick = {
                        category.value = dropDownOption.name
                        expanded = false
                    }
                ) {
                    Text(text = dropDownOption.name)
                }

            }
        }
    }
}

//internal fun String.toDateLong(): Long { //antes era private
//    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd--hh-mm", Locale.getDefault())
//    //val nDate = LocalDate.parse(this, formatter).atStartOfDay() //.atTime(9, 0)
//    //return nDate.toMillis()
//    return formatter.toMillis()
//}
//
//fun toMillis()

//fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) = atZone(zone).toInstant().toEpochMilli()
