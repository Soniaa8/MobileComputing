package com.codemave.mobilecomputing.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun Help2(onBackPress: () -> Unit, navController: NavController) {

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar {
                IconButton(
                    onClick = {navController.navigate("Login")}
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(text = "Help (2/3)")
            }
            Spacer(modifier = Modifier.height(220.dp))

            Text(text = " (1). You can create a new reminder. Just click the + button.")
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = " (2). Also you can see all your reminders packed in the different categories.")

            Spacer(modifier = Modifier.height(180.dp))

            BottomAppBar() {
                Spacer(modifier = Modifier.width(110.dp))
                IconButton(
                    onClick = onBackPress
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                IconButton(
                    onClick = {navController.navigate("Help3")}
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                }
            }


        }
    }
}






