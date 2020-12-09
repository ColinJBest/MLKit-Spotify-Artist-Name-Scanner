package com.example.cbest.jarredcolinfinalproject

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.pghaz.spotify.webapi.auth.SpotifyAuthorizationCallback
import com.pghaz.spotify.webapi.auth.SpotifyAuthorizationClient
import io.github.kaaes.spotify.webapi.core.models.UserPrivate
import net.openid.appauth.TokenResponse

class ArtistInformationActivity : AppCompatActivity(), SpotifyAuthorizationCallback.Authorize, SpotifyAuthorizationCallback.RefreshToken  {
    lateinit var spotifyAuthClient: SpotifyAuthorizationClient
    private fun initSpotifyAuthClient() {
        spotifyAuthClient = SpotifyAuthorizationClient
            .Builder("7e6e0e4f8ebe4b6aa021e7e457b97572", "random://callback")
//                .setScopes(
//                        arrayOf(
//                                "app-remote-control",
//                                "user-read-recently-played"
//                        )
//                )
            //               .setCustomTabColor(Color.RED)
            //              .setFetchUserAfterAuthorization(true)
            .build(this)

        spotifyAuthClient.addAuthorizationCallback(this)
        spotifyAuthClient.addRefreshTokenCallback(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_information)
        val textView: TextView = findViewById(R.id.txtArtistInfo)
        textView.setText(intent.getStringExtra("ArtistName").toString())
        Log.e("LOGGING", intent.getStringExtra("ArtistName").toString())

        initSpotifyAuthClient()

        if (spotifyAuthClient.isAuthorized()) {
            if (spotifyAuthClient.getNeedsTokenRefresh()) {
                spotifyAuthClient.refreshAccessToken()
            } else {
                onSpotifyAuthorizedAndAvailable(spotifyAuthClient.getLastTokenResponse()?.accessToken)
            }
        } else {
            spotifyAuthClient.authorize(this, MainActivity.REQUEST_CODE_SPOTIFY_LOGIN)
        }

    }

    private fun onSpotifyAuthorizedAndAvailable(accessToken: String?) {
        // make your Spotify Web API calls here
        Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // At this point it is authorized but we don't have access token yet.
        // We get it when onAuthorizationSucceed() is called
        spotifyAuthClient.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        spotifyAuthClient.onStart()
    }

    override fun onStop() {
        super.onStop()
        spotifyAuthClient.onStop()
    }


    override fun onAuthorizationCancelled() {
        Toast.makeText(this, "auth cancelled", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthorizationFailed(error: String?) {
        Toast.makeText(this, "auth failed", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthorizationRefused(error: String?) {
        Toast.makeText(this, "auth refused", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthorizationStarted() {
        Toast.makeText(this, "auth start", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthorizationSucceed(tokenResponse: TokenResponse?, user: UserPrivate?) {
        onSpotifyAuthorizedAndAvailable(tokenResponse?.accessToken)
    }

    override fun onRefreshAccessTokenStarted() {
        Toast.makeText(this, "refresh start", Toast.LENGTH_SHORT).show()
    }

    override fun onRefreshAccessTokenSucceed(tokenResponse: TokenResponse?, user: UserPrivate?) {
        onSpotifyAuthorizedAndAvailable(tokenResponse?.accessToken)
    }
    override fun onDestroy() {
        super.onDestroy()
        spotifyAuthClient.removeAuthorizationCallback(this)
        spotifyAuthClient.removeRefreshTokenCallback(this)
        spotifyAuthClient.onDestroy()
    }
}