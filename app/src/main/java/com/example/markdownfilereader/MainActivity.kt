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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
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
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.bufferedReader().use { reader ->
                markdownContent = reader?.readText()
            }
        }
    }

    MarkdownScreen(
        modifier = modifier,
        markdownContent = markdownContent,
        onSelectFile = { launcher.launch("text/markdown") },
        onClose = { markdownContent = null }
    )
}

@Composable
fun MarkdownScreen(
    modifier: Modifier = Modifier,
    markdownContent: String?,
    onSelectFile: () -> Unit,
    onClose: () -> Unit
) {
    if (markdownContent == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = onSelectFile) {
                Text("Select Markdown File")
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RichText {
                    Markdown(content = markdownContent)
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close document"
                )
            }
        }
    }
}

@Preview(name = "File Picker View")
@Composable
fun PreviewFilePicker() {
    MarkdownFileReaderTheme {
        MarkdownScreen(markdownContent = null, onSelectFile = {}, onClose = {})
    }
}

@Preview(name = "Markdown View")
@Composable
fun PreviewMarkdown() {
    MarkdownFileReaderTheme {
        MarkdownScreen(
            markdownContent = "# Hello\n\nThis is a preview.",
            onSelectFile = {},
            onClose = {}
        )
    }
}
