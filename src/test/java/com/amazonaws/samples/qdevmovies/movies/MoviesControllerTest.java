package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Unit tests for the MoviesController treasure hunting endpoints, ye scallywag!
 * These tests ensure our controller handles requests like a seasoned pirate captain.
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MockMovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create enhanced mock services with search functionality
        mockMovieService = new MockMovieService();
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Ahoy! Test original getMovies functionality")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
    }

    @Test
    @DisplayName("Yo ho ho! Test original getMovieDetails functionality")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    @DisplayName("Shiver me timbers! Test getMovieDetails not found")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    @DisplayName("Batten down the hatches! Test search treasure by name")
    public void testSearchMovieTreasureByName() {
        String result = moviesController.searchMovieTreasure("test", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Arrr! Test search treasure by ID")
    public void testSearchMovieTreasureById() {
        String result = moviesController.searchMovieTreasure(null, 1L, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals(1L, movies.get(0).getId());
    }

    @Test
    @DisplayName("Dead men tell no tales! Test search with no results")
    public void testSearchMovieTreasureNoResults() {
        String result = moviesController.searchMovieTreasure("nonexistent", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        
        assertTrue(model.containsAttribute("message"));
        String message = (String) model.getAttribute("message");
        assertTrue(message.contains("No movies were found"));
    }

    @Test
    @DisplayName("Scurvy dog! Test search with invalid parameters")
    public void testSearchMovieTreasureInvalidParameters() {
        String result = moviesController.searchMovieTreasure(null, null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        
        assertTrue(model.containsAttribute("message"));
        String message = (String) model.getAttribute("message");
        assertTrue(message.contains("at least one search parameter"));
    }

    @Test
    @DisplayName("Splice the mainbrace! Test API search treasure successful")
    public void testSearchMovieTreasureApiSuccessful() {
        ResponseEntity<?> response = moviesController.searchMovieTreasureApi("test", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(1, searchResponse.getTotalResults());
        assertEquals("Test Movie", searchResponse.getMovies().get(0).getMovieName());
    }

    @Test
    @DisplayName("Walk the plank! Test API search treasure with invalid parameters")
    public void testSearchMovieTreasureApiInvalidParameters() {
        ResponseEntity<?> response = moviesController.searchMovieTreasureApi(null, null, null);
        
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof MoviesController.SearchErrorResponse);
        
        MoviesController.SearchErrorResponse errorResponse = (MoviesController.SearchErrorResponse) response.getBody();
        assertTrue(errorResponse.getError().contains("at least one valid search parameter"));
    }

    @Test
    @DisplayName("Avast ye! Test API search treasure no results")
    public void testSearchMovieTreasureApiNoResults() {
        ResponseEntity<?> response = moviesController.searchMovieTreasureApi("nonexistent", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof MoviesController.SearchResponse);
        
        MoviesController.SearchResponse searchResponse = (MoviesController.SearchResponse) response.getBody();
        assertEquals(0, searchResponse.getTotalResults());
        assertTrue(searchResponse.getMovies().isEmpty());
    }

    @Test
    @DisplayName("Hoist the colors! Test search preserves search parameters in model")
    public void testSearchPreservesParameters() {
        String result = moviesController.searchMovieTreasure("test movie", 5L, "action", model);
        
        assertEquals("movies", result);
        assertEquals("test movie", model.getAttribute("searchName"));
        assertEquals(5L, model.getAttribute("searchId"));
        assertEquals("action", model.getAttribute("searchGenre"));
    }

    @Test
    @DisplayName("Pieces of eight! Test combined search criteria")
    public void testSearchMovieTreasureCombined() {
        // Mock service will return results for combined search
        String result = moviesController.searchMovieTreasure("test", 1L, "drama", model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("searchPerformed"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size()); // Mock service returns one movie for combined search
    }

    /**
     * Enhanced mock MovieService that supports search functionality for testing
     */
    private static class MockMovieService extends MovieService {
        private final List<Movie> testMovies;

        public MockMovieService() {
            testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Movie", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
        }

        @Override
        public List<Movie> getAllMovies() {
            return testMovies;
        }
        
        @Override
        public Optional<Movie> getMovieById(Long id) {
            return testMovies.stream()
                .filter(movie -> movie.getId() == id)
                .findFirst();
        }

        @Override
        public List<Movie> searchMovieTreasure(String movieName, Long movieId, String genre) {
            List<Movie> results = new ArrayList<>(testMovies);
            
            // Filter by name if provided
            if (movieName != null && !movieName.trim().isEmpty()) {
                String searchName = movieName.trim().toLowerCase();
                results = results.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Filter by ID if provided
            if (movieId != null && movieId > 0) {
                results = results.stream()
                    .filter(movie -> movie.getId() == movieId)
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Filter by genre if provided
            if (genre != null && !genre.trim().isEmpty()) {
                String searchGenre = genre.trim().toLowerCase();
                results = results.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            return results;
        }

        @Override
        public boolean areSearchParametersValid(String movieName, Long movieId, String genre) {
            boolean hasValidName = movieName != null && !movieName.trim().isEmpty();
            boolean hasValidId = movieId != null && movieId > 0;
            boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
            
            return hasValidName || hasValidId || hasValidGenre;
        }
    }

    @Test
    @DisplayName("Savvy! Test movie service integration")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }
}
