import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

    private Checkout checkout;

    /**
     * Provides the list of Checkout classes to test.
     * Each test will run against ALL implementations.
     */
    @SuppressWarnings("unchecked")
    static Stream<Class<? extends Checkout>> checkoutClassProvider() {
        return (Stream<Class<? extends Checkout>>) Stream.of(
                Checkout0.class,
                Checkout1.class,
                Checkout2.class,
                Checkout3.class);
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
        assertEquals(0.0, result, 0.01,
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
                "Test Author", Book.BookType.FICTION, 5);
        book.setAvailableCopies(0);  // We are pretending it has been checked out by others and is not available anymore

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 2.0 for unavailable book
        assertEquals(2.0, result, 0.01,
                "Expected error code 2.0 for unavailable book for " + checkoutClass.getSimpleName());

        // Verify: Patron should NOT have the book
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should NOT have book in list for " + checkoutClass.getSimpleName());
    }

    //Test 3
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


    //Test 4
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T4: Successful checkout - available book, eligible patron with overdue books within two of max limit")
    public void testBookAvailableWithOverdueTwo(Class<? extends Checkout> checkoutClass) throws Exception {


        checkout = createCheckout(checkoutClass);
        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);


        Patron patron = new Patron("P002", "Test Patron", "test@example.com",
                Patron.PatronType.CHILD);

        checkout.addBook(book); // adding the book to the library
        checkout.addBook(book1); // adding the book to the library
        checkout.addBook(book2); // adding the book to the library

        checkout.checkoutBook(book1, patron);
        checkout.checkoutBook(book2, patron);

        checkout.registerPatron(patron); // adding a patron to the system

        // Execute checkout

        double result = checkout.checkoutBook(book, patron);

        //verify
        assertEquals(1.1, result,
                "Expected warning message 1.1 for available book for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in list for " + checkoutClass.getSimpleName());
    }
    //Test 5
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
        assertEquals(2.1, result,
                "Expected warning message 2.1 for available book for " + checkoutClass.getSimpleName());


        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should be 0 for " + checkoutClass.getSimpleName());
    }
    //Test 6
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T6: unsuccessful checkout - Available Book, ineligible patron ")
    public void testBookAvailableSuspendedAcc(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);


        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        patron.setAccountSuspended(true);
        checkout.registerPatron(patron); // adding a patron to the system
        //Execute checkout
        double result = checkout.checkoutBook(book, patron);
        //verify
        assertEquals(3.0, result,
                "Expected warning message 3.0 for available book for " + checkoutClass.getSimpleName());


        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should be 0 for " + checkoutClass.getSimpleName());
    }
    //Test 7
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T7: unsuccessful checkout - Available Book, ineligible patron due to max checkout ")
    public void testBookAvailablePatronMax(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);
        Book book1 = new Book("978-0-123456-78-8", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);
        Book book2 = new Book("978-0-123456-78-7", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);
        Book book3 = new Book("978-0-123456-78-6", "Test Book",
                "Test Author", Book.BookType.FICTION, 50);

        Patron patron = new Patron("P002", "Test Patron", "test@example.com",
                Patron.PatronType.CHILD);
        // adding the books to the library
        checkout.addBook(book);
        checkout.addBook(book1);
        checkout.addBook(book2);
        checkout.addBook(book3);
        checkout.checkoutBook(book1, patron);
        checkout.checkoutBook(book2, patron);
        checkout.checkoutBook(book3, patron);
        checkout.registerPatron(patron); // adding a patron to the system

        // Execute checkout

        double result = checkout.checkoutBook(book, patron);

        //verify
        assertEquals(3.2, result,
                "Expected warning message 3.2 for available book for " + checkoutClass.getSimpleName());
        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

    }
    //Test 8
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
        patron.setOverdueCount(3);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.0 for success
        assertEquals(4.0, result, 0.01,
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
    //Test 9
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
        assertEquals(3.1, result, 0.01,
                "Expected successful checkout (3.1) for " + checkoutClass.getSimpleName());

        // Verify: Book should now be available
        assertTrue(book.isAvailable(),
                "Book should be available after checkout for " + checkoutClass.getSimpleName());


    }
    //Test 10
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
        assertEquals(5.0, result, 0.01,
                "Expected successful checkout (5.0) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

        // Verify: Checkout count
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should be 0 for " + checkoutClass.getSimpleName());
    }
    //Test 11
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T11: successful checkout - available book(renew), eligible patron")
    public void testBookRenewal(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 5);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system
        checkout.checkoutBook(book, patron);
        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 0.1 for success
        assertEquals(0.1, result, 0.01,
                "Expected successful checkout (0.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());


    }
    //Test 12
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
        patron.addFine(11.0);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.1 for success
        assertEquals(4.1, result, 0.01,
                "Expected warning code (4.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }
    //Test 13
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T13: unsuccessful checkout - available book, ineligible patron")
    public void testBookAvailableFacultyLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        //creating patron
        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.FACULTY);
        checkout.registerPatron(patron);
        //temporary book to pull from the loop (wasnt allowing me use book inside the loop)
        Book temp = null;
        // Setup: recursively creating 20 books to check out for faculty
        for (int i = 0; i < 21; i++) {
            Book book = new Book("978-0-123456-" + i, "Test Book",
                    "Test Author", Book.BookType.FICTION, 1);

            checkout.addBook(book); // adding every book to the library
            checkout.checkoutBook(book, patron);
            temp = book;
        }

        // Execute checkout
        double result = checkout.checkoutBook(temp, patron);


        // Verify: Should return 3.2 for success
        assertEquals(3.2, result, 0.01,
                "Expected warning code (3.2) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(temp.getIsbn()),
                "Patron should not have book in checked-out list for " + checkoutClass.getSimpleName());
    }
    //Test 14
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
        assertEquals(0.0, result, 0.01,
                "Expected success code (0.0) for " + checkoutClass.getSimpleName());


        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book(s) in checked-out list for " + checkoutClass.getSimpleName());
    }

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
        assertEquals(3.2, result, 0.01,
                "Expected success code (3.2) for " + checkoutClass.getSimpleName());


        // Verify: Patron should have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book(s) in checked-out list for " + checkoutClass.getSimpleName());
    }
    //Test 16
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
        checkout.addBook(book2);

        checkout.checkoutBook(book1, patron);
        checkout.checkoutBook(book2, patron);


        // Execute checkout
        double result = checkout.checkoutBook(book, patron);


        // Verify: Should return 1.1 for success
        assertEquals(1.1, result, 0.01,
                "Expected success code (1.1) for " + checkoutClass.getSimpleName());

        // Verify: Checkout count
        assertEquals(3, patron.getCheckoutCount(),
                "Patron checkout count should be 3 for " + checkoutClass.getSimpleName());

        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book(s) in checked-out list for " + checkoutClass.getSimpleName());
    }
    //Test 17
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
        patron.addFine(10.0);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.1 for success
        assertEquals(4.1, result, 0.01,
                "Expected warning code (4.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }
    //Test 18
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
        assertEquals(0.0, result, 0.01,
                "Expected warning code (0.0) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }
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
        patron.addFine(15);
        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 4.1 for success
        assertEquals(4.1, result, 0.01,
                "Expected warning code (4.1) for " + checkoutClass.getSimpleName());


        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());
    }
    //Test 20
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
        assertEquals(3.2, result, 0.01,
                "Expected warning code (3.2) for " + checkoutClass.getSimpleName());
        // Verify: Patron should not have the book in their checked-out list
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());


    }
    //Test 21
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

        assertEquals(1.1, result, 0.01,
                "Expected warning code (1.1) for " + checkoutClass.getSimpleName());

        assertEquals(loan, daysOverdue,
                "Expected return should be 14 day loan period for a child" + checkoutClass.getSimpleName());

        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

    }

}

