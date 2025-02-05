package com.starwars.model;

import java.util.List;

public class SWAPIResponse<T> {
    private List<T> results; // List of objects of type T (Film, Person, etc.)
    private String next; // Link to the next page of results (if pagination is used)
    private String previous; // Link to the previous page of results (if pagination is used)

    // Getters and Setters
    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
