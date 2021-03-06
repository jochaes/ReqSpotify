package com.example.reqspotify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Connectors.Spotify;
import Connectors.TrackService;


public class MainActivity extends AppCompatActivity  {
    //Servicio para utilizar el Wrapper del API que se comunica con Spotify,
    // para pedir la lista de reproducción y guardar canciones en la lista
    private TrackService trackService;

    //Arrraylist que guarda la lista de canciones de la playlist.
    private ArrayList<Track> PlaylistTracks;

    //Se conecta con el SDK de spotify, funciona como reproductor
    Spotify spotifyPlayer;

    EditText songID_input;

    private String SongID; //Id introducido por el usuario de una canción de spotify
    private final String PLaylistID = "2QxyWlAp7iiSvuglz6qRl0";
    //private final String PLaylistID = "7wIcYj7ZvSLnTu2nFY4i6j"; //Id de Spotify de la playlist REqs
    //private final String PLaylistID = "2J6mJOjC5Ub7uAAdA2Uf1G"; //Id de spotif de la playlist Hola Joshua
    public TextView text_NowPLaying;                            //Texr view que dice que cancion está sonando
    public ListView listView_PlaylistTracks;                    //List View de las canciones de la playlist
    public ArrayAdapter<Track> ArrayAdapter;                    //Adaptador del List View


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        text_NowPLaying = findViewById(R.id.text_NowPLaying);   //Busca por id el componente de texto del layout
        listView_PlaylistTracks = findViewById(R.id.listView_PlaylistTracks); // Busca por id el componente de ListView del layout
        songID_input = (EditText) findViewById(R.id.txtin_SongID); // Busca por id el componente de EditText del layout
    }


    @Override
    protected void onStart() {
        super.onStart();
        spotifyPlayer = new Spotify();                          //Instancia un objeto de Spotify
        spotifyPlayer.connect(this);                  //Se conecta mediante SDK a la app de spotify

        trackService = new TrackService(getApplicationContext()); //Se instancia un objeto de trackservice que se conecta por medio del api a spotify
        PlaylistTracks = new ArrayList<Track>();                  //Instanciación del arrraylist
        text_NowPLaying.setText("Now PLaying: Nothing...Touch a song below to play...");

        //Se agrega un listener para que cuando se selecciones una cancion del list view,
        //  se comunicque con el SDK y reproduzca la canción
        listView_PlaylistTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = PlaylistTracks.get(position);     //Busca la canción
                Log.d("TrackName", track.toString());      //LOG del nombre
                playTrack(track.getId(), text_NowPLaying);      //Envía a reproducir la canción
            }

        });
        
        //Crea la playlist
        getPlaylists();

        //Espera las canciones
        (new Handler()).postDelayed(this::getPLaylistTracks, 3000);

    }

    private void getPlaylists(){
        trackService.getPlayLists();
    }

    /**
     * Reproduce una canción utilizando el SDK de spotify.
     * @param pTrackId  Identificador de la canción en spotify. URI pero solo el id"
     * @param pTextView Text view, para cambiar el texto
     */
    private void playTrack(String pTrackId, TextView pTextView){
        spotifyPlayer.playTrack(pTrackId,pTextView);
    }

    public void resumeTrack(View view){
        spotifyPlayer.resumeTrack();
    }

    public void pauseTrack(View view){
        spotifyPlayer.pauseTrack();
    }

    /**
     * Añade una cancion a la playlist
     * @param view Cuando se presiona el boton, Accede a este método.
     */
    public void addTrackToPlaylist(View view){

        SongID = songID_input.getText().toString();
        if (SongID.matches("")) {
            songID_input.getText().clear();
            return;
        }
        trackService.addTrackToPlaylist(SongID ); //Añade la cancion a la playlist
        songID_input.getText().clear();
        getPLaylistTracks();                                        //Actualiza el textview
    }

    /**
     * Borra una cancion a la playlist
     * @param view Cuando se presiona el boton, Accede a este método.
     */
    public void deleteTrackFromPlaylist(View view){
        SongID = songID_input.getText().toString();
        songID_input.getText().clear();
        if (SongID.matches("")) {
            return;
        }
        trackService.deleteTrackFromPlaylist(SongID);
        getPLaylistTracks();
    }

    /**
     * Carga las canciones de la playlist en el textview
     */
    private void getPLaylistTracks(){
        trackService.getPlaylistTracks(PlaylistTracks, listView_PlaylistTracks,ArrayAdapter,this);
    }


    @Override
    protected void onStop() {
        spotifyPlayer.shutDown();
        super.onStop();
    }

}
