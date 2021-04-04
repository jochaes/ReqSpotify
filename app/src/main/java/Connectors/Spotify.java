package Connectors;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

/**
 * Clase que trabaja con el SDK de Spotify, para reproducir canciones
 */
public class Spotify {
    private static final String CLIENT_ID = "69a48a944b0e4e8f908d0198a668fcdd";       //Código del cliente del Dashboard
    private static final String REDIRECT_URI = "reqspotify://callback";               //Redirect del Dashboard

    private SpotifyAppRemote mSpotifyAppRemote;                                        //Meneja "remotamente" el reproductor de spotify
    private ConnectionParams connectionParams;                                         //Parametros de conecieon para conectar con spotify.

    //Crea el objeto y hace el build de los parametros para la conexión
    public Spotify(){
        connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
    }

    //Se conecta con Spotify
    public void connect(Context pContext){
        SpotifyAppRemote.connect(pContext, connectionParams,
                new Connector.ConnectionListener() {

                    //Si se conecta envía un mensaje al cliente que ya puede reproducir canciones
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("SpotifyPlayer", "Connected! Yay!");
                        Toast.makeText(pContext, "Connected to Spotify Player", Toast.LENGTH_SHORT).show();
                    }

                    //Despliega un error, cuando no se puede conectar
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("SpotifyPlayer", throwable.getMessage(), throwable);
                        Toast.makeText(pContext, "Can't connect with Spotify Player", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Reproduce una canción mediante el reproductor de spotify
     * @param pTRackId  Id del URI del track en spotify
     * @param pTextView //Text View para cambiarle el nombre de la cancion que se está reproduciendo
     */
    public void playTrack(String pTRackId, TextView pTextView){
        String trackURI = "spotify:track:"+pTRackId;  //Forma el URI con el ID del track

        //Reproduce una playlist, el parametro es el codigo de la playlist en spotify
        mSpotifyAppRemote.getPlayerApi().play(trackURI);

        //Obtiene el nombre de la canción que está reproduciendo.
        //Para cambiarlo en el TextView
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        String trackInfo = "Playing: " + track.name + " by " + track.artist.name;
                        pTextView.setText( trackInfo);
                        Log.d("SpotifyPlayer", trackInfo);
                    }
                });
    }

    public void pauseTrack() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void resumeTrack(){
        mSpotifyAppRemote.getPlayerApi().resume();
    }



    /**
     * Se desconecta del AppRemote de spotify
     */
    public void shutDown(){
        mSpotifyAppRemote.getPlayerApi().pause();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }


}
