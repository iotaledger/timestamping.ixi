# timestamping.ixi

## About

A directed, acyclic graph (DAG) is a graph with only a partial order structure. It's possible for two vertices u,v to be incomparable, in other words one is not a descendent of the other one in the graph. This makes it difficult to establish the correct order of its vertices. In the Tangle, to deal with this effect, we enforce every transaction to declare a timestamp: the time when it was attached. However, even though all the transactions contain timestamps, we can not be sure if they are correct (e.g. nodes could lie about timestamps; or declare wrong timestamps because of wrong clocks, ...). Either way, we expect most transactions to declare their timestamp as accurate and precise as they can. By inspecting other transactions relative to a transaction, we expect to be able to determine the time interval within the transaction was attached. Serguei Popov described two algorithms [1] to find this confidence interval.<br><br>
**timestamping.ixi** is an [IXI (IOTA eXtension Interface) module](https://github.com/iotaledger/ixi) for the [Iota Controlled agenT (Ict)](https://github.com/iotaledger/ict).
It helps us find the time interval within a transaction was issued [2]. 

[1] [On the timestamps in the tangle, by Serguei Popov](https://assets.ctfassets.net/r1dr6vzfxhev/4XgiKaTkUgEyW8O8qGg6wm/32f3a7c28022e35e4d5d0e858c0973a9/On_the_timestamps_in_the_tangle_-_20182502.pdf)\
[2] [Specification of timestamping.ixi, by Paul Handy](https://github.com/iotaledger/omega-docs/blob/master/ixi/timestamping/Spec.md)

## Installation

### Step 1: Install Ict

Please find instructions on [iotaledger/ict](https://github.com/iotaledger/ict#installation).

Make sure you are connected to the main network and not to an island, otherwise you won't be able to message anyone in the main network.

### Step 2: Get Timestamping.ixi

There are two ways to do this:

#### Simple Method

Go to [releases](https://github.com/iotaledger/timestamping.ixi/releases) and download the **timestamping-{VERSION}.jar**
from the most recent release.

#### Advanced Method

You can also build the .jar file from the source code yourself. You will need **Git** and **Gradle**.

```shell
# download the source code from github to your local machine
git clone https://github.com/iotaledger/timestamping.ixi
# if you don't have git, you can also do this instead:
#   wget https://github.com/iotaledger/timestamping.ixi/archive/master.zip
#   unzip master.zip

# change into the just created local copy of the repository
cd timestamping.ixi

# build the timestamping-{VERSION}.jar file
gradle ixi
```

### Step 3: Install Timestamping.ixi
Move timestamping-{VERSION}.jar to the **modules/** directory of your Ict:
```shell
mv timestamping-{VERSION}.jar ict/modules
```

### Step 4: Run Ict
Switch to Ict directory and run:
```shell
java -jar ict-{VERSION}.jar
```