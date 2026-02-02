import http from 'k6/http';
import { check } from 'k6';

// Token cache (per VU)
let cachedToken = null;
let tokenExpiry = 0;

/**
 * Login and get JWT token
 * @param {string} baseUrl - Auth service base URL
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {string|null} JWT token or null on failure
 */
export function login(baseUrl, email, password) {
  // Return cached token if still valid (with 1 minute buffer)
  if (cachedToken && Date.now() < tokenExpiry - 60000) {
    return cachedToken;
  }

  const loginUrl = `${baseUrl}/api/auth/login`;
  const payload = JSON.stringify({ email, password });

  const res = http.post(loginUrl, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
    timeout: '10s',
  });

  const success = check(res, {
    'login: status is 200': (r) => r.status === 200,
    'login: has token': (r) => {
      return r.headers['Authorization'] && r.headers['Authorization'].length > 0;
    },
  });

  if (success) {
    cachedToken = res.headers['Authorization'];
    // Assume 1 hour expiry, cache for 55 minutes
    tokenExpiry = Date.now() + 55 * 60 * 1000;
    return cachedToken;
  }

  return null;
}

/**
 * Get authorization headers with token
 * @param {string} token - JWT token
 * @returns {Object} Headers object
 */
export function authHeaders(token) {
  return {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
  };
}

/**
 * Clear cached token (for testing different users)
 */
export function clearTokenCache() {
  cachedToken = null;
  tokenExpiry = 0;
}

/**
 * Login and return headers ready for use
 * @param {string} baseUrl - Auth service base URL
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Object|null} Headers object or null on failure
 */
export function loginAndGetHeaders(baseUrl, email, password) {
  const token = login(baseUrl, email, password);
  return token ? authHeaders(token) : null;
}

/**
 * Login multiple users and collect their tokens
 * Called in setup() to prepare tokens for all VUs
 * @param {string} baseUrl - Auth service base URL
 * @param {Array<{email: string, password: string}>} users - Array of user credentials
 * @returns {string[]} Array of JWT tokens (null entries for failed logins are filtered out)
 */
export function loginMultipleUsers(baseUrl, users) {
  const tokens = [];
  let successCount = 0;

  for (const user of users) {
    const loginUrl = `${baseUrl}/api/auth/login`;
    const payload = JSON.stringify({ email: user.email, password: user.password });

    const res = http.post(loginUrl, payload, {
      headers: { 'Content-Type': 'application/json' },
      timeout: '10s',
    });

    if (res.status === 200 && res.headers['Authorization']) {
      tokens.push(res.headers['Authorization']);
      successCount++;
    } else {
      tokens.push(null);
    }
  }

  // Filter out null tokens
  const validTokens = tokens.filter((t) => t !== null);
  console.log(`Multi-user login: ${successCount} succeeded out of ${users.length}`);

  if (validTokens.length === 0) {
    throw new Error('No users could be authenticated');
  }

  return validTokens;
}
