## Left-leaning red-black tree implementation (Sedgewick 2008)
Uses C++17 (e.g. for `std::optional`)

Compile with `cmake` or e.g.

```
$ g++ -std=c++17 -O3 red_black_tree.cpp -o red_black_tree
$ ./red_black_tree
Test of a left-leaning red-black tree implementation

- Execute N initial put operations with random data
- Calculate how fast further put operations are by executing 1000 more
- Multiply N by 10

1000 initial puts with random keys took 0.449577 ms
1000 more puts took 0.521114 ms (0.521114 us/put)
---
10000 initial puts with random keys took 6.1856 ms
1000 more puts took 0.757172 ms (0.757172 us/put)
---
100000 initial puts with random keys took 99.5772 ms
1000 more puts took 1.14301 ms (1.14301 us/put)
---
1000000 initial puts with random keys took 1832.41 ms
1000 more puts took 2.31109 ms (2.31109 us/put)
---
10000000 initial puts with random keys took 30892.8 ms
1000 more puts took 3.57413 ms (3.57413 us/put)
---
^C
```

