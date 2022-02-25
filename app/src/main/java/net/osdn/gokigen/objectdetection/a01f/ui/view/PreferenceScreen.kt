package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.osdn.gokigen.objectdetection.a01f.R


@Composable
fun PreferenceScreen(navController: NavHostController)
{
    Row(verticalAlignment = Alignment.CenterVertically,)
    {
        Button(onClick = { navController.navigate("LiveViewScreen") }) {
            Text(text = "Navigate next")
        }

        Common(
            text = "this is Config View",
            action = { navController.navigate("LiveViewScreen") }
        )

        Spacer(modifier = Modifier.width(12.dp))

    }
}

@Composable
fun Common(text: String, action: () -> Unit)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "TopAppBar") },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = action) {
                        Image(
                            painter = painterResource(R.drawable.a01f),
                            contentDescription = "sample0 picture",
                            modifier = Modifier
                                .clickable {
                                    Log.v(
                                        ViewRootComponent::class.java.simpleName,
                                        "onClick!"
                                    )
                                }
                                .size(80.dp, 80.dp)
                                .border(1.5.dp, MaterialTheme.colors.secondary)
                                .padding(4.dp)
                        )
                    }
                }
            )
        },
        content = {
            ShowText(text = text)
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = action,
                content = { Icon(Icons.Filled.Add, contentDescription = "") }
            )
        },
        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.background,
                cutoutShape = CircleShape
            ) {

            }
        }
    )
}

@Composable
fun ShowText(text: String) {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = text,
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    start = 36.dp,
                    end = 36.dp
                )
        )
    }
}
