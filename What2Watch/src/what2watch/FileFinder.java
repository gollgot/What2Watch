/*
 * This class is responsible for reacting upon receiving events in the file tree walking process
 * Its main tasks are to find and store movie file names and their respective paths
 * This class is based on https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/essential/io/examples/Find.java
 */
package what2watch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;

/**
 *
 * @author Raphael.BAZZARI
 */
public class FileFinder extends SimpleFileVisitor<Path> {
    private Path initialDirectory;
    private String pattern;
    private PathMatcher matcher;
    private ArrayList<String> movieFileNames = new ArrayList<String>();
    private ArrayList<String> movieFilePaths = new ArrayList<String>();

    
    public FileFinder() {
    }

    public FileFinder(Path initialDirectory, String givenPattern) {
        this.initialDirectory = initialDirectory;
        this.pattern = givenPattern;
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + givenPattern);
    }

    public Path getInitialDirectory() {
        return initialDirectory;
    }

    public void setInitialDirectory(Path initialDirectory) {
        this.initialDirectory = initialDirectory;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }
    
    public ArrayList<String> getMovieFileNames() {
        return movieFileNames;
    }
    
    public ArrayList<String> getMovieFilePaths() {
        return movieFilePaths;
    }

    
    // SimpleFileVisitor overriding methods

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        // Skipping folders that aren't readable
        if (!Files.isReadable(dir)) {
            System.out.println("==============================");
            System.out.println(dir + "\n has been skipped because it's not readable");
            return SKIP_SUBTREE;
        }
        
        if (dir != this.initialDirectory) {
            try {
                DosFileAttributes attr = Files.readAttributes(dir, DosFileAttributes.class);
                if (attr.isSystem()) {
                    return SKIP_SUBTREE;
                }
            } catch (UnsupportedOperationException x) {
                System.err.println("DOS file" + " attributes not supported:" + x);
            }
        }

        return super.preVisitDirectory(dir, attrs);
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        find(file);
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
    
    // Custom methods
    
    /** 
     * Retrieves the movie file names and paths of any file that matches 
     * the pattern specified in FileFinder's "pattern" property.
     * 
     * Those informations are then stored into FileFinder's
     * "movieFileNames" and "movieFilePaths" properties
     *  
     */
    void find(Path file) {
        Path fileName = file.getFileName();
        if (fileName != null && this.matcher.matches(fileName)) {
            if (!ParsingFiles.containsTVShowPattern(fileName.toString())) {
                this.movieFileNames.add(fileName.toString());
                this.movieFilePaths.add(file.toString());
            }
        }
    }
    
    
    /** 
     * Finds the path of the raw movie file name by comparing it with the list
     * of movie file paths stored in FileFinder's "movieFilePaths" property
     * 
     * @param   rawMovieName the raw movie name for which to find a path
     *  
     * @return  the movie file path or an empty string if no
     * path has been found
     */
    public String findPathOf(String rawMovieName) {
        for (int i = 0; i < this.movieFilePaths.size(); i++) {
            if (this.movieFilePaths.get(i).contains(rawMovieName)) {
                return this.movieFilePaths.get(i);
            }
        }
        return "";
    }
}
