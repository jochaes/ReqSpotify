package com.example.reqspotify;


public class Track {

    private String id;
    private String name;

    public Track(String id, String name) {
        this.name = name;
        this.id = id;
    }


    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
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