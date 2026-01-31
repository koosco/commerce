import http from 'k6/http';
import { check, sleep } from 'k6';
import { config } from '../../config/index.js';
import { buildUrl } from '../../lib/http.js';

/**
 * Test Data Setup Script
 *
 * Purpose: Create test users and seed data before running load tests
 *
 * Usage:
 *   ENV=local k6 run scripts/setup/create-test-data.js
 *   ENV=prod k6 run scripts/setup/create-test-data.js
 *
 * Note: Run this ONCE before running other load tests
 */

export const options = {
  vus: 1,
  iterations: 1, // Run once
};

// Test users to create
const TEST_USERS = [
  { email: 'loadtest1@example.com', password: 'Test@1234', name: 'Load Test User 1' },
  { email: 'loadtest2@example.com', password: 'Test@1234', name: 'Load Test User 2' },
  { email: 'loadtest3@example.com', password: 'Test@1234', name: 'Load Test User 3' },
  { email: 'loadtest4@example.com', password: 'Test@1234', name: 'Load Test User 4' },
  { email: 'loadtest5@example.com', password: 'Test@1234', name: 'Load Test User 5' },
];

export default function () {
  console.log(`Setting up test data for environment: ${config.name}`);
  console.log('---');

  // Create test users
  const userServiceUrl = config.userService;
  const userPath = config.paths.users;

  let successCount = 0;
  let skipCount = 0;
  let errorCount = 0;

  for (const user of TEST_USERS) {
    const url = buildUrl(userServiceUrl, userPath);
    const payload = JSON.stringify({
      email: user.email,
      password: user.password,
      name: user.name,
    });

    const res = http.post(url, payload, {
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: '10s',
    });

    if (res.status === 200 || res.status === 201) {
      console.log(`Created user: ${user.email}`);
      successCount++;
    } else if (res.status === 409 || res.status === 400) {
      // User already exists or validation error
      try {
        const body = JSON.parse(res.body);
        if (body.code && body.code.includes('DUPLICATE')) {
          console.log(`User already exists: ${user.email}`);
          skipCount++;
        } else {
          console.log(`Failed to create user: ${user.email} - ${body.message || res.status}`);
          errorCount++;
        }
      } catch {
        console.log(`User likely exists: ${user.email} (status: ${res.status})`);
        skipCount++;
      }
    } else {
      console.log(`Failed to create user: ${user.email} - Status: ${res.status}`);
      errorCount++;
    }

    sleep(0.5); // Small delay between requests
  }

  console.log('---');
  console.log(`Setup complete: ${successCount} created, ${skipCount} skipped, ${errorCount} errors`);

  // Verify login works for first test user
  console.log('---');
  console.log('Verifying login...');

  const authServiceUrl = config.authService;
  const authPath = config.paths.auth;
  const loginUrl = buildUrl(authServiceUrl, `${authPath}/login`);

  const loginRes = http.post(
    loginUrl,
    JSON.stringify({
      email: TEST_USERS[0].email,
      password: TEST_USERS[0].password,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );

  const loginSuccess = check(loginRes, {
    'login verification: status is 200': (r) => r.status === 200,
    'login verification: has token': (r) => {
      return r.headers['Authorization'] && r.headers['Authorization'].length > 0;
    },
  });

  if (loginSuccess) {
    console.log('Login verification successful');
  } else {
    console.log(`Login verification failed - Status: ${loginRes.status}`);
  }

  console.log('---');
  console.log('Test data setup finished!');
}
