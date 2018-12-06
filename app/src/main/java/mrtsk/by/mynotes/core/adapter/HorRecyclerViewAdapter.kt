package mrtsk.by.mynotes.core.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.horizontal_recycler_view_item.view.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.R.id.name


class HorRecyclerViewAdapter(private val images : ArrayList<Int>, val descriptions : ArrayList<String>, val context: Context):
        RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.horizontal_recycler_view_item, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(context)
            .asBitmap()
            .load(images[p1])
            .into(p0!!.image)

        p0.description.text = descriptions[p1]
    }

    // Gets the number of images in the list
    override fun getItemCount(): Int {
        return images.size
    }

    // Inflates the item views
   /* override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.horizontal_recycler_view_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        Glide.with(context)
            .asBitmap()
            .load(images[position])
            .into(holder!!.img)

        holder.descr.text = descriptions[position]
    }*/
}

class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var image : CircleImageView = itemView.findViewById(R.id.image_view)
    internal var description  :TextView = itemView.findViewById(R.id.name)
}
