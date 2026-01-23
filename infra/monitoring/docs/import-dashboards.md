# Grafana 대시보드 Import 가이드

## 🚀 빠른 시작 (5분 안에 완료)

### 필수 대시보드 3개 Import

#### 1. Node Exporter Full (인프라 모니터링)

```
1. Grafana 접속: http://localhost:3000
2. 좌측 메뉴 → Dashboards → Import
3. Dashboard ID: 1860 입력
4. Load 클릭
5. Prometheus 데이터소스 선택
6. Import 클릭
```

#### 2. Spring Boot Metrics (애플리케이션 모니터링)

```
Dashboard ID: 11378
※ 이 대시보드는 변수를 지원하여 모든 서비스를 하나의 대시보드에서 확인 가능!
```

#### 3. JVM (Micrometer) (JVM 상세 분석)

```
Dashboard ID: 4701
※ 메모리 누수, GC 문제 디버깅용
```

---

## 📊 추천 대시보드 목록

### ⭐⭐⭐ 필수 (3개)

| ID        | 이름                           | 설명                          | 용도                 |
| --------- | ------------------------------ | ----------------------------- | -------------------- |
| **1860**  | Node Exporter Full             | CPU, 메모리, 디스크, 네트워크 | 서버 인프라 모니터링 |
| **11378** | Spring Boot 2.1 System Monitor | HTTP, JVM, 스레드, 로그백     | 애플리케이션 전반    |
| **4701**  | JVM (Micrometer)               | Heap, GC, Thread Pool         | JVM 상세 분석        |

### ⭐⭐ 권장 (4개)

| ID        | 이름                   | 설명                       | 용도          |
| --------- | ---------------------- | -------------------------- | ------------- |
| **12856** | Spring Boot Statistics | HTTP 요청, DB 커넥션, 캐시 | 성능 최적화   |
| **193**   | Docker & Host Metrics  | 컨테이너 메트릭            | Docker 환경   |
| **6417**  | Spring Boot APM        | 트랜잭션 추적              | 성능 분석     |
| **12707** | Spring Boot Logback    | 로그 레벨별 통계           | 로깅 모니터링 |

### ⭐ 선택 (특정 기술 사용 시)

#### Kafka

| ID       | 이름                    | 설명                       | 용도           |
| -------- | ----------------------- | -------------------------- | -------------- |
| **7589** | Kafka Exporter Overview | Kafka 브로커, 토픽, 파티션 | Kafka 모니터링 |
| **721**  | Kafka Overview          | Consumer Lag 포함          | Kafka 상세     |

#### k6 (성능 테스트)

| ID        | 이름                         | 설명                           | 용도                 |
| --------- | ---------------------------- | ------------------------------ | -------------------- |
| **2587**  | k6 Prometheus                | HTTP 메트릭, VUs, 체크 통과율  | k6 부하 테스트       |
| **18030** | k6 Load Testing Results      | 시나리오별 성능 분석           | 테스트 결과 상세     |
| **19665** | k6 Performance Test          | P95/P99 응답시간, 에러율       | 성능 테스트 종합     |

**k6 사용 예시**:
```bash
# Prometheus remote write로 메트릭 전송
k6 run --out experimental-prometheus-rw script.js
```

---

## 🎨 커스텀 대시보드

### Commerce System Overview

프로젝트 전용 Overview 대시보드 (이미 생성됨)

**Import 방법**:

```bash
# Grafana UI
1. Dashboards → Import
2. "Upload JSON file" 선택
3. grafana/dashboards/system-overview.json 업로드
4. Import
```

**포함 내용**:

- ✅ 6개 서비스 상태 (UP/DOWN)
- 📊 서비스별 요청 처리량 (RPS)
- ⚠️ 에러율 (5xx)
- ⏱️ 응답 시간 (p95)

---

## 🔧 대시보드 Import 상세 가이드

### 방법 1: Dashboard ID로 Import (가장 쉬움)

#### 단계별 과정

```
1. Grafana 좌측 메뉴 → Dashboards
2. "New" 버튼 클릭 → "Import" 선택
3. "Import via grafana.com" 입력란에 Dashboard ID 입력
   예: 1860
4. "Load" 버튼 클릭
5. 설정 확인:
   - Name: 원하는 이름으로 변경 가능
   - Folder: 폴더 선택 (예: Infrastructure)
   - Prometheus: 데이터소스 선택
6. "Import" 버튼 클릭
```

#### 스크린샷 예시

```
┌─────────────────────────────────────┐
│ Import Dashboard                    │
├─────────────────────────────────────┤
│ Import via grafana.com              │
│ ┌─────────────────────────────────┐ │
│ │ 1860                            │ │
│ └─────────────────────────────────┘ │
│         [Load]                      │
└─────────────────────────────────────┘
```

### 방법 2: JSON 파일로 Import

#### 직접 업로드

```
1. Dashboards → Import
2. "Upload JSON file" 클릭
3. 파일 선택:
   - grafana/dashboards/system-overview.json
4. Import 클릭
```

#### URL로 Import

```
1. Dashboards → Import
2. "Import via panel json" 입력란에 JSON 내용 붙여넣기
3. Load
4. Import
```

---

## 📁 대시보드 폴더 구조 권장

```
📁 General (기본)
   └── Commerce System Overview ⭐ 여기!

📁 Infrastructure
   └── Node Exporter Full (1860)
   └── Docker & Host Metrics (193)

📁 Application
   └── Spring Boot Monitor (11378) ⭐ 주로 사용
   └── JVM Micrometer (4701)
   └── Spring Boot Statistics (12856)

📁 Kafka (선택)
   └── Kafka Exporter Overview (7589)
```

---

## ⚙️ Import 후 필수 설정

### 1. 변수 설정 (Spring Boot Monitor)

**왜 필요한가?**

- 하나의 대시보드로 모든 서비스(Auth, User, Order 등) 모니터링

**설정 방법**:

```
1. Spring Boot Monitor 대시보드 열기
2. 우측 상단 Settings (⚙️) 클릭
3. Variables 탭 선택
4. "Add variable" 클릭
5. 설정:
   Name: service
   Type: Custom
   Custom options: auth,user,catalog,inventory,order,payment
6. Save dashboard
```

**사용 방법**:

```
대시보드 상단에 드롭다운이 생성됨:
Service: [Order ▼]

여기서 서비스 선택 → 해당 서비스 메트릭만 표시
```

### 2. 시간 범위 설정

**권장 시간 범위**:

```
- Overview Dashboard: Last 15 minutes (실시간)
- Application Dashboard: Last 1 hour (트렌드)
- Infrastructure Dashboard: Last 6 hours (장기 추세)
```

**설정 방법**:

```
1. 우측 상단 시간 선택기 클릭
2. Quick ranges → "Last 15 minutes" 선택
3. Settings → Time options
   - Timezone: Browser Time
   - Auto refresh: 10s (실시간 모니터링 시)
4. Save dashboard
```

### 3. 알림 설정 (선택)

**Critical 지표 알림**:

```
1. 패널 클릭 → Edit
2. Alert 탭 선택
3. Create Alert 클릭
4. 조건 설정:
   - Metric: Error Rate
   - Condition: WHEN avg() OF query(A) IS ABOVE 5
   - FOR: 5m
5. Notifications 설정:
   - Send to: Email 또는 Slack
6. Save
```

---

## 🎯 대시보드별 활용 시나리오

### Node Exporter Full (1860)

**언제 보는가?**

- 서버 리소스 부족 의심 시
- CPU 100% 또는 메모리 부족
- 디스크 I/O 병목

**주요 패널**:

- CPU Usage: 80% 이상 → 스케일 아웃 고려
- Memory Usage: 90% 이상 → 메모리 증설
- Disk I/O: IOPS 포화 → SSD 업그레이드

### Spring Boot Monitor (11378)

**언제 보는가?**

- 일상적인 애플리케이션 모니터링
- 배포 후 상태 확인
- 성능 이슈 트러블슈팅

**주요 패널**:

- HTTP Request Rate: 트래픽 증가 추세
- HTTP Error Rate: 5xx 에러 발생 시 즉시 확인
- JVM Heap: 메모리 누수 의심 시

### JVM Micrometer (4701)

**언제 보는가?**

- OutOfMemoryError 발생 시
- GC 시간이 길어질 때
- 스레드 풀 고갈 의심 시

**주요 패널**:

- Heap Memory: 톱니 패턴이 정상 (GC 작동 증거)
- GC Pause Time: 1초 이상 → GC 튜닝 필요
- Thread Count: 계속 증가 → 스레드 누수

### k6 Prometheus (2587)

**언제 보는가?**

- 부하 테스트 실행 중/후
- 성능 목표 달성 여부 확인
- 병목 지점 식별

**주요 패널**:

- HTTP Request Rate: 초당 요청 수 (RPS)
- HTTP Request Duration: P95/P99 응답 시간
- HTTP Request Failed: 에러율 (목표: <1%)
- Virtual Users: 동시 사용자 수 추이
- Checks: 테스트 시나리오 통과율

**활용 팁**:

```bash
# k6 스크립트 예시
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  stages: [
    { duration: '1m', target: 100 },  // Ramp-up
    { duration: '3m', target: 100 },  // Stay
    { duration: '1m', target: 0 },    // Ramp-down
  ],
};

export default function () {
  let res = http.get('http://localhost:8080/api/products');
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
}
```

---

## 🐛 트러블슈팅

### 문제 1: "No data" 표시

**원인**: Prometheus에서 메트릭 수집 실패

**해결**:

```bash
# 1. Prometheus 타겟 확인
http://localhost:9090/targets

# 2. 메트릭 수집 확인
curl http://172.31.43.230:8089/actuator/prometheus

# 3. Grafana 데이터소스 테스트
Grafana → Configuration → Data Sources → Prometheus → Test
```

### 문제 2: 변수가 작동 안 함

**원인**: 변수 쿼리가 잘못됨

**해결**:

```
1. Settings → Variables
2. Query 확인:
   - 올바른 예: label_values(up, service)
   - 잘못된 예: service
3. Preview values에서 값 확인
4. Regex 필터 확인
```

### 문제 3: 대시보드가 느림

**원인**: 너무 많은 쿼리 또는 긴 시간 범위

**해결**:

```
1. 시간 범위 축소 (24h → 6h)
2. Refresh interval 증가 (5s → 30s)
3. 불필요한 패널 제거
4. Query 최적화:
   - rate() 대신 irate() 사용 (짧은 시간)
   - sum() by (label) 활용
```

### 문제 4: Import 실패

**원인**: Grafana 버전 불일치

**해결**:

```
1. Grafana 버전 확인: http://localhost:3000/api/health
2. 대시보드 JSON 수동 수정:
   - "version": 1 로 변경
   - "schemaVersion": 현재 버전에 맞게
3. 또는 대시보드 재생성
```

---

## 💡 유용한 팁

### 1. 즐겨찾기 설정

```
자주 보는 대시보드:
- 대시보드 우측 상단 ⭐ 클릭
- Home 화면에서 "Starred" 섹션에 표시
```

### 2. Snapshot 공유

```
대시보드 현재 상태 공유:
1. Share → Snapshot
2. Publish to snapshots.raintank.io
3. URL 복사하여 팀원에게 공유
```

### 3. Playlist 생성

```
여러 대시보드 자동 순환:
1. Playlists → New Playlist
2. Add dashboard
   - System Overview (15초)
   - Infrastructure (30초)
   - Spring Boot Monitor (30초)
3. Start playlist → TV 모드로 표시
```

### 4. Kiosk 모드

```
대형 모니터에 대시보드 표시:
URL에 추가: ?kiosk

예: http://localhost:3000/d/commerce-overview?kiosk

- 메뉴 숨김
- 전체 화면
- TV 모드에 적합
```

---

## 📚 참고 자료

- [Grafana Dashboard Gallery](https://grafana.com/grafana/dashboards/)
- [Dashboard JSON Model](https://grafana.com/docs/grafana/latest/dashboards/json-model/)
- [Variables Documentation](https://grafana.com/docs/grafana/latest/dashboards/variables/)
- [Alerting Documentation](https://grafana.com/docs/grafana/latest/alerting/)

---

## ✅ Import 체크리스트

완료 후 확인:

- [ ] Node Exporter Full (1860) Import
- [ ] Spring Boot Monitor (11378) Import
- [ ] JVM Micrometer (4701) Import
- [ ] Commerce System Overview Import (커스텀)
- [ ] 변수 설정 (service 변수 추가)
- [ ] 시간 범위 설정 (15분)
- [ ] Auto-refresh 설정 (10초)
- [ ] 폴더 정리 (Infrastructure, Application)
- [ ] 즐겨찾기 등록
- [ ] 데이터 표시 확인 (No data 없음)
