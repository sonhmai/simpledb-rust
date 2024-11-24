# Programming Exercises

(from the book)

``` 
8.6. The getVal, getInt, and getString methods in ProjectScan check that their argument field names are valid. 
None of the other scan classes do this. 

For each of the other scan classes:
(a) Say what problem will occur (and in what method) if those methods are called with an invalid field.
(b) Fix the SimpleDB code so that an appropriate exception is thrown.
```


``` 
8.11. In Exercise 6.13, you extended the SimpleDB record manager to handle
database null values. 

Now extend the query processor to handle nulls. In particular:
• Modify the class Constant appropriately.
• Modify the methods getVal and setVal in TableScan so that they
recognize null values and handle them appropriately.
• Determine which of the various Expression, Term, and Predicate
classes need to be modified to handle null constants.
```