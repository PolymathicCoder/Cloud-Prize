CREATE EXTERNAL TABLE $s3HiveTableName
(
#foreach ($hiveColumn in $hiveColumns)
${hiveColumn.hiveColumnName} 
#if (${hiveColumn.hiveColumnType.name().endsWith("_ARRAY")})
ARRAY<${hiveColumn.hiveColumnType}>
#else
${hiveColumn.hiveColumnType}
#end
#if ($foreach.count != ${hiveColumns.size()})
,
#end
#end
)
LOCATION '$s3FileUrl';