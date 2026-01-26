# Code Review Checklist

**Reviewer Name:** \[Nisan Chiproot]
**Date:** \[01/26/2026]
**Branch:** Review

## Instructions

Review ALL source files (in main not test) in the project and identify defects using the categories below. Log at least 5 defects total:

* At least 1 from CS (Coding Standards)
* At least 1 from CG (Code Quality/General)
* At least 1 from FD (Functional Defects)
* Remaining can be from any category

## Review Categories

* **CS**: Coding Standards (naming conventions, formatting, style violations)
* **CG**: Code Quality/General (design issues, code smells, maintainability)
* **FD**: Functional Defects (logic errors, incorrect behavior, bugs)
* **MD**: Miscellaneous (documentation, comments, other issues)

## Defect Log

|Defect ID|File|Line(s)|Category|Description|Severity|
|-|-|-|-|-|-|
|1|Checkout.java|142-144|FD|reference only books expect return 5.0 but resulted in 0.0|high|
|2|Checkout.java|39|CS|this.booklist should be named this.bookmap. booklist is not a list its a hashmap.|medium|
|3|Patron.java|108|CG|getmaxcheckoutlimits is a switch that shows the limit on how many books can be checked out. Its default is 5.0 but a child is only allowed 3 checked out books at once.|medium|
|4|Checkout.java|142-144|FD|Patron had fines over $10, was supposed to get error message 4.1 when checking out. Users return code was 0.0 (successful checkout)|high|
|5|Book.java|105-107|CG|The return books only has an option to return if there are less then 100 copies available. what if there is 100 copies available to check out and someone returns one?|high|
|6||||||
|7||||||
|8||||||
|9||||||
|10||||||

**Severity Levels:**

* **Critical**: Causes system failure, data corruption, or security issues
* **High**: Major functional defect or significant quality issue
* **Medium**: Moderate issue affecting maintainability or minor functional problem
* **Low**: Minor style issue or cosmetic problem

## Example Entry

|Defect ID|File|Line(s)|Category|Description|Severity|
|-|-|-|-|-|-|
|1|Checkout.java|17|CS|Variable bookList misleading - Map not List|Medium|
|2|Book.java|107|FD|Magic number 100 should be totalCopies|High|

## Notes

* Be specific with line numbers
* Provide clear, actionable descriptions
* Consider: readability, maintainability, correctness, performance, security
* Focus on issues that impact code quality or functionality
