package com.example.markdownfilereader

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.markdownfilereader.ui.theme.MarkdownFileReaderTheme
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownFileReaderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MarkdownRenderer(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MarkdownRenderer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var markdownContent by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent())
    { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.bufferedReader().use { reader ->
                markdownContent = reader?.readText()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { launcher.launch("text/markdown") }
            ) {
                Text("Select Markdown File")
            }

            markdownContent?.let {
                RichText(modifier = modifier) {
                    Markdown(content = it)
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    MarkdownRenderer()
}
