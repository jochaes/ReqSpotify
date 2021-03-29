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

import Connectors.SongService;


public class MainActivity extends AppCompatActivity {
    private SongService songService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        songService = new SongService(getApplicationContext());

        // We will start writing our code here.
        getTracks();

    }

    private void getTracks(){
        songService.getTracks();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
