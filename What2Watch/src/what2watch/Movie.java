package what2watch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author loic.dessaules
 */
public class Movie {
    
    
    private String title;
    private String director;
    private String[] actors;
    private String year;
    private String poster;
    private String genre;
    private String synopsis;
    

    
    public Movie() {
    }
    
    
    // GETTERS
    
    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String[] getActors() {
        return actors;
    }

    public String getYear() {
        return year;
    }

    public String getPoster() {
        return poster;
    }

    public String getGenre() {
        return genre;
    }
    
    public String getSynopsis() {
        return synopsis;
    }
    
    
    
    
    // SETTERS

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setActors(String actors) {
        this.actors = actors.split(", ");
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    
    
    
    
    
    
    
}
