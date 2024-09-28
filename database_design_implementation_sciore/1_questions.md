
## 1.1
```
1.1. Suppose that an organization needs to manage a relatively small number of shared records (say, 100 or so).
(a) Would it make sense to use a commercial database system to manage these records?
(b) What features of a database system would not be required?
(c) Would it be reasonable to use a spreadsheet to store these records? What are the potential problems?
```

Would it make sense to use a commercial database system to manage these records?
- in most situation, no but it also depends on the importance of that data. If the data is highly critical like financial one then maybe yes in some special cases.

What features of a database system would not be required?
- indexing: data too small
- query optimization
- frontend

Would it be reasonable to use a spreadsheet to store these records? 
- yes it is reasonable in most cases where the data is not that highly critical

What are the potential problems?
- with current spreadsheet cloud, concurrent user accesses for a reasonable amount of users would not be a problem.
- potential problem when it needs to be accessed concurrently by big amount of systems, applications.
- without sql, it might be hard for other reporting systems to query those data and join it with others.

## 1.2
```
1.2
Suppose you want to store a large amount of personal data in a database. What features of a database system wouldn’t you need?
```
- concurrency control

## 1.4 version control system comparison
```
1.4.
If you know how to use a version control system (such as Git or Subversion), compare its features to those of a database system.

(a) Does a version control system have a concept of a record?
(b) How does check-in/checkout correspond to database concurrency control?
(c) How does a user perform a commit? How does a user undo uncommitted
changes?
(d) Many version control systems save updates in difference files, which are small files that describe how to transform the previous version of the file into the new one. If a user needs to see the current version of the file, the system starts with the original file and applies all of the difference files to it. How well does this implementation strategy satisfy the needs of a database system?
```

### Does a version control system have a concept of a record?
- no, a commit can be thought of as something similar.
- closest if blob for storing file content.
- the git model is more document-oriented than record-oriented.

### How does check-in/checkout correspond to database concurrency control?
- VCS checkin/ checkout has some common points with `multiversion concurrency control`
- while db system wants `immediate consistency`, VCS supports `async consistency`
  - conflicts are resolved later using merge in VCS
  - conflicts in db systems are resolved while transaction commit. there is no interactive process but the failed transaction has to retry.

### (c) How does a user perform a commit? 


### How does a user undo uncommitted changes?
- with `git checkout -- <FILE>` or `git reset` to revert working dir to previous committed state.
  - `git reset --hard` to discard all
  - `git reset` to unstage changes
- this is similar to rolling back changes made to the db of `uncommitted transactions`.
- the staging area of git is similar to a transaction operations
- transaction log ~ git commit log
- transaction commit ~ git commit
- change to different tables ~ changes to different files

### (d) How well does this implementation strategy satisfy the needs of a database system?

not optimal for db
- bad read performance
- not supporting random access
  - applying series of linear diffs can be slow and does not support random access that a db needs.
