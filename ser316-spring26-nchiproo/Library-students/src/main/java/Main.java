

/**
 * Demo application for the Library Management System.
 * Demonstrates checkout operations and various scenarios.
 */
public class Main {
    private static final int One_Copy = 1;
    private static final int Three_Copies = 3;
    private static final int Five_Copies = 5;
    private static final int Ten_Copies = 10;
    private static final int Five_Days = 5;
    private static final int Ten_Days = 10;
    private static final int Thirty_Days = 30;
    private static final double Fine_Twelve_Fifty = 12.50;

    /**
     * Main for library management system
     * @param args Library management system demo
     */
    public static void main(String[] args) {
        System.out.println("=== Library Management System Demo ===\n");

        // Create checkout system
        Checkout checkout = new Checkout(); // will not work for checkoutBook since it is not implemented  yet.

        // Add some books to inventory
        Book book1 = new Book("978-0-1234-5678-9", "Introduction to Java", "John Smith",
                              Book.BookType.TEXTBOOK, Three_Copies);
        Book book2 = new Book("0123456789", "Mystery Novel", "Jane Doe",
                              Book.BookType.FICTION, Five_Copies);
        Book book3 = new Book("978-0-9999-8888-7", "Data Structures Reference", "Bob Johnson",
                              Book.BookType.REFERENCE, One_Copy);

        Book book4 = new Book("978-1-1111-2222-3", "Children's Stories", "Alice Wonder",
                              Book.BookType.CHILDREN, Ten_Copies);

        checkout.addBook(book1);
        checkout.addBook(book2);
        checkout.addBook(book3);
        checkout.addBook(book4);

        // Register some patrons
        Patron student = new Patron("P-10001", "Alice Johnson", "alice@university.edu",
                                    Patron.PatronType.STUDENT);
        Patron faculty = new Patron("P-20001", "Dr. Robert Smith", "robert@university.edu",
                                    Patron.PatronType.FACULTY);
        Patron child = new Patron("P-30001", "Emily Brown", "parent@email.com",
                                  Patron.PatronType.CHILD);

        checkout.registerPatron(student);
        checkout.registerPatron(faculty);
        checkout.registerPatron(child);

        // Demo scenarios
        System.out.println("--- Scenario 1: Normal checkout ---");
        System.out.println("Student checking out: " + book2.getTitle());
        double result = checkout.checkoutBook(book2, student);
        System.out.println("Result code: " + result);
        System.out.println("Expected: 0.0 (success)\n");

        System.out.println("--- Scenario 2: Reference-only book ---");
        System.out.println("Student trying to checkout: " + book3.getTitle());
        result = checkout.checkoutBook(book3, student);
        System.out.println("Result code: " + result);
        System.out.println("Expected: 5.0 (reference-only)\n");

        System.out.println("--- Scenario 3: Patron with fines ---");
        student.addFine(Fine_Twelve_Fifty);
        System.out.println("Student has $12.50 in fines");
        System.out.println("Trying to checkout: " + book1.getTitle());
        result = checkout.checkoutBook(book1, student);
        System.out.println("Result code: " + result);
        System.out.println("Expected: 4.1 (fines >= $10)\n");

        System.out.println("--- Scenario 4: Suspended account ---");
        child.setAccountSuspended(true);
        System.out.println("Child account is suspended");
        System.out.println("Trying to checkout: " + book4.getTitle());
        result = checkout.checkoutBook(book4, child);
        System.out.println("Result code: " + result);
        System.out.println("Expected: 3.0 (account suspended)\n");

        System.out.println("--- Scenario 5: Testing helper methods ---");
        System.out.println("\nvalidatePatronEligibility() tests:");
        System.out.println("Student eligibility: " + checkout.validatePatronEligibility(student));
        System.out.println("Child eligibility: " + checkout.validatePatronEligibility(child));

        System.out.println("\ncalculateFine() tests:");
        System.out.println("5 days overdue (FICTION): $" +
            String.format("%.2f", checkout.calculateFine(Five_Days, Book.BookType.FICTION)));
        System.out.println("10 days overdue (TEXTBOOK): $" +
            String.format("%.2f", checkout.calculateFine(Ten_Days, Book.BookType.TEXTBOOK)));
        System.out.println("30 days overdue (REFERENCE): $" +
            String.format("%.2f", checkout.calculateFine(Thirty_Days, Book.BookType.REFERENCE)));

        System.out.println("\nisValidISBN() tests:");
        System.out.println("'978-0-1234-5678-9' valid? " + checkout.isValidISBN("978-0-1234-5678-9"));
        System.out.println("'123456789X' valid? " + checkout.isValidISBN("123456789X"));
        System.out.println("'978-INVALID' valid? " + checkout.isValidISBN("978-INVALID"));

        System.out.println("\n=== Demo Complete ===");
    }
}
