package com.patientping;

public enum HeaderFields {
    bookID("bookID"),
    title("title"),
    authors("authors"),
    average_rating("average_rating"),
    isbn("isbn"),
    isbn13("isbn13"),
    language_code("language_code"),
    num_pages("# num_pages"),
    ratings_count("ratings_count"),
    text_reviews_count("text_reviews_count");

    private final String field;

    HeaderFields(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
