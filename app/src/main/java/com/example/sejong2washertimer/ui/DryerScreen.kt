package com.example.sejong2washertimer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sejong2washertimer.data.Datasource
import com.example.sejong2washertimer.model.Dryer

@Composable
fun DryerList(
    dryerList: List<Dryer>,
    modifier: Modifier = Modifier
) {
    Column {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = modifier) {
            items(dryerList) {
                    dryer -> DryerCard(
                dryer = dryer,
                modifier= Modifier.padding(8.dp)
            )
            }
        }
    }
}
@Composable
fun DryerCard(
    dryer: Dryer,
    modifier: Modifier = Modifier
){
    Card {
        Row(
            horizontalArrangement= Arrangement.Center,
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
fun DryerApp() {
    DryerList(dryerList = Datasource().dryers)

}