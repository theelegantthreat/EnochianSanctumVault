package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.data.reference.EnochianData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

object SigilExportUtils {

    /**
     * Generates a high-resolution 1200x1200 bitmap of an Enochian Sigil on a sacred ritual canvas.
     */
    fun createSigilBitmap(
        title: String,
        intentionPhrase: String,
        sigilMethod: String,
        lineColorHex: String,
        strokeWidthPx: Float = 8f
    ): Bitmap {
        val width = 1200
        val height = 1200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Parse line color
        val parsedLineColor = try {
            Color.parseColor(lineColorHex)
        } catch (e: Exception) {
            Color.parseColor("#FFD54F") // Default Enochian Gold
        }

        // Paints
        val bgPaint = Paint().apply {
            color = Color.parseColor("#0D0818") // Deep Esoteric Velvet Dark
            style = Paint.Style.FILL
        }

        val cardBgPaint = Paint().apply {
            color = Color.parseColor("#150F28")
            style = Paint.Style.FILL
        }

        val outerFramePaint = Paint().apply {
            color = Color.parseColor("#D4AF37") // Gold
            strokeWidth = 3f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val innerFramePaint = Paint().apply {
            color = Color.parseColor("#805C11")
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val headerTitlePaint = Paint().apply {
            color = Color.parseColor("#F3E5AB")
            textSize = 34f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val headerSubPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.parseColor("#9CA3AF")
            textSize = 18f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val wheelRingPaint = Paint().apply {
            color = Color.parseColor("#5A451A")
            strokeWidth = 3f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val nodePaint = Paint().apply {
            color = Color.parseColor("#FFD54F")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val nodeTextPaint = Paint().apply {
            color = Color.parseColor("#E2D8B3")
            textSize = 15f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val sigilPathPaint = Paint().apply {
            color = parsedLineColor
            strokeWidth = strokeWidthPx * 2.2f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        val sigilGlowPaint = Paint().apply {
            color = parsedLineColor
            alpha = 80
            strokeWidth = strokeWidthPx * 4.5f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        // 1. Draw Background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Draw Decorative Border / Frame for Ritual Print
        val margin = 36f
        canvas.drawRect(margin, margin, width - margin, height - margin, outerFramePaint)
        canvas.drawRect(margin + 8f, margin + 8f, width - margin - 8f, height - margin - 8f, innerFramePaint)

        // Decorative corner accents
        val cornerSize = 40f
        val cp = Paint().apply { color = Color.parseColor("#FFD54F"); strokeWidth = 4f; style = Paint.Style.STROKE; isAntiAlias = true }
        canvas.drawLine(margin, margin + cornerSize, margin + cornerSize, margin, cp)
        canvas.drawLine(width - margin - cornerSize, margin, width - margin, margin + cornerSize, cp)
        canvas.drawLine(margin, height - margin - cornerSize, margin + cornerSize, height - margin, cp)
        canvas.drawLine(width - margin - cornerSize, height - margin, width - margin, height - margin - cornerSize, cp)

        // 3. Header Text
        val displayTitle = if (title.isBlank()) "SACRED ENOCHIAN SIGIL" else title.uppercase()
        canvas.drawText("✦ ENOCHIAN GRIMOIRE ✦", (width / 2).toFloat(), 95f, headerSubPaint)
        canvas.drawText(displayTitle, (width / 2).toFloat(), 142f, headerTitlePaint)

        // 4. Center Wheel Parameters
        val centerX = width / 2f
        val centerY = 620f
        val outerRadius = 380f
        val innerRadius = 180f

        // Draw Wheel Rings
        canvas.drawCircle(centerX, centerY, outerRadius, wheelRingPaint)
        canvas.drawCircle(centerX, centerY, innerRadius, wheelRingPaint)
        canvas.drawCircle(centerX, centerY, (outerRadius + innerRadius) / 2f, Paint(wheelRingPaint).apply { alpha = 80; strokeWidth = 1.5f })

        // Map Enochian Letters to Nodes
        val letters = EnochianData.ENNOCHIAN_LETTERS
        val nodeOffsets = mutableMapOf<Char, Pair<Float, Float>>()

        letters.forEachIndexed { index, letter ->
            val angleRad = Math.toRadians(letter.wheelAngleDegrees.toDouble() - 90.0)
            val radius = if (index % 2 == 0) outerRadius else innerRadius
            val nx = (centerX + radius * cos(angleRad)).toFloat()
            val ny = (centerY + radius * sin(angleRad)).toFloat()

            nodeOffsets[letter.englishChar] = Pair(nx, ny)

            // Draw Node Circle
            canvas.drawCircle(nx, ny, 7f, nodePaint)

            // Draw Letter Label near node
            val labelRadius = radius + (if (index % 2 == 0) 22f else -22f)
            val lx = (centerX + labelRadius * cos(angleRad)).toFloat()
            val ly = (centerY + labelRadius * sin(angleRad)).toFloat() + 5f
            canvas.drawText(letter.englishChar.toString(), lx, ly, nodeTextPaint)
        }

        // 5. Trace Intention Sigil Path
        val cleanPhrase = intentionPhrase.uppercase().filter { it in 'A'..'Z' }
        if (cleanPhrase.isNotEmpty()) {
            val path = Path()
            val matchedPoints = mutableListOf<Pair<Float, Float>>()

            cleanPhrase.forEach { char ->
                nodeOffsets[char]?.let { point ->
                    matchedPoints.add(point)
                }
            }

            if (matchedPoints.isNotEmpty()) {
                path.moveTo(matchedPoints.first().first, matchedPoints.first().second)

                for (i in 1 until matchedPoints.size) {
                    path.lineTo(matchedPoints[i].first, matchedPoints[i].second)
                }

                // Draw Path Glow & Core Line
                canvas.drawPath(path, sigilGlowPaint)
                canvas.drawPath(path, sigilPathPaint)

                // Start node indicator circle
                val startP = matchedPoints.first()
                canvas.drawCircle(
                    startP.first,
                    startP.second,
                    strokeWidthPx * 2.2f,
                    Paint().apply {
                        color = parsedLineColor
                        style = Paint.Style.STROKE
                        strokeWidth = 3.5f
                        isAntiAlias = true
                    }
                )

                // End terminal node solid dot
                val endP = matchedPoints.last()
                canvas.drawCircle(
                    endP.first,
                    endP.second,
                    strokeWidthPx * 1.8f,
                    Paint().apply {
                        color = parsedLineColor
                        style = Paint.Style.FILL
                        isAntiAlias = true
                    }
                )
            }
        }

        // 6. Footer Card Information Box for Physical Printing
        val cardRect = RectF(70f, 1050f, width - 70f, 1145f)
        canvas.drawRoundRect(cardRect, 12f, 12f, cardBgPaint)
        canvas.drawRoundRect(cardRect, 12f, 12f, outerFramePaint)

        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        metaPaint.textAlign = Paint.Align.LEFT
        metaPaint.color = Color.parseColor("#E2D8B3")
        metaPaint.textSize = 17f

        val intentionTextFormatted = "INTENTION: \"$intentionPhrase\""
        canvas.drawText(intentionTextFormatted.take(55), 90f, 1085f, metaPaint)

        metaPaint.color = Color.parseColor("#9CA3AF")
        metaPaint.textSize = 15f
        val detailsText = "METHOD: $sigilMethod  |  DATE: $dateStr"
        canvas.drawText(detailsText, 90f, 1120f, metaPaint)

        return bitmap
    }

    /**
     * Write generated sigil bitmap to a given output stream (e.g., Uri stream).
     */
    fun writeSigilBitmapToStream(
        bitmap: Bitmap,
        outputStream: OutputStream
    ): Boolean {
        return try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Saves the sigil PNG image to device's MediaStore gallery / Pictures directory for physical ritual prints.
     */
    fun exportSigilToMediaStore(
        context: Context,
        title: String,
        intentionPhrase: String,
        sigilMethod: String,
        lineColorHex: String
    ): Uri? {
        val bitmap = createSigilBitmap(title, intentionPhrase, sigilMethod, lineColorHex)
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "sigil_${dateStr}.png"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EnochianSigils")
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                try {
                    resolver.openOutputStream(uri)?.use { os ->
                        val success = writeSigilBitmapToStream(bitmap, os)
                        if (success) return uri
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            try {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val sigilFolder = File(picturesDir, "EnochianSigils")
                if (!sigilFolder.exists()) sigilFolder.mkdirs()
                val targetFile = File(sigilFolder, fileName)
                FileOutputStream(targetFile).use { os ->
                    val success = writeSigilBitmapToStream(bitmap, os)
                    if (success) return Uri.fromFile(targetFile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}
