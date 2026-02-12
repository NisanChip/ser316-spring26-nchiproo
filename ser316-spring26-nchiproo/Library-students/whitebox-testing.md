# White Box Testing Report - Assignment 3

**Student Name:** [Nisan Chiproot]
**ASU ID:** [nchiproo]
**Date:** [February 2, 2026]

---

## Part 1: Control Flow Graph for countBooksByType()

### Graph Description

Draw or describe your control flow graph here. Include:
- Node numbers and what they represent
- Edges showing control flow
- Conditions at decision points

**You can hand-draw and insert an image, or describe it in text format.**
                                            Start
                                              |
                                        countBookByType()
                                              |
                                         type == null
                                   False/            \True
                            b.getType()==type       continue
                       False/                \True         
                        continue          onlyAvailable
                                     False/             \True 
                            looped++ (count anyways)     looped++
                                            \             /
                                             Return looped;
                                    
                                    

### Node Coverage Sequences

List the sequences needed for complete node coverage:

**Sequence 1:lines 367-368**
- **Path: type == null -> true -> return 0   lines 367-368** 
- **Purpose: if we dont know what book type then it should return 0**
- **Test case: 1**


**Sequence 2: lines 376-377**
- **Path: b == null**
- **Purpose: book is unavailable**
- **Test case: 2,9**


**Sequence 3: lines 381-394**
- **Path: b.gettype == type**
- **Purpose: checking if the book type is the type we are searching for**
- **Test case: 3,5**

**Sequence 4: lines 383-391**
- **Path: onlyAvailable()**
- **Purpose: checking if the book type is available**
- **Test case: 4,5,10,11**




### Edge Coverage Sequences

List the sequences needed for complete edge coverage:

**Sequence 1: lines 367-371 **
- **Edges covered: type == null -> True -> return 0 /False -> looped = 0**
- **Test case: 1**


**Sequence 2: 376-377**
- **Edges covered: b == null -> True -> continue -> next condition / False -> b.gettype == type**
- **Test case: 2,9**

**Sequence 3: 381-394**
- **Edges covered: b.gettype() == type -> True -> onlyAvailable / False -> Continue **
- **Test case: 3,5**

**Sequence 4: 383-394**
- **Edges covered: onlyAvailable() if -> b.isAvailable -> looped++ -> return looped / Else -> looped++ -> return looped**
- **Test case: 4,5,10,11**


---

## Part 2: Code Coverage with JaCoCo

### Initial Coverage for Checkout.java

**Before adding tests:**
- **Line Coverage:** 50%
- **Branch Coverage:** 44%

### Coverage for countBooksByType()

**Before additional tests:**
- **Branch Coverage:** 45%

**After reaching 80% (supposed to be 60?) branch coverage:**
- **Branch Coverage:** 63%
- **Tests added: 11**

### Final Overall Coverage

- **Line Coverage:** 83%
- **Branch Coverage:** 63%

---

## Part 3: checkoutBook() Implementation

### Test-Driven Development Process

**Number of tests from BlackBox assignment:** 21

**Implementation challenges:**
1. I struggled with the format/layout of blackbox testing i had to redo a few tests.
2. Whitebox testing seemed to be a bit easier at first but took me a while to figure out how to find coverage.

**All tests passing:** [Yes/No]
YES
---

## Part 4: Reflection

**How did white-box testing differ from black-box testing?**
White box focused more on path coverage, while blackbox testing needed to be able to handle specific behaviour of each case.

**Which approach do you find more effective? Why?**
I find whitebox testing a lot more effective. White box goes through every possible path and ensures the outcome is correct.
**Would you prefer TDD or implementation first test later? Why?**
I prefer implementation first then test. I do acknowledge the importance of TDD, but I have always been implement firs tthen test and see how i can fix the code.
I do understand that as a i get more experienced i should trust the TDD proccess.