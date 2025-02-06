package com.starwars.dto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

public class GenericStarWarsResponse<T> extends RepresentationModel<GenericStarWarsResponse<T>> {
    private String type;
    private int count;
    private String name;
    private String films;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFilms() {
        return films;
    }
    public void setFilms(String films) {
        this.films = films;
    }

}
