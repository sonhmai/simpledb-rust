

## 6.1

Assume that the block size is 400 bytes and that records cannot span blocks. 
Calculate the maximum number of records that can fit in a SimpleDB record page and the amount of wasted space in the page for each of the following `record sizes`: 10 bytes, 20 bytes, 50 bytes, and 100 bytes.

- block = arrays of records = record page (simpledb)
- block divided into slots
- slot = record + 1 byte (flag empty/ used_)
- slot size = size of above slot

``` 
1 record page = 1 block = 400 B

record size, slot size, max records, wasted
10B, 11B, 36 records, 4B wasted
20B, 21B, 19, 1B wasted
50B, 51B, 7, 43
100B, 101B, 3 recs, 97B wasted
```