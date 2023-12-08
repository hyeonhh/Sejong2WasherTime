package com.example.sejong2washertimer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sejong2washertimer.data.Datasource
import com.example.sejong2washertimer.model.Washer

enum class RoutingScreen() {
    Timer,
    Washer,
    Dryer
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WasherApp(
    modifier: Modifier=Modifier
) {
    val navController= rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RoutingScreen.Washer.name,
    ){
        composable(RoutingScreen.Washer.name) {
            WasherList(washerList = Datasource().washers, navController= navController)
        }

        composable(RoutingScreen.Timer.name) {
            TimerScreen()
        }
        composable(RoutingScreen.Dryer.name){
            DryerApp()
        }
    }

}



@Composable
fun WasherList(
    navController: NavController,
    washerList: List<Washer>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier=modifier
    ) {
        items(washerList) {
                washer -> WasherCard(

            washer = washer,
            modifier= Modifier
                .padding(8.dp)
                .clickable { navController.navigate("${RoutingScreen.Timer.name}") }
        )
        }
    }
}
@Composable
fun WasherCard(
    washer: Washer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier=modifier

    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = washer.washerImageResourceId),
                    contentDescription = stringResource(id = washer.washerStringResourceId),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = stringResource(id = washer.washerStringResourceId),
                )
                Spacer(modifier = Modifier.size(20.dp))

            }
            Spacer(modifier = Modifier.size(80.dp))

            if(washer.isAvailable) {
                Text(
                    text = "✅사용 가능",
                    style = TextStyle(fontSize = 13.sp)
                    )
            }
            else{
                Text(text = "⌛30분 남았어요!",
                    style = TextStyle(fontSize = 13.sp)

                )
            }
        }
    }

}
