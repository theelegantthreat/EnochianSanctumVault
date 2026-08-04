package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.data.model.JournalEntry
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportUtils {

    /**
     * Writes formatted ritual journal entries into a PDF output stream.
     */
    fun writeJournalPdfToStream(entries: List<JournalEntry>, outputStream: OutputStream): Boolean {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val contentWidth = pageWidth - (margin * 2f)

        // Paints
        val titlePaint = Paint().apply {
            color = Color.parseColor("#F3E5AB") // Soft Gold
            textSize = 16f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#D4AF37") // Gold Accent
            textSize = 9.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val entryTitlePaint = Paint().apply {
            color = Color.parseColor("#1B122C") // Deep Esoteric Violet
            textSize = 13f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val labelPaint = Paint().apply {
            color = Color.parseColor("#4B5563") // Slate Gray
            textSize = 9.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.parseColor("#1F2937") // Charcoal
            textSize = 9.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.parseColor("#6B7280")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.parseColor("#E5E7EB")
            strokeWidth = 0.8f
            style = Paint.Style.STROKE
        }

        val dividerPaint = Paint().apply {
            color = Color.parseColor("#C5A059") // Gold Divider
            strokeWidth = 1.2f
            style = Paint.Style.STROKE
        }

        val cardBackgroundPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }

        val cardBorderPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            strokeWidth = 0.8f
            style = Paint.Style.STROKE
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = 0f

        fun drawHeader() {
            // Dark Header Banner
            val headerPaint = Paint().apply { color = Color.parseColor("#181028") }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 62f, headerPaint)

            // Header Title
            canvas.drawText("ENOCHIAN GRIMOIRE — RITUAL JOURNAL", margin.toFloat(), 32f, titlePaint)

            // Subtitle
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
            canvas.drawText("EXPORTED: $dateStr | TOTAL RECORDS: ${entries.size}", margin.toFloat(), 48f, subtitlePaint)

            y = 78f
        }

        fun drawFooter(pageNum: Int) {
            val footerPaint = Paint().apply {
                color = Color.parseColor("#9CA3AF")
                textSize = 8.5f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawLine(margin.toFloat(), (pageHeight - 30).toFloat(), (pageWidth - margin).toFloat(), (pageHeight - 30).toFloat(), linePaint)
            canvas.drawText("Page $pageNum — Enochian Grimoire Archived Ritual Log", (pageWidth / 2).toFloat(), (pageHeight - 16).toFloat(), footerPaint)
        }

        fun checkPageBreak(requiredHeight: Float) {
            if (y + requiredHeight > pageHeight - 40) {
                drawFooter(pageNumber)
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                drawHeader()
            }
        }

        fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
            val lines = mutableListOf<String>()
            val paragraphs = text.split("\n")
            for (paragraph in paragraphs) {
                if (paragraph.isBlank()) {
                    lines.add("")
                    continue
                }
                val words = paragraph.split(" ")
                var currentLine = StringBuilder()
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    if (paint.measureText(testLine) <= maxWidth) {
                        currentLine.append(if (currentLine.isEmpty()) word else " $word")
                    } else {
                        if (currentLine.isNotEmpty()) {
                            lines.add(currentLine.toString())
                        }
                        currentLine = StringBuilder(word)
                    }
                }
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                }
            }
            return lines
        }

        drawHeader()

        if (entries.isEmpty()) {
            bodyPaint.textSize = 11f
            canvas.drawText("No ritual journal entries recorded in the vault.", margin.toFloat(), y + 30f, bodyPaint)
        } else {
            val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' HH:mm", Locale.US)
            for ((index, entry) in entries.withIndex()) {
                checkPageBreak(110f)

                // Divider line between entries
                if (index > 0 && y > 82f) {
                    canvas.drawLine(margin.toFloat(), y, (pageWidth - margin).toFloat(), y, dividerPaint)
                    y += 14f
                }

                // Entry Header Title
                val entryTitleText = "${index + 1}. ${entry.title.ifBlank { "Untitled Ritual Operation" }}"
                canvas.drawText(entryTitleText, margin.toFloat(), y + 10f, entryTitlePaint)
                y += 16f

                // Date & Metadata String
                val dateFormatted = try { sdf.format(Date(entry.timestamp)) } catch (e: Exception) { "" }
                val stars = "★".repeat(entry.rating.coerceIn(1, 5)) + "☆".repeat(5 - entry.rating.coerceIn(1, 5))
                val metaString = "$dateFormatted | Mood: ${entry.mood} | Energy: $stars"
                canvas.drawText(metaString, margin.toFloat(), y + 8f, metaPaint)
                y += 14f

                // Metadata Cards Box
                checkPageBreak(42f)
                val cardTop = y
                val cardHeight = 34f
                val cardRect = RectF(margin.toFloat(), cardTop, (pageWidth - margin).toFloat(), cardTop + cardHeight)
                canvas.drawRoundRect(cardRect, 5f, 5f, cardBackgroundPaint)
                canvas.drawRoundRect(cardRect, 5f, 5f, cardBorderPaint)

                val colWidth = contentWidth / 3f
                canvas.drawText("CALL / KEY:", margin + 8f, cardTop + 13f, labelPaint)
                canvas.drawText(entry.keyOrCallUsed.take(28).ifBlank { "None Specified" }, margin + 8f, cardTop + 26f, bodyPaint)

                canvas.drawText("PLANETARY HOUR:", margin + colWidth + 8f, cardTop + 13f, labelPaint)
                canvas.drawText(entry.planetaryHour.take(28).ifBlank { "Standard" }, margin + colWidth + 8f, cardTop + 26f, bodyPaint)

                canvas.drawText("MOON PHASE:", margin + (colWidth * 2) + 8f, cardTop + 13f, labelPaint)
                canvas.drawText(entry.moonPhase.take(28).ifBlank { "Full Moon" }, margin + (colWidth * 2) + 8f, cardTop + 26f, bodyPaint)

                y += cardHeight + 10f

                // Intention Section
                if (entry.intention.isNotBlank()) {
                    val wrappedIntention = wrapText(entry.intention, bodyPaint, contentWidth)
                    checkPageBreak(18f + wrappedIntention.size * 12f)
                    canvas.drawText("INTENTION & PURPOSE:", margin.toFloat(), y + 8f, labelPaint)
                    y += 14f
                    for (line in wrappedIntention) {
                        checkPageBreak(14f)
                        canvas.drawText(line, margin.toFloat(), y + 8f, bodyPaint)
                        y += 12f
                    }
                    y += 4f
                }

                // Outcome Notes Section
                if (entry.outcomeNotes.isNotBlank()) {
                    val wrappedOutcome = wrapText(entry.outcomeNotes, bodyPaint, contentWidth)
                    checkPageBreak(18f + wrappedOutcome.size * 12f)
                    canvas.drawText("RITUAL OUTCOME & OBSERVATIONS:", margin.toFloat(), y + 8f, labelPaint)
                    y += 14f
                    for (line in wrappedOutcome) {
                        checkPageBreak(14f)
                        canvas.drawText(line, margin.toFloat(), y + 8f, bodyPaint)
                        y += 12f
                    }
                    y += 4f
                }

                // Insights Section
                if (entry.insights.isNotBlank()) {
                    val wrappedInsights = wrapText(entry.insights, bodyPaint, contentWidth)
                    checkPageBreak(18f + wrappedInsights.size * 12f)
                    canvas.drawText("INSIGHTS & ESOTERIC REVELATIONS:", margin.toFloat(), y + 8f, labelPaint)
                    y += 14f
                    for (line in wrappedInsights) {
                        checkPageBreak(14f)
                        canvas.drawText(line, margin.toFloat(), y + 8f, bodyPaint)
                        y += 12f
                    }
                    y += 4f
                }

                y += 8f
            }
        }

        drawFooter(pageNumber)
        pdfDocument.finishPage(page)

        return try {
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            false
        }
    }

    /**
     * Export PDF directly to Downloads folder on device storage.
     */
    fun exportToDownloads(context: Context, entries: List<JournalEntry>): File? {
        val dateStr = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US).format(Date())
        val fileName = "ritual_journal_$dateStr.pdf"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                try {
                    resolver.openOutputStream(uri)?.use { os ->
                        val success = writeJournalPdfToStream(entries, os)
                        if (success) {
                            return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, fileName)
                FileOutputStream(targetFile).use { os ->
                    val success = writeJournalPdfToStream(entries, os)
                    if (success) return targetFile
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}
