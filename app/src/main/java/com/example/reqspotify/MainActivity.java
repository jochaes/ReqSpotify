package com.example.reqspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import Connectors.TrackService;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private TrackService trackService;
    private ArrayList<Track> PlaylistTracks;

    ListView listView_PlaylistTracks;
    ArrayAdapter<Track> ArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView_PlaylistTracks = findViewById(R.id.listView_PlaylistTracks);

    }


    @Override
    protected void onStart() {
        super.onStart();
        trackService = new TrackService(getApplicationContext());
        PlaylistTracks = new ArrayList<Track>();

        //Carga la playlist en el list view
        getPLaylistTracks();

    }

    /**
     * Añade una canción a la lista de reproducción
     */
    private void addTrackToPlaylist( String pTrackURI){
        trackService.addTrackToPlaylist();
    }

    private void getPLaylistTracks(){
        trackService.getPlaylistTracks(PlaylistTracks, listView_PlaylistTracks,ArrayAdapter,this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Track track = (Track) parent.getSelectedItem();
        Toast.makeText(this, track.getId(), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


}
