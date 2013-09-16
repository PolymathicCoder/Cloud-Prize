SET dynamodb.throughput.write.percent = 1.0;
SET dynamodb.throughput.read.percent = 1.0;
SET dynamodb.endpoint=dynamodb.eu-west-1.amazonaws.com;
SET hive.base.inputformat=org.apache.hadoop.hive.ql.io.HiveInputFormat;
SET mapred.map.tasks = 100;
SET mapred.reduce.tasks=20;
SET hive.exec.reducers.max = 100;
SET hive.exec.reducers.min = 50;

# old dynamo table
CREATE EXTERNAL TABLE hive_tbl_dynamodb_$dynamodb-old-message-tbl
(
hive_hash_key STRING,
hive_range_key STRING,
hive_conversation_id STRING,
hive_message_bundle_id STRING,
hive_message_id STRING,
hive_in_reply_to_message_bundle_id STRING,
hive_owning_user_cname STRING,
hive_exchange_type STRING,
hive_category STRING,
hive_body STRING,
hive_timestamp BIGINT,
hive_from_user_cname STRING,
hive_to_users_cnames  ARRAY<STRING>,
hive_number_of_to_users BIGINT,
hive_labels STRING,
hive_authoring_data_geo_longitude DOUBLE,
hive_authoring_data_geo_latitude DOUBLE,
hive_authoring_data_date BIGINT,
hive_authoring_data_language STRING,
hive_is_media_attached STRING
)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES ("dynamodb.table.name" = "$dynamodb-old-message-tbl",
"dynamodb.column.mapping" = "hive_hash_key:key,hive_range_key:range,hive_conversation_id:conversation_id,hive_message_bundle_id:id_of_original,hive_message_id:message_id,hive_in_reply_to_message_bundle_id:id_of_replied_to,hive_owning_user_cname:owning_user_cname,hive_exchange_type:exchange_type,hive_category:category,hive_body:body,hive_timestamp:timestamp,hive_from_user_cname:from_user_cname,hive_to_users_cnames:to_users_cnames,hive_number_of_to_users:number_of_to_users,hive_labels:labels,hive_authoring_data_geo_longitude:longitude,hive_authoring_data_geo_latitude:latitude,hive_authoring_data_date:date,hive_authoring_data_language:language,hive_is_media_attached:is_media_attached");

# s3
CREATE EXTERNAL TABLE hive_tbl_s3
(
hive_hash_key STRING,
hive_range_key BIGINT,
hive_conversation_id STRING,
hive_message_bundle_id STRING,
hive_message_id STRING,
hive_in_reply_to_message_bundle_id STRING,
hive_owning_user_cname STRING,
hive_exchange_type STRING,
hive_category STRING,
hive_body STRING,
hive_timestamp BIGINT,
hive_from_user_cname STRING,
hive_to_users_cnames  ARRAY<STRING>,
hive_number_of_to_users BIGINT,
hive_labels STRING,
hive_authoring_data_geo_longitude DOUBLE,
hive_authoring_data_geo_latitude DOUBLE,
hive_authoring_data_date BIGINT,
hive_authoring_data_language STRING,
hive_is_media_attached STRING
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\;'
LOCATION '$s3-message-file';

# from old dynamodb to s3
INSERT OVERWRITE TABLE hive_tbl_s3 SELECT
hive_hash_key,
cast(hive_range_key as BIGINT),
hive_conversation_id,
hive_message_bundle_id,
hive_message_id,
hive_in_reply_to_message_bundle_id,
hive_owning_user_cname,
hive_exchange_type,
hive_category,
hive_body,
hive_timestamp,
hive_from_user_cname,
hive_to_users_cnames,
hive_number_of_to_users,
hive_labels,
hive_authoring_data_geo_longitude,
hive_authoring_data_geo_latitude,
hive_authoring_data_date,
hive_authoring_data_language,
hive_is_media_attached
FROM hive_tbl_dynamodb_$dynamodb-old-message-tbl;

CREATE TABLE hive_tbl_temp
(
hive_hash_key STRING,
hive_range_key BIGINT,
hive_conversation_id STRING,
hive_message_bundle_id STRING,
hive_message_id STRING,
hive_in_reply_to_message_bundle_id STRING,
hive_owning_user_cname STRING,
hive_exchange_type STRING,
hive_category STRING,
hive_body STRING,
hive_timestamp BIGINT,
hive_from_user_cname STRING,
hive_to_users_cnames  ARRAY<STRING>,
hive_number_of_to_users BIGINT,
hive_labels STRING,
hive_authoring_data_geo_longitude DOUBLE,
hive_authoring_data_geo_latitude DOUBLE,
hive_authoring_data_date BIGINT,
hive_authoring_data_language STRING,
hive_is_media_attached STRING
);

INSERT OVERWRITE TABLE hive_tbl_temp SELECT
hive_hash_key,
cast(hive_range_key as BIGINT),
hive_conversation_id,
hive_message_bundle_id,
hive_message_id,
hive_in_reply_to_message_bundle_id,
hive_owning_user_cname,
hive_exchange_type,
hive_category,
hive_body,
hive_timestamp,
hive_from_user_cname,
hive_to_users_cnames,
hive_number_of_to_users,
hive_labels,
hive_authoring_data_geo_longitude,
hive_authoring_data_geo_latitude,
hive_authoring_data_date,
hive_authoring_data_language,
hive_is_media_attached
FROM hive_tbl_dynamodb_$dynamodb-old-message-tbl;

# new dynamo table
CREATE EXTERNAL TABLE hive_tbl_dynamodb_$dynamodb-new-message-tbl
(
hive_hash_key STRING,
hive_range_key BIGINT,
hive_conversation_id STRING,
hive_message_bundle_id STRING,
hive_message_id STRING,
hive_in_reply_to_message_bundle_id STRING,
hive_owning_user_cname STRING,
hive_exchange_type STRING,
hive_category STRING,
hive_body STRING,
hive_timestamp BIGINT,
hive_from_user_cname STRING,
hive_to_users_cnames  ARRAY<STRING>,
hive_number_of_to_users BIGINT,
hive_labels STRING,
hive_authoring_data_geo_longitude DOUBLE,
hive_authoring_data_geo_latitude DOUBLE,
hive_authoring_data_date BIGINT,
hive_authoring_data_language STRING,
hive_is_media_attached STRING
)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES ("dynamodb.table.name" = "$dynamodb-new-message-tbl",
"dynamodb.column.mapping" = "hive_hash_key:hash,hive_range_key:range,hive_conversation_id:conversation_id,hive_message_bundle_id:message_bundle_id,hive_message_id:message_id,hive_in_reply_to_message_bundle_id:in_reply_to_message_bundle_id,hive_owning_user_cname:owning_user_cname,hive_exchange_type:exchange_type,hive_category:category,hive_body:body,hive_timestamp:timestamp,hive_from_user_cname:from_user_cname,hive_to_users_cnames:to_users_cnames,hive_number_of_to_users:number_of_to_users,hive_labels:labels,hive_authoring_data_geo_longitude:authoring_data_geo_longitude,hive_authoring_data_geo_latitude:authoring_data_geo_latitude,hive_authoring_data_date:authoring_data_date,hive_authoring_data_language:authoring_data_language,hive_is_media_attached:is_media_attached");

# from s3 to new dynamodb
INSERT OVERWRITE TABLE hive_tbl_dynamodb_$dynamodb-new-message-tbl SELECT * FROM hive_tbl_s3;
