package com.patientping;

public class Book {
    private final int id;
    private final String title;
    private final String authors;
    private final float averageRating;
    private final String isbn;
    private final String isbn13;
    private final String language;
    private final int numPages;
    private final int ratingCount;
    private final int textReviewsCount;

    public Book(int id, String title, String authors, float averageRating, String isbn, String isbn13, String language, int numPages, int ratingCount, int textReviewsCount) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.averageRating = averageRating;
        this.isbn = isbn;
        this.isbn13 = isbn13;
        this.language = language;
        this.numPages = numPages;
        this.ratingCount = ratingCount;
        this.textReviewsCount = textReviewsCount;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getLanguage() {
        return language;
    }

    public int getNumPages() {
        return numPages;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getTextReviewsCount() {
        return textReviewsCount;
    }

    public String toString() {
        return String.format("\"%s\" by %s", getTitle(), getAuthors());
    }

    public int getId() {
        return id;
    }
}
