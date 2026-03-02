
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Sample Black-Box tests for the Checkout system.
 * This class demonstrates how to write black-box tests using:
 * - Equivalence Partitioning (EP)
 * - Boundary Value Analysis (BVA)
 * - Parametrized tests across multiple implementations
 *
 * Black-box testing focuses on testing the SPECIFICATION WITHOUT
 * looking at the implementation.
 *
 * The parameterized structure allows testing all Checkout implementations
 * with the same tests to identify which implementations have bugs.
 */
public class CheckoutBlackBoxSample {
    private static final double Delta = 0.01;
    private static final int Five_Copies = 5;
    private static final int Fifty_Copies = 50;
    private static final int Checkout_Three_Books = 3;
    private static final int Overdue_Count_Three = 3;
    private static final int Twenty_Books = 20;
    private static final int Eighteen_Books = 18;
    private static final int Checkout_Nineteen_Books = 19;
    private static final double Fine_Equals_Fifteen = 15.0;
    private static final double Patron_Null = 3.1;
    private static final double Patron_Suspended = 3.0;
    private static final double Patron_Overdue = 4.0;
    private static final double Patron_Fine_Balance_Over_Ten = 4.1;
    private static final double Fine_Equals_Ten = 10.0;
    private static final double Book_Null = 2.1;
    private static final double Book_Renewal = 0.1;
    private static final double Book_Reference_Only = 5.0;
    private static final double Patron_Max_Checkout_Limit = 3.2;
    private static final double Successful_Checkout_With_Warning = 1.1;
    private static final double Patron_Fine_Eleven = 11.0;


    private Checkout checkout;

    /**
     * Provides the list of Checkout classes to test.
     * Each test will run against ALL implementations.
     */
    @SuppressWarnings("unchecked")
    static Stream<Class<? extends Checkout>> checkoutClassProvider() {
        return Stream.of(Checkout.class);
    }

    // Uncomment when you implement the method in assign 3 and comment the above
//    static Stream<Class<? extends Checkout>> checkoutClassProvider() {
//        return Stream.of(Checkout.class);
//    }


    /**
     * Helper method to create Checkout instance from class using reflection.
     */
    private Checkout createCheckout(Class<? extends Checkout> clazz) throws Exception {
        Constructor<? extends Checkout> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }

    /**
     * SAMPLE TEST 1: Tests successful checkout of an available book
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T2: Successful checkout - available book, eligible patron")
    public void testBookAvailable(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 0.0 for success
        assertEquals(0.0, result, Delta,
                "Expected successful checkout (0.0) for " + checkoutClass.getSimpleName());

        // Verify: Book should now be unavailable
        assertFalse(book.isAvailable(),
                "Book should be unavailable after checkout for " + checkoutClass.getSimpleName());

        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

        // Verify: Checkout count increased
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 2: Tests checkout with unavailable book
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T1: Unavailable book, eligible patron")
    public void testUnavailableBook(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create unavailable book
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, Five_Copies);
        book.setAvailableCopies(0);  // We are pretending it has been checked out by others and is not available anymore

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 2.0 for unavailable book
        assertEquals(2.0, result, Delta,
                "Expected error code 2.0 for unavailable book for " + checkoutClass.getSimpleName());

        // Verify: Patron should NOT have the book
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should NOT have book in list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 3: Checks Successful checkout
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T3: Successful checkout - available book, eligible patron with overdue books")
    public void testBookAvailableWithOverdue(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.PUBLIC);
        //setting 1-2 overdue books
        patron.setOverdueCount(2);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system


        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        //verify
        assertEquals(1.0, result,
                "Expected warning 1.0 for overdue books for " + checkoutClass.getSimpleName());

        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in list for " + checkoutClass.getSimpleName());

    }


    /**
     * Test 4: Successful Checkout with eligible patron within two of max limit
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T4: Successful checkout - available book, eligible patron with overdue books within two of max limit")
    public void testBookAvailableWithOverdueTwo(Class<? extends Checkout> checkoutClass) throws Exception {


        checkout = createCheckout(checkoutClass);
        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);
        Book book3 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);


        Patron patron = new Patron("P002", "Test Patron", "test@example.com",
                Patron.PatronType.PUBLIC);

        checkout.addBook(book); // adding the book to the library
        checkout.addBook(book1); // adding the book to the library
        checkout.addBook(book2);
        checkout.addBook(book3);

        checkout.checkoutBook(book1, patron);
        checkout.checkoutBook(book2, patron);
        checkout.checkoutBook(book3, patron);


        checkout.registerPatron(patron); // adding a patron to the system

        // Execute checkout

        double result = checkout.checkoutBook(book, patron);

        //verify
        assertEquals(Successful_Checkout_With_Warning, result,
                "Expected warning message 1.1 for available book for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in list for " + checkoutClass.getSimpleName());
        // Verify: Checkout count
        assertEquals(Checkout_Three_Books, patron.getCheckoutCount(),
                "Patron checkout count should be 3 for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 5: Unsuccessful checkout Book not available eligible patron
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T5: unsuccessful checkout - null book, eligible patron ")
    public void testBookEqualsNull(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = null;

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);


        checkout.registerPatron(patron); // adding a patron to the system
        //Execute checkout
        double result = checkout.checkoutBook(book, patron);
        //verify
        assertEquals(Book_Null, result,
                "Expected warning message 2.1 for available book for " + checkoutClass.getSimpleName());


        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should be 0 for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 6: Unsuccessful checkout with available book but ineligible patron
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T6: unsuccessful checkout - Available Book, ineligible patron ")
    public void testBookAvailableSuspendedAcc(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);


        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.setAccountSuspended(true);
        checkout.registerPatron(patron); // adding a patron to the system
        //Execute checkout
        double result = checkout.checkoutBook(book, patron);
        //verify
        assertEquals(Patron_Suspended, result,
                "Expected warning message 3.0 for available book for " + checkoutClass.getSimpleName());


        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should be 0 for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 7: unsuccessful checkout Patron is at max checkout limit with available books
     */

    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T7: unsuccessful checkout - Available Book, ineligible patron ")
    public void testBookAvailablePatronMax(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);
        Book book3 = new Book("978-0-123456-78-6", "Test Book",
                "Test Author", Book.BookType.FICTION, Fifty_Copies);


        Patron patron = new Patron("P002", "Test Patron", "test@example.com",
                Patron.PatronType.CHILD);
        // adding the books to the library
        checkout.addBook(book);
        checkout.addBook(book1);
        checkout.addBook(book2);
        checkout.addBook(book3);

        checkout.registerPatron(patron); // adding a patron to the system
        checkout.checkoutBook(book1, patron);
        checkout.checkoutBook(book2, patron);
        checkout.checkoutBook(book3, patron);



        // Execute checkout

        double result = checkout.checkoutBook(book, patron);

        //verify
        assertEquals(Patron_Max_Checkout_Limit, result,
                "Expected warning message 3.2 for available book for " + checkoutClass.getSimpleName());
        // Verify: Patron should have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

    }

    /**
     * Test 8: unsuccessful checkout available book but patron has overdue
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T8: unsuccessful checkout - available book, ineligible patron")
    public void testBookAvailableOverdueCount(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.CHILD);
        //Setting overDue count to 3 = max for child
        patron.setOverdueCount(Overdue_Count_Three);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.0 for success
        assertEquals(Patron_Overdue, result, Delta,
                "Expected successful checkout (4.0) for " + checkoutClass.getSimpleName());

        // Verify: Book should now be available
        assertTrue(book.isAvailable(),
                "Book should be available after checkout for " + checkoutClass.getSimpleName());

        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

        // Verify: Checkout count
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should be 0 for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 9: unsuccessful checkout: available book but patron is null
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T9: unsuccessful checkout - available book, ineligible patron")
    public void testBookAvailablePatronNull(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        //Patron is null
        Patron patron = null;

        checkout.addBook(book); // adding the book to the library


        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 3.1 for success
        assertEquals(Patron_Null, result, Delta,
                "Expected successful checkout (3.1) for " + checkoutClass.getSimpleName());

        // Verify: Book should now be available
        assertTrue(book.isAvailable(),
                "Book should be available after checkout for " + checkoutClass.getSimpleName());


    }

    /**
     * Test 10: Successful Checkout Book is reference only and patron is eligible
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T10: successful checkout - available book(Reference Only), eligible patron")
    public void testReferenceOnly(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.REFERENCE, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);
        book.isReferenceOnly();
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);


        // Verify: Should return 5.0 for success
        assertEquals(Book_Reference_Only, result, Delta,
                "Expected successful checkout (5.0) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

        // Verify: Checkout count
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should be 0 for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 11: Successful checkout: Book is available for renewal with eligible patron
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T11: successful checkout - available book(renew), eligible patron")
    public void testBookRenewal(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);
        //first checkout
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system
        checkout.checkoutBook(book, patron);
        LocalDate old = patron.getCheckedOutBooks().get(book.getIsbn());
        // Execute checkout
        double result = checkout.checkoutBook(book, patron);
        LocalDate newDate = patron.getCheckedOutBooks().get(book.getIsbn());
        // Verify: Should return 0.1 for success
        assertEquals(Book_Renewal, result, Delta,
                "Expected successful checkout (0.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
        // Verify: Checkout count
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());

    }

    /**
     * Test 12: unsuccessful checkout: book is available but ineligible patron with fine balance over ten
     * @param checkoutClass
     * @throws Exception
     */

    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T12: unsuccessful checkout - available book, ineligible patron")
    public void testBookFineBalanceOverTen(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.FACULTY);
        patron.addFine(Patron_Fine_Eleven);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.1 for success
        assertEquals(Patron_Fine_Balance_Over_Ten, result, Delta,
                "Expected warning code (4.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 13: successful checkout: Available book and eligible patron faculty at limit
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T13: successful checkout - available book, eligible patron")
    public void testBookAvailableFacultyLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        //creating patron
        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.FACULTY);


        // Setup: recursively creating 20 books to check out for faculty
        for (int i = 0; i < Twenty_Books; i++) {
            Book book = new Book("978-0-123456-" + i, "Test Book",
                    "Test Author", Book.BookType.FICTION, 1);

            checkout.addBook(book); // adding every book to the library
            checkout.registerPatron(patron);
            double result = checkout.checkoutBook(book, patron);

            if (i == Eighteen_Books) {
                // Verify: Should return 1.1 for success with max warning
                assertEquals(Successful_Checkout_With_Warning, result, Delta,
                        "Expected warning code (1.1) for " + checkoutClass.getSimpleName());
                // Verify: Checkout count
                assertEquals(Checkout_Nineteen_Books, patron.getCheckoutCount(),
                        "Patron checkout count should be 20 for " + checkoutClass.getSimpleName());
            }

            // Verify: Patron should have the book in their checked-out list
            assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                    "Patron should  have book in checked-out list for " + checkoutClass.getSimpleName());
        }





    }

    /**
     * Test 14: successful checkout: available book Eligible patron Faculty member below limit
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T14: successful checkout - available book, eligible patron")
    public void testBookAvailableFacultyBelow(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.FACULTY);

        checkout.registerPatron(patron);
        checkout.addBook(book);


        // Execute checkout
        double result = checkout.checkoutBook(book, patron);


        // Verify: Should return 0.0 for success
        assertEquals(0.0, result, Delta,
                "Expected success code (0.0) for " + checkoutClass.getSimpleName());
        // Verify: Checkout count
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());


        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book(s) in checked-out list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 15: unsuccessful checkout: available book but ineligible patron child is above limit
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T15: unsuccessful checkout - available book, ineligible patron")
    public void testBookAvailableChildAbove(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book3 = new Book("978-0-123456-78-6", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.CHILD);

        checkout.registerPatron(patron);
        checkout.addBook(book);
        checkout.addBook(book1);
        checkout.addBook(book2);
        checkout.addBook(book3);
        checkout.checkoutBook(book1, patron);
        checkout.checkoutBook(book2, patron);
        checkout.checkoutBook(book3, patron);


        // Execute checkout
        double result = checkout.checkoutBook(book, patron);


        // Verify: Should return 3.2 for success
        assertEquals(Patron_Max_Checkout_Limit, result, Delta,
                "Expected success code (3.2) for " + checkoutClass.getSimpleName());


        // Verify: Patron should have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book(s) in checked-out list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 16: successful checkout: available book with eligible patron child just below limit
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T16: successful checkout - available book, eligible patron")
    public void testBookAvailableChildJustBelow(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);


        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.CHILD);

        checkout.registerPatron(patron);
        checkout.addBook(book);
        checkout.addBook(book1);


        checkout.checkoutBook(book1, patron);



        // Execute checkout
        double result = checkout.checkoutBook(book, patron);


        // Verify: Should return 1.1 for success
        assertEquals(Successful_Checkout_With_Warning, result, Delta,
                "Expected success code (1.1) for " + checkoutClass.getSimpleName());

        // Verify: Checkout count
        assertEquals(2, patron.getCheckoutCount(),
                "Patron checkout count should be 2 for " + checkoutClass.getSimpleName());

        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book(s) in checked-out list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 17: unsuccessful checkout: available book but ineligible patron with fine equal to ten
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T17: unsuccessful checkout - available book, ineligible patron")
    public void testBookAvailableFineEqualTen(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.FACULTY);
        patron.addFine(Fine_Equals_Ten);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.1 for success
        assertEquals(Patron_Fine_Balance_Over_Ten, result, Delta,
                "Expected warning code (4.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 18: successful checkout: available book with eligible patron under limit for fine
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T18: successful checkout - available book, eligible patron")
    public void testBookAvailableFineEqualOne(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.FACULTY);
        patron.addFine(0);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 0.0 for success
        assertEquals(0.0, result, Delta,
                "Expected warning code (0.0) for " + checkoutClass.getSimpleName());

        // Verify: Checkout count
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
        // Verify: Patron should not have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 19: unsuccessful checkout: available book and ineligible patron due to fine being greater then 10
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T19: unsuccessful checkout - available book, ineligible patron")
    public void testBookAvailableFineGreatTen(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.FACULTY);
        patron.addFine(Fine_Equals_Fifteen);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.1 for success
        assertEquals(Patron_Fine_Balance_Over_Ten, result, Delta,
                "Expected warning code (4.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }

    /**
     * Test 20: unsuccessful checkout available book but ineligible patron public at limit
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T20: unsuccessful checkout - available book, ineligible patron")
    public void testBookAvailablePublicAt(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book3 = new Book("978-0-123456-78-6", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book4 = new Book("978-0-123456-78-5", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Book book5 = new Book("978-0-123456-78-4", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);


        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.PUBLIC);

        checkout.registerPatron(patron);
        checkout.addBook(book);
        checkout.addBook(book1);
        checkout.addBook(book2);
        checkout.addBook(book3);
        checkout.addBook(book4);
        checkout.addBook(book5);
        checkout.checkoutBook(book1, patron);
        checkout.checkoutBook(book2, patron);
        checkout.checkoutBook(book3, patron);
        checkout.checkoutBook(book4, patron);
        checkout.checkoutBook(book5, patron);

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 3.2 for success
        assertEquals(Patron_Max_Checkout_Limit, result, Delta,
                "Expected warning code (3.2) for " + checkoutClass.getSimpleName());
        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());


    }

    /**
     * Test 21: Successful checkout: available book with eligible patron child checking loan period days
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T21: successful checkout - available book, eligible patron")
    public void testBookAvailableChildDate(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.CHILD);
        checkout.registerPatron(patron);
        checkout.addBook(book);

        int loan = patron.getLoanPeriodDays();
        double result = checkout.checkoutBook(book, patron);

        LocalDate dueDate = patron.getCheckedOutBooks().get(book.getIsbn());
        LocalDate today = LocalDate.now();
        long daysOverdue = ChronoUnit.DAYS.between(today, dueDate);

        assertEquals(Successful_Checkout_With_Warning, result, Delta,
                "Expected warning code (1.1) for " + checkoutClass.getSimpleName());

        assertEquals(loan, daysOverdue,
                "Expected return should be 14 day loan period for a child" + checkoutClass.getSimpleName());

        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

    }

}

