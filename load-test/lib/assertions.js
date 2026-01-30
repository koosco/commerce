import { check } from 'k6';

/**
 * Validate standard API response
 * @param {Object} res - k6 response
 * @param {string} context - Context name for check descriptions
 * @returns {boolean} All checks passed
 */
export function validateApiResponse(res, context = 'API') {
  return check(res, {
    [`${context}: status is 200`]: (r) => r.status === 200,
    [`${context}: response time < 1s`]: (r) => r.timings.duration < 1000,
    [`${context}: has response body`]: (r) => r.body && r.body.length > 0,
    [`${context}: content-type is JSON`]: (r) =>
      r.headers['Content-Type'] && r.headers['Content-Type'].includes('application/json'),
  });
}

/**
 * Validate success response (ApiResponse wrapper)
 * @param {Object} res - k6 response
 * @param {string} context - Context name
 * @returns {boolean} All checks passed
 */
export function validateSuccessResponse(res, context = 'API') {
  let body;
  try {
    body = JSON.parse(res.body);
  } catch {
    return check(res, {
      [`${context}: valid JSON body`]: () => false,
    });
  }

  return check(res, {
    [`${context}: status is 200`]: (r) => r.status === 200,
    [`${context}: success is true`]: () => body.success === true,
    [`${context}: has data`]: () => body.data !== undefined,
    [`${context}: has timestamp`]: () => body.timestamp !== undefined,
  });
}

/**
 * Validate paged response
 * @param {Object} res - k6 response
 * @param {string} context - Context name
 * @returns {boolean} All checks passed
 */
export function validatePagedResponse(res, context = 'Paged') {
  let body;
  try {
    body = JSON.parse(res.body);
  } catch {
    return check(res, {
      [`${context}: valid JSON body`]: () => false,
    });
  }

  return check(res, {
    [`${context}: status is 200`]: (r) => r.status === 200,
    [`${context}: success is true`]: () => body.success === true,
    [`${context}: has content array`]: () => Array.isArray(body.data?.content),
    [`${context}: has totalElements`]: () => typeof body.data?.totalElements === 'number',
    [`${context}: has totalPages`]: () => typeof body.data?.totalPages === 'number',
  });
}

/**
 * Validate created response (201)
 * @param {Object} res - k6 response
 * @param {string} context - Context name
 * @returns {boolean} All checks passed
 */
export function validateCreatedResponse(res, context = 'Create') {
  let body;
  try {
    body = JSON.parse(res.body);
  } catch {
    return check(res, {
      [`${context}: valid JSON body`]: () => false,
    });
  }

  return check(res, {
    [`${context}: status is 200 or 201`]: (r) => r.status === 200 || r.status === 201,
    [`${context}: success is true`]: () => body.success === true,
    [`${context}: has data with id`]: () => body.data && (body.data.id !== undefined || body.data.orderId !== undefined),
  });
}

/**
 * Validate error response
 * @param {Object} res - k6 response
 * @param {number} expectedStatus - Expected HTTP status
 * @param {string} context - Context name
 * @returns {boolean} All checks passed
 */
export function validateErrorResponse(res, expectedStatus, context = 'Error') {
  let body;
  try {
    body = JSON.parse(res.body);
  } catch {
    return check(res, {
      [`${context}: valid JSON body`]: () => false,
    });
  }

  return check(res, {
    [`${context}: status is ${expectedStatus}`]: (r) => r.status === expectedStatus,
    [`${context}: success is false`]: () => body.success === false,
    [`${context}: has error code`]: () => body.code !== undefined,
    [`${context}: has error message`]: () => body.message !== undefined,
  });
}
