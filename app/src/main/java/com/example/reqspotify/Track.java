package com.example.reqspotify;

/**
 * Clase para guardar la info de las canciones
 */
public class Track {

    private String id;                      //Id del URI de Spotify
    private String name;                    //Nombre de la canción

    public Track(String id, String name) {
        this.name = name;
        this.id = id;
    }


    @Override
    public String toString() {
        return getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}