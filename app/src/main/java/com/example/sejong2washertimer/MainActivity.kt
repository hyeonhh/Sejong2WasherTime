package com.example.sejong2washertimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.W
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sejong2washertimer.data.Datasource
import com.example.sejong2washertimer.model.Dryer
import com.example.sejong2washertimer.model.Washer
import com.example.sejong2washertimer.ui.theme.Sejong2WasherTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Sejong2WasherTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WasherApp()

                }

            }
        }
    }
}

@Composable
fun WasherList(
    washerList: List<Washer>,
    modifier: Modifier=Modifier
) {
    LazyColumn(modifier=modifier) {
        items(washerList) {
            washer -> WasherCard(
            washer = washer,
                modifier=Modifier.padding(8.dp)
                )
        }
    }
}
@Composable
fun WasherCard(
    washer:Washer,
    modifier: Modifier=Modifier
) {
    Card(modifier=modifier) {
        Row(
            horizontalArrangement=Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(painter = painterResource(id = washer.washerImageResourceId),
                contentDescription = stringResource(id = washer.washerStringResourceId),
                contentScale = ContentScale.Crop
                    )
            Text(
                text = stringResource(id = washer.washerStringResourceId),
                )
            Spacer(modifier = Modifier.size(20.dp))
            Text(text = "남은 시간 : ")
            Text(
                text = washer.washerRemainedTime.toString())
            Text(text = "분")
        }
    }

}

@Composable
fun DryerList(
    dryerList: List<Dryer>,
    modifier: Modifier=Modifier
) {
    Column {
        LazyColumn(modifier = modifier) {
            items(dryerList) {
                dryer -> DryerCard(
                dryer = dryer,
                    modifier=Modifier.padding(8.dp)
                    )
            }
        }
    }
}
@Composable
fun DryerCard(
    dryer: Dryer,
    modifier: Modifier=Modifier
){
  Card {
      Row(
          horizontalArrangement=Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
      ) {

          Image(painter = painterResource(id = dryer.dryerImageResourceId),
              contentDescription = stringResource(id = dryer.dryerResourceId),
              contentScale = ContentScale.Crop
          )
          Text(
              text = stringResource(id = dryer.dryerResourceId),
          )
          Spacer(modifier = Modifier.size(20.dp))
          Text(text = "남은 시간 : ")
          Text(
              text = dryer.dryerRemainedTime.toString())
          Text(text = "분")
      }
  }
}

@Composable
fun WasherApp() {
    WasherList(washerList = Datasource().washers)
}

@Composable
fun DryerApp() {
    DryerList(dryerList = Datasource().dryers)

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Sejong2WasherTimerTheme {
        WasherApp()
    }
}