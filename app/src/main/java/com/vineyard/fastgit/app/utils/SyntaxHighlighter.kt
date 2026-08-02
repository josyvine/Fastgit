package com.vineyard.fastgit.app.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import java.util.regex.Pattern

object SyntaxHighlighter {

    private val KEYWORD_COLOR = Color(0xFFFF7B72) // Coral red
    private val STRING_COLOR = Color(0xFFA5D6FF)  // Soft light blue
    private val COMMENT_COLOR = Color(0xFF8B949E) // Gray
    private val NUMBER_COLOR = Color(0xFF79C0FF)  // Cyan
    private val ANNOTATION_COLOR = Color(0xFFD2A8FF) // Purple
    private val DEFAULT_TEXT_COLOR = Color(0xFFC9D1D9) // Light off-white

    private val KEYWORDS = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "class", "const",
        "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
        "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
        "long", "native", "new", "package", "private", "protected", "public", "return", "short",
        "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while", "fun", "val", "var", "when", "sealed",
        "data", "object", "typealias", "override", "open", "internal", "companion", "lateinit",
        "by", "in", "is", "where", "suspend", "coroutine", "flow", "state", "recompose", "true", "false", "null"
    )

    fun highlight(code: String, fileName: String = ""): AnnotatedString {
        return buildAnnotatedString {
            val lines = code.split("\n")
            lines.forEachIndexed { index, line ->
                highlightLine(line)
                if (index < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    private fun AnnotatedString.Builder.highlightLine(line: String) {
        if (line.trimStart().startsWith("//") || line.trimStart().startsWith("#") || line.trimStart().startsWith("<!--")) {
            withStyle(SpanStyle(color = COMMENT_COLOR, fontFamily = FontFamily.Monospace)) {
                append(line)
            }
            return
        }

        val tokens = line.split(Regex("(?<=[\\s(),.<>:;{}\\[\\]=+\\-*/%&|^!?])|(?=[\\s(),.<>:;{}\\[\\]=+\\-*/%&|^!?])"))
        for (token in tokens) {
            when {
                KEYWORDS.contains(token.trim()) -> {
                    withStyle(SpanStyle(color = KEYWORD_COLOR, fontFamily = FontFamily.Monospace)) {
                        append(token)
                    }
                }
                token.startsWith("\"") || token.endsWith("\"") || token.startsWith("'") || token.endsWith("'") -> {
                    withStyle(SpanStyle(color = STRING_COLOR, fontFamily = FontFamily.Monospace)) {
                        append(token)
                    }
                }
                token.startsWith("@") -> {
                    withStyle(SpanStyle(color = ANNOTATION_COLOR, fontFamily = FontFamily.Monospace)) {
                        append(token)
                    }
                }
                token.matches(Regex("\\d+")) -> {
                    withStyle(SpanStyle(color = NUMBER_COLOR, fontFamily = FontFamily.Monospace)) {
                        append(token)
                    }
                }
                else -> {
                    withStyle(SpanStyle(color = DEFAULT_TEXT_COLOR, fontFamily = FontFamily.Monospace)) {
                        append(token)
                    }
                }
            }
        }
    }
}
