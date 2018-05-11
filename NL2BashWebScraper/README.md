# NL2BashWebScraper

This project scrapes StackOverflow pages to search for potential English/Bash natural language pairs to be checked by human verifiers. It iterates over all questions tagged with either 'bash' or 'shell' and outputs a .verify JSON file that contains the question title and all lines of code found within the first five answers, seperated by answer.

This project is built using Gradle. 
To build:

    $ ./gradlew build
    
To begin scraping:

    $ ./gradlew run

To run tests:

    $ ./gradlew test
