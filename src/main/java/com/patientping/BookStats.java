package com.patientping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.patientping.HeaderFields.*;

public class BookStats {
    public static void main(String[] args) throws IOException {
        BookStats bookStats = new BookStats();
        bookStats.run();
    }

    /**
     * Read in a csv file of books and convert it to a list of Book objects.
     *
     * @param in an input stream from a csv file containing books.
     * @return A list of Book objects.
     * @throws IOException if the buffered reader throws an exception.
     */
    public List<Book> loadCsv(InputStream in) throws IOException {
        List<Book> books = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        List<String> header = Arrays.asList(reader.readLine().split(","));
        if (!header.containsAll(Arrays.stream(HeaderFields.values()).map(HeaderFields::getField).collect(Collectors.toList()))) {
            System.out.println("Book CSV file is not formatted correctly. No books will be read in.");
        } else {
            String line = reader.readLine();
            while (line != null) {
                //System.out.println(line);
                String[] parts = line.split(",");
                if (parts.length != 10) { //If the line does not have the correct number of fields, skip it.
                    line = reader.readLine();
                    continue;
                }
                Book b = new Book(
                        Integer.parseInt(parts[header.indexOf(bookID.getField())]),
                        parts[header.indexOf(title.getField())],
                        parts[header.indexOf(authors.getField())],
                        Float.parseFloat(parts[header.indexOf(average_rating.getField())]),
                        parts[header.indexOf(isbn.getField())],
                        parts[header.indexOf(isbn13.getField())],
                        parts[header.indexOf(language_code.getField())],
                        Integer.parseInt(parts[header.indexOf(num_pages.getField())]),
                        Integer.parseInt(parts[header.indexOf(ratings_count.getField())]),
                        Integer.parseInt(parts[header.indexOf(text_reviews_count.getField())])
                );
                books.add(b);
                line = reader.readLine();
            }
        }
        return books;
    }

    /**
     * Filter a list of books to just books written in English.
     *
     * @param books a list of Book objects.
     * @return a list of Books that have their language listed as English;
     */
    public List<Book> filterToEnglishBooks(List<Book> books) {
        return books.stream().filter(b ->
                b.getLanguage().equalsIgnoreCase("eng") ||
                        b.getLanguage().equalsIgnoreCase("en-US"))
                .collect(Collectors.toList());
    }

    /**
     * Index a list of books by author.
     *
     * @param books a list of Book objects.
     * @return a map of Authors -> a list of their books.
     */
    public Map<String, List<Book>> indexByAuthor(List<Book> books) {
        Map<String, List<Book>> booksByAuthor = new HashMap<>();
        for (Book b : books) {
            String[] authors = b.getAuthors().split("-");
            for (String author : authors) {
                if (!booksByAuthor.containsKey(author)) {
                    booksByAuthor.put(author, new ArrayList<>());
                }
                booksByAuthor.get(author).add(b);
            }
        }
        return booksByAuthor;
    }

    /**
     * Takes in a map of author -> List of Books and finds the author with the most books.
     *
     * @param booksByAuthor a Map of Author Name -> List of Books.
     * @return an Author Object with the most books.
     */
    public Author findMostBooks(Map<String, List<Book>> booksByAuthor) {
        Author authorWithMostBooks = new Author("");
        if (!booksByAuthor.isEmpty()) {
            for (Map.Entry<String, List<Book>> entry : booksByAuthor.entrySet()) {
                int count = entry.getValue().size();
                if (count > authorWithMostBooks.getBooks().size()) {
                    authorWithMostBooks = new Author(entry.getKey(), entry.getValue());
                }
            }
            System.out.printf("Author with the most books: %s. %d books.%n", authorWithMostBooks.getName(), authorWithMostBooks.getBooks().size());
        } else {
            System.out.println("The list of books is empty.");
        }

        return authorWithMostBooks;
    }

    /**
     * Find the highest rated book and print out the author and the book's average rating.
     *
     * @param books a list of Books.
     * @return The highest rated book.
     */
    public Book findHighestRatedBook(List<Book> books) {
        Book highestRatedBook = new Book(-1, "", "", -1, "", "", "", -1, -1, -1);
        if (!books.isEmpty()) {
            for (Book b : books) {
                if (b.getRatingCount() < 25) { //exclude any book with fewer than 25 ratings
                    continue;
                }
                float rating = b.getAverageRating();
                if (rating > highestRatedBook.getAverageRating()) {
                    highestRatedBook = b;
                }
            }
            System.out.printf("Author with the highest rating: %s. %f stars.%n", highestRatedBook.getAuthors(), highestRatedBook.getAverageRating());
        } else {
            System.out.println("The list of books is empty.");
        }
        return highestRatedBook;
    }

    /**
     * Finds the book with the highest ratio of text reviews to star reviews and prints the author of the book and the ratio.
     *
     * @param books a list of books.
     * @return the book with the highest ratios.
     */
    public Book findHighestRatio(List<Book> books) {
        Book highestRatioBook = new Book(-1, "", "", -1, "", "", "", -1, -1, -1);
        if (!books.isEmpty()) {
            float highestRatio = 0f;
            for (Book b : books) {
                if (b.getRatingCount() <= 0) { //don't divide by zero
                    continue;
                }
                float ratio = ((float) b.getTextReviewsCount()) / ((float) b.getRatingCount());
                if (ratio > highestRatio) {
                    highestRatio = ratio;
                    highestRatioBook = b;
                }
            }
            System.out.printf("Author with the highest ratio of text reviews to star reviews: %s. %f%n", highestRatioBook.getAuthors(), highestRatio);
        } else {
            System.out.println("The list of books is empty.");
        }

        return highestRatioBook;
    }

    /**
     * Print the author that has the highest average star rating across all of their books along with their average rating.
     * @param booksByAuthor a map of authors to their list of books.
     * @return the author with the highest average star rating across all their books.
     */
    public Author findAuthorWithHighestAverageRating(Map<String, List<Book>> booksByAuthor) {
        Author authorWithHighestAverageRating = new Author("");
        float highestAverageRating = 0f;
        if (!booksByAuthor.isEmpty()) {
            for (Map.Entry<String, List<Book>> entry : booksByAuthor.entrySet()) {
                Author authorToEvaluate = new Author(entry.getKey(), entry.getValue());
                float average = authorToEvaluate.getAverageRatingForAllBooks();
                if (average > highestAverageRating) {
                    authorWithHighestAverageRating = authorToEvaluate;
                    highestAverageRating = average;
                }
            }
            System.out.printf("Author with the highest average rating across all books: %s. %f average rating.%n", authorWithHighestAverageRating.getName(), highestAverageRating);
        } else {
            System.out.println("List of books is empty.");
        }

        return authorWithHighestAverageRating;
    }

    public void run() throws IOException {
        InputStream in = BookStats.class.getResourceAsStream("/books.csv");

        List<Book> books = loadCsv(in);
        System.out.println("Loaded " + books.size() + " books");

        List<Book> englishBooks = filterToEnglishBooks(books);
        System.out.println("" + englishBooks.size() + " Books in English");

        System.out.println("English Book Stats:");
        Map<String, List<Book>> englishBooksByAuthor = indexByAuthor(englishBooks);
        findMostBooks(englishBooksByAuthor);
        findHighestRatedBook(englishBooks);
        findHighestRatio(englishBooks);
        findAuthorWithHighestAverageRating(englishBooksByAuthor);

        System.out.println("All Language Book Stats:");
        findHighestRatedBook(books);
        Map<String, List<Book>> booksByAuthor = indexByAuthor(books);
        findMostBooks(booksByAuthor);
        findHighestRatedBook(books);
        findHighestRatio(books);
        findAuthorWithHighestAverageRating(booksByAuthor);

    }
}
