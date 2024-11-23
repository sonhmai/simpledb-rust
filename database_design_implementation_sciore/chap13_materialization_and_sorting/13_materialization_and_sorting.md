# Chapter 13: Materialization and Sorting

- relational algebra operators
  - materialize
  - sort
  - groupby
  - mergejoin
- operator impl: `materialization` vs `pipelined`
- materialize operator
  - preprocess input records
  - save output records in temp table(s)
  - no recomputation
  - pros: more efficient than only pipelined operators
  - cons: 
    - IO - write/read overhead to temp tables
    - compute - preprocessing entire input
- temp table
  - not created by table manager, not in system catalog
  - auto deleted by db when not needed anymore
  - recovery manager does not log change to temp tables
- sort(inputTable, fieldNames)
  - used in sql order by, operator `groupby`, `mergejoin`
  - mergesort
    - staging area
- groupby
- mergejoin algo


code entities of this chapter
```java 
class TempTable

interface Plan
  Scan open()
  int blocksAccessed()
  int recordsOutput()
  Schema schema()

class MaterializePlan implements Plan

class SortPlan implements Plan
class SortScan implements Scan 

class GroupByPlan implements Plan 
class GroupByScan implements Scan 
class GroupValue

interface AggregationFn
class MaxFn implements AggregationFn

class MergeJoinPlan implements Plan
class MergeJoinScan implements Scan 

```


![img.png](img_when_materialize_operator.png)

![img.png](img_query_tree_with_sort_node.png)