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
                ArrayList<String> insertQueries;
              
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
    
    
        public String doSqlQuerry(String sql){

    String retour = "";

      try (Connection conn = this.connect();
           Statement stmt  = conn.createStatement();
           ResultSet rs    = stmt.executeQuery(sql)){
           ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            while (rs.next()) {    
                  for(int i=1;i<=columnCount;i++)
                  {
                      retour += rs.getString(i) + ";";
                  }
                  
                retour = retour + "\n";     
            }
          
      } catch (SQLException e) {
          System.out.println(e.getMessage());
      } 
        return retour;
    }

    
    private ArrayList<String> getTablesQueries() {
        
        ArrayList<String> queries = new ArrayList<String>();

        
        // All the SQL queries for create the tables
        String sqlCreateTableMovies = " CREATE TABLE movies(\n"
                +"id INTEGER PRIMARY KEY NOT NULL,\n"
                +"title VARCHAR(150) NOT NULL,\n"
                +"year YEAR NOT NULL,\n"
                +"genre VARCHAR(50) NOT NULL,\n"
                +"director VARCHAR(50) NOT NULL,\n"
                +"image_link VARCHAR(300) NOT NULL,\n"
                + "synopsis TEXT NOT NULL);";
        
        String sqlCreateTableActors = " CREATE TABLE actors(\n"
                +"id INTEGER PRIMARY KEY NOT NULL,\n"
                +"name VARCHAR(50) NOT NULL,\n"
                +"last_name VARCHAR(50) NOT NULL);";
        
        String sqlCreateTableMoviesHasActors = "CREATE TABLE movies_has_actors(\n"
                +"movies_id INTEGER NOT NULL,"
                +"actors_id INTEGER NOT NULL,"
                +"FOREIGN KEY(movies_id) REFERENCES movies(id),"
                +"FOREIGN KEY(actors_id) REFERENCES actors(id));";
        
        
        // Add SQL queries to the arrayList
        queries.add(sqlCreateTableActors);
        queries.add(sqlCreateTableMovies);
        queries.add(sqlCreateTableMoviesHasActors);
           
        // Return the arrayList
        return queries; 
    }

    
    
    // ONLY FOR TRYING (For to have on the BDD a few datas)
    private ArrayList<String> getInsertQueries() {
        ArrayList<String> queries = new ArrayList<String>();
 
        // All the SQL queries for insert the datas
        String sqlInsertIntoActors = "INSERT INTO `actors`"
                + "VALUES (NULL,'Leonardo','DiCaprio'),"
                + "(NULL,'Kate','Winslet'),"
                + "(NULL,'Billy','Zane');";
        
        String sqlInsertIntoMovies = "INSERT INTO `movies`"
                + "VALUES (NULL,'Titanic','1997','Action & Adventure','James Cameron','http://is2.mzstatic.com/image/thumb/Video/v4/7c/78/e4/7c78e482-f2e5-f2ce-83b6-188b26c3706d/source/100x100bb.jpg','So how did a ship that was apparently built to be impregnable sink on its maiden voyage? The genuine'),"
                + "(NULL,'The Lord of the Rings: The Fellowship of the Ring','2002','Action & Adventure','Peter Jackson','http://is5.mzstatic.com/image/thumb/Video41/v4/5d/18/48/5d184891-f937-2513-7cea-e1b0878af529/source/100x100bb.jpg','One ring to rule them all. One ring to find them. One ring to bring them all and in the darkness bind them.');";
        
        String sqlInsertIntoMoviesHasActors = "INSERT INTO `movies_has_actors`"
                + "VALUES ('1','2'),"
                + "('2','2');";
        // Add SQL queries to the arrayList
        queries.add(sqlInsertIntoActors);
        queries.add(sqlInsertIntoMovies);
        queries.add(sqlInsertIntoMoviesHasActors);

           
        // Return the arrayList
        return queries; 
    }
    
    
}
