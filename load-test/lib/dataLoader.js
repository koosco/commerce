import { SharedArray } from 'k6/data';
import http from 'k6/http';
import { buildUrl } from './http.js';

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
 * Fallback SKU IDs from the database (used when dynamic fetch fails)
 */
export const FALLBACK_SKU_IDS = [
  'f7588581-d517-40fc-bff6-1a7ae66e9ed8',
  '8f335eb5-3722-4a51-98d9-b766b5d5cc0f',
  '2ec46b84-e6ed-4acf-bc24-bd0bd5caaa50',
  '22226e59-c35c-4494-8fa9-4beafeb2026d',
  '5dd08bc5-6865-48f6-918e-898e0af928df',
  'bfcf0c3a-27cc-4eac-aedc-1f9f241a1196',
  'd72a8547-6a0d-46b9-a08a-c2a3fe18e88e',
  '159819bb-4be5-4c24-b654-83d757147c67',
  '5e806992-3007-4f56-b7f2-628cbe79d820',
  '7e012d8a-bf16-4cae-a81b-ce04f206bdb7',
];

/**
 * Load product data from JSON file
 */
export const testProducts = new SharedArray('products', function () {
  try {
    const data = JSON.parse(open('../data/products.json'));
    return Array.isArray(data) ? data : [data];
  } catch {
    // Default test products if file not found
    return [{
      skuIds: FALLBACK_SKU_IDS,
      productIds: [1, 2, 3],
      categoryIds: [1, 2],
    }];
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
 * Prefers dynamically fetched SKU IDs passed as parameter
 * @param {string[]} [dynamicSkuIds] - SKU IDs fetched in setup()
 * @returns {string} SKU ID
 */
export function getRandomSkuId(dynamicSkuIds) {
  if (dynamicSkuIds && dynamicSkuIds.length > 0) {
    return getRandomItem(dynamicSkuIds);
  }
  const products = testProducts[0] || testProducts;
  const skuIds = products.skuIds || [];
  if (skuIds.length === 0) {
    console.warn('No SKU IDs available. Ensure setup() fetches SKUs dynamically.');
    return null;
  }
  return getRandomItem(skuIds);
}

/**
 * Fetch SKU IDs dynamically from catalog API
 * Call this in setup() to get real SKU IDs from the database
 * @param {string} catalogBaseUrl - Catalog service base URL
 * @param {string} productsPath - Products API path
 * @param {string} token - Auth token
 * @param {number} [count=5] - Number of products to fetch SKUs from
 * @returns {string[]} Array of SKU IDs
 */
export function fetchSkuIds(catalogBaseUrl, productsPath, token, count = 5) {
  const skuIds = [];
  const headers = { Authorization: `Bearer ${token}` };

  try {
    const listRes = http.get(
      buildUrl(catalogBaseUrl, `${productsPath}?page=0&size=${count}`),
      { headers },
    );
    const body = JSON.parse(listRes.body);
    if (!body.success || !body.data || !body.data.content) {
      console.warn('Failed to fetch products from catalog, using fallback SKUs');
      return FALLBACK_SKU_IDS.slice(0, count);
    }

    for (const product of body.data.content) {
      const skuRes = http.get(
        buildUrl(catalogBaseUrl, `${productsPath}/${product.id}/skus`),
        { headers },
      );
      const skuBody = JSON.parse(skuRes.body);
      if (skuBody.success && skuBody.data) {
        const skus = Array.isArray(skuBody.data) ? skuBody.data : [skuBody.data];
        for (const sku of skus) {
          if (sku.skuId) {
            skuIds.push(sku.skuId);
          }
        }
      }
    }
  } catch (e) {
    console.warn(`Failed to fetch SKU IDs dynamically: ${e.message}`);
  }

  if (skuIds.length === 0) {
    console.warn('No SKUs found via API, using fallback SKU IDs');
    return FALLBACK_SKU_IDS.slice(0, count);
  }

  return skuIds;
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
