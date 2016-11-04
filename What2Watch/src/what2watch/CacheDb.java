/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loïc
 */
public class CacheDb {
    
    private String path = "cache/cache.db";
    
    // Constructor
    public CacheDb() {
    }
    
    
    
    // Method for check if the DB file exists or not
    public boolean exists() {
        
        File cacheFile = new File(path);

        // Check if cache file already exists (if not, one new database cache will be created)
        if(cacheFile.exists() && cacheFile.isFile()) {
            return true;
        }else {
            return false;
        }

    }
    
    private Connection connect() {
               
        String url = "jdbc:sqlite:"+path;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
    
    // Method for create a new database
    public void create() {
        
        File cacheDirectory = new File("cache");
        ArrayList<String> tablesQueries;
        
        // Check if cache directory already exists (if not, one will be created)
        if(cacheDirectory.exists() && cacheDirectory.isDirectory()) {
            System.out.println("The directory cache already exists."); 
        }else {
            cacheDirectory.mkdir();
        }
        
        // Create database    
        Connection connection = connect();   
        if (connection != null) {
            try {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created");
                
                // SQL statement for creating a new table
                Statement stmt = connection.createStatement();
                // create all the tables
                tablesQueries = getTablesQueries();
                for (int i=0; i < tablesQueries.size(); i++) {
                    stmt.execute(tablesQueries.get(i));
                }
                System.out.println(tablesQueries.size()+" tables has been created");
                
                
                /* TEST */
                // ONLY FOR TRYING (For to have on the BDD a few datas)
                /*ArrayList<String> insertQueries;
              
                insertQueries = getInsertQueries();
                for (int i=0; i < insertQueries.size(); i++) {
                    stmt.execute(insertQueries.get(i));
                }
                System.out.println("Datas has been inputed into "+insertQueries.size()+" tables");
                /* /TEST */
                
                
                
                
                // Close the connection
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }                     

    }

    
    private ArrayList<String> getTablesQueries() {
        
        ArrayList<String> queries = new ArrayList<String>();

        
        // All the SQL queries for create the tables
        String sqlCreateTableMovie = " CREATE TABLE movie("
                +"id INTEGER PRIMARY KEY NOT NULL,"
                +"raw_title VARCHAR(300) NOT NULL,"
                +"title VARCHAR(150) NOT NULL,"
                +"year YEAR NOT NULL,"
                +"image_link VARCHAR(300) NOT NULL,"
                +"synopsis TEXT NOT NULL);";
        
        String sqlCreateTableActor = " CREATE TABLE actor("
                +"id INTEGER PRIMARY KEY NOT NULL,"
                +"name VARCHAR(100) NOT NULL);";
        
        String sqlCreateTableGenre = " CREATE TABLE genre("
                +"id INTEGER PRIMARY KEY NOT NULL,"
                +"type VARCHAR(45) NOT NULL);";
        
        String sqlCreateTableDirector = " CREATE TABLE director("
                +"id INTEGER PRIMARY KEY NOT NULL,"
                +"name VARCHAR(100) NOT NULL);";
        
        String sqlCreateTableMovieHasActor = "CREATE TABLE movie_has_actor("
                +"movie_id INTEGER NOT NULL,"
                +"actor_id INTEGER NOT NULL,"
                +"FOREIGN KEY(movie_id) REFERENCES movie(id),"
                +"FOREIGN KEY(actor_id) REFERENCES actor(id));";
        
        String sqlCreateTableMovieHasGenre = "CREATE TABLE movie_has_genre("
                +"movie_id INTEGER NOT NULL,"
                +"genre_id INTEGER NOT NULL,"
                +"FOREIGN KEY(movie_id) REFERENCES movie(id),"
                +"FOREIGN KEY(genre_id) REFERENCES genre(id));";
        
        String sqlCreateTableMovieHasDirector = "CREATE TABLE movie_has_director("
                +"movie_id INTEGER NOT NULL,"
                +"director_id INTEGER NOT NULL,"
                +"FOREIGN KEY(movie_id) REFERENCES movie(id),"
                +"FOREIGN KEY(director_id) REFERENCES director(id));";
        
        
        // Add SQL queries to the arrayList
        queries.add(sqlCreateTableMovie);
        queries.add(sqlCreateTableActor);
        queries.add(sqlCreateTableGenre);
        queries.add(sqlCreateTableDirector);
        queries.add(sqlCreateTableMovieHasActor);
        queries.add(sqlCreateTableMovieHasGenre);
        queries.add(sqlCreateTableMovieHasDirector);
           
        // Return the arrayList
        return queries; 
    }

    
    
    // ONLY FOR TRYING (For to have on the BDD a few datas)
    private ArrayList<String> getInsertQueries() {
        ArrayList<String> queries = new ArrayList<String>();
 
        // All the SQL queries for insert the datas
        String sqlInsertIntoActor = "INSERT INTO 'actor'"
                + "VALUES"
                + "(NULL,'Orlando Bloom'),"
                + "(NULL,'Leonardo DiCaprio'),"
                + "(NULL,'Chris Pratt');";
        
        String sqlInsertIntoMovie = "INSERT INTO 'movie'"
                + "VALUES"
                + "(NULL,'The Lord of the Rings: The Fellowship of the Ring','2001','https://images-na.ssl-images-amazon.com/images/M/MV5BNTEyMjAwMDU1OV5BMl5BanBnXkFtZTcwNDQyNTkxMw@@._V1_SX300.jpg','An ancient Ring thought lost for centuries has been found, and through a strange twist in fate has been given to a small Hobbit named Frodo. When Gandalf discovers the Ring is in fact the One Ring of the Dark Lord Sauron, Frodo must make an epic quest to the Cracks of Doom in order to destroy it! However he does not go alone. He is joined by Gandalf, Legolas the elf, Gimli the Dwarf, Aragorn, Boromir and his three Hobbit friends Merry, Pippin and Samwise. Through mountains, snow, darkness, forests, rivers and plains, facing evil and danger at every corner the Fellowship of the Ring must go. Their quest to destroy the One Ring is the only hope for the end of the Dark Lords reign!'),"
                + "(NULL,'Titanic','1997','https://images-na.ssl-images-amazon.com/images/M/MV5BZDNiMjE0NDgtZWRhNC00YTlhLTk2ZjItZTQzNTU2NjAzNWNkXkEyXkFqcGdeQXVyNjUwNzk3NDc@._V1_SX300.jpg','84 years later, a 101-year-old woman named Rose DeWitt Bukater tells the story to her granddaughter Lizzy Calvert, Brock Lovett, Lewis Bodine, Bobby Buell and Anatoly Mikailavich on the Keldysh about her life set in April 10th 1912, on a ship called Titanic when young Rose boards the departing ship with the upper-class passengers and her mother, Ruth DeWitt Bukater, and her fiancé, Caledon Hockley. Meanwhile, a drifter and artist named Jack Dawson and his best friend Fabrizio De Rossi win third-class tickets to the ship in a game. And she explains the whole story from departure until the death of Titanic on its first and last voyage April 15th, 1912 at 2:20 in the morning.');";
               
        String sqlInsertIntoGenre = "INSERT INTO 'genre'"
                + "VALUES "
                + "(NULL,'Drama'),"
                + "(NULL,'Romance'),"
                + "(NULL,'Action'),"
                + "(NULL,'Adventure');";
        
        String sqlInsertIntoDirector = "INSERT INTO 'director'"
                + "VALUES "
                + "(NULL,'Peter Jackson'),"
                + "(NULL,'James Cameron');";
        
        String sqlInsertIntoMovieHasActor = "INSERT INTO 'movie_has_actor'"
                + "VALUES "
                + "('1','1'),"
                + "('2','2');";
        
        String sqlInsertIntoMovieHasGenre = "INSERT INTO 'movie_has_genre'"
                + "VALUES "
                + "('1','1'),"
                + "('1','3'),"
                + "('1','4'),"
                + "('2','1'),"
                + "('2','2');";
        
        String sqlInsertIntoMovieHasDirector = "INSERT INTO 'movie_has_director'"
                + "VALUES "
                + "('1','1'),"
                + "('2','2');";
        
        
        // Add SQL queries to the arrayList
        queries.add(sqlInsertIntoActor);
        queries.add(sqlInsertIntoMovie);
        queries.add(sqlInsertIntoGenre);
        queries.add(sqlInsertIntoDirector);
        queries.add(sqlInsertIntoMovieHasActor);
        queries.add(sqlInsertIntoMovieHasGenre);
        queries.add(sqlInsertIntoMovieHasDirector);

           
        // Return the arrayList
        return queries; 
    }
    
    
    public String doSelectQuery(String query) {

        String resultant = "";

        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet result    = stmt.executeQuery(query)){
            ResultSetMetaData metadata = result.getMetaData();
            int columnCount = metadata.getColumnCount();
            while (result.next()) {    
                for(int i=1;i<=columnCount;i++) {
                    resultant += result.getString(i) + ";";
                }    
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
            return resultant;
    }
    
    
    // (No return query is for example : Insert / Update / Delete)
    void doNoReturnQuery(String query) {
 
        Connection conn = this.connect();
        try {
            Statement stmt  = conn.createStatement();
            stmt.execute(query);
        } catch (SQLException ex) {
            System.out.println("Error on CacheDb class / doNoReturnQuery. Exeption = "+ex.getMessage().toString());
        }
        
    }
    
    
}
