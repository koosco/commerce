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
