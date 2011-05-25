SET hive.support.concurrency=false;

DROP TABLE cassandra_hive_pushdown;
CREATE EXTERNAL TABLE cassandra_hive_pushdown(key int, value string)
STORED BY 'org.apache.hadoop.hive.cassandra.CassandraStorageHandler'
WITH SERDEPROPERTIES ("cassandra.columns.mapping" = ":key,value" , "cassandra.cf.name" = "pushdown" , "cassandra.host" = "127.0.0.1" , "cassandra.port" = "9160", "cassandra.partitioner" = "org.apache.cassandra.dht.RandomPartitioner" )
TBLPROPERTIES ("cassandra.ks.name" = "Hive", "cassandra.ks.repfactor" = "1", "cassandra.ks.strategy" = "org.apache.cassandra.locator.SimpleStrategy");

INSERT OVERWRITE TABLE cassandra_hive_pushdown
SELECT *
FROM src;

-- with full pushdown
explain select * from cassandra_hive_pushdown where key=90;
select * from cassandra_hive_pushdown where key=90;

DROP TABLE cassandra_hive_pushdown;