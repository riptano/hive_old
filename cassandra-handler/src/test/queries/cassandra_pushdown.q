SET hive.support.concurrency=false;

DROP TABLE cassandra_hive_pushdown;
CREATE EXTERNAL TABLE cassandra_hive_pushdown(key int, value string)
STORED BY 'org.apache.hadoop.hive.cassandra.CassandraStorageHandler'
WITH SERDEPROPERTIES ("cassandra.columns.mapping" = ":key,value" , "cassandra.cf.name" = "pushdown" , "cassandra.host" = "127.0.0.1" , "cassandra.port" = "9160", "cassandra.partitioner" = "org.apache.cassandra.dht.RandomPartitioner" )
TBLPROPERTIES ("cassandra.ks.name" = "Hive", "cassandra.ks.repfactor" = "1", "cassandra.ks.strategy" = "org.apache.cassandra.locator.SimpleStrategy", "cassandra.indexed.columns" = "value");

INSERT OVERWRITE TABLE cassandra_hive_pushdown
SELECT *
FROM src;

-- This would not be handled by pushdown yet since key is a rowkey. 
explain select * from cassandra_hive_pushdown where key=90;
select * from cassandra_hive_pushdown where key=90;

-- value is indexed and pushdown should work
explain select * from cassandra_hive_pushdown where value='val_90';
select * from cassandra_hive_pushdown where value='val_90';

DROP TABLE cassandra_hive_pushdown;