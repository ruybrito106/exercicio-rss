package br.ufpe.cin.if710.rss

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.preference.PreferenceManager;

class MainActivity : Activity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var dbInstance: SQLiteRSSHelper

    private lateinit var dynamicReceiver: BroadcastReceiver
    private lateinit var receiverIntentFilter: IntentFilter

    private var prefs: PreferencesHelper? = null
    private var conteudoRSS: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init dynamic receiver
        dynamicReceiver = object: BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                // on fetch finish, start adapter filling
                when (intent?.action) {
                    FeedFetchService.FETCH_DONE -> fillAdapter()
                }
            }
        }

        receiverIntentFilter = IntentFilter(FeedFetchService.FETCH_DONE)

        dbInstance = SQLiteRSSHelper.getInstance(this)

        prefs = PreferencesHelper(this)

        // get conteudoRSS as RecyclerView
        conteudoRSS = findViewById(R.id.conteudoRSS) as RecyclerView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // on preference change, update preferences and fill adapter items
        R.id.actionSearch -> {
            val intent = Intent(this, RSSPreferencesActivity::class.java)
            startActivity(intent)

            val newPrefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs!!.rssfeed = newPrefs.getString("rssfeed", "")

            fillAdapter()

            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun fillAdapter() {
        val items = dbInstance.getItems()

        // intialize manager and adapter for RecyclerView
        viewManager = LinearLayoutManager(this)
        viewAdapter = RSSAdapter(items, dbInstance)

        conteudoRSS!!.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    override fun onStart() {
        super.onStart()
        registerReceiver(dynamicReceiver, receiverIntentFilter)
        startSvc()
        fillAdapter()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(dynamicReceiver, receiverIntentFilter)
        startSvc()
        fillAdapter()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(dynamicReceiver)
    }
    
    fun startSvc() {
        if (prefs!!.rssfeed.isEmpty()) prefs!!.rssfeed = getString(R.string.rssfeed)
        val svcIntent = Intent(this, FeedFetchService::class.java)
        svcIntent.data = Uri.parse(prefs!!.rssfeed)
        startService(svcIntent)
    }
}
