package com.example.reqspotify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Connectors.Spotify;
import Connectors.TrackService;


public class MainActivity extends AppCompatActivity  {
    private TrackService trackService;
    private ArrayList<Track> PlaylistTracks;

    Spotify spotifyPlayer;


    private final String PLaylistID = "7wIcYj7ZvSLnTu2nFY4i6j";
    public TextView text_NowPLaying;
    public ListView listView_PlaylistTracks;
    public ArrayAdapter<Track> ArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_NowPLaying = findViewById(R.id.text_NowPLaying);
        listView_PlaylistTracks = findViewById(R.id.listView_PlaylistTracks);

    }


    @Override
    protected void onStart() {
        super.onStart();
        spotifyPlayer = new Spotify();
        spotifyPlayer.connect(this);

        trackService = new TrackService(getApplicationContext());
        PlaylistTracks = new ArrayList<Track>();
        text_NowPLaying.setText("Now PLaying: Nothing...Touch a song below to play...");

        listView_PlaylistTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = PlaylistTracks.get(position);
                Log.d("TrackName", track.toString());
                playTrack(track.getId(), text_NowPLaying);
            }

        });

        //Carga la playlist en el list view
        getPLaylistTracks();

    }

    private void playTrack(String pTrackId, TextView pTextView){
        spotifyPlayer.playTrack(pTrackId,pTextView);
    }

    public void addTrackToPlaylist(View view){
        String randomSongID = "4uLU6hMCjMI75M1A2tKUQC";
        trackService.addTrackToPlaylist(randomSongID, PLaylistID );
        getPLaylistTracks();
    }


    private void getPLaylistTracks(){
        trackService.getPlaylistTracks(PLaylistID, PlaylistTracks, listView_PlaylistTracks,ArrayAdapter,this);
    }


    @Override
    protected void onStop() {
        spotifyPlayer.shutDown();
        super.onStop();
    }

}
