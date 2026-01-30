/**
 * Smoke Test Thresholds
 * - Basic functionality validation
 * - Lenient thresholds for quick checks
 */
export const smokeThresholds = {
  http_req_duration: ['p(95)<1000', 'avg<500'],
  http_req_failed: ['rate<0.1'], // < 10% error rate
  checks: ['rate>0.9'], // > 90% checks passing
};

/**
 * Baseline Test Thresholds
 * - Performance baseline under normal load
 * - Stricter thresholds for production standards
 */
export const baselineThresholds = {
  http_req_duration: ['p(95)<500', 'p(99)<1000', 'avg<200'],
  http_req_failed: ['rate<0.01'], // < 1% error rate
  checks: ['rate>0.99'], // > 99% checks passing
};

/**
 * Stress Test Thresholds
 * - System limit identification
 * - Moderate thresholds allowing for degradation
 */
export const stressThresholds = {
  http_req_duration: ['p(95)<2000', 'avg<1000'],
  http_req_failed: ['rate<0.05'], // < 5% error rate
  checks: ['rate>0.95'], // > 95% checks passing
};

/**
 * Smoke Test Stages
 * - Quick validation: 2 VUs for 30s
 */
export const smokeStages = [
  { duration: '30s', target: 2 },
];

/**
 * Baseline Test Stages
 * - Ramp up -> Hold -> Ramp down
 * - Total: ~7 minutes
 */
export const baselineStages = [
  { duration: '1m', target: 20 },  // Ramp up
  { duration: '1m', target: 50 },  // Increase to normal load
  { duration: '5m', target: 50 },  // Hold at normal load
];

/**
 * Stress Test Stages
 * - Progressive load increase to find breaking point
 * - Total: ~20 minutes
 */
export const stressStages = [
  { duration: '2m', target: 100 },  // Ramp up
  { duration: '3m', target: 300 },  // Push higher
  { duration: '5m', target: 500 },  // Peak load
  { duration: '5m', target: 500 },  // Hold at peak
  { duration: '3m', target: 200 },  // Scale down
  { duration: '2m', target: 0 },    // Ramp down
];

/**
 * Get options preset by test type
 * @param {string} type - 'smoke' | 'baseline' | 'stress'
 * @returns {Object} k6 options object
 */
export function getOptionsPreset(type) {
  switch (type) {
    case 'smoke':
      return {
        vus: 2,
        duration: '30s',
        thresholds: smokeThresholds,
      };
    case 'baseline':
      return {
        stages: baselineStages,
        thresholds: baselineThresholds,
      };
    case 'stress':
      return {
        stages: stressStages,
        thresholds: stressThresholds,
      };
    default:
      return {
        vus: 1,
        duration: '10s',
        thresholds: smokeThresholds,
      };
  }
}
