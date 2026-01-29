# Black Box Testing Report - Assignment 2

**Student Name:** [Nisan Chiproot]  
**ASU ID:** [nchiproo]  
**Date:** [01/26/2026]

---

## Part 1: Equivalence Partitioning (EP)

Identify equivalence partitions for the `checkoutBook(Book book, Patron patron)` method based on the specification (JavaDoc).

Create **multiple tables**, one per partition category (e.g., book state, patron state, renewal, limits, etc.).

Do **not** put everything into one table.

**Column Explanations:**
- **Partition ID**: Unique identifier (e.g., EP 1.1, EP 2.1)
- **State**: The specific state/value for this partition (e.g., "Unavailable", "Available")
- **Valid/Invalid**: Whether this partition represents valid or invalid input
- **Input Condition**: Precise condition that defines this partition
- **Expected Return**: What return code you expect
- **Expected Behavior**: What should happen

### Example EP Table: Book Availability

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 1.1 | Unavailable (0 copies) | Invalid | availableCopies == 0 AND other conditions allow checkout | 2.0 | No copies to checkout |
| EP 1.2 | Available (1+ copies) | Valid | availableCopies > 0 AND other conditions allow checkout | Success | Book can be checked out |

**Example test cases:** `testBookAvailable()`, `testUnavailableBook()`

---

### Your EP Tables (add as many as needed)

### Table #1: Book Availability
| Partition ID |       State              | Valid/Invalid | 			  Input Condition 	            | Expected Return |    Expected Behavior          |
|--------------|--------------------------|---------------|---------------------------------------------------------|-----------------|-------------------------------|
| EP 1.1       |Available (1+ copies)     |     Valid     |availableCopies > 0, AND other conditions allow checkout |0.0 (success)    |Book can be checked out        |
| EP 1.5       |unavailable (0 copies)    |    invalid    |availableCopies == 0,AND other conditions allow checkout |2.0 (unavailable)|No copies to check out         |
| EP 1.6       |unavailable (null)        |    invalid    |Book == null, AND other conditions allows checkout	    |2.1 (null)       |Book cannot be checked out     |
| EP 2.3       |Available (Reference only)|     Valid     |availableCopies == 0, AND other conditions allow checkout|5.0 (success)    |book unavailable reference only|


### Table #2: Patron State
| Partition ID |       State        		 | Valid/Invalid | 			  Input Condition 	                | Expected Return |    Expected Behavior       |
|--------------|---------------------------------|---------------|--------------------------------------------------------------|-----------------|----------------------------|
| EP 1.7       |Unavailable (suspended account)  |     Invalid   |isAccountSuspended() = T, AND other conditions allow checkout |  3.0 (suspended)| Book cannot be checked out |
| EP 1.8       |Unavailable (patron at max limit)|     Invalid   | availableCopies > 0, BUT other conditions wont allow checkout|  3.2 (Max limit)| Book cannot be checked out |
| EP 2.1       |    Unavailable                  |     Invalid   |patron == null, AND other conditions allow checkout           |  3.1 (fail)     | Book cannot be checked out |


### Table #3: renewing books
| Partition ID |       State         | Valid/Invalid | 			  Input Condition 	               | Expected Return |    Expected Behavior       |
|--------------|---------------------|---------------|---------------------------------------------------------|-----------------|----------------------------|
| EP 1.2       |Available (renewal)  |     Valid     | 			      renewal			       |  0.1 (success)  | Book can be checked out    |

### Table #4: Limits/fines on account
| Partition ID |       State        | Valid/Invalid | 			  Input Condition 	                   |      Expected Return            |    Expected Behavior       |
|--------------|--------------------|---------------|--------------------------------------------------------------|---------------------------------|----------------------------|
| EP 1.3       |     Available      |     Valid     | availableCopies > 0, AND other conditions allow checkout     |1.0 (success with warning)       | Book can be checked out    |
| EP 1.4       |     Available      |     Valid     | availableCopies > 0, AND other conditions allow checkout     |1.1 (success with warning)       | Book can be checked out    |
| EP 1.9       |    Unavailable     |    Invalid    |getOverDueCount() >= 3, AND other conditions allow checkout   |4.0 (Fail, reached overdue limit)| Book cannot be checked out |
| EP 2.0       |    Unavailable     |    Invalid    |getFineBalance() >= 10.0, AND other conditions allow checkout |4.1 (fail, unpaid fines >= 10.0) | Book cannot be checked out |
| EP 2.1       |    Unavailable     |    Invalid    |patron == null, AND other conditions allow checkout           |3.1 (fail, patron == null)       | Book cannot be checked out |
| EP 2.2       |    Unavailable     |    Invalid    |isAccountSuspended() = T, AND other conditions allow checkout |3.0 (fail, unpaid fines >= 10.0) | Book cannot be checked out |
| EP 2.4       |    Unavailable     |    Invalid    | availableCopies > 0, BUT other conditions wont allow checkout|3.2 (Max limit)                  | Book cannot be checked out |
| EP 2.5       |    Available       |     Valid     | availableCopies > 0, AND other conditions allow checkout     |1.1                              | Book can be checked out    |


## Part 2: Boundary Value Analysis (BVA)

Important BVA cases may overlap with EP. That is OK. You can reference all relevant EP/BVA coverage in Part 3.

### Example BVA Table: Overdue Count (Threshold: 3)

| Test ID | Boundary | Input Value | Expected Return | Rationale |
|---------|----------|-------------|-----------------|-----------|
| BVA 1.1 | Below | overdueCount = 0 | Success (depends on other setup) | Below warning threshold |
| BVA 1.2 | Warning High | overdueCount = 2 | 1.0 | Just below reject threshold |
| BVA 1.3 | At | overdueCount = 3 | 4.0 | At rejection boundary |
| BVA 1.4 | Above | overdueCount = 4 | 4.0 | Above rejection boundary |

---

### Your BVA Tables (add more as needed)
BVA Table 1: Patrons
| Test ID |      Boundary       | Input Value                          | Expected Return |           Rationale         |
|---------|---------------------|--------------------------------------|-----------------|-----------------------------|
| BVA 1.1 |       Below         |getCheckoutCount(Patron faculty) = 0  |       0.0       |Below warning threshold      |
| BVA 1.2 |    Warning High     |getCheckoutCount(Patron faculty) = 19 |       1.1       |Just below reject threshold  |
| BVA 1.3 |         At          |getCheckoutCount(Patron faculty) = 20 |       3.2       |At Rejection boundary        |
| BVA 1.4 |       Above         |getCheckoutCount(Patron faculty) = 21 |       3.2       |Above Rejection Boundary     |
| BVA 1.5 |       Below         |getCheckoutCount(Patron Staff) = 10   |       0.0       |Below warning threshold      |
| BVA 1.6 |    Warning High     |getCheckoutCount(Patron Staff) = 14   |       1.1       |Just below reject threshold  |
| BVA 1.7 |         At          |getCheckoutCount(Patron Staff) = 15   |       3.2       |At Rejection boundary        |
| BVA 1.8 |       Above         |getCheckoutCount(Patron Staff) = 16   |       3.2       |Above Rejection Boundary     |
| BVA 1.9 |       Below         |getCheckoutCount(Patron Student) = 5  |       0.0       |Below warning threshold      |
| BVA 2.1 |    Warning High     |getCheckoutCount(Patron Student) = 9  |       1.1       |Just below reject threshold  |
| BVA 2.2 |         At          |getCheckoutCount(Patron Student) = 10 |       3.2       |At Rejection boundary        |
| BVA 2.3 |       Above         |getCheckoutCount(Patron Student) = 11 |       3.2       |Above Rejection Boundary     |
| BVA 2.4 |       Below         |getCheckoutCount(Patron public) = 1   |       0.0       |Below warning threshold      |
| BVA 2.5 |    Warning High     |getCheckoutCount(Patron public) = 4   |       1.1       |Just below reject threshold  |
| BVA 2.6 |         At          |getCheckoutCount(Patron public) = 5   |       3.2       |At Rejection boundary        |
| BVA 2.7 |       Above         |getCheckoutCount(Patron public) = 6   |       3.2       |Above Rejection Boundary     |
| BVA 2.8 |       Below         |getCheckoutCount(Patron Child) = 0    |       0.0       |Below warning threshold      |
| BVA 2.9 |    Warning High     |getCheckoutCount(Patron Child) = 2    |       1.1       |Just below reject threshold  |
| BVA 3.1 |         At          |getCheckoutCount(Patron Child) = 3    |       3.2       |At Rejection boundary        |
| BVA 3.2 |       Above         |getCheckoutCount(Patron Child) = 4    |       3.2       |Above Rejection Boundary     |
| BVA 4.2 |        Below        |getLoanPeriod(Child) = 3              |       0.0       |Below warning threshold      |
| BVA 4.2 |    Warning High     |getLoanPeriod(Child) = 13             |       0.0       |Below warning threshold      |
| BVA 4.2 |        Below        |getLoanPeriod(Child) = 3              |       0.0       |Below warning threshold      |
| BVA 4.2 |        Below        |getLoanPeriod(Child) = 3              |       0.0       |Below warning threshold      |



BVA Table 2: General limits/fines
| Test ID | Boundary            |             Input Value              | Expected Return   |          Rationale          |
|---------|---------------------|--------------------------------------|-------------------|-----------------------------|
| BVA 3.3 |        Below        |getoverdueCount() = 0                 | 0.0               |   Below warning threshold   |
| BVA 3.4 |     Warning High    |getoverdueCount() = 2                 | 1.0               | Just below reject threshold |
| BVA 3.5 |         At          |getoverdueCount() = 3                 | 4.0               |    At rejection boundary    |
| BVA 3.6 |       Above         |getoverdueCount() = 4                 | 4.0               |   Above rejection boundary  |
| BVA 3.7 |        Below        |getFineBalance() = 0                  | 0.0               |   Below warning threshold   |
| BVA 3.8 |     Warning High    |getFineBalance() = 9                  | 0.0               | Just below reject threshold |
| BVA 3.9 |         At          |getFineBalance() = 10                 | 4.1               |    At rejection boundary    |
| BVA 4.1 |       Above         |getFineBalance() = 15                 | 4.1               |   Above rejection boundary  |
                 






---

## Part 3: Test Cases Designed

List at least **20** test cases you designed based on your EP/BVA analysis.

Each test case should include:
- EP/BVA coverage
- specific inputs / setup
- expected return code
- expected **observable state changes** (if any)

> Do not test console output.

### Test Case Table
At least some of your tests should verify observable state changes, not just return values.

**Checkout0-3 Columns:** Mark each implementation as Pass (✓) or Fail (✗) for this test case. This helps you track which implementations have bugs and will be useful for Part 4 analysis.

| Test ID Name 		           | EP/BVA |                                 Input Description			       | Expected Return | Expected State Changes                    | Checkout0 | Checkout1 | Checkout2 | Checkout3 |
|-------------------------|--------|-----------------------------------------------------------------------------------|-----------------|-------------------------------------------|-----------|-----------|-----------|-----------|
| T1 testUnavailableBook  	   | EP 1.5 | Book unavailable (0 copies), eligible patron                             | 2.0             | No state change                           | ✓         | ✓        | ✗         | ✓        |
| T2 testBookAvailable    	   | EP 1.1 | Book available (1+ copies), eligible patron, no warnings normal checkout | 0.0             | Patron map updated; copies of book change | ✗         | ✗        | ✓         | ✓        |
| T3 testBookAvailableWithOverdue  | EP 1.3 | Book available (1+ copies), eligible patron, Warning: overdue Books (1-2)| 1.0             | Patron map updated; copies of book change | ✓         | ✗        | ✓         | ✗        |
| T4 testBookAvailableOverdueTwo   | EP 1.4 | Book available (1+ copies), eligible patron, Warning: overdue Books(within 2 of max limit)| 1.1|Patron map updated; copies of book change |  ✓     | ✗        | ✓         | ✓        |
| T5 testBookEqualsNull            | EP 1.6 | Book unavailable (null), eligible patron,                                | 2.1             | No state change                           | ✓         | ✓        | ✓         | ✓        |
| T6 testBookAvailableSuspendedAcc | EP 1.7 | Book available (1+ copies), ineligible patron                            | 3.0             | No state change                           | ✓         | ✓        | ✓         | ✓        |
| T7 testBookAvailablePatronMax    | EP 1.8 | Book available (1+ copies), ineligible patron,                           | 3.2             | No state change                           | ✓         | ✗        | ✓         | ✗        |
| T8 testBookAvailableOverdueCount | EP 1.9 | Book available (1+ copies), ineligible patron,                           | 4.0             | No state change                           | ✓         | ✓        | ✓         | ✓        |
| T9 testBookAvailablePatronNull   | EP 2.1 | Book available (1+ copies), ineligible patron,                           | 3.1             | No state change                           | ✓         | ✓        | ✓         | ✓        |
| T10 testReferenceOnly            | EP 2.3 | book unavailable, reference only, eligible patron,                       | 5.0             | No state change                           | ✗         | ✓        | ✓         | ✓        |
| T11 testBookRenewal              | EP 1.2 | book available (renew), eligible patron,                                 | 0.1             | Patron map Updated; copies stays the same | ✓         | ✗        | ✗         | ✓        |
| T12 testBookFineBalanceOverTen   | EP 2.0 | book available, ineligible patron,                                       | 4.1             | No state change                           | ✓         | ✓        | ✓         | ✓        |
| T13 testBookAvailableFacultyAtLimit|BVA 1.3| book available, ineligible patron,                                      | 3.2             | No state change                           | ✓         | ✗        | ✓         | ✓        |
| T14 testBookAvailableFacultyBelow| BVA 2.3| book available, eligible patron                                          | 0.0             | Patron map updated; copies of book change | ✓         | ✗        | ✓         | ✓        |
| T15 TestBookAvailableChildAbove  | BVA 3.2| book available, ineligible patron,                                       | 3.2             | No state change                           | ✓         | ✗        | ✓         | ✓        |
| T16 testBookAvailableChildJustBelow|BVA 2.9|Book available (1+ copies), eligible patron, Warning: overdue Books(within 2 of max limit) | 1.1|Patron map Updated; copies of book change| ✓      | ✗        | ✓         | ✓        |
| T17 testBookAvailableFineEqualTen| BVA 3.9| book available, ineligible patron                                        | 4.1             | No State Change                           | ✓         | ✓        | ✓         | ✓        |
| T18 TestBookAvailableFineEqualOne|BVA 3.7| book available, eligible patron,                                          | 0.0             | Patron map Updated; copies of book change | ✓         | ✗        | ✓         | ✓        |
| T19 testBookAvailableFineGreatTen| BVA 4.1| book available, ineligible patron,                                       | 4.1             | No state change                           | ✓         | ✓        | ✓         | ✓        |
| T20 testBookAvailablepublicAt    | EP 3.1 | book available, ineligible patron,                                       | 3.2             | No state change                           | ✓         | ✗        | ✓         | ✓        |
| T21 testBookAvailable            | EP 2.5 | book available, Eligible Patron,                                         | 1.1             | patron map updated. copies of book change | ✓         | ✗        | ✗         | ✓        |
(Add rows until you have at least 20.)


## Part 4: Bug Analysis

### Easter Eggs Found
List any easter egg messages you observed:
- 17: The happy path matters too.
- 10.2: but never their absence 
- 15.2: ...xvFZjo5PgG0 (test renewal to complete!)
- 10.2/3: 'but never their absence'
- 18: 'Remember to test all the edge cases.' 
- 18: 'The best code is no code at all... but this isn't it.'
- 10.1/3: 'Testing can show the presence of bugs.'
- 20: Reference books are meant to be consulted not carried home, these books stay home, reference materials: look but dont touch, stay in the library, book"
- 19: cant check out what isn't there
- 15.1: https://www.youtube.com/watch?v=xvFZjo5PgG0
- 13: limits exist to be thoroughly test.
- 14 "A book is a book re-loved


### Implementation Results

| Implementation | Bugs Found (count)  |
|----------------|---------------------|
| Checkout0      | 2                   |
| Checkout1      | 12                  |
| Checkout2      | 3                   |
| Checkout3      | 2                   |

### Bugs Discovered
List distinct bugs you identified for each implementation. Each bug must cite at least one test case that revealed it.

**Checkout0:**
- Bug 1: [Book should be unavailable after checkout] — Revealed by: [Test 2]
- Bug 2: [Reference only book is being implemented as regular book: expected 5.0 returned 2.0] — Revealed by: [Test 10]

**Checkout1:**
- Bug 1: [Patron should have book in list for checkout1] — Revealed by: [Test 3]
- Bug 2: [Expected warning code 1.1, returned 0.0 for available book for checkout 1] — Revealed by: [Test 4]
- Bug 3: [Expected warning message 3.2, returned 0.0 for available book for checkout 1] — Revealed by: [Test 7]
- Bug 4: [Expected successful checkout 0.1, returned 0.0 for checkout1] — Revealed by: [Test 11]
- Bug 5: [Expected error code 3.2 returned 2.0 for available book for checkout 1] — Revealed by: [Test 13]
- Bug 6: [expected warning code 3.2, returned 2.0 for checkout 1] — Revealed by: [Test 14]
- Bug 7: [Expected error code 3.2, returned 0.0 for available book for checkout 1] — Revealed by: [Test 15]
- Bug 8: [Expected success code 1.1, retuned 0.0 for Checkout1] — Revealed by: [Test 16]
- Bug 9: [Patron Should have book in check-out list for checkout1] — Revealed by: [Test 18]
- Bug 10: [Expected warning code 3.2 returned 0.0 for available book for checkout 1] — Revealed by: [Test 20]
- Bug 11: [java null pointer exception] — Revealed by: [Test 21]
- Bug 12: [Patron should have book in list for checkout1] — Revealed by: [Test 2]


**Checkout2:**
- Bug 1: [Expected error code 2.0 for unavailable book for checkout 2] — Revealed by: [Test 1]
- Bug 2: [Expected successful checkout 0.1, returned 0.0 for checkout 2] — Revealed by: [Test 11]
- Bug 3: [Expected warning code 1.1 returned 0.0, for checkout 2] — Revealed by: [Test 1]

**Checkout3:**
- Bug 1: [Expected warning 1.0, returned 0.0 for overdue books] — Revealed by: [Test 3]
- Bug 2: [Expected error code 2.0, returned 0.0 for unavailable book for checkout 3] — Revealed by: [Test 1]

### Comparative Analysis
Compare the four implementations:
- Which bugs are most critical (cause the worst failures)?
The most critical bug is error code 3.2 keep returning as 0.0, but we should be getting a max limit warning. Followed by error code 1.1 which is returning 0.0 when it should be successful but send a warning. 
- Which implementation would you use if you had to choose?
If i had to choose one implementation it would be either checkout 0 or checkout 4 since they have the least amount of bugs found.
- Why? Justify your choice considering bug severity and frequency.
I would choose checkout 3. Checkout 0 has a total of 2 bug out of the 21 tests The 2 bugs are a slightly more severe then checkout 3. Checkout 3 also had 2 bugs found, but seem to be easier to fix/implement.



## Part 5: Reflection

**Which testing technique was most effective for finding bugs?**
I would say that the EP and BVAs were great for finding where bugs start to create issues. Using boundaries through the BVA was most effective.
**What was the most challenging aspect of this assignment?**
The most challenging part of this was testing. I have tested in other classes using EPS but this was a higher level of testing for sure. This assignment took me over 35+ hours. While i learned a lot the length of it was truly a lot. 
**How did you decide on your EP and BVA?**
The EPs were used to test every possibility of returning code. The BVA helped identify the target boundaries that would execute different code.
**Describe one test where checking only the return value would NOT have been sufficient to detect a bug.**
The test case testBookAvailable has all 4 tests passed if we only check the return code. Once we verify 2 tests fail.

