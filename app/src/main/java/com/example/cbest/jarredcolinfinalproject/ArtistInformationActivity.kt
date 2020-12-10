package com.example.cbest.jarredcolinfinalproject

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.adamratzman.spotify.SpotifyApiOptionsBuilder
import com.adamratzman.spotify.spotifyAppApi

class ArtistInformationActivity : AppCompatActivity()  {
    lateinit var clientID: String
    lateinit var clientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_information)
        val textView: TextView = findViewById(R.id.txtArtistInfo)
        val artistName = intent.getStringExtra("artist")

        if (artistName != null) {
            Log.e("LOGGING", artistName)
        }


        val token = spotifyAppApi("3be87163056a4e2287252ee400bb051b", "96322df2437d4f54bb3cfd6b45f7488b").build().token
        val apiBuilder = spotifyAppApi(
                "clientId",
                "clientSecret",
                token,
                SpotifyApiOptionsBuilder(
                        automaticRefresh = false
                )
        ).build()
        Log.d("TAG", "API SHIT: " + apiBuilder.browse.getNewReleases().complete())
        println(apiBuilder.browse.getNewReleases().complete()) // use it
        if (artistName != null) {
            Log.d(
                "TAG",
                "ID OF BULLSHIT" + apiBuilder.search.searchArtist(artistName).complete()[0].name
            )
        var artists = ""
            for(artist in apiBuilder.search.searchArtist(artistName).complete())
                {if (artist != null) {
                    artists += "\nName: " + artist.name + " Popularity: " + artist.popularity + " Followers: " + artist.followers
                }
            }
        textView.setText(artists)
        }
    }
}