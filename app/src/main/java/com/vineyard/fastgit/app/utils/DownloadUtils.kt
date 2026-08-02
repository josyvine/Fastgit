package com.vineyard.fastgit.app.utils

import android.content.Context
import android.os.Environment
import com.vineyard.fastgit.app.models.FileItem
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DownloadUtils {

    fun downloadSingleFileToCache(context: Context, fileName: String, content: String): File {
        val file = File(context.cacheDir, fileName)
        file.writeText(content)
        return file
    }

    /**
     * Downloads a FileItem directory structure as a local ZIP file.
     */
    fun createZipFromFolderItems(
        context: Context,
        folderName: String,
        items: List<FileItem>
    ): File {
        val zipFile = File(context.cacheDir, "$folderName.zip")
        val fos = FileOutputStream(zipFile)
        val zos = ZipOutputStream(fos)

        fun zipItem(item: FileItem, prefix: String) {
            val entryPath = if (prefix.isEmpty()) item.name else "$prefix/${item.name}"
            if (item.type == "dir") {
                item.children.forEach { child ->
                    zipItem(child, entryPath)
                }
            } else {
                val ze = ZipEntry(entryPath)
                zos.putNextEntry(ze)
                val bytes = item.byteContent ?: item.content?.toByteArray(Charsets.UTF_8) ?: ByteArray(0)
                zos.write(bytes)
                zos.closeEntry()
            }
        }

        items.forEach { item ->
            zipItem(item, "")
        }

        zos.close()
        fos.close()
        return zipFile
    }
}
