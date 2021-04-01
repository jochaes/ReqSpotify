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


    public void getAlbumName(String pAlbumID) {

        try {
            spotify.getAlbum(pAlbumID, new Callback<Album>() {
                @Override
                public void success(Album album, Response response) {
                    Log.d("Album success", album.name);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("Album failure", error.toString());
                }
            });
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            Log.d("PlaylistName failure", spotifyError.toString());
        }
    }

    public void getPlayListName(String pPlaylistID) {
        try {
            spotify.getPlaylist(sharedPreferences.getString("userid", ""), pPlaylistID, new SpotifyCallback<Playlist>() {
                @Override
                public void success(Playlist playlist, Response response) {
                    Log.d("PlaylistName success", playlist.name);
                }
                @Override
                public void failure(SpotifyError error) {
                    Log.e("PlaylistName failure", error.toString());
                }
            });
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            Log.d("PlaylistName failure", spotifyError.toString());
        }
    }

    public void getPlaylistTracks(String pPlaylistID, ArrayList<Track> localPlayList, ListView pListView, ArrayAdapter<Track> pAdapterTrack, Context pContext ){
        try {
            spotify.getPlaylistTracks(sharedPreferences.getString("userid", ""), pPlaylistID, new SpotifyCallback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    Log.d("GetTracks Success", "GOT the tracks in playlist");
                    List<PlaylistTrack> items = playlistTrackPager.items;

                    //Carga las cacnciones en el List View
                    loadPlaylistTracksListView( localPlayList, items,pListView,pAdapterTrack,pContext);

                    for( PlaylistTrack pt : items){
                        Log.i("Tracks from Playlist", pt.track.name + " - " + pt.track.id);
                    }
                }
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.e("GetTracks Failure", "Could not get playlist tracks");

                }
            });} catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            Log.e("GetTracks Failure", spotifyError.toString());
        }
    }

    private void loadPlaylistTracksListView(ArrayList<Track> pLocalPlayList, List<PlaylistTrack> pAPITrackList, ListView pListView, ArrayAdapter<Track> pAdapterTrack, Context pContext){
        pLocalPlayList.clear();
        for( PlaylistTrack pt : pAPITrackList){
            Track track = new Track(pt.track.id,pt.track.name);
            pLocalPlayList.add(track);
        }
        pAdapterTrack = new ArrayAdapter<Track>(pContext, android.R.layout.simple_list_item_1, pLocalPlayList );
        pListView.setAdapter( pAdapterTrack);
    }

    public void addTrackToPlaylist(String pTrackId, String pPLaylistId){
        String trackURI = "spotify:track:" + pTrackId;

        Map<String, Object> query = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        query.put("uris", trackURI);
        try {
            spotify.addTracksToPlaylist(sharedPreferences.getString("userid", ""), pPLaylistId,
                    query, body, new Callback<Pager<PlaylistTrack>>() {
                        @Override
                        public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                            Log.d("Add Track PL Succes", "Success in adding tracks");
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Add Track PL Failure", error.getUrl());
                        }
                    });
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            Log.e("Add Track PL Failure", spotifyError.toString());
        }
    }





}