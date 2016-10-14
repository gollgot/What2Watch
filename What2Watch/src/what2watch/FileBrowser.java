/*
 * The purpose of this class is to handle the movie files used within the application
 */
package what2watch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author Raphael.BAZZARI
 */
public class FileBrowser {
    private String[] extensions = {".avi", ".mkv", ".mpeg", ".wmv", ".m4v", ".mp4", ".flv", ".mov"};
    private ArrayList<String> movieFileNames = new ArrayList<String>();
    private ParsingFiles fileParser = new ParsingFiles();

    public FileBrowser() {
    }

    public String[] getExtensions() {
        return extensions;
    }

    public ArrayList<String> getMovieFileNames() {
        return movieFileNames;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    public void setMovieFileNames(ArrayList<String> movieFileNames) {
        this.movieFileNames = movieFileNames;
    }
    
    
    // Custom methods
    
    // Fectches the movie file names contained within the folder specified by the path parameter
    public void fetchMoviesFileNames(String path) throws IOException {
        // Making sure the path isn't empty
        if (!path.equals("")) {
            Files.find(Paths.get(path),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach((Path sourcePath) -> {
                        // Get rid of this (loop complexity)
                        for (int i = 0; i < this.extensions.length; i++) {
                            String videoFileName = sourcePath.getFileName().toString();
                            // Making sure the file has a video type
                            if (videoFileName.endsWith(this.extensions[i])) {
                                // Making sure it doesn't contain TV Shows patterns
                                if (!fileParser.containsTVShowPattern(videoFileName)) {
                                    movieFileNames.add(sourcePath.getFileName().toString());
                                    //System.out.println(sourcePath.getFileName().toString());
                                    //System.out.println(sourcePath);
                                    break;
                                }
                            }
                        }
                    });
        }
    }
}
