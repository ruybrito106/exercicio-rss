package br.ufpe.cin.if710.rss

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceActivity

class RSSPreferencesActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, RSSPreferenceFragment()).commit()
    }

    class RSSPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }
    }

}