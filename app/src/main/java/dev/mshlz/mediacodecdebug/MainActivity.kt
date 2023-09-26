package dev.mshlz.mediacodecdebug

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.mshlz.mediacodecdebug.ui.theme.MediaCodecDebugTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            MediaCodecDebugTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        SearchView(
                            modifier = Modifier.fillMaxWidth(),
                            state = textState,
                            placeHolder = "search by name, type.. hw=1, sw=1, enc=1"
                        )
                        getAll(textState.value.text)
                    }
                }
            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    modifier: Modifier = Modifier, state: MutableState<TextFieldValue>, placeHolder: String
) {
    TextField(
        modifier = modifier,
        value = state.value,
        placeholder = {
                      Text(text = placeHolder)
        },
        onValueChange = { value -> state.value = value })
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun getAll(filter: CharSequence?): Any {
    val list = ArrayList<MediaCodecInfo>()
    MediaCodecList(MediaCodecList.ALL_CODECS).let {
        list.addAll(it.codecInfos)
    }

    return LazyColumn {
        items(list.filter {
            if (filter.isNullOrEmpty()) {
                true
            } else {
                it.supportedTypes.joinToString().contains(
                    filter,
                    ignoreCase = true
                ) || it.name.contains(filter) || it.canonicalName.contains(filter) || if (filter.contains(
                        "hw=1"
                    )
                ) {
                    it.isHardwareAccelerated
                } else if (filter.contains("hw=0")) {
                    !it.isHardwareAccelerated
                } else if (filter.contains("sw=1")) {
                    it.isSoftwareOnly
                } else if (filter.contains("sw=0")) {
                    !it.isSoftwareOnly
                } else if (filter.contains("enc=1")) {
                    it.isEncoder
                } else if (filter.contains("enc=0")) {
                    !it.isEncoder
                } else {
                    false
                }
            }

        }) {
            Column(modifier = Modifier.padding(1.dp)) {
                Text(text = it.name)
                Text(text = "Canonical: $it.canonicalName", fontSize = 2.5.em)
                Row {
                    Text(text = "HW Accelerated: ${it.isHardwareAccelerated}", fontSize = 2.5.em)
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(text = "SW Only: ${it.isSoftwareOnly}", fontSize = 2.5.em)
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(text = "Encoder: ${it.isEncoder}", fontSize = 2.5.em)
                }
                Text(text = it.supportedTypes.joinToString(), fontSize = 2.5.em)
                Divider()
            }

        }
    }
}