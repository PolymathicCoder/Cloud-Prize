Edit this page to describe your Submission.

## Which Categories Best Fit Your Submission and Why?

__Best datastore integration__

Avempace is POJO-based Java framework that extends and hides the complexity the AWS DynamoDB API. The motivation behind it is to enable and trivilize advanced cloud features like cross-region support, distributed transaction supporting, batching, etc…

## Describe your Submission

### Features

* __Annotation-Driven Mapping:__ Easily define mapping between PJOs and DynamoDB tables through a set of annotations.
* __Automatic schema generation__
* __Repository Interface:__ Providing a simple and statically-typed interface on your POJOs to support CRUD operations.
* __Qureying Crireria API:__ A simple and concise DSL.
* __Intelligent Qureying:__ No need to know what DynamoDB operation you should use. Let the framework decide the best way for you. It will query by range, by LSI, or fall back to a simple scan.
* __Multi-Region Support:__ cross-region propagation and location-aware querying. the framework will persist your entities across tables in multiple-regeion and will always query data from the closest region to your location using your public IP address.
* __Automatic Data Serialization:__ Complex type are automatically serialized/deserialized to and from JSON and Avro binary.

### Upcomming Features (In order & real soon)

* Much-needed error checking and better validation
* Pagination
* Conditional Writes
* Optimistic locking support
* Intelligent batching
* Plugable Caching
* Transaction Support
* A SQL-like Query Language
* Advice and Performnce Recommendations

## Provide Links to Github Repo's for your Submission
https://github.com/PolymathicCoder/Cloud-Prize
