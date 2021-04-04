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

/**
 * Servicio para controlar el API, se encarga de traer las canciones de un playlist, cargar la playlist a un listview,
 *  añadir una nueva canción a la playlist.
 *
 *  Utiliza en Wrapper de Kaees para los request del API
 */
public class TrackService {
    private SharedPreferences sharedPreferences;  //Para acceder al Token de acceso

    SpotifyApi api;                               //Wrapper Kaees
    SpotifyService spotify;                       //wrapper Kaees

    public TrackService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        api = new SpotifyApi();
        api.setAccessToken(sharedPreferences.getString("token", ""));
        spotify = api.getService();  //Se conecta con espotify
    }


    /**
     * Busca el nombre de un album de spotify mediante el Id
     * @param pAlbumID ID del URI de spotify
     */
    public void getAlbumName(String pAlbumID) {

        try {
            //Wrapper de Kaees, get album
            spotify.getAlbum(pAlbumID, new Callback<Album>() {
                //Logró encontrar el Album, lod del nombre
                @Override
                public void success(Album album, Response response) {
                    Log.d("Album success", album.name);
                }

                //No logra encontrar el album, log del error
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

    /**
     * Busca el nombre de la playlist en spotify, mediante el Id de la playlist
     * @param pPlaylistID ID del URI de la plylist
     */
    public void getPlayListName(String pPlaylistID) {
        try {
            //Wrapper de Kaees para buscar el Nombre de la Playlist
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

    /**
     * Get de las canciones dentro de una PLaylist
     * @param pPlaylistID   Id de la playlist
     * @param localPlayList ArrayList donde se va a guardar las canciones
     * @param pListView     Listview donde se van a mostrar las canciones
     * @param pAdapterTrack Adapter del listview
     * @param pContext      contexto del main activity
     */
    public void getPlaylistTracks(String pPlaylistID, ArrayList<Track> localPlayList, ListView pListView, ArrayAdapter<Track> pAdapterTrack, Context pContext ){
        try {
            //Wrapper de Kaees para getplaylistTracks
            spotify.getPlaylistTracks(sharedPreferences.getString("userid", ""), pPlaylistID, new SpotifyCallback<Pager<PlaylistTrack>>() {
                //Trae las canciones en un playListTrackPager y las carga q
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    Log.d("GetTracks Success", "GOT the tracks in playlist");
                    List<PlaylistTrack> items = playlistTrackPager.items;

                    //Carga las canciones en el List View
                    loadPlaylistTracksListView( localPlayList, items,pListView,pAdapterTrack,pContext);

                    //Imprime la lista de canciones
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

    /**
     * Carga las canciones en el playlist
     * @param pLocalPlayList Playlist donde se van a guardar las canciones
     * @param pAPITrackList Lista de canciones del API
     * @param pListView     List view donde se van a mostrar las canciones
     * @param pAdapterTrack Adapter track del listview
     * @param pContext      contexto del main activity
     */
    private void loadPlaylistTracksListView(ArrayList<Track> pLocalPlayList, List<PlaylistTrack> pAPITrackList, ListView pListView, ArrayAdapter<Track> pAdapterTrack, Context pContext){
        pLocalPlayList.clear();    //Se limpia la lista
        for( PlaylistTrack pt : pAPITrackList){   //Se crean las tracks
            Track track = new Track(pt.track.id,pt.track.name);
            pLocalPlayList.add(track);
        }
        //Se crea un nuevo adapter para el listview que contiene las canciones
        pAdapterTrack = new ArrayAdapter<Track>(pContext, android.R.layout.simple_list_item_1, pLocalPlayList );
        pListView.setAdapter( pAdapterTrack); //Setea el adapter al listview
    }

    /**
     * Añade una canción a la playlist
     * @param pTrackId Track ID de uri de la cancion en spotify
     * @param pPLaylistId Lista donde se va a guardar esa canción
     */
    public void addTrackToPlaylist(String pTrackId, String pPLaylistId){
        String trackURI = "spotify:track:" + pTrackId;              //Forma el URI

        Map<String, Object> query = new HashMap<>();                //Query con la infro del track
        Map<String, Object> body = new HashMap<>();
        query.put("uris", trackURI);
        try {
            //Wrapper de Kaees para añadir tracks a la playlist
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

    public void deleteTrackFromPlaylist(String pTrackId, string pPlaylistId){
        String trackURI = "spotify:track:" + pTrackId;              //Forma el URI
        try {
            //Wrapper de Kaeees para borrar tracks a la playlist
            spotify.removeTracksFromPlaylist(sharedPreferences.getString("userid", ""), pPLaylistId, trackURI, new Callback<Pager<PlaylistTrack>>() {
                        @Override
                        public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                            Log.d("Remove Track PL Succes", "Success in Removing tracks");
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Remove Track PL Failure", error.getUrl());
                        }
                    });
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            Log.e("Remove Track PL Failure", spotifyError.toString());
        }
    }
}