package com.example.reqspotify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import com.example.reqspotify.Spotify;


public class MainActivity extends AppCompatActivity {

    //Acá se crea el objeto de spotify
    String Client_Id = "69a48a944b0e4e8f908d0198a668fcdd";
    String Redirect_URI = "reqspotify://callback";
    Spotify spotify = new Spotify(Client_Id, Redirect_URI);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Se conecta en este contexto
        spotify.connect(this);

    }


    @Override
    protected void onStop() {
        super.onStop();

        // Se desconecta de spotify?
        spotify.shutDown();

    }
}
