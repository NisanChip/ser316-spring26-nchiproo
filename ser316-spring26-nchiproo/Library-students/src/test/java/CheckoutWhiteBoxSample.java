import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Sample White-Box tests for the Checkout system.
 * This class demonstrates how to write white-box tests using:
 * - Control Flow Graph (CFG) analysis
 * - Statement coverage
 * - Branch coverage
 * - Path coverage
 *
 * White-box testing focuses on testing the IMPLEMENTATION by
 * examining the code structure and ensuring all paths are tested.
 */
public class CheckoutWhiteBoxSample {
    private static final int Three_Books = 3;
    private static final int Thirty_days = 30;
    private static final double Delta = 0.01;
    private static final double Fine_Amount_Twenty_One_Point_Two_Five = 21.25;
    private Checkout checkout;

    /**
     * Set up for new checkout
     */
    @BeforeEach
    public void setUp() {
        checkout = new Checkout();
    }

    /**
     * Test 1: count books by type null
     * Sequence 1
     */
    @Test
    @DisplayName("WB Test: countBooksByType - null type branch")
    public void testCountBooksByTypeNullType() {
        // Branch: type == null → TRUE
        int result = checkout.countBooksByType(null, false);
        assertEquals(0, result, "Should return 0 for null type");
    }

    /**
     * Test 2:  book type fiction not available, book type unavailable, should return 0 for looped
     * Sequence 2
     */
    @Test
    @DisplayName("WB Test 2: countBooksByType - Fiction type branch")
    public void testCountBooksByTypeNotAvailable() {
        // Branch: type == Fiction → False
        int result = checkout.countBooksByType(Book.BookType.FICTION, false);
        assertEquals(0, result, "Should return 0 for null type");
    }


    /**
     * Test 3: Book type fiction available only one copy
     * Sequence 3
     */
    @Test
    @DisplayName("WB Test 3: countBooksByType - Fiction type branch")
    public void testCountBooksByTypeAvailable() {
        // Branch: type == Fiction → True
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        checkout.addBook(book);
        int result = checkout.countBooksByType(Book.BookType.FICTION, true);
        assertEquals(1, result, "Should return 1 book for Fiction type");
    }

    /**
     * Test 4: Multiple books available for fiction type
     * Sequence 4
     */
    @Test
    @DisplayName("WB Test 4: countBooksByType - Fiction type branch")
    public void testCountBooksByTypeMultipleCopiesAvailable() {
        // Branch: type == Fiction → True
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        checkout.addBook(book);
        checkout.addBook(book1);
        checkout.addBook(book2);

        int result = checkout.countBooksByType(Book.BookType.FICTION, true);
        assertEquals(Three_Books, result, "Should return 3 book for Fiction type");
    }

    /**
     * Test 5: book type fiction unavailable but other book types are available
     * Sequence 3 and 4
     */
    @Test
    @DisplayName("WB Test 5: countBooksByType - Fiction type branch")
    public void testCountBooksByTypeOtherTypesAvailable() {
        // Branch: type == Fiction → False
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.CHILDREN, 1);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.NONFICTION, 1);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.TEXTBOOK, 1);
        checkout.addBook(book);
        checkout.addBook(book1);
        checkout.addBook(book2);

        int result1 = checkout.countBooksByType(Book.BookType.FICTION, false);
        int resultChildren = checkout.countBooksByType(Book.BookType.CHILDREN, true);
        int resultNonfiction = checkout.countBooksByType(Book.BookType.NONFICTION, true);
        int resultTextBook = checkout.countBooksByType(Book.BookType.TEXTBOOK, true);
        assertEquals(0, result1, "Should return 0 books available");
        assertEquals(1, resultChildren, "Should return 1 book available");
        assertEquals(1, resultNonfiction, "Should return 1 book available");
        assertEquals(1, resultTextBook, "Should return 1 book available");
    }


    /**
     * Test 6: Testing patron type Student
     */
    @Test
    @DisplayName("WB Test 6: isPatronType")
    public void testPatronType() {
        // Branch: Patron == Student → True
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.CHILDREN, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);
        checkout.addBook(book);
        checkout.registerPatron(patron);


        boolean result = checkout.isPatronType("STUDENT", Patron.PatronType.STUDENT);

    }


    /**
     * Test 7: testing returned book with returnbook()
     */
    @Test
    @DisplayName("WB Test 7: returnbook()")
    public void testReturnBook() {

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.CHILDREN, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);
        checkout.addBook(book);
        checkout.registerPatron(patron);
        checkout.checkoutBook(book, patron);

        assertFalse(checkout.getPatrons().isEmpty(), "Patron should not be empty");

        boolean checkoutFirst = patron.hasBookCheckedOut(book.getIsbn());
        assertTrue(checkoutFirst);

        book.returnBook();
        double returned = checkout.returnBook(book.getIsbn(), patron);
        assertTrue(returned >= 0);

        boolean checkoutReturned = patron.hasBookCheckedOut(book.getIsbn());
        assertFalse(checkoutReturned, "Should not have book checked out");

    }

    /**
     * Test 8: testing testCalculateFine()
     */
    @Test
    @DisplayName("WB Test 8: testCalculateFine()")
    public void testCalculateFine() {

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.CHILDREN, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);
        checkout.addBook(book);
        checkout.registerPatron(patron);
        double firstCheckout = checkout.checkoutBook(book, patron);
        assertEquals(0.0, firstCheckout, Delta,
                "Expected success code 0.0 for unavailable book");
        patron.getCheckedOutBooks().put(book.getIsbn(), LocalDate.now().minusDays(Thirty_days));


        double result = checkout.returnBook(book.getIsbn(), patron);
        // Verify: Should return 2.0 for unavailable book
        assertEquals(Fine_Amount_Twenty_One_Point_Two_Five, result, Delta,
                "Expected fine of 21.25");

        // Verify: Patron should NOT have the book
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should NOT have book in list");
    }

    /**
     * Test 9: testing testBook if b == null
     * Sequence 2
     */
    @Test
    @DisplayName("WB Test 9: countBooksByTypebisNullType ")
    public void testCountBooksByTypebIsNullType() {
        // Branch: b == null → TRUE
        checkout.getInventory().put("null", null);
        int result = checkout.countBooksByType(Book.BookType.FICTION, false);
        assertEquals(0, result, "Should return 0 for null type");

    }


    /**
     * Test 10: testing countBooks by type with only available set to false
     * Sequence 4
     */
    @Test
    @DisplayName("WB Test 10: countBooksByType")
    public void testCountBooksByTypeOnlyAvailableFalse() {
        // Branch: b == null → TRUE
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        checkout.addBook(book);
        book.checkout();
        int result = checkout.countBooksByType(Book.BookType.FICTION, false);
        assertEquals(1, result, "Should return 0 for null type");
    }

    /**
     * Test 11: testing count books by type b is available set to true
     * Sequence 4
     */
    @Test
    @DisplayName("WB Test 11: countBooksByType")
    public void testCountBooksByTypebIsAvailableTrue() {
        // Branch: b == available → TRUE
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        checkout.addBook(book);

        int result = checkout.countBooksByType(Book.BookType.FICTION, true);
        assertEquals(1, result, "Should return 0 for null type");
    }

}
