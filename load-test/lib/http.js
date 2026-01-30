import http from 'k6/http';
import { check } from 'k6';

/**
 * POST JSON request with standard headers
 * @param {string} url - Full URL
 * @param {Object} payload - Request body
 * @param {Object} options - Additional options (headers, etc)
 * @returns {Object} k6 response
 */
export function postJson(url, payload, options = {}) {
  const params = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };
  delete params.headers;
  return http.post(url, JSON.stringify(payload), {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    timeout: options.timeout || '30s',
  });
}

/**
 * GET JSON request with standard headers
 * @param {string} url - Full URL
 * @param {Object} options - Additional options (headers, etc)
 * @returns {Object} k6 response
 */
export function getJson(url, options = {}) {
  return http.get(url, {
    headers: {
      'Accept': 'application/json',
      ...options.headers,
    },
    timeout: options.timeout || '30s',
  });
}

/**
 * Add authorization header to options
 * @param {string} token - JWT token
 * @returns {Object} Headers object with Authorization
 */
export function withAuth(token) {
  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
}

/**
 * Standard API response check
 * @param {Object} res - k6 response
 * @param {string} name - Check name prefix
 * @returns {boolean} All checks passed
 */
export function checkApiResponse(res, name = 'API') {
  return check(res, {
    [`${name}: status is 200`]: (r) => r.status === 200,
    [`${name}: response time < 1s`]: (r) => r.timings.duration < 1000,
    [`${name}: has response body`]: (r) => r.body && r.body.length > 0,
  });
}

/**
 * Build full URL from base and path
 * @param {string} baseUrl - Base URL (e.g., http://localhost:8081)
 * @param {string} path - API path (e.g., /api/users)
 * @returns {string} Full URL
 */
export function buildUrl(baseUrl, path) {
  const base = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
  const pathPart = path.startsWith('/') ? path : `/${path}`;
  return `${base}${pathPart}`;
}
