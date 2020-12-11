package com.example.cbest.jarredcolinfinalproject

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adamratzman.spotify.SpotifyApiOptionsBuilder
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.spotifyAppApi

class ArtistInformationActivity : AppCompatActivity()  {
    private val clientID: String = "3be87163056a4e2287252ee400bb051b"
    private val clientSecret: String = "96322df2437d4f54bb3cfd6b45f7488b"
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_information)
        val artistName = intent.getStringExtra("artist")
        viewManager = LinearLayoutManager(this)
        val token = spotifyAppApi(clientID, clientSecret).build().token
        val apiBuilder = spotifyAppApi(
                "clientId",
                "clientSecret",
                token,
                SpotifyApiOptionsBuilder(
                        automaticRefresh = false
                )
        ).build()
        println(apiBuilder.browse.getNewReleases().complete()) // use it
        if (artistName != null) {
        var artists = ""
            var artistList : MutableList<Artist> = ArrayList()
            for(artist in apiBuilder.search.searchArtist(artistName).complete())
                {if (artist != null) {
                    artists += "\nName: " + artist.name + " Popularity: " + artist.popularity + " Followers: " + artist.followers
                    artistList.add(artist)
                }
            }

       // textView.setText(artists)
            viewAdapter = MyRecyclerAdapter(artistList as ArrayList<Artist>)

            recyclerView = findViewById<RecyclerView>(R.id.rcyView).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }
        }
    }
}