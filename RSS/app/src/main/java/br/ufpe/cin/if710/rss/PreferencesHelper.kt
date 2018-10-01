package br.ufpe.cin.if710.rss

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(ctx: Context) {
    companion object {
        val PREFS_FILE = "br.ufpe.cin.if710.rss.prefs"
        val RSS_FEED = "rssfeed"
    }

    private val prefs: SharedPreferences = ctx.getSharedPreferences(PREFS_FILE, 0)

    var rssfeed: String
        get() = prefs.getString(RSS_FEED, "")
        set(value) = prefs.edit().putString(RSS_FEED, value).apply()
}