package br.ufpe.cin.if710.rss

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ListView
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8

class MainActivity : Activity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // private val RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml"
    // RSS_FEED path is fetched from resources during execution

    private var conteudoRSS: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get conteudoRSS as RecyclerView
        conteudoRSS = findViewById(R.id.conteudoRSS) as RecyclerView
    }

    private fun fillAdapter(feedXML: String) {
        val parser = ParserRSS
        val items = parser.parse(feedXML)

        // intialize manager and adapter for RecyclerView
        viewManager = LinearLayoutManager(this)
        viewAdapter = RSSAdapter(items)

        conteudoRSS!!.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    override fun onStart() {
        super.onStart()
        try {
            doAsync {
                // fetching RSS_FEED path from resources
                val feedXML = getRssFeed(getString(R.string.rssfeed))
                uiThread {
                    // call function to parse feed and apply manager and adapter to view
                    fillAdapter(feedXML)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun getRssFeed(feed: String): String {
        var `in`: InputStream? = null
        var rssFeed: String
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
            rssFeed = String(response, UTF_8)
        } finally {
            `in`?.close()
        }
        return rssFeed
    }
}
