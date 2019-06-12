package io.photos.domain.utils

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.photos.domain.exceptions.FileUploadingException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext

interface FileUploader {
    suspend fun uploadMultipart(
        dispatchersProvider: DispatchersProvider,
        multipart: MultiPartData,
        postfix: String
    ): Either<File, FileUploadingException>

    val uploadDir: File
}

class FileUploaderImpl : FileUploader {

    override val uploadDir = File("./uploads")

    override suspend fun uploadMultipart(
        dispatchersProvider: DispatchersProvider,
        multipart: MultiPartData,
        postfix: String
    ): Either<File, FileUploadingException> {
        uploadDir.mkdirs()
        var uploadedFile: File? = null
        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val ext = File(part.originalFileName).extension
                if (ext != "png" && ext != "jpg" && ext != "jpeg")
                    return@forEachPart
                val file = File(uploadDir, "img_$postfix.$ext")
                part.streamProvider().use { input ->
                    file.outputStream().buffered().use { output ->
                        input.copyToSuspend(
                            dispatchersProvider.io,
                            output
                        )
                    }
                }
                uploadedFile = file
            }
            part.dispose()
        }

        return if (uploadedFile == null)
            Either.Failure(FileUploadingException)
        else Either.Success(uploadedFile!!)
    }
}

private suspend fun InputStream.copyToSuspend(
    ioDispatcher: CoroutineContext,
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024
): Long {
    return withContext(ioDispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}