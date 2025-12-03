package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Searches for movies based on the provided criteria like a pirate hunting for treasure!
     * Arrr! This method filters the movie treasure chest by name, id, and genre.
     * 
     * @param movieName The name to search for (partial match, case-insensitive)
     * @param movieId The specific movie ID to find
     * @param genre The genre to filter by (case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovieTreasure(String movieName, Long movieId, String genre) {
        logger.info("Ahoy! Starting treasure hunt for movies with name: '{}', id: '{}', genre: '{}'", 
                   movieName, movieId, genre);
        
        List<Movie> treasureHaul = new ArrayList<>(movies);
        
        // Filter by movie name if provided - partial match, case-insensitive
        if (movieName != null && !movieName.trim().isEmpty()) {
            String searchName = movieName.trim().toLowerCase();
            treasureHaul = treasureHaul.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
            logger.debug("Filtered by name '{}', found {} movies in the treasure chest", searchName, treasureHaul.size());
        }
        
        // Filter by movie ID if provided - exact match
        if (movieId != null && movieId > 0) {
            treasureHaul = treasureHaul.stream()
                .filter(movie -> movie.getId() == movieId)
                .collect(Collectors.toList());
            logger.debug("Filtered by ID '{}', found {} movies in the treasure chest", movieId, treasureHaul.size());
        }
        
        // Filter by genre if provided - case-insensitive
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            treasureHaul = treasureHaul.stream()
                .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                .collect(Collectors.toList());
            logger.debug("Filtered by genre '{}', found {} movies in the treasure chest", searchGenre, treasureHaul.size());
        }
        
        logger.info("Arrr! Treasure hunt complete! Found {} movies matching the search criteria", treasureHaul.size());
        return treasureHaul;
    }

    /**
     * Validates search parameters to ensure they're seaworthy!
     * 
     * @param movieName The movie name parameter
     * @param movieId The movie ID parameter  
     * @param genre The genre parameter
     * @return true if at least one valid search parameter is provided
     */
    public boolean areSearchParametersValid(String movieName, Long movieId, String genre) {
        boolean hasValidName = movieName != null && !movieName.trim().isEmpty();
        boolean hasValidId = movieId != null && movieId > 0;
        boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
        
        boolean isValid = hasValidName || hasValidId || hasValidGenre;
        logger.debug("Search parameters validation - Name: {}, ID: {}, Genre: {}, Valid: {}", 
                    hasValidName, hasValidId, hasValidGenre, isValid);
        
        return isValid;
    }
}
