/*
 * This is the DataBase class. I used it for do all main treatment like :
 * Create DB and tables, check if exists, connect, do query, ...
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

/**
 *
 * @author Loïc Dessaules
 */
public class CacheDb {
    
    private String path = "cache/cache.db";
    
    public CacheDb() {
    }

    /**
     * Check if the database "cache/cache.db" exists
     * 
     * @return  {@code true} if the DB exists;
     *          {@code false} otherwise
     */
    public boolean exists() {        
        File cacheFile = new File(path);

        if(cacheFile.exists() && cacheFile.isFile()) {
            return true;
        }else {
            return false;
        }
    }
    
    /**
     * Return the connection from the DriverManager
     * 
     * @return  The connection to the DB
     * @see     DriverManager#getConnection
     */
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
    
    /**
     * Create the folder: "cache" 
     * and the file : "cache.db" if they are not already created.
     * 
     * And after that, created all tables we need.
     * 
     * @see     CacheDb#getTablesQueries
     */
    public void create() {
        File cacheDirectory = new File("cache");
        ArrayList<String> tablesQueries;
        
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
                
                // Close the connection
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }                     

    }

    /**
     * Return all tables we have to create
     * 
     * @return  Queries of all tables we have to create
     */
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
        
        queries.add(sqlCreateTableMovie);
        queries.add(sqlCreateTableActor);
        queries.add(sqlCreateTableGenre);
        queries.add(sqlCreateTableDirector);
        queries.add(sqlCreateTableMovieHasActor);
        queries.add(sqlCreateTableMovieHasGenre);
        queries.add(sqlCreateTableMovieHasDirector);
           
        return queries;
    }
    
    /**
     * Return all query results in this format : result1;result2;result3 
     * (always a ";" at the end of each result)
     * 
     * @param   query the query to execute
     * @return  The query results
     */
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
    
    /**
     * Execute a no return query (Insert, Update, Delete)
     * 
     * @param   query the query to execute
     */
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
