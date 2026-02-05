import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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

    private Checkout checkout;

    @BeforeEach
    public void setUp() {
        checkout = new Checkout();
    }
    //Test 1
    //sequence 1
    @Test
    @DisplayName("WB Test: countBooksByType - null type branch")
    public void testCountBooksByType_NullType() {
        // Branch: type == null → TRUE
        int result = checkout.countBooksByType(null, false);
        assertEquals(0, result, "Should return 0 for null type");
    }


    // Test 2 booktype fiction not available, booktype unavailable, should return 0 for looped
    //sequence 2
    @Test
    @DisplayName("WB Test 2: countBooksByType - Fiction type branch")
    public void testCountBooksByType_NotAvailable() {
        // Branch: type == Fiction → False
        int result = checkout.countBooksByType(Book.BookType.FICTION, false);
        assertEquals(0, result, "Should return 0 for null type");
    }

    // Test 3 booktype fiction available, 1 copy
    //sequence 3
    @Test
    @DisplayName("WB Test 3: countBooksByType - Fiction type branch")
    public void testCountBooksByType_Available() {
        // Branch: type == Fiction → True
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        checkout.addBook(book);
        int result = checkout.countBooksByType(Book.BookType.FICTION, true);
        assertEquals(1, result, "Should return 1 book for Fiction type");
    }

    // Test 4 multiple books available for type
    //sequence 4
    @Test
    @DisplayName("WB Test 4: countBooksByType - Fiction type branch")
    public void testCountBooksByType_MultipleCopiesAvailable() {
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
        assertEquals(3, result, "Should return 1 book for Fiction type");
    }

    // Test 5 booktype fiction unavailable, other books available
    //sequence 3
    //sequence 4
    @Test
    @DisplayName("WB Test 5: countBooksByType - Fiction type branch")
    public void testCountBooksByType_OtherTypesAvailable() {
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

    // Test 6 testing patrontype
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

    // Test 7 testing returnbook()
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

    // Test 8 testing testCalculateFine()
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
        assertEquals(0.0, firstCheckout, 0.01,
                "Expected success code 0.0 for unavailable book");
        patron.getCheckedOutBooks().put(book.getIsbn(), LocalDate.now().minusDays(30));


        double result = checkout.returnBook(book.getIsbn(), patron);
        // Verify: Should return 2.0 for unavailable book
        assertEquals(21.25, result, 0.01,
                "Expected fine of 21.25");

        // Verify: Patron should NOT have the book
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should NOT have book in list");
    }

    //Test 9 Testbook if b == null
    //sequence 2
    @Test
    @DisplayName("WB Test 9: countBooksByType_bisNullType ")
    public void testCountBooksByType_bIsNullType() {
        // Branch: b == null → TRUE
        checkout.getInventory().put("null", null);
        int result = checkout.countBooksByType(Book.BookType.FICTION, false);
        assertEquals(0, result, "Should return 0 for null type");

    }

    //Test 10
    //sequence 4
    @Test
    @DisplayName("WB Test 10: countBooksByType")
    public void testCountBooksByType_OnlyAvailableFalse() {
        // Branch: b == null → TRUE
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        checkout.addBook(book);
        book.checkout();
        int result = checkout.countBooksByType(Book.BookType.FICTION, false);
        assertEquals(1, result, "Should return 0 for null type");
    }
    //Test 11
    //sequence 4
    @Test
    @DisplayName("WB Test 11: countBooksByType")
    public void testCountBooksByType_bIsAvailableTrue() {
        // Branch: b == available → TRUE
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        checkout.addBook(book);

        int result = checkout.countBooksByType(Book.BookType.FICTION, true);
        assertEquals(1, result, "Should return 0 for null type");
    }

}
