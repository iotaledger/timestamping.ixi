# timestamping.ixi

## About

The Tangle is a graph with only a partial order structure, which makes it difficult to establish the correct time order of transactions. Even if a transaction has a decleared timestamp, we can not be sure if it's correct.  

**timestamping.ixi** is an [IXI (IOTA eXtension Interface) module](https://github.com/iotaledger/ixi) for the [Iota Controlled agenT (Ict)](https://github.com/iotaledger/ict).
This module helps us find the confidence interval of the time at which a transaction was issued with reasonable accuracy. 

The specifications of timestamping.ixi are described in detail here:

[On the timestamps in the tangle](https://assets.ctfassets.net/r1dr6vzfxhev/4XgiKaTkUgEyW8O8qGg6wm/32f3a7c28022e35e4d5d0e858c0973a9/On_the_timestamps_in_the_tangle_-_20182502.pdf)\
[timestamping.ixi](https://github.com/iotaledger/omega-docs/blob/master/ixi/timestamping/Spec.md)

