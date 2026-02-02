/**
 * k6 HTML Report Generator
 *
 * 모든 테스트에서 재사용 가능한 HTML 리포트 생성 유틸리티
 */

/**
 * 결과 파일 경로에 타임스탬프를 추가하여 이전 결과를 보존합니다.
 * @param {string} path - 기본 결과 파일 경로 (e.g. 'results/auth/login/smoke.test.result.html')
 * @returns {string} 타임스탬프가 포함된 경로 (e.g. 'results/auth/login/smoke_2026-02-02T08-30-00.html')
 */
export function resultPath(path) {
  const ts = new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-');
  return path.replace('.result.html', `_${ts}.html`);
}

export function generateHTMLReport(data, config = {}) {
  const {
    title = "k6 Load Test Results",
    theme = "default", // default, smoke, baseline, stress
  } = config;

  const metrics = data.metrics;
  const timestamp = new Date().toLocaleString("ko-KR");

  // 테마별 색상
  const themes = {
    default: { primary: "#667eea", secondary: "#764ba2" },
    smoke: { primary: "#a855f7", secondary: "#7c3aed" },
    baseline: { primary: "#4f46e5", secondary: "#7c3aed" },
    stress: { primary: "#dc2626", secondary: "#ea580c" },
  };

  const colors = themes[theme] || themes.default;

  // 메트릭 계산
  const totalRequests = metrics.http_reqs?.values.count || 0;
  const failedRequests = metrics.http_req_failed?.values.count || 0;
  const successRate = totalRequests > 0
    ? ((1 - (metrics.http_req_failed?.values.rate || 0)) * 100).toFixed(2)
    : 0;
  const errorRate = totalRequests > 0
    ? ((failedRequests / totalRequests) * 100).toFixed(2)
    : 0;

  const avgResponseTime = metrics.http_req_duration?.values.avg?.toFixed(2) || 0;
  const p95ResponseTime = metrics.http_req_duration?.values['p(95)']?.toFixed(2) || 0;
  const p99ResponseTime = metrics.http_req_duration?.values['p(99)']?.toFixed(2) || 0;
  const maxResponseTime = metrics.http_req_duration?.values.max?.toFixed(2) || 0;

  const vus = metrics.vus?.values.value || 0;
  const vusMax = metrics.vus_max?.values.max || 0;

  // Checks 계산
  const checksData = data.root_group?.checks || [];
  const totalChecks = checksData.reduce((sum, check) => sum + check.passes + check.fails, 0);
  const passedChecks = checksData.reduce((sum, check) => sum + check.passes, 0);
  const checksPassRate = totalChecks > 0
    ? ((passedChecks / totalChecks) * 100).toFixed(2)
    : 0;

  return `<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${title}</title>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
      min-height: 100vh;
    }

    .container {
      max-width: 1400px;
      margin: 0 auto;
      background: white;
      border-radius: 12px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      overflow: hidden;
    }

    .header {
      background: linear-gradient(135deg, ${colors.primary} 0%, ${colors.secondary} 100%);
      color: white;
      padding: 40px;
      text-align: center;
    }

    .header h1 {
      font-size: 32px;
      margin-bottom: 10px;
    }

    .header .timestamp {
      opacity: 0.9;
      font-size: 14px;
    }

    .content {
      padding: 40px;
    }

    .section {
      margin-bottom: 40px;
    }

    .section h2 {
      font-size: 24px;
      margin-bottom: 20px;
      color: #1f2937;
      border-bottom: 2px solid #e5e7eb;
      padding-bottom: 10px;
    }

    .summary-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 40px;
    }

    .metric-card {
      background: linear-gradient(135deg, ${colors.primary} 0%, ${colors.secondary} 100%);
      color: white;
      padding: 24px;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .metric-card h3 {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 12px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .metric-card .value {
      font-size: 36px;
      font-weight: bold;
      line-height: 1;
    }

    .metric-card .subvalue {
      font-size: 14px;
      opacity: 0.8;
      margin-top: 8px;
    }

    .metrics-table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }

    .metrics-table th,
    .metrics-table td {
      padding: 12px;
      text-align: left;
      border-bottom: 1px solid #e5e7eb;
    }

    .metrics-table th {
      background: #f9fafb;
      font-weight: 600;
      color: #374151;
    }

    .metrics-table tr:hover {
      background: #f9fafb;
    }

    .pass {
      color: #10b981;
      font-weight: bold;
    }

    .fail {
      color: #ef4444;
      font-weight: bold;
    }

    .checks-list {
      list-style: none;
      margin-top: 20px;
    }

    .checks-list li {
      padding: 12px;
      margin-bottom: 8px;
      background: #f9fafb;
      border-radius: 6px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .badge {
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: bold;
    }

    .badge.success {
      background: #d1fae5;
      color: #065f46;
    }

    .badge.error {
      background: #fee2e2;
      color: #991b1b;
    }

    .footer {
      text-align: center;
      padding: 20px;
      background: #f9fafb;
      color: #6b7280;
      font-size: 14px;
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>📊 ${title}</h1>
      <div class="timestamp">Generated at: ${timestamp}</div>
    </div>

    <div class="content">
      <!-- Summary Cards -->
      <div class="section">
        <h2>📈 Summary</h2>
        <div class="summary-grid">
          <div class="metric-card">
            <h3>Total Requests</h3>
            <div class="value">${totalRequests}</div>
            <div class="subvalue">Virtual Users: ${vusMax}</div>
          </div>
          <div class="metric-card">
            <h3>Success Rate</h3>
            <div class="value">${successRate}%</div>
            <div class="subvalue">Failed: ${failedRequests}</div>
          </div>
          <div class="metric-card">
            <h3>Avg Response Time</h3>
            <div class="value">${avgResponseTime} ms</div>
            <div class="subvalue">P95: ${p95ResponseTime} ms</div>
          </div>
          <div class="metric-card">
            <h3>Error Rate</h3>
            <div class="value">${errorRate}%</div>
            <div class="subvalue">Max RT: ${maxResponseTime} ms</div>
          </div>
        </div>
      </div>

      <!-- Performance Metrics -->
      <div class="section">
        <h2>⚡ Performance Metrics</h2>
        <table class="metrics-table">
          <thead>
            <tr>
              <th>Metric</th>
              <th>Avg</th>
              <th>Min</th>
              <th>Med</th>
              <th>Max</th>
              <th>P90</th>
              <th>P95</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><strong>HTTP Request Duration</strong></td>
              <td>${metrics.http_req_duration?.values.avg?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_duration?.values.min?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_duration?.values.med?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_duration?.values.max?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_duration?.values['p(90)']?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_duration?.values['p(95)']?.toFixed(2) || 0} ms</td>
            </tr>
            <tr>
              <td><strong>HTTP Request Waiting</strong></td>
              <td>${metrics.http_req_waiting?.values.avg?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_waiting?.values.min?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_waiting?.values.med?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_waiting?.values.max?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_waiting?.values['p(90)']?.toFixed(2) || 0} ms</td>
              <td>${metrics.http_req_waiting?.values['p(95)']?.toFixed(2) || 0} ms</td>
            </tr>
            <tr>
              <td><strong>Iteration Duration</strong></td>
              <td>${metrics.iteration_duration?.values.avg?.toFixed(2) || 0} ms</td>
              <td>${metrics.iteration_duration?.values.min?.toFixed(2) || 0} ms</td>
              <td>${metrics.iteration_duration?.values.med?.toFixed(2) || 0} ms</td>
              <td>${metrics.iteration_duration?.values.max?.toFixed(2) || 0} ms</td>
              <td>${metrics.iteration_duration?.values['p(90)']?.toFixed(2) || 0} ms</td>
              <td>${metrics.iteration_duration?.values['p(95)']?.toFixed(2) || 0} ms</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Checks -->
      <div class="section">
        <h2>✅ Validation Checks</h2>
        <div class="metric-card" style="display: inline-block; margin-bottom: 20px;">
          <h3>Overall Pass Rate</h3>
          <div class="value">${checksPassRate}%</div>
          <div class="subvalue">${passedChecks} / ${totalChecks} checks passed</div>
        </div>
        <ul class="checks-list">
          ${checksData.map(check => `
            <li>
              <span>${check.name}</span>
              <span>
                <span class="${check.fails === 0 ? 'pass' : 'fail'}">
                  ${check.passes} passed, ${check.fails} failed
                </span>
                <span class="badge ${check.fails === 0 ? 'success' : 'error'}">
                  ${check.fails === 0 ? '✓ PASS' : '✗ FAIL'}
                </span>
              </span>
            </li>
          `).join('')}
        </ul>
      </div>

      <!-- Request Stats -->
      <div class="section">
        <h2>📊 Request Statistics</h2>
        <table class="metrics-table">
          <tbody>
            <tr>
              <td><strong>Total HTTP Requests</strong></td>
              <td>${totalRequests}</td>
            </tr>
            <tr>
              <td><strong>Requests Per Second</strong></td>
              <td>${metrics.http_reqs?.values.rate?.toFixed(2) || 0} req/s</td>
            </tr>
            <tr>
              <td><strong>Total Iterations</strong></td>
              <td>${metrics.iterations?.values.count || 0}</td>
            </tr>
            <tr>
              <td><strong>Data Sent</strong></td>
              <td>${((metrics.data_sent?.values.count || 0) / 1024).toFixed(2)} KB</td>
            </tr>
            <tr>
              <td><strong>Data Received</strong></td>
              <td>${((metrics.data_received?.values.count || 0) / 1024).toFixed(2)} KB</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="footer">
      Generated by k6 Load Testing Tool | ${title}
    </div>
  </div>
</body>
</html>`;
}
