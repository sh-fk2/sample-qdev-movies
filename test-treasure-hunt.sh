#!/bin/bash

# Ahoy! Test script to verify our treasure hunting functionality works like a charm!

echo "🏴‍☠️ Ahoy matey! Starting the treasure hunt tests..."
echo "=================================================="

# Run the tests
mvn test

# Check if tests passed
if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 Yo ho ho! All tests passed successfully!"
    echo "⚓ The treasure hunting functionality is ship-shape and ready to sail!"
    echo ""
    echo "🗺️ Next steps:"
    echo "1. Run 'mvn spring-boot:run' to start the treasure hunt"
    echo "2. Navigate to http://localhost:8080/movies"
    echo "3. Use the search form to hunt for movie treasures!"
    echo ""
    echo "Arrr! May fair winds fill yer sails! 🏴‍☠️"
else
    echo ""
    echo "💀 Shiver me timbers! Some tests failed!"
    echo "🔧 Check the test output above and fix any scurvy bugs, matey!"
fi