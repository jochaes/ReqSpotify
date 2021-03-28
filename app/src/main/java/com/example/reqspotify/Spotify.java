package com.example.reqspotify;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;


public class Spotify {
    private static String CLIENT_ID = "";
    private static String REDIRECT_URI = "";
    private SpotifyAppRemote mSpotifyAppRemote;
    private ConnectionParams connectionParams;

    //Crea el objeto y hace el build de los parametros para la conexión
    public Spotify(String pCLIENT_ID, String pREDIRECT_URI){
        CLIENT_ID = pCLIENT_ID;
        REDIRECT_URI = pREDIRECT_URI;
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
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    //DEspliega un error, cuando no se puede conectar
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connected(){
        //Reproduce una playlist, el parametro es el codigo de la playlist en spotify
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:7wIcYj7ZvSLnTu2nFY4i6j");

        //Obtiene el nombre de la canción que está reproduciendo.
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }

    public void shutDown(){
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }


}
