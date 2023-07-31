#!/usr/bin/env bash

# Check S3
awslocal --endpoint-url=http://localhost:4566 s3api head-bucket --bucket music-file-bucket ||
awslocal --endpoint-url=http://localhost:4566 s3api create-bucket --bucket music-file-bucket

# Check SQS
awslocal --endpoint-url=http://localhost:4566 sqs get-queue-url --queue-name music-file-queue ||
awslocal --endpoint-url=http://localhost:4566 sqs create-queue --queue-name music-file-queue

awslocal --endpoint-url=http://localhost:4566 sqs get-queue-url --queue-name music-data-queue ||
awslocal --endpoint-url=http://localhost:4566 sqs create-queue --queue-name music-data-queue