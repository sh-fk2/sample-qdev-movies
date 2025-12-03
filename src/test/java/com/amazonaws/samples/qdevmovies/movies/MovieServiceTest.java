package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Unit tests for the MovieService treasure hunting functionality, matey!
 * These tests ensure our movie search methods work like a well-oiled pirate ship.
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Ahoy! Test searching by movie name - partial match")
    public void testSearchMovieTreasureByName() {
        // Test partial name search (case-insensitive)
        List<Movie> results = movieService.searchMovieTreasure("prison", null, null);
        
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Arrr! Test searching by movie name - case insensitive")
    public void testSearchMovieTreasureByNameCaseInsensitive() {
        // Test case insensitive search
        List<Movie> results = movieService.searchMovieTreasure("FAMILY", null, null);
        
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Yo ho ho! Test searching by movie ID")
    public void testSearchMovieTreasureById() {
        // Test exact ID match
        List<Movie> results = movieService.searchMovieTreasure(null, 3L, null);
        
        assertEquals(1, results.size());
        assertEquals("The Masked Hero", results.get(0).getMovieName());
        assertEquals(3L, results.get(0).getId());
    }

    @Test
    @DisplayName("Shiver me timbers! Test searching by genre")
    public void testSearchMovieTreasureByGenre() {
        // Test genre search (case-insensitive, partial match)
        List<Movie> results = movieService.searchMovieTreasure(null, null, "drama");
        
        assertTrue(results.size() >= 5); // Multiple drama movies exist
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    @DisplayName("Batten down the hatches! Test combined search criteria")
    public void testSearchMovieTreasureCombined() {
        // Test searching with multiple criteria
        List<Movie> results = movieService.searchMovieTreasure("family", null, "crime");
        
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertTrue(results.get(0).getGenre().toLowerCase().contains("crime"));
    }

    @Test
    @DisplayName("Dead men tell no tales! Test search with no results")
    public void testSearchMovieTreasureNoResults() {
        // Test search that should return no results
        List<Movie> results = movieService.searchMovieTreasure("nonexistent", null, null);
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Arrr! Test search with invalid ID")
    public void testSearchMovieTreasureInvalidId() {
        // Test search with non-existent ID
        List<Movie> results = movieService.searchMovieTreasure(null, 999L, null);
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Splice the mainbrace! Test search parameter validation - all valid")
    public void testSearchParametersValidAllValid() {
        assertTrue(movieService.areSearchParametersValid("test", 1L, "drama"));
        assertTrue(movieService.areSearchParametersValid("test", null, null));
        assertTrue(movieService.areSearchParametersValid(null, 1L, null));
        assertTrue(movieService.areSearchParametersValid(null, null, "drama"));
    }

    @Test
    @DisplayName("Scurvy dog! Test search parameter validation - all invalid")
    public void testSearchParametersValidAllInvalid() {
        assertFalse(movieService.areSearchParametersValid(null, null, null));
        assertFalse(movieService.areSearchParametersValid("", null, null));
        assertFalse(movieService.areSearchParametersValid("   ", null, null));
        assertFalse(movieService.areSearchParametersValid(null, 0L, null));
        assertFalse(movieService.areSearchParametersValid(null, -1L, null));
        assertFalse(movieService.areSearchParametersValid(null, null, ""));
        assertFalse(movieService.areSearchParametersValid(null, null, "   "));
    }

    @Test
    @DisplayName("Avast ye! Test search with whitespace handling")
    public void testSearchMovieTreasureWhitespaceHandling() {
        // Test that whitespace is properly trimmed
        List<Movie> results1 = movieService.searchMovieTreasure("  prison  ", null, null);
        List<Movie> results2 = movieService.searchMovieTreasure("prison", null, null);
        
        assertEquals(results1.size(), results2.size());
        if (!results1.isEmpty() && !results2.isEmpty()) {
            assertEquals(results1.get(0).getMovieName(), results2.get(0).getMovieName());
        }
    }

    @Test
    @DisplayName("Pieces of eight! Test genre search with multiple matches")
    public void testSearchMovieTreasureGenreMultipleMatches() {
        // Test genre that should match multiple movies
        List<Movie> results = movieService.searchMovieTreasure(null, null, "action");
        
        assertTrue(results.size() >= 2); // Should find multiple action movies
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("action")));
    }

    @Test
    @DisplayName("Walk the plank! Test search with special characters")
    public void testSearchMovieTreasureSpecialCharacters() {
        // Test search with special characters (should handle gracefully)
        List<Movie> results = movieService.searchMovieTreasure("@#$%", null, null);
        
        assertTrue(results.isEmpty()); // Should return empty list, not crash
    }

    @Test
    @DisplayName("Hoist the colors! Test all movies are loaded")
    public void testAllMoviesLoaded() {
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertEquals(12, allMovies.size()); // Based on the movies.json file
        
        // Verify some key movies are present
        assertTrue(allMovies.stream().anyMatch(movie -> 
            movie.getMovieName().equals("The Prison Escape")));
        assertTrue(allMovies.stream().anyMatch(movie -> 
            movie.getMovieName().equals("The Family Boss")));
        assertTrue(allMovies.stream().anyMatch(movie -> 
            movie.getMovieName().equals("Space Wars: The Beginning")));
    }

    @Test
    @DisplayName("Savvy! Test search maintains original movie data integrity")
    public void testSearchMovieTreasureDataIntegrity() {
        // Ensure search doesn't modify original movie data
        List<Movie> originalMovies = movieService.getAllMovies();
        List<Movie> searchResults = movieService.searchMovieTreasure("the", null, null);
        List<Movie> afterSearchMovies = movieService.getAllMovies();
        
        assertEquals(originalMovies.size(), afterSearchMovies.size());
        
        // Verify a specific movie's data hasn't changed
        Movie originalMovie = originalMovies.stream()
            .filter(m -> m.getId() == 1L)
            .findFirst()
            .orElse(null);
        Movie afterSearchMovie = afterSearchMovies.stream()
            .filter(m -> m.getId() == 1L)
            .findFirst()
            .orElse(null);
        
        assertNotNull(originalMovie);
        assertNotNull(afterSearchMovie);
        assertEquals(originalMovie.getMovieName(), afterSearchMovie.getMovieName());
        assertEquals(originalMovie.getDirector(), afterSearchMovie.getDirector());
        assertEquals(originalMovie.getGenre(), afterSearchMovie.getGenre());
    }
}