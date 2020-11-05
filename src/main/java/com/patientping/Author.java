package com.patientping;

import java.util.ArrayList;
import java.util.List;

public class Author {
    private String name;
    private List<Book> books;
    private float averageRatingForAllBooks = -1;

    public Author(String name, List<Book> books) {
        this.name = name;
        this.books = books;
    }

    public Author(String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public float getAverageRatingForAllBooks() {
        return averageRatingForAllBooks < 0 ? calculateAverageRatingAcrossAllBooks() : averageRatingForAllBooks;
    }

    private float calculateAverageRatingAcrossAllBooks() {
        float ratingTotal = 0f;
        for(Book book: books) {
            ratingTotal += book.getAverageRating();
        }
        averageRatingForAllBooks = ratingTotal / ((float) books.size());

        return averageRatingForAllBooks;
    }
}
