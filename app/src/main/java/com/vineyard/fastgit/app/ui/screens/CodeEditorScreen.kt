package com.vineyard.fastgit.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vineyard.fastgit.app.models.FileItem
import com.vineyard.fastgit.app.ui.theme.*
import com.vineyard.fastgit.app.utils.SyntaxHighlighter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(
    fileItem: FileItem,
    initialContent: String,
    onBack: () -> Unit,
    onSaveAndCommit: (updatedContent: String, commitMessage: String) -> Unit,
    onDownloadClick: (content: String) -> Unit
) {
    val context = LocalContext.current

    // Keying these state variables to initialContent ensures that when the asynchronous 
    // network call finishes loading, the state resets from empty to the loaded file content.
    var codeText by remember(initialContent) { mutableStateOf(initialContent) }
    var undoStack by remember(initialContent) { mutableStateOf(listOf(initialContent)) }
    var redoStack by remember(initialContent) { mutableStateOf(listOf<String>()) }
    var showCommitDialog by remember { mutableStateOf(false) }

    val lines = codeText.split("\n")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = fileItem.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = fileItem.path,
                            fontSize = 11.sp,
                            color = GhTextSecondaryDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Copy Code Button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Copied Code", codeText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = Color.White)
                    }

                    // Download File Button
                    IconButton(
                        onClick = {
                            onDownloadClick(codeText)
                        }
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download File", tint = Color.White)
                    }

                    // Undo
                    IconButton(
                        onClick = {
                            if (undoStack.size > 1) {
                                val current = undoStack.last()
                                redoStack = redoStack + current
                                val prev = undoStack[undoStack.size - 2]
                                undoStack = undoStack.dropLast(1)
                                codeText = prev
                            }
                        },
                        enabled = undoStack.size > 1
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = if (undoStack.size > 1) Color.White else GhTextSecondaryDark)
                    }

                    // Redo
                    IconButton(
                        onClick = {
                            if (redoStack.isNotEmpty()) {
                                val next = redoStack.last()
                                redoStack = redoStack.dropLast(1)
                                undoStack = undoStack + next
                                codeText = next
                            }
                        },
                        enabled = redoStack.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = "Redo", tint = if (redoStack.isNotEmpty()) Color.White else GhTextSecondaryDark)
                    }

                    // Save & Commit Button
                    IconButton(onClick = { showCommitDialog = true }) {
                        Icon(Icons.Default.Check, contentDescription = "Commit Changes", tint = GhSuccessGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GhSurfaceDark)
            )
        },
        containerColor = Color(0xFF0D1117)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val verticalScrollState = rememberScrollState()
            val horizontalScrollState = rememberScrollState()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Line Numbers Column
                Column(
                    modifier = Modifier
                        .background(Color(0xFF161B22))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lines.size) {
                        Text(
                            text = "$i",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = GhTextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Editor Code Area with Syntax Highlighting
                BasicTextField(
                    value = codeText,
                    onValueChange = { newText ->
                        codeText = newText
                        undoStack = undoStack + newText
                        redoStack = emptyList()
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = Color(0xFFC9D1D9)
                    ),
                    cursorBrush = SolidColor(GhAccentBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    visualTransformation = {
                        androidx.compose.ui.text.input.TransformedText(
                            SyntaxHighlighter.highlight(it.text, fileItem.name),
                            androidx.compose.ui.text.input.OffsetMapping.Identity
                        )
                    }
                )
            }
        }
    }

    // Commit Message Entry Dialog
    if (showCommitDialog) {
        var commitMsg by remember { mutableStateOf("Update ${fileItem.name}") }

        AlertDialog(
            onDismissRequest = { showCommitDialog = false },
            title = { Text("Commit Changes", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter a commit message for this update:", fontSize = 13.sp, color = GhTextSecondaryDark)
                    OutlinedTextField(
                        value = commitMsg,
                        onValueChange = { commitMsg = it },
                        label = { Text("Commit Message") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCommitDialog = false
                        onSaveAndCommit(codeText, commitMsg)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GhSuccessGreen)
                ) {
                    Text("Commit & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommitDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = GhSurfaceDark
        )
    }
}