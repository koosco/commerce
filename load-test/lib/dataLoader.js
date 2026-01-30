import { SharedArray } from 'k6/data';

/**
 * Load users from JSON file
 * Uses SharedArray for memory efficiency across VUs
 */
export const testUsers = new SharedArray('users', function () {
  try {
    return JSON.parse(open('../data/users.json'));
  } catch {
    // Default test users if file not found
    return [
      { email: 'loadtest1@example.com', password: 'Test@1234', name: 'Load Test User 1' },
      { email: 'loadtest2@example.com', password: 'Test@1234', name: 'Load Test User 2' },
      { email: 'loadtest3@example.com', password: 'Test@1234', name: 'Load Test User 3' },
    ];
  }
});

/**
 * Load product data from JSON file
 */
export const testProducts = new SharedArray('products', function () {
  try {
    return JSON.parse(open('../data/products.json'));
  } catch {
    // Default test products if file not found
    return {
      skuIds: ['00008217-b1ae-4045-9500-2d4b9fffaa32'],
      productIds: [1, 2, 3],
      categoryIds: [1, 2],
    };
  }
});

/**
 * Get random item from array
 * @param {Array} array - Source array
 * @returns {*} Random item
 */
export function getRandomItem(array) {
  return array[Math.floor(Math.random() * array.length)];
}

/**
 * Get item by VU ID (for consistent data per VU)
 * @param {Array} array - Source array
 * @param {number} vuId - Virtual User ID (__VU)
 * @returns {*} Item at vuId index (wraps around)
 */
export function getByVuId(array, vuId) {
  return array[(vuId - 1) % array.length];
}

/**
 * Get user credentials for current VU
 * @param {number} vuId - Virtual User ID (__VU)
 * @returns {Object} User object with email, password, name
 */
export function getUserForVu(vuId) {
  return getByVuId(testUsers, vuId);
}

/**
 * Get random SKU ID for testing
 * @returns {string} SKU ID
 */
export function getRandomSkuId() {
  const products = testProducts[0] || testProducts;
  const skuIds = products.skuIds || ['00008217-b1ae-4045-9500-2d4b9fffaa32'];
  return getRandomItem(skuIds);
}

/**
 * Generate unique email for registration tests
 * @param {number} vuId - Virtual User ID
 * @param {number} iteration - Current iteration
 * @returns {string} Unique email
 */
export function generateUniqueEmail(vuId, iteration) {
  const timestamp = Date.now();
  return `loadtest_vu${vuId}_iter${iteration}_${timestamp}@example.com`;
}
