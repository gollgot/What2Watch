/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

/**
 *
 * @author David.ROSAT & loic.dessaules
 */
public class DbHandler {
    
    private CacheDb dataBase;
    private String movieName;

    public DbHandler(CacheDb dataBase, String movieName) {
        this.dataBase = dataBase;
        this.movieName = movieName;
        
        if(movieExists(movieName)){
            System.out.println(movieName+" Existe !");
        }else{
            System.out.println(movieName+" Existe pas !");
        }
    }
    
    private boolean movieExists(String movieName){
        
        // I get the movieName in lower case and I check in the DB on a LOWER(title)
        // like that we don't have any conflict with sensitiveCase
        String movieNameLower = movieName.toLowerCase();
        String query = "SELECT LOWER(title) AS title FROM movies WHERE title LIKE \""+movieNameLower+"\"";
        
        String result = dataBase.doSqlQuerry(query);       
        System.out.println("Resultat : "+result);
        
        if(result.equals("")){
            return false;   
        }else{
            return true;        
        }
    }
   
    
}
