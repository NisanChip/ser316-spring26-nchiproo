import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a library patron (user).
 * Tracks checked out books, fines, and account status.
 */
public class Patron {
    private String patronId;
    private String name;
    private String email;
    private PatronType type;
    private boolean suspended;
    private double fines;
    private Map<String, LocalDate> bookMap;
    private int overdue;
    private LocalDate memberDate;
    private static final int Max_Checkout_Limit_Faculty = 20;
    private static final int Max_Checkout_Limit_Staff = 15;
    private static final int Max_Checkout_Limit_Student = 10;
    private static final int Max_Checkout_Limit_Public = 5;
    private static final int Max_Checkout_Limit_Children = 3;
    private static final int Max_Checkout_Limit_Default = 3;
    private static final int Loan_Period_Days_Faculty = 60;
    private static final int Loan_Period_Days_Staff = 45;
    private static final int Loan_Period_Days_Student = 30;
    private static final int Loan_Period_Days_Public = 21;
    private static final int Loan_Period_Days_Children = 14;
    private static final int Loan_Period_Days_Default = 14;

    public enum PatronType {
        STUDENT,
        FACULTY,
        STAFF,
        PUBLIC,
        CHILD
    }

    /**
     * Creates a new Patron.
     *
     * @param patronId Unique patron ID (format: P-XXXXX)
     * @param name Patron's full name
     * @param email Patron's email address
     * @param type Patron type (determines checkout limits)
     */
    public Patron(String patronId, String name, String email, PatronType type) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.type = type;
        this.suspended = false;
        this.fines = 0.0;
        this.bookMap = new HashMap<>();
        this.overdue = 0;
        this.memberDate = LocalDate.now();
    }

    // Getters
    public String getPatronId() {
        return patronId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public PatronType getType() {
        return type;
    }

    public boolean isAccountSuspended() {
        return suspended;
    }

    public double getFineBalance() {
        return fines;
    }

    public Map<String, LocalDate> getCheckedOutBooks() {
        return bookMap;
    }

    public int getCheckoutCount() {
        return bookMap.size();
    }

    public int getOverdueCount() {
        return overdue;
    }

    public LocalDate getMemberSince() {
        return memberDate;
    }

    /**
     * Returns the maximum number of books this patron can check out
     * based on their patron type.
     *
     * @return Maximum checkout limit
     */
    public int getMaxCheckoutLimit() {
        switch (type) {
            case FACULTY:
                return Max_Checkout_Limit_Faculty;
            case STAFF:
                return Max_Checkout_Limit_Staff;
            case STUDENT:
                return Max_Checkout_Limit_Student;
            case PUBLIC:
                return Max_Checkout_Limit_Public;
            case CHILD:
                return Max_Checkout_Limit_Children;
            default:
                return Max_Checkout_Limit_Default;
        }
    }

    /**
     * Returns the standard loan period in days for this patron type.
     *
     * @return Loan period in days
     */
    public int getLoanPeriodDays() {
        if (type == PatronType.FACULTY) return Loan_Period_Days_Faculty;
        else if (type == PatronType.STAFF) return Loan_Period_Days_Staff;
        else if (type == PatronType.STUDENT) return Loan_Period_Days_Student;
        else if (type == PatronType.PUBLIC) return Loan_Period_Days_Public;
        else if (type == PatronType.CHILD) return Loan_Period_Days_Children;
        else return Loan_Period_Days_Default;
    }

    /**
     * Reset previous fines to zero
     */
    public void resetFines() {
        this.fines = 0.0;
    }

    /**
     * Checks if patron is suspended from checking out book
     * @return returns boolean if patron is suspended true and false if patron is not suspended.
     */
    public boolean chkSuspended() {
        return this.suspended;
    }

    // Setters
    public void setAccountSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public void setOverdueCount(int count) {
        this.overdue = count;
    }

    /**
     * Adds a fine to the patron's balance.
     *
     * @param amount Amount to add
     */
    public void addFine(double amount) {
        if (amount > 0) {
            this.fines += amount;
        } else {
        }
    }

    /**
     * Pays off a portion of the fine balance.
     *
     * @param amount Amount to pay
     * @return Remaining balance
     */
    public double payFine(double amount) {
        this.fines = Math.max(0, this.fines - amount);
        return this.fines;
    }

    /**
     * Adds a book to the checked out books list.
     *
     * @param isbn Book ISBN
     * @param dueDate Due date for the book
     */
    public void addCheckedOutBook(String isbn, LocalDate dueDate) {
        bookMap.put(isbn, dueDate);
    }

    /**
     * Removes a book from the checked out books list.
     *
     * @param isbn Book ISBN to remove
     */
    public void removeCheckedOutBook(String isbn) {
        bookMap.remove(isbn);
    }

    /**
     * Checks if this patron currently has a specific book checked out.
     *
     * @param isbn Book ISBN
     * @return true if book is checked out by this patron
     */
    public boolean hasBookCheckedOut(String isbn) {
        if (bookMap.containsKey(isbn) == true) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Compares patrons based on patronId.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Patron other = (Patron) obj;
        if (patronId == null) {
            if (other.patronId != null) return false;
        } else if (!patronId.equals(other.patronId)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return patronId + "-" + name + "(" + type + ")" + "[Books:" + bookMap.size() + "/" + getMaxCheckoutLimit() +
                ",Fines:$" + fines + "]";
    }
}
