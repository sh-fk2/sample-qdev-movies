package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! Search for movie treasures using various criteria.
     * This endpoint handles both HTML and JSON responses for the treasure hunt!
     * 
     * @param movieName The name of the movie to search for (partial match)
     * @param movieId The specific movie ID to find
     * @param genre The genre to filter by
     * @param model The model for HTML rendering
     * @return Either a JSON response or HTML template with search results
     */
    @GetMapping("/movies/search")
    public String searchMovieTreasure(
            @RequestParam(value = "name", required = false) String movieName,
            @RequestParam(value = "id", required = false) Long movieId,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Starting movie treasure hunt with name: '{}', id: '{}', genre: '{}'", 
                   movieName, movieId, genre);
        
        // Validate that at least one search parameter is provided
        if (!movieService.areSearchParametersValid(movieName, movieId, genre)) {
            logger.warn("Arrr! No valid search parameters provided for the treasure hunt");
            model.addAttribute("title", "Invalid Search Parameters");
            model.addAttribute("message", "Arrr! Ye need to provide at least one search parameter to find yer movie treasure, matey!");
            model.addAttribute("searchPerformed", true);
            model.addAttribute("movies", movieService.getAllMovies());
            return "movies";
        }
        
        try {
            // Perform the treasure hunt!
            List<Movie> treasureHaul = movieService.searchMovieTreasure(movieName, movieId, genre);
            
            // Prepare the model for the template
            model.addAttribute("movies", treasureHaul);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", movieName);
            model.addAttribute("searchId", movieId);
            model.addAttribute("searchGenre", genre);
            
            if (treasureHaul.isEmpty()) {
                logger.info("Arrr! No treasure found matching the search criteria");
                model.addAttribute("title", "No Treasure Found");
                model.addAttribute("message", "Shiver me timbers! No movies were found matching yer search criteria. Try adjusting yer search parameters, ye scurvy dog!");
            } else {
                logger.info("Yo ho ho! Found {} movies in the treasure hunt", treasureHaul.size());
                model.addAttribute("title", "Movie Treasure Found!");
                model.addAttribute("message", String.format("Arrr! Found %d movie treasure%s matching yer search, matey!", 
                                                           treasureHaul.size(), treasureHaul.size() == 1 ? "" : "s"));
            }
            
            return "movies";
            
        } catch (Exception e) {
            logger.error("Arrr! Error during movie treasure hunt: {}", e.getMessage(), e);
            model.addAttribute("title", "Treasure Hunt Failed");
            model.addAttribute("message", "Blimey! Something went wrong during the treasure hunt. The kraken might have eaten our search results!");
            model.addAttribute("searchPerformed", true);
            model.addAttribute("movies", movieService.getAllMovies());
            return "movies";
        }
    }

    /**
     * Ahoy! JSON API endpoint for movie treasure hunting.
     * Returns search results in JSON format for API consumers.
     * 
     * @param movieName The name of the movie to search for
     * @param movieId The specific movie ID to find
     * @param genre The genre to filter by
     * @return ResponseEntity with search results or error message
     */
    @GetMapping("/api/movies/search")
    @ResponseBody
    public ResponseEntity<?> searchMovieTreasureApi(
            @RequestParam(value = "name", required = false) String movieName,
            @RequestParam(value = "id", required = false) Long movieId,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! API treasure hunt requested with name: '{}', id: '{}', genre: '{}'", 
                   movieName, movieId, genre);
        
        // Validate search parameters
        if (!movieService.areSearchParametersValid(movieName, movieId, genre)) {
            logger.warn("Arrr! Invalid API search parameters provided");
            return ResponseEntity.badRequest()
                .body(new SearchErrorResponse("Arrr! Ye need to provide at least one valid search parameter, matey!"));
        }
        
        try {
            List<Movie> treasureHaul = movieService.searchMovieTreasure(movieName, movieId, genre);
            
            SearchResponse response = new SearchResponse(
                treasureHaul,
                treasureHaul.size(),
                String.format("Yo ho ho! Found %d movie treasure%s", treasureHaul.size(), treasureHaul.size() == 1 ? "" : "s"),
                movieName,
                movieId,
                genre
            );
            
            logger.info("API treasure hunt successful! Found {} movies", treasureHaul.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Arrr! API treasure hunt failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new SearchErrorResponse("Blimey! The kraken attacked our treasure hunt! Try again later, matey."));
        }
    }

    /**
     * Response wrapper for successful search results
     */
    public static class SearchResponse {
        private final List<Movie> movies;
        private final int totalResults;
        private final String message;
        private final String searchName;
        private final Long searchId;
        private final String searchGenre;

        public SearchResponse(List<Movie> movies, int totalResults, String message, 
                            String searchName, Long searchId, String searchGenre) {
            this.movies = movies;
            this.totalResults = totalResults;
            this.message = message;
            this.searchName = searchName;
            this.searchId = searchId;
            this.searchGenre = searchGenre;
        }

        public List<Movie> getMovies() { return movies; }
        public int getTotalResults() { return totalResults; }
        public String getMessage() { return message; }
        public String getSearchName() { return searchName; }
        public Long getSearchId() { return searchId; }
        public String getSearchGenre() { return searchGenre; }
    }

    /**
     * Response wrapper for search errors
     */
    public static class SearchErrorResponse {
        private final String error;

        public SearchErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}