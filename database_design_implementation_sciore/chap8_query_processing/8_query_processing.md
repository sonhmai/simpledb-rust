# Chapter 8: Query Processing


- operator
- relational algebra ~ set of data-retrieval operators
- SQL query to relational algebra translation
- operator
  - select
  - project
  - product
- query tree
  - scan
    - updatable scan
    - operator intent
- pipelined implementation
  - need-to-know
  - not preprocess data, read ahead, cache, sort
  - not construct output records
- predicate
  - term
  - expression
  - constant

```java
interface Scan
  void      beforeFirst()
  boolean   next()
  int       getInt(String fieldname)
  String    getString(String fieldname)
  Constant  getVal(String fieldname)
  boolean   hasField(String fieldname)
  void      close()
  
interface UpdateScan extends Scan
class SelectScan implements UpdateScan
class ProjectScan implements Scan
class ProductScan implements Scan

class Constant implements Comparable<Constant>
class Expression
class Term
class Predicate
  void conjoinWith(Predicate pred)
  boolean isSatisfied(Scan s)
```

predicate code
```java
Expression left = new Expression("StudentName");
Constant c = new Constant("joe");
Expression right = new Expression(c);
Term t1 = new Term(left, right);

Expression left2 = new Expression("MajorId");
Expression right2 = new Expression("DepartmentId");
Term t2 = new Term(left2, right2);

Predicate pred1 = new Predicate(t1);
Predicate pred2 = new Predicate(t2);
pred1.conjoinWith(pred2);
```


further readings
1. Atzeni, P., & DeAntonellis, V. (1992). *Relational database theory*. Upper Saddle River, NJ: Prentice-Hall.
2. Chaudhuri, S. (1998). *An overview of query optimization in relational systems*. In Proceedings of the ACM Principles of Database Systems Conference (pp. 34–43).
3. Graefe, G. (1993). *Query evaluation techniques for large databases*. ACM Computing Surveys, 25(2), 73–170.
