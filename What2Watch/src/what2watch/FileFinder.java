/*
 * This class is responsible for reacting upon receiving events in the file tree browsing process
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
 * @author untoten
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
        // Skippding folders that aren't readable
        if (!Files.isReadable(dir)) {
            System.out.println("==============================");
            System.out.println(dir + "\n has been skipped because it's not readable");
            return SKIP_SUBTREE;
        }
        
        // Skipping system folders | TODO: handle C:\ like starting points
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

        return super.preVisitDirectory(dir, attrs); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        find(file);
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        //System.out.format("Directory: %s%n", dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
    
    // Custom methods
    
    // Compares the glob pattern against the file or directory name.
    void find(Path file) {
        Path fileName = file.getFileName();
        if (fileName != null && this.matcher.matches(fileName)) {
            if (!ParsingFiles.containsTVShowPattern(fileName.toString())) {
                this.movieFileNames.add(fileName.toString());
                this.movieFilePaths.add(file.toString());
//                System.out.println("==============================");
//                System.out.println(file.getFileName());
//                System.out.println(file);
            }
        }
    }
    
    // Compare the raw movie file name against the list movie file paths
    // Returns the movie file path that contains the raw movie file name specified by parameter
    public String findPathOf(String rawMovieName) {
        for (int i = 0; i < this.movieFilePaths.size(); i++) {
            if (this.movieFilePaths.get(i).contains(rawMovieName)) {
                return this.movieFilePaths.get(i);
            }
        }
        return "";
    }
}
