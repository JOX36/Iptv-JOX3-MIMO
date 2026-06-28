package com.jox3.tv.data.remote.api

import com.jox3.tv.domain.model.EpgProgram
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpgParser @Inject constructor() {

    companion object {
        private val dateFormats = listOf(
            SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US),
            SimpleDateFormat("yyyyMMddHHmmss", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") },
            SimpleDateFormat("yyyyMMddHHmmssZ", Locale.US)
        )
    }

    fun parse(inputStream: InputStream): Map<String, List<EpgProgram>> {
        val result = mutableMapOf<String, MutableList<EpgProgram>>()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var currentChannelId: String? = null
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "programme" -> {
                            currentChannelId = parser.getAttributeValue(null, "channel")
                            val start = parser.getAttributeValue(null, "start") ?: ""
                            val stop = parser.getAttributeValue(null, "stop") ?: ""
                            val title = readTag(parser, "title")
                            val desc = readTag(parser, "desc")
                            val category = readTag(parser, "category")

                            if (currentChannelId != null) {
                                val program = EpgProgram(
                                    channelId = currentChannelId,
                                    title = title ?: "",
                                    description = desc ?: "",
                                    startTime = parseEpgDate(start),
                                    endTime = parseEpgDate(stop),
                                    category = category ?: ""
                                )
                                result.getOrPut(currentChannelId) { mutableListOf() }.add(program)
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return result
    }

    private fun readTag(parser: XmlPullParser, tagName: String): String? {
        var result: String? = null
        var depth = 0
        var eventType = parser.eventType
        var found = false

        // We need to look ahead without consuming
        // Simple approach: if current is the start of our tag, read text
        if (parser.name == tagName) {
            found = true
            eventType = parser.next()
            if (eventType == XmlPullParser.TEXT) {
                result = parser.text
            }
            // Skip to end tag
            while (eventType != XmlPullParser.END_TAG || parser.name != tagName) {
                eventType = parser.next()
            }
        }
        return result
    }

    private fun parseEpgDate(dateStr: String): Long {
        val cleaned = dateStr.trim()
        for (fmt in dateFormats) {
            try {
                return fmt.parse(cleaned)?.time ?: 0L
            } catch (_: Exception) {
                // try next
            }
        }
        return 0L
    }
}
