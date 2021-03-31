package Connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.protocol.types.ListItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import kaaes.spotify.webapi.android.models.SavedTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SongService {
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    SpotifyApi api;
    SpotifyService spotify;

    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);

        api = new SpotifyApi();
        api.setAccessToken(sharedPreferences.getString("token", ""));
        spotify = api.getService();
    }


    ///Esto es super importante omg si sirvió jejeps
    public void getAlbumName() {

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


    }

    ///Esto es super importante omg si sirvió jejeps
    public void getPlayListName() {

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

        //Log.d("Nombre de la cosa", playlistone[0].name);
    }

    public void getPlaylistTracks(){


       spotify.getPlaylistTracks(sharedPreferences.getString("userid", ""), "7wIcYj7ZvSLnTu2nFY4i6j", new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                Log.e("TEST", "GOT the tracks in playlist");
                Log.d("PAGER", playlistTrackPager.items.toString());
                List<PlaylistTrack> items = playlistTrackPager.items;
                for( PlaylistTrack pt : items){
                    Log.e("TEST", pt.track.toString());
                    Log.e("TEST", pt.track.name + " - " + pt.track.id);
                }

                Map<String, Object> query = new HashMap<>();
                Map<String, Object> body = new HashMap<>();
                query.put("uris", playlistTrackPager.items);
            }

           @Override
           public void failure(SpotifyError spotifyError) {
               Log.e("TEST", "Could not get playlist tracks");

           }
        });

    }

    public void addTrackToPlaylist(){
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        query.put("uris", "spotify:track:2MfgdGJUFyZAprtuOEujRb");

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
        })  ;

    }





}