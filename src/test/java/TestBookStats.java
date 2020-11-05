import com.patientping.Author;
import com.patientping.Book;
import com.patientping.BookStats;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBookStats {

    @Test
    public void testLoadCsv() throws IOException {
        String inputData =
                "bookID,title,authors,average_rating,isbn,isbn13,language_code,# num_pages,ratings_count,text_reviews_count\n" +
                        "14428,The Inheritors,William Golding,3.53,0156443791,9780156443791,en-US,240,2681,257\n"
                        + "2386,Moby Dick,Herman Melville-William Hootkins,3.49,9626343583,9789626343586,eng,25,66,17\n";
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes(StandardCharsets.UTF_8));
        BookStats stats = new BookStats();
        List<Book> books = stats.loadCsv(inputStream);
        assertEquals(2, books.size());

        assertEquals("William Golding", books.get(0).getAuthors());
        assertEquals("The Inheritors", books.get(0).getTitle());
        assertEquals("en-US", books.get(0).getLanguage());
        assertEquals("Herman Melville-William Hootkins", books.get(1).getAuthors());
        assertEquals("Moby Dick", books.get(1).getTitle());
        assertEquals("eng", books.get(1).getLanguage());
    }

    @Test
    public void testLoadCsvWithShuffledHeaders() throws IOException {
        String inputData =
                "title,authors,average_rating,isbn,isbn13,language_code,# num_pages,ratings_count,text_reviews_count,bookID\n" +
                        "The Inheritors,William Golding,3.53,0156443791,9780156443791,en-US,240,2681,257,14428\n"
                        + "Moby Dick,Herman Melville-William Hootkins,3.49,9626343583,9789626343586,eng,25,66,17,2386\n";
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes(StandardCharsets.UTF_8));
        BookStats stats = new BookStats();
        List<Book> books = stats.loadCsv(inputStream);
        assertEquals(2, books.size());

        assertEquals("William Golding", books.get(0).getAuthors());
        assertEquals("The Inheritors", books.get(0).getTitle());
        assertEquals("en-US", books.get(0).getLanguage());
        assertEquals("Herman Melville-William Hootkins", books.get(1).getAuthors());
        assertEquals("Moby Dick", books.get(1).getTitle());
        assertEquals("eng", books.get(1).getLanguage());
    }

    @Test
    public void testLoadCsvWithBadHeaders() throws IOException {
        String inputData =
                "bookID,title,authors,average_rating,isbn,isbn13,language_code,# num_pages,ratings_count,header\n" +
                        "14428,The Inheritors,William Golding,3.53,0156443791,9780156443791,en-US,240,2681,257\n"
                        + "2386,Moby Dick,Herman Melville-William Hootkins,3.49,9626343583,9789626343586,eng,25,66,17\n";
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes(StandardCharsets.UTF_8));
        BookStats stats = new BookStats();
        List<Book> books = stats.loadCsv(inputStream);
        assertEquals(0, books.size());
    }

    @Test
    public void testLoadCsvWithIncompleteData() throws IOException {
        String inputData =
                "bookID,title,authors,average_rating,isbn,isbn13,language_code,# num_pages,ratings_count,text_reviews_count\n" +
                        "14428,The Inheritors,William Golding,3.53,0156443791,9780156443791,en-US,240,2681,257\n"
                        + "2386,Moby Dick,Herman Melville-William Hootkins,3.49,9626343583,eng,25,66,17\n";
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes(StandardCharsets.UTF_8));
        BookStats stats = new BookStats();
        List<Book> books = stats.loadCsv(inputStream);
        assertEquals(1, books.size());
    }

    @Test
    public void testFilterBooksToEnglish() {
        Book englishBookEnUs = new Book(280, "The Ravishing of Lol Stein", "Marguerite Duras-Richard Seever", 3.67f, "0394743040", "9780394743042", "en-US", 181, 1939, 122);
        Book englishBookEng = new Book(1053, "The Richest Man in Babylon", "George S. Clason", 4.25f, "1419349996", "9781419349997", "eng", 4, 78, 12);
        Book spanishBook = new Book(1642, "Formas breves", "Ricardo Piglia", 4.16f, "843392463X", "9788433924636", "spa", 144, 210, 27);
        List<Book> books = Arrays.asList(englishBookEng, englishBookEnUs, spanishBook);

        BookStats stats = new BookStats();
        List<Book> englishBooks = stats.filterToEnglishBooks(books);
        assertEquals(2, englishBooks.size());
        assertTrue(englishBooks.contains(englishBookEng));
        assertTrue(englishBooks.contains(englishBookEnUs));
    }

    @Test
    public void testIndexBooksByAuthor() {
        Book multipleAuthors = new Book(1681, "The Confessions (Works of Saint Augustine 1)", "Augustine of Hippo-John E. Rotelle-Maria Boulding", 3.91f, "1565480848", "9781565480841", "eng", 416, 138, 24);
        Book multipleAuthorsWithRepeatAuthor = new Book(1684, "The City of God", "Augustine of Hippo-Thomas Merton-Marcus Dods", 3.92f, "0679783199", "9780679783190", "eng", 905, 99, 15);
        Book multipleAuthorsWithMultipleRepeats = new Book(1685, "The Enchiridion on Faith Hope and Love (Augustine Series 1)", "Augustine of Hippo-Bruce Harbert-John E. Rotelle", 4.04f, "1565481240", "9781565481244", "eng", 144, 251, 19);
        Book singleAuthor = new Book(4, "Harry Potter and the Chamber of Secrets (Harry Potter  #2)", "J.K. Rowling", 4.41f, "0439554896", "9780439554893", "eng", 352, 6267, 272);
        Book repeatSingleAuthor = new Book(10, "Harry Potter Collection (Harry Potter  #1-6)", "J.K. Rowling", 4.73f, "0439827604", "9780439827607", "eng", 3342, 27410, 820);
        List<Book> books = Arrays.asList(multipleAuthors, multipleAuthorsWithRepeatAuthor, singleAuthor, repeatSingleAuthor, multipleAuthorsWithMultipleRepeats);

        BookStats bookStats = new BookStats();
        Map<String, List<Book>> indexByAuthor = bookStats.indexByAuthor(books);
        assertTrue(indexByAuthor.containsKey("Augustine of Hippo"));
        assertTrue(indexByAuthor.containsKey("John E. Rotelle"));
        assertTrue(indexByAuthor.containsKey("Maria Boulding"));
        assertTrue(indexByAuthor.containsKey("Thomas Merton"));
        assertTrue(indexByAuthor.containsKey("Marcus Dods"));
        assertTrue(indexByAuthor.containsKey("J.K. Rowling"));
        assertTrue(indexByAuthor.containsKey("Bruce Harbert"));

        assertEquals(2, indexByAuthor.get("J.K. Rowling").size());
        assertEquals(3, indexByAuthor.get("Augustine of Hippo").size());
        assertEquals(2, indexByAuthor.get("John E. Rotelle").size());
        assertEquals(1, indexByAuthor.get("Maria Boulding").size());
        assertEquals(1, indexByAuthor.get("Thomas Merton").size());
        assertEquals(1, indexByAuthor.get("Marcus Dods").size());
        assertEquals(1, indexByAuthor.get("Bruce Harbert").size());
    }

    @Test
    public void testFindMostBooks() {
        Book augustine1 = new Book(1681, "The Confessions (Works of Saint Augustine 1)", "Augustine of Hippo-John E. Rotelle-Maria Boulding", 3.91f, "1565480848", "9781565480841", "eng", 416, 138, 24);
        Book augustine2 = new Book(1684, "The City of God", "Augustine of Hippo-Thomas Merton-Marcus Dods", 3.92f, "0679783199", "9780679783190", "eng", 905, 99, 15);
        Book augustine3 = new Book(1685, "The Enchiridion on Faith Hope and Love (Augustine Series 1)", "Augustine of Hippo-Bruce Harbert-John E. Rotelle", 4.04f, "1565481240", "9781565481244", "eng", 144, 251, 19);
        Book rowling1 = new Book(4, "Harry Potter and the Chamber of Secrets (Harry Potter  #2)", "J.K. Rowling", 4.41f, "0439554896", "9780439554893", "eng", 352, 6267, 272);
        Book rowling2 = new Book(10, "Harry Potter Collection (Harry Potter  #1-6)", "J.K. Rowling", 4.73f, "0439827604", "9780439827607", "eng", 3342, 27410, 820);
        Map<String, List<Book>> indexByAuthor = new HashMap<>();
        indexByAuthor.put("Augustine of Hippo", Arrays.asList(augustine1, augustine2, augustine3));
        indexByAuthor.put("John E. Rotelle", Arrays.asList(augustine1, augustine3));
        indexByAuthor.put("J.K. Rowling", Arrays.asList(rowling1, rowling2));

        BookStats bookStats = new BookStats();
        Author authorWithMostWorks = bookStats.findMostBooks(indexByAuthor);

        assertEquals("Augustine of Hippo", authorWithMostWorks.getName());
        assertEquals(3, authorWithMostWorks.getBooks().size());
    }

    @Test
    public void testFindHighestRating() {
        Book lowRating = new Book(11854, "Puzzle Pack: The Witch of Blackbird Pond", "Mary B. Collins", 1.00f, "1583377824", "9781583377826", "eng", 134, 29, 0);
        Book highRating = new Book(30, "J.R.R. Tolkien 4-Book Boxed Set: The Hobbit and The Lord of the Rings", "J.R.R. Tolkien", 4.59f, "0345538374", "9780345538376", "eng", 1728, 97731, 1536);
        Book middlingRating = new Book(137, "Starting an eBay Business for Dummies", "Marsha Collier", 3.52f, "0764569244", "9780764569241", "eng", 384, 51, 4);
        List<Book> books = Arrays.asList(lowRating, highRating, middlingRating);

        BookStats bookStats = new BookStats();
        Book highestRatedBook = bookStats.findHighestRatedBook(books);

        assertEquals(highRating, highestRatedBook);
    }

    @Test
    public void testFindHighestRatingWhenBookWithHighestRatingHasFewerThanTwentyFiveReviews() {
        Book lowRating = new Book(11854, "Puzzle Pack: The Witch of Blackbird Pond", "Mary B. Collins", 1.00f, "1583377824", "9781583377826", "eng", 134, 29, 0);
        Book highRating = new Book(30, "J.R.R. Tolkien 4-Book Boxed Set: The Hobbit and The Lord of the Rings", "J.R.R. Tolkien", 4.59f, "0345538374", "9780345538376", "eng", 1728, 24, 1536);
        Book middlingRating = new Book(137, "Starting an eBay Business for Dummies", "Marsha Collier", 3.52f, "0764569244", "9780764569241", "eng", 384, 25, 4);
        List<Book> books = Arrays.asList(lowRating, highRating, middlingRating);

        BookStats bookStats = new BookStats();
        Book highestRatedBook = bookStats.findHighestRatedBook(books);

        assertEquals(middlingRating, highestRatedBook);
    }

    @Test
    public void testFindHighestRatingWithNoBooks() {
        List<Book> books = Collections.emptyList();

        BookStats bookStats = new BookStats();
        Book highestRatedBook = bookStats.findHighestRatedBook(books);

        assertEquals(-1, highestRatedBook.getId());
    }

    @Test
    public void testFindHighestRatio() {
        Book zeroRatio = new Book(11854, "Puzzle Pack: The Witch of Blackbird Pond", "Mary B. Collins", 1.00f, "1583377824", "9781583377826", "eng", 134, 29, 0);
        Book middlingRatio = new Book(30, "J.R.R. Tolkien 4-Book Boxed Set: The Hobbit and The Lord of the Rings", "J.R.R. Tolkien", 4.59f, "0345538374", "9780345538376", "eng", 1728, 97731, 1536);
        Book highRatio = new Book(137, "Starting an eBay Business for Dummies", "Marsha Collier", 3.52f, "0764569244", "9780764569241", "eng", 384, 25, 4);
        List<Book> books = Arrays.asList(zeroRatio, highRatio, middlingRatio);

        BookStats bookStats = new BookStats();
        Book highestRatioBook = bookStats.findHighestRatio(books);

        assertEquals(highRatio, highestRatioBook);
    }

    @Test
    public void testFindHighestRatioWithNoBooks() {
        List<Book> books = Collections.emptyList();

        BookStats bookStats = new BookStats();
        Book highestRatioBook = bookStats.findHighestRatio(books);

        assertEquals(-1, highestRatioBook.getId());
    }

    @Test
    public void testFindHighestRatioWithDivideByZero() {
        Book zeroRatio = new Book(11854, "Puzzle Pack: The Witch of Blackbird Pond", "Mary B. Collins", 1.00f, "1583377824", "9781583377826", "eng", 134, 29, 0);
        Book highRatio = new Book(30, "J.R.R. Tolkien 4-Book Boxed Set: The Hobbit and The Lord of the Rings", "J.R.R. Tolkien", 4.59f, "0345538374", "9780345538376", "eng", 1728, 97731, 1536);
        Book noRatings = new Book(137, "Starting an eBay Business for Dummies", "Marsha Collier", 0.00f, "0764569244", "9780764569241", "eng", 384, 0, 0);
        List<Book> books = Arrays.asList(zeroRatio, noRatings, highRatio);

        BookStats bookStats = new BookStats();
        Book highestRatioBook = bookStats.findHighestRatio(books);

        assertEquals(highRatio, highestRatioBook);
    }

    @Test
    public void testFindAuthorWithHighestAverageStarRatingAcrossAllBooks() {
        Book augustine1 = new Book(1681, "The Confessions (Works of Saint Augustine 1)", "Augustine of Hippo-John E. Rotelle-Maria Boulding", 4.91f, "1565480848", "9781565480841", "eng", 416, 138, 24);
        Book augustine2 = new Book(1684, "The City of God", "Augustine of Hippo-Thomas Merton-Marcus Dods", 2.92f, "0679783199", "9780679783190", "eng", 905, 99, 15);
        Book augustine3 = new Book(1685, "The Enchiridion on Faith Hope and Love (Augustine Series 1)", "Augustine of Hippo-Bruce Harbert-John E. Rotelle", 4.04f, "1565481240", "9781565481244", "eng", 144, 251, 19);
        Book rowling1 = new Book(4, "Harry Potter and the Chamber of Secrets (Harry Potter  #2)", "J.K. Rowling", 4.41f, "0439554896", "9780439554893", "eng", 352, 6267, 272);
        Book rowling2 = new Book(10, "Harry Potter Collection (Harry Potter  #1-6)", "J.K. Rowling", 4.43f, "0439827604", "9780439827607", "eng", 3342, 27410, 820);
        Map<String, List<Book>> indexByAuthor = new HashMap<>();
        indexByAuthor.put("Augustine of Hippo", Arrays.asList(augustine1, augustine2, augustine3));
        indexByAuthor.put("John E. Rotelle", Arrays.asList(augustine1, augustine3));
        indexByAuthor.put("J.K. Rowling", Arrays.asList(rowling1, rowling2));

        BookStats bookStats = new BookStats();
        Author authorWithHighestRatingAverage = bookStats.findAuthorWithHighestAverageRating(indexByAuthor);

        assertEquals("John E. Rotelle", authorWithHighestRatingAverage.getName());
        assertEquals(4.474999904632568, authorWithHighestRatingAverage.getAverageRatingForAllBooks());
    }
}
