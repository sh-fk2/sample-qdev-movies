# 🏴‍☠️ Movie Treasure Service - Spring Boot Demo Application

Ahoy matey! A swashbuckling movie catalog web application built with Spring Boot, now featuring **pirate-themed movie treasure hunting** capabilities! This application demonstrates Java development best practices while providing a fun, interactive way to search and discover movie treasures.

## ⚓ Features

- **🎬 Movie Treasure Catalog**: Browse 12 classic movie treasures with detailed information
- **🔍 Treasure Hunt Search**: Advanced search functionality to find movies by name, ID, or genre
- **📋 Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **⭐ Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **📱 Responsive Design**: Mobile-first design that works on all devices like a ship that sails on any sea
- **🎨 Modern Pirate UI**: Dark theme with treasure-themed gradients and smooth animations
- **🏴‍☠️ Pirate Language**: Full pirate-themed interface with authentic nautical terminology

## 🗺️ New Treasure Hunt Features

### Interactive Search Form
- **Movie Name Search**: Find treasures by partial name matching (case-insensitive)
- **Movie ID Search**: Locate specific treasures by their unique ID
- **Genre Filtering**: Discover movies by genre (Drama, Action, Adventure, etc.)
- **Combined Search**: Use multiple criteria simultaneously for precise treasure hunting

### API Endpoints
- **HTML Search Interface**: `/movies/search` - Interactive web form for treasure hunting
- **JSON API**: `/api/movies/search` - RESTful API for programmatic access
- **Comprehensive Error Handling**: Pirate-themed messages for all scenarios

## 🛠️ Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for templating
- **Log4j 2** for logging
- **JUnit 5.8.2** for testing
- **JSON** for data processing

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Treasure

- **🗺️ Movie Treasure Map**: http://localhost:8080/movies
- **🔍 Treasure Hunt**: Use the search form on the main page
- **📋 Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)

## 🏴‍☠️ API Treasure Map

### Search Movie Treasures (HTML Interface)
```
GET /movies/search?name={movieName}&id={movieId}&genre={genre}
```
Returns an HTML page with search results and pirate-themed messages.

**Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Specific movie ID to find (exact match)
- `genre` (optional): Genre to filter by (partial match, case-insensitive)

**Examples:**
```
# Search by name
http://localhost:8080/movies/search?name=prison

# Search by genre
http://localhost:8080/movies/search?genre=action

# Combined search
http://localhost:8080/movies/search?name=family&genre=crime

# Search by ID
http://localhost:8080/movies/search?id=3
```

### Search Movie Treasures (JSON API)
```
GET /api/movies/search?name={movieName}&id={movieId}&genre={genre}
```
Returns JSON response with search results and metadata.

**Response Format:**
```json
{
  "movies": [...],
  "totalResults": 2,
  "message": "Yo ho ho! Found 2 movie treasures",
  "searchName": "action",
  "searchId": null,
  "searchGenre": "action"
}
```

**Error Response:**
```json
{
  "error": "Arrr! Ye need to provide at least one valid search parameter, matey!"
}
```

### Get All Movie Treasures
```
GET /movies
```
Returns an HTML page displaying all movie treasures with the search form.

### Get Movie Treasure Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

## 🏗️ Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java     # Main Spring Boot application
│   │       │   ├── MoviesController.java      # REST controller with search endpoints
│   │       │   ├── MovieService.java          # Service layer with search logic
│   │       │   ├── Movie.java                 # Movie data model
│   │       │   ├── Review.java                # Review data model
│   │       │   └── ReviewService.java         # Review service
│   │       └── utils/
│   │           ├── MovieIconUtils.java        # Movie icon utilities
│   │           └── MovieUtils.java            # Movie validation utilities
│   └── resources/
│       ├── application.yml                    # Application configuration
│       ├── movies.json                        # Movie treasure data
│       ├── mock-reviews.json                  # Mock review data
│       ├── log4j2.xml                         # Logging configuration
│       ├── static/css/
│       │   └── movies.css                     # Pirate-themed styles
│       └── templates/
│           ├── movies.html                    # Main treasure map with search
│           └── movie-details.html             # Individual treasure details
└── test/                                      # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java          # Service layer tests
            ├── MoviesControllerTest.java      # Controller tests
            └── MovieTest.java                 # Model tests
```

## 🧪 Testing the Treasure Hunt

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage
- **MovieService**: Search functionality, parameter validation, edge cases
- **MoviesController**: HTML and JSON endpoints, error handling, pirate messages
- **Integration Tests**: End-to-end search scenarios

## 🔧 Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that at least one search parameter is provided
2. Verify movie data is loaded (check logs for "Ahoy! Starting treasure hunt")
3. Ensure proper URL encoding for special characters

## 🎯 Usage Examples

### Basic Treasure Hunting

1. **Find Action Movies**: Enter "action" in the Genre field
2. **Search by Name**: Enter "family" in the Movie Name field
3. **Find Specific Movie**: Enter "3" in the Movie ID field
4. **Combined Search**: Use multiple fields for precise results

### API Usage

```bash
# Using curl to search for drama movies
curl "http://localhost:8080/api/movies/search?genre=drama"

# Search for movies with "the" in the name
curl "http://localhost:8080/api/movies/search?name=the"

# Get specific movie by ID
curl "http://localhost:8080/api/movies/search?id=1"
```

## 🏴‍☠️ Pirate Language Guide

The application uses authentic pirate terminology throughout:

- **Treasure Hunt**: Movie search functionality
- **Treasure Chest**: Movie database/collection
- **Treasure Haul**: Search results
- **Matey/Ye Scurvy Dog**: Friendly user address
- **Arrr!/Ahoy!**: Expressions of excitement or greeting
- **Shiver me timbers**: Expression of surprise
- **Batten down the hatches**: Prepare for action

## 🤝 Contributing

This project welcomes contributions! Feel free to:
- Add more movie treasures to the catalog
- Enhance the pirate-themed UI/UX
- Implement additional search features (sorting, pagination)
- Add more comprehensive error handling
- Improve the responsive design for mobile treasure hunters

### Development Guidelines
- Follow existing pirate theme conventions
- Add comprehensive unit tests for new features
- Update documentation with pirate flair
- Maintain backward compatibility with existing endpoints

## 📜 License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye navigate through this treasure trove of movies, matey! 🏴‍☠️*
