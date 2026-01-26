# Performance Analysis: REST vs GraphQL (Smart E-Commerce System)

## Overview
This report outlines a short, reproducible benchmarking plan and initial observations to compare REST and GraphQL endpoints in this project. Use the outlined commands to run load tests locally against REST and GraphQL endpoints, capture metrics from `/actuator/prometheus`, and compare latency, throughput, and error rates.

## Endpoints to test
- REST: GET /api/v1/products/?page=0&size=20
- REST: GET /api/v1/orders/?page=0&size=20 (if implemented)
- GraphQL: POST /graphql with query: `{ products(page:0,size:20){ content { id name price } } }`

## Tools
- `hey` (https://github.com/rakyll/hey) or `wrk`
- Prometheus + Grafana (optional) or scrape metrics from `/actuator/prometheus`

## Sample commands
- REST (GET products):

```
hey -n 1000 -c 50 "http://localhost:8080/api/v1/products/?page=0&size=20"
```

- GraphQL (query):

```
hey -n 1000 -c 50 -m POST -H "Content-Type: application/json" -d '{"query":"{ products(page:0,size:20){ content { id name price } } }"}' http://localhost:8080/graphql
```

## Metrics to capture
- p50/p90/p99 latency
- requests per second
- error rate
- JVM and GC metrics
- Micrometer metrics (service.execution.time, graphql.execution.time, service.invocations)

## Observations (to collect during test runs)
1. Are GraphQL queries more efficient in returned payload size for comparable data?
2. Does GraphQL add CPU overhead due to query parsing/execution?
3. How do caches and DB query plans affect both endpoints?

## Next steps
- Run tests with different payload sizes and query complexities.
- Integrate Prometheus to capture metrics over time and visualize with Grafana.
- Implement data loaders for GraphQL to avoid N+1 query problems if present.

## Quick Recommendations
- For complex, nested data retrieval prefer GraphQL with batching & data loaders.
- For simple list endpoints prefer REST for lower overhead and caching benefits.

