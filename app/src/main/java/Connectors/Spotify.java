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


public class Spotify {
    private static final String CLIENT_ID = "69a48a944b0e4e8f908d0198a668fcdd";
    private static final String REDIRECT_URI = "reqspotify://callback";

    private SpotifyAppRemote mSpotifyAppRemote;
    private ConnectionParams connectionParams;

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

                    //Si se conecta, ejecuta el método connnected() que reproduce una lista de reproducción
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("SpotifyPlayer", "Connected! Yay!");
                        Toast.makeText(pContext, "Connected to Spotify Player", Toast.LENGTH_SHORT).show();
                        // Now you can start interacting with App Remote
                    }

                    //DEspliega un error, cuando no se puede conectar
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("SpotifyPlayer", throwable.getMessage(), throwable);
                        Toast.makeText(pContext, "Can't connect with Spotify Player", Toast.LENGTH_SHORT).show();
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }



    public void playTrack(String pTRackId, TextView pTextView){
        String trackURI = "spotify:track:"+pTRackId;

        //Reproduce una playlist, el parametro es el codigo de la playlist en spotify
        mSpotifyAppRemote.getPlayerApi().play(trackURI);

        //Obtiene el nombre de la canción que está reproduciendo.
        // Subscribe to PlayerState
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

    public void shutDown(){
        mSpotifyAppRemote.getPlayerApi().pause();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }


}
