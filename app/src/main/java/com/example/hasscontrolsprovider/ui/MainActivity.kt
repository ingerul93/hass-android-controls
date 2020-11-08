package com.example.hasscontrolsprovider.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val hassControl = object : HassControl {
//            override val entityId =
//                intent.getStringExtra(PendingIntentConstants.EXTRA_ENTITY_ID) ?: ""
//            override val availability = Availability.AVAILABLE
//            override val name = intent.getStringExtra(PendingIntentConstants.EXTRA_NAME) ?: ""
//            override val status = intent.getStringExtra(PendingIntentConstants.EXTRA_STATUS) ?: ""
//            override val lastChanged: ZonedDateTime = ZonedDateTime.now() // TODO
//        }
//
//        val hassControlLiveData: LiveData<HassControl> = MutableLiveData(hassControl)

        setContent {
            HassTheme {
                Button(
                    onClick = { startActivity(createShowkaseBrowserIntent(this)) }
                ) {
                    Text("Launch Showkase")
                }
            }
        }
    }
}

@Composable
fun HassTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = colorPalette) {
        content()
    }
}