package br.ufpe.cin.if710.rss

import android.app.IntentService
import android.content.Intent
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class FeedFetchService: IntentService("FeedFetchService") {
    override fun onCreate() {
        super.onCreate()
    }

    @Throws(IOException::class)
    private fun getRssFeed(feed: String): String {
        var `in`: InputStream? = null
        var rssFeed = ""
        try {
            val url = URL(feed)
            val conn = url.openConnection() as HttpURLConnection
            `in` = conn.inputStream
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count = `in`.read(buffer)
            while (count != -1) {
                out.write(buffer, 0, count)
                count = `in`.read(buffer)
            }
            val response = out.toByteArray()
            rssFeed = String(response, StandardCharsets.UTF_8)
        } finally {
            `in`?.close()
        }
        return rssFeed
    }

    override fun onHandleIntent(intent: Intent?) {
        val feedXML = getRssFeed(intent!!.data.toString())
        val items = ParserRSS.parse(feedXML)
        sendBroadcast(Intent(FETCH_DONE))
        val dbInstance = SQLiteRSSHelper.getInstance(this)
        var someNewItem = false
        items.forEach { it -> someNewItem = someNewItem || dbInstance.insertItem(it) != -1L }
        if (someNewItem) sendBroadcast(Intent(NEW_ITEM))
    }

    companion object {
        val FETCH_DONE = "br.ufpe.cin.if710.rss.FETCH_DONE"
        val NEW_ITEM = "br.ufpe.cin.if710.rss.NEW_ITEM"
    }

}
