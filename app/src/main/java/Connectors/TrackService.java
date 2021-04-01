package Connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.reqspotify.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TrackService {
    private SharedPreferences sharedPreferences;

    SpotifyApi api;
    SpotifyService spotify;

    public TrackService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        api = new SpotifyApi();
        api.setAccessToken(sharedPreferences.getString("token", ""));
        spotify = api.getService();
    }


    ///Esto es super importante omg si sirvió jejeps
    public void getAlbumName() {

        try {
            spotify.getAlbum("1GbtB4zTqAsyfZEsm1RZfx", new Callback<Album>() {
                @Override
                public void success(Album album, Response response) {
                    Log.d("Album success", album.name);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Album failure", error.toString());
                }
            });
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            // handle error
        }
    }

    ///Esto es super importante omg si sirvió jejeps
    public void getPlayListName() {

        try {
            spotify.getPlaylist(sharedPreferences.getString("userid", ""), "7wIcYj7ZvSLnTu2nFY4i6j", new SpotifyCallback<Playlist>() {
                @Override
                public void success(Playlist playlist, Response response) {
                    Log.d("playlist success", playlist.name);

                }

                @Override
                public void failure(SpotifyError error) {
                    Log.d("playlist failure", error.toString());
                }
            });
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            // handle error
        }



        //Log.d("Nombre de la cosa", playlistone[0].name);
    }

    public void getPlaylistTracks(ArrayList<Track> localPlayList, ListView pListView, ArrayAdapter<Track> pAdapterTrack, Context pContext ){
        try {
            spotify.getPlaylistTracks(sharedPreferences.getString("userid", ""), "7wIcYj7ZvSLnTu2nFY4i6j", new SpotifyCallback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    Log.e("TEST", "GOT the tracks in playlist");
                    List<PlaylistTrack> items = playlistTrackPager.items;

                    loadPlaylistTracksListView( localPlayList, items,pListView,pAdapterTrack,pContext);

                    for( PlaylistTrack pt : items){
                        Log.e("Track", pt.track.name + " - " + pt.track.id);
                    }

                }

                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.e("TEST", "Could not get playlist tracks");

                }
            });} catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            // handle error
        }
    }

    private void loadPlaylistTracksListView(ArrayList<Track> pLocalPlayList, List<PlaylistTrack> pAPITrackList, ListView pListView, ArrayAdapter<Track> pAdapterTrack, Context pContext){
        for( PlaylistTrack pt : pAPITrackList){
            Track track = new Track(pt.track.id,pt.track.name);
            pLocalPlayList.add(track);
        }
        pAdapterTrack = new ArrayAdapter<Track>(pContext, android.R.layout.simple_list_item_1, pLocalPlayList );
        pListView.setAdapter( pAdapterTrack);
    }

    public void addTrackToPlaylist(){
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        query.put("uris", "spotify:track:2MfgdGJUFyZAprtuOEujRb");

        try {
            spotify.addTracksToPlaylist(sharedPreferences.getString("userid", ""), "7wIcYj7ZvSLnTu2nFY4i6j",
                    query, body, new Callback<Pager<PlaylistTrack>>() {
                        @Override
                        public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                            Log.d("Succes Add Track", "Success in adding tracks");

                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error Add Track", error.getUrl());

                        }
                    });
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            // handle error
        }

    }





}