package br.ufpe.cin.if710.rss

import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class RSSAdapter(private val items: List<ItemRSS>) :
        RecyclerView.Adapter<RSSAdapter.MyViewHolder>() {

    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RSSAdapter.MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemlista, parent, false) as LinearLayout

        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val title = holder.linearLayout.findViewById(R.id.item_titulo) as TextView
        val text = holder.linearLayout.findViewById(R.id.item_data) as TextView

        // set title and description on item view
        // assuming data is the description attribute of the ItemRSS object
        title.text = items[position].title
        text.text = items[position].description

        // set on click listener to title of each item
        title.setOnClickListener {
            val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(items[position].link)
            )

            startActivity(title.context, intent, null)
        }
    }

    override fun getItemCount() = items.size
}