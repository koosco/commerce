# Test Data Setup

This directory contains scripts to set up test data before running load tests.

## Prerequisites

- Target environment services must be running
- User-service and auth-service must be accessible

## Usage

```bash
# Setup for local environment (default)
k6 run scripts/setup/create-test-data.js

# Setup for prod environment
ENV=prod k6 run scripts/setup/create-test-data.js
```

## What it creates

1. **Test Users** (5 users)
   - loadtest1@example.com through loadtest5@example.com
   - All with password: Test@1234

2. **Verification**
   - Verifies login works with first test user

## Notes

- Run this script ONCE before running other load tests
- If users already exist, they will be skipped
- The script is idempotent - safe to run multiple times
