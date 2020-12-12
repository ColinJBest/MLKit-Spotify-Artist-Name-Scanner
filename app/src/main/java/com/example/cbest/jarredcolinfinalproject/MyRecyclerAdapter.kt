package com.example.cbest.jarredcolinfinalproject

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adamratzman.spotify.models.Artist
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat

// I stole this from your example mwahaha
class MyRecyclerAdapter(private val myDataset: ArrayList<Artist>) :
        RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyRecyclerAdapter.MyViewHolder {
        // LW create a new view, use the layout,   change TextView to View
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_artist_layout, parent, false) as View
        // LW set the view's size, margins, paddings and layout parameters
        val lp = view.getLayoutParams()
        lp.height = parent.measuredHeight/6
        view.setLayoutParams(lp)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // LW use the view and drill down to the textview in the layout
         holder.view.findViewById<TextView>(R.id.textViewRecyclerItem).text = "" + myDataset[position].name
        val decFormat: DecimalFormat = DecimalFormat("#,###")
        // Grab the follower textview as well to edit
        holder.view.findViewById<TextView>(R.id.textViewFollowers).text = ("Followers: " + String.format("%,d",myDataset[position].followers.total)).padEnd(35, '\t')
        //Add an if statement in case there isn't an artist image (was crashing previously)
        if(!myDataset[position].images.isEmpty())
            //Add the image using Picasso since they are URLs
            Picasso.get().load(myDataset[position].images[0].url).into(holder.view.findViewById<ImageView>(R.id.imageViewArtist))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}