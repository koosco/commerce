# load-test/CLAUDE.md

This file provides guidance for Claude Code (claude.ai/code) when working with load testing code under the `load-test/` directory.

## Purpose

The purpose of load testing in this project is not to achieve maximum throughput, but to:
- Observe system behavior under different traffic levels
- Identify bottlenecks and failure points in a distributed environment
- Validate assumptions about performance, stability, and scalability
- Practice iterative performance tuning based on real metrics

Load tests are designed as **intentional, user-triggered experiments**, not automated CI steps.

---

## Execution Rules (IMPORTANT)

- Load tests must NOT be executed automatically.
- Load tests must only be triggered explicitly by the user.
- Do NOT add load tests to CI/CD pipelines.
- Be mindful of resource usage, especially in shared or production-like environments.

---

## Load Test Structure

Load tests are organized using a **three-stage approach**:

```
Smoke Test → Baseline Test → Stress Test
```


Each stage serves a distinct purpose and should be executed in order.

---

## 1. Smoke Test

### Purpose
- Verify that the system is reachable and functional under minimal load
- Catch obvious errors before running heavier tests

### Characteristics
- Very low concurrency
- Short duration
- Focus on correctness, not performance

### Typical Configuration
- Virtual Users (VUs): 1–2
- Duration: ~30 seconds

### Expectations
- Error rate: 0%
- Requests should complete successfully
- No abnormal logs or crashes

---

## 2. Baseline Test

### Purpose
- Establish a performance baseline under expected, normal load
- Measure typical response times and resource usage

### Characteristics
- Moderate concurrency
- Sustained duration
- Represents “normal traffic”

### Typical Configuration
- Virtual Users (VUs): 20–50
- Duration: 5–10 minutes

### Expectations
- Stable response times
- Error rate within acceptable limits
- System remains responsive and healthy

Baseline results are used as a reference point for future comparisons.

---

## 3. Stress Test

### Purpose
- Identify system limits and failure behavior
- Observe how the system degrades under excessive load

### Characteristics
- High concurrency
- Longer duration
- Intentionally pushes the system beyond normal capacity

### Typical Configuration
- Virtual Users (VUs): 100+
- Duration: 15–30 minutes

### Expectations
- Increased latency is acceptable
- Errors may occur, but should be explainable
- System should fail gracefully (no cascading failures or data corruption)

Stress tests are primarily exploratory and diagnostic.

---

## Metrics & Observability

### Metrics Collection
- Load tests are executed using k6
- Metrics are exported to Prometheus

### Visualization
- Test results can be analyzed using Grafana dashboards
- Key metrics to observe:
    - Response time (P50 / P95 / P99)
    - Error rate
    - Throughput (RPS)
    - CPU / Memory usage
    - Kafka lag and consumer behavior (if applicable)

Grafana is the primary tool for interpreting load test results.

---

## Guidelines for Modification

- Keep load test scenarios simple and focused
- Avoid unrealistic traffic patterns unless explicitly testing edge cases
- Do not hardcode environment-specific values
- Prefer configuration via environment variables or parameters

---

## Summary

Load testing in this project is a deliberate learning tool.
Each test stage serves a specific purpose, and results should be analyzed through Grafana to understand system behavior, not just raw performance numbers.

