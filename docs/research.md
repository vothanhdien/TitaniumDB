# Introduction

**TiDB ("Ti" is titanium) is an open-source NewSQL database that supports Hybrid Transactional and Analytical Processing (HTAP) workloads. It is MySQL compatible and features horizontal scalability, strong consistency, and high availability.**


# TiDB Cluster
 
![tidb-architecture.png](https://download.pingcap.com/images/docs/stable/tidb-architecture.png)
 

## TiKV Cluter ( Storage layer)
TiKV cluster is responsible for storing data, a distributed transactional key-value store. TiKV uses RocksDB as store engine. TiKV uses the Raft protocol for replication to maintain data consistency and disaster tolerance. All replication process is scheduled by the PD.

![basic-architecture.png](https://tikv.org/img/basic-architecture.png)

### Write in Rust

### Use RockDB to storage
Why TiKV use rocksDB.

* RocksDB stable
* RocksDB fast
* RocksDB easy to embedded
* RocksDB has many useful features
* RocksDB has a very active community

1 TiKV Node has 2 RocksDB instance: 1 for data another for raft log

### Use Raft protocol to replication

About [Raft protocol](https://raft.github.io/)

![tikv-instance.png](https://tikv.org/img/tikv-instance.png)


## TiDB

**This is query layer**

[TiKV \| Distributed SQL](https://tikv.org/deep-dive/distributed-sql/dist-sql/)

![select-from-tidb.png](https://tikv.org/img/deep-dive/select-from-tidb.png)


## PD( Placement Driver)

