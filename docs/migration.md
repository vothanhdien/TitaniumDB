# Sync process

There are 2 way to sync data from Mysql to TiDB.
  * Using Syncer, Loader and Mydumper
  * Using Data Migration

TiDB provide Syncer, Loader and Mydumper for migrate purpose.
You can download all of them in  [[Download \| TiDB Documentation](https://pingcap.com/docs/stable/reference/tools/download/#tidb-dm-data-migration)]


  * mydumper: dump data from mySQL into folder
  * loader: read the folder and import data to tiDB.
  * Syncer: sync data from  mySQL to tiDB.


## Sync full data
### Step 1 : dump data from Mysql
```bash
./bin/mydumper -h 127.0.0.1 -P 3306 -u root -p abc@123 -t 16 -F 64 -B zpTopupLog -T OrderLog202004 --skip-tz-utc -o ./var/orderLog202004
```
-B test: means the data is exported from the test database.
-T t1,t2: means only the t1 and t2 tables are exported.
-t 16: means 16 threads are used to export the data.
-F 64: means a table is partitioned into chunks and one chunk is 64MB.
--skip-tz-utc: the purpose of adding this parameter is to ignore the inconsistency of time zone setting between MySQL and the data exporting machine and to disable automatic conversion.

**Expected out put**
```
ll var/orderLog202004 
total 184K
-rw-rw-r-- 1 lap11757 lap11757   75 Thg 4 21 09:21 metadata
-rw-rw-r-- 1 lap11757 lap11757 1,9K Thg 4 21 09:21 zpTopupLog.OrderLog202004-schema.sql
-rw-rw-r-- 1 lap11757 lap11757 169K Thg 4 21 09:21 zpTopupLog.OrderLog202004.sql
-rw-rw-r-- 1 lap11757 lap11757   71 Thg 4 21 09:21 zpTopupLog-schema-create.sql

```


### Step 2: Import data to tiDB

```bash
./bin/loader -h 127.0.0.1 -u root -P 4000 -t 32 -d ./var/orderLog202004
```

**Expected out put**
```log
./bin/loader -h 127.0.0.1 -u root -P 4000 -t 32 -d ./var/orderLog202004 
2020/04/21 09:23:39 printer.go:52: [info] Welcome to loader
2020/04/21 09:23:39 printer.go:53: [info] Release Version: v1.0.0-78-g6aea485
2020/04/21 09:23:39 printer.go:54: [info] Git Commit Hash: 6aea4851bb0c6e599c64b5c952ce257863c21586
2020/04/21 09:23:39 printer.go:55: [info] Git Branch: master
2020/04/21 09:23:39 printer.go:56: [info] UTC Build Time: 2019-12-18 04:25:49
2020/04/21 09:23:39 printer.go:57: [info] Go Version: go version go1.13 linux/amd64
2020/04/21 09:23:39 main.go:51: [info] config: {"log-level":"info","log-file":"","status-addr":":8272","pool-size":32,"dir":"./var/orderLog202004","db":{"host":"127.0.0.1","user":"root","port":4000,"sql-mode":"@DownstreamDefault","max-allowed-packet":67108864},"checkpoint-schema":"tidb_loader","config-file":"","route-rules":null,"do-table":null,"do-db":null,"ignore-table":null,"ignore-db":null,"rm-checkpoint":false}
2020/04/21 09:23:39 loader.go:532: [info] [loader] prepare takes 0.000086 seconds
2020/04/21 09:23:39 checkpoint.go:207: [info] calc checkpoint finished. finished tables (map[])
2020/04/21 09:23:39 loader.go:715: [info] [loader][run db schema]./var/orderLog202004/zpTopupLog-schema-create.sql[start]
2020/04/21 09:23:40 loader.go:720: [info] [loader][run db schema]./var/orderLog202004/zpTopupLog-schema-create.sql[finished]
2020/04/21 09:23:40 loader.go:736: [info] [loader][run table schema]./var/orderLog202004/zpTopupLog.OrderLog202004-schema.sql[start]
2020/04/21 09:23:40 loader.go:741: [info] [loader][run table schema]./var/orderLog202004/zpTopupLog.OrderLog202004-schema.sql[finished]
2020/04/21 09:23:40 loader.go:773: [info] [loader] create tables takes 0.161658 seconds
2020/04/21 09:23:40 loader.go:788: [info] [loader] all data files have been dispatched, waiting for them finished 
2020/04/21 09:23:40 loader.go:158: [info] [loader][restore table data sql]./var/orderLog202004/zpTopupLog.OrderLog202004.sql[start]
2020/04/21 09:23:40 loader.go:216: [info] data file var/orderLog202004/zpTopupLog.OrderLog202004.sql scanned finished.
2020/04/21 09:23:40 loader.go:165: [info] [loader][restore table data sql]./var/orderLog202004/zpTopupLog.OrderLog202004.sql[finished]
2020/04/21 09:23:40 loader.go:791: [info] [loader] all data files has been finished, takes 0.347854 seconds
2020/04/21 09:23:40 main.go:88: [info] loader stopped and exits 
```

### Note 

To migrate data quickly, especially for huge amount of data, you can refer to the following recommendations.

 * Keep the exported data file as small as possible and it is recommended keep it within 64M. You can use the -F parameter to set the value.
 * You can adjust the -t parameter of loader based on the number and the load of TiKV instances. For example, if there are three TiKV instances, -t can be set to 3 * (1 ~ n). If the load of TiKV is too high and the log backoffer.maxSleep 15000ms is exceeded is displayed many times, decrease the value of -t; otherwise, increase it.


## Sync increment data

### Note

  * This way need mysql enable MySQL binary logging

### Step 1: sync old data 

  * sync old data like Sync full data
  * run script
```bash
./bin/mydumper -h 127.0.0.1 -P 3306 -u root -p abc@123 -t 16 -F 1 -B TransactionLog -T CreatedOrderLog --skip-tz-utc -o ./var/createOrderLog 
```

  * now let see file metadata

```log
cat var/createOrderLog/metadata 
Started dump at: 2020-04-21 10:46:18
SHOW MASTER STATUS:
	Log: mysql-bin.000001
	Pos: 145054
	GTID:

Finished dump at: 2020-04-21 10:46:18

```

### Step 2: run syncer
create syncer.meta with data like metadata in step 1
```
binlog-name = "mysql-bin.000001"
binlog-pos = 145054
```

create config.toml

```
log-level = "info"
log-file = "syncer.log"
log-rotate = "day"

server-id = 101

# The file path for meta:
meta = "./syncer.meta"
worker-count = 16
batch = 1000
flavor = "mysql"

# The testing address for pprof. It can also be used by Prometheus to pull Syncer metrics.
status-addr = ":8271"

# If you set its value to true, Syncer stops and exits when it encounters the DDL operation.
stop-on-ddl = false

# max-retry is used for retry during network interruption.
max-retry = 100

# Specify the database name to be replicated. Support regular expressions. Start with '~' to use regular expressions.
# replicate-do-db = ["~^b.*","s1"]

# Specify the database you want to ignore in replication. Support regular expressions. Start with '~' to use regular expressions.
# replicate-ignore-db = ["~^b.*","s1"]

# skip-ddls skips the ddl statements.
# skip-ddls = ["^OPTIMIZE\\s+TABLE"]

# skip-dmls skips the DML statements. The type value can be 'insert', 'update' and 'delete'.
# The 'delete' statements that skip-dmls skips in the foo.bar table:
# [[skip-dmls]]
# db-name = "foo"
# tbl-name = "bar"
# type = "delete"
#
# The 'delete' statements that skip-dmls skips in all tables:
# [[skip-dmls]]
# type = "delete"
#
# The 'delete' statements that skip-dmls skips in all foo.* tables:
# [[skip-dmls]]
# db-name = "foo"
# type = "delete"

# Specify the db.table to be replicated.
# db-name and tbl-name do not support the `db-name ="dbname, dbname2"` format.
# [[replicate-do-table]]
# db-name ="dbname"
# tbl-name = "table-name"

# [[replicate-do-table]]
# db-name ="dbname1"
# tbl-name = "table-name1"

# Specify the db.table to be replicated. Support regular expressions. Start with '~' to use regular expressions.
# [[replicate-do-table]]
# db-name ="test"
# tbl-name = "~^a.*"

# Specify the database table you want to ignore in replication.
# db-name and tbl-name do not support the `db-name ="dbname, dbname2"` format.
# [[replicate-ignore-table]]
# db-name = "your_db"
# tbl-name = "your_table"

# Specify the database table you want to ignore in replication. Support regular expressions. Start with '~' to use regular expressions.
# [[replicate-ignore-table]]
# db-name ="test"
# tbl-name = "~^a.*"

# The sharding replicating rules support wildcharacter.
# 1. The asterisk character ("*", also called "star") matches zero or more characters,
#    For example, "doc*" matches "doc" and "document" but not "dodo";
#    The asterisk character must be in the end of the wildcard word,
#    and there is only one asterisk in one wildcard word.
# 2. The question mark ("?") matches any single character.
# [[route-rules]]
# pattern-schema = "route_*"
# pattern-table = "abc_*"
# target-schema = "route"
# target-table = "abc"

# [[route-rules]]
# pattern-schema = "route_*"
# pattern-table = "xyz_*"
# target-schema = "route"
# target-table = "xyz"

[from]
host = "127.0.0.1"
user = "root"
password = ""
port = 3306

[to]
host = "127.0.0.1"
user = "root"
password = ""
port = 4000
```

**Start syncer**
```
./bin/syncer -config config.toml
```


**you can check data synced by running**

```
mysql -h 127.0.0.1 -P 4000 -u root -e 'select * from CreatedOrderLog' TransactionLog | tail
```

and metadata in symcer.meta already change

```
binlog-name = "mysql-bin.000001"
binlog-pos = 760396
binlog-gtid = ""
```

## Using DM (Data Migration)