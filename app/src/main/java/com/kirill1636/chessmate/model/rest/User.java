package com.kirill1636.chessmate.model.rest;

public class User {
    private Integer id;
    private String name;
    private String vkid;
    private Integer rating;

    public User() {
    }

    public User(int id, String name, String vkid, int rating){
        this.id = id;
        this.name = name;
        this.vkid = vkid;
        this.rating = rating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVkid() {
        return vkid;
    }

    public void setVkid(String vkid) {
        this.vkid = vkid;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vkid='" + vkid + '\'' +
                ", rating=" + rating +
                '}';
    }
}