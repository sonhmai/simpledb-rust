
Questions
1. what does `pin` mean?
2. `pin` and `unpin` a buffer?
3. what is a `log block`? what is `last log block`?
4. what is `buffer manager`?

## 4.11 Log Manager

The SimpleDB `log manager` allocates its own page and writes it explicitly to disk .
Another design option is for it to `pin a buffer to the last log block` and let the buffer manager handle the disk accesses.
1. Work out a design for this option. 
2. What are the issues that need to be addressed? Is it a good idea?
3. Modify SimpleDB to implement your design.

## 4.12 LogIterator

Each LogIterator object allocates a page to hold the log blocks it accesses.
1. Explain why using a buffer instead of a page would be much more efficient.
2. Modify the code to use a buffer instead of a page. 
3. How should the buffer get unpinned?

## 4.13 malicious JDBC program

This exercise examines whether a JDBC program could maliciously pin all of the buffers in the buffer pool.

(a) Write a JDBC program to pin all of the buffers of the SimpleDB buffer pool. What happens when all of the buffers are pinned?

(b) The Derby database system does buffer management differently than SimpleDB. When a JDBC client requests a buffer, Derby pins the buffer, sends a copy of the buffer to the client, and unpins the buffer. Explain why your code will not be malicious to other Derby clients.

(c) Derby avoids SimpleDB’s problem by always copying pages from engine to client. Explain the consequences of this approach. Do you prefer it to the SimpleDB approach?

(d) Another way to keep a rogue client from monopolizing all of the buffers is to allow each transaction to pin no more than a certain percentage (say, 10%) of the buffer pool. Implement and test this modification to the SimpleDB buffer manager.

## 4.14 Buffer Manager

Modify class BufferMgr to implement each of the other replacement strategies described in this chapter.
1. Naive
2. FIFO
3. LRU
4. Clock

## 4.15 replacement strategy

Exercise 4.4 suggests a page replacement strategy that chooses unmodified pages over modified ones. Implement this replacement strategy.

## 4.16 LSN replacement strategy

Exercise 4.5 suggests a page replacement strategy that chooses the modified page having the lowest LSN. Implement this strategy.

## 4.17 improve buffer search time

- The SimpleDB buffer manager traverses the buffer pool sequentially when searching for buffers. 
- This search will be time-consuming when there are thousands of buffers in the pool. 
- Modify the code, adding data structures (such as special-purpose lists and hash tables) that will improve the search times.

## 4.18 Statistics

In Exercise 3.15, you were asked to write code that maintained statistics about disk usage. Extend this code to also give information about buffer usage.
