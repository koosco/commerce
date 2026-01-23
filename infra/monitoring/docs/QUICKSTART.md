# 🚀 Grafana 빠른 시작 가이드

## ✅ 현재 상태

```
✅ Prometheus    - http://localhost:9090
✅ Grafana       - http://localhost:3000
✅ Node Exporter - http://localhost:9100
```

---

## 📊 Step 1: Grafana 접속 (1분)

### 브라우저에서 접속

```
URL: http://localhost:3000
Username: admin
Password: admin123
```

---

## 🎨 Step 2: 대시보드 Import (3분)

### 2-1. Node Exporter Full (인프라 모니터링)

**가장 먼저 Import!** - 로컬 컴퓨터의 CPU, 메모리, 디스크 확인

1. Grafana 좌측 메뉴 → **Dashboards**
2. 우측 상단 **New** → **Import** 클릭
3. **Import via grafana.com** 입력란에 `1860` 입력
4. **Load** 클릭
5. 설정 화면에서:
   - **Name**: Node Exporter Full (그대로)
   - **Folder**: Infrastructure (새로 만들기)
   - **Prometheus**: Prometheus 선택
6. **Import** 클릭

**결과**: 즉시 로컬 컴퓨터의 메트릭 확인 가능

- CPU 사용률
- 메모리 사용량
- 디스크 I/O
- 네트워크 트래픽

### 2-2. Spring Boot Monitor (애플리케이션)

**나중에 마이크로서비스 실행 시 사용**

1. Dashboards → Import
2. Dashboard ID: `11378`
3. Load
4. Folder: Application
5. Import

### 2-3. JVM Micrometer (JVM 상세)

**JVM 메모리 분석용**

1. Dashboards → Import
2. Dashboard ID: `4701`
3. Load
4. Folder: Application
5. Import

---

## 🔍 Step 3: 메트릭 확인

### 방법 1: Grafana 대시보드

```
1. Grafana → Dashboards
2. "Node Exporter Full" 클릭
3. 실시간 메트릭 확인!
```

**확인 가능한 정보**:

- CPU 코어별 사용률
- 메모리 사용량 (사용/캐시/버퍼)
- 디스크 읽기/쓰기 속도
- 네트워크 송수신량
- 시스템 부하 (Load Average)

### 방법 2: Prometheus 직접 쿼리

```
1. http://localhost:9090 접속
2. 검색창에 쿼리 입력:
   - node_cpu_seconds_total
   - node_memory_MemAvailable_bytes
   - node_disk_io_time_seconds_total
3. Execute 클릭
```

---

## 🎯 현재 수집 중인 메트릭

### ✅ 정상 수집 중

- **Prometheus 자체**: 메모리, 쿼리 성능
- **Node Exporter**: 로컬 컴퓨터 시스템 메트릭

### ⏳ 대기 중 (서비스 실행 필요)

- **Auth Service** (8089)
- **User Service** (8081)
- **Catalog Service** (8084)
- **Inventory Service** (8083)
- **Order Service** (8085)
- **Payment Service** (8087)

---

## 🚀 다음 단계: 마이크로서비스 연동

### Spring Boot 애플리케이션 설정

**1. 의존성 추가 (build.gradle)**

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

**2. 설정 추가 (application.yml)**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info,metrics
  metrics:
    export:
      prometheus:
        enabled: true
```

**3. 애플리케이션 재시작**

```bash
./gradlew bootRun
```

**4. 메트릭 확인**

```bash
curl http://localhost:8089/actuator/prometheus
```

**5. Prometheus에서 자동 수집 시작!**

- Grafana에서 즉시 확인 가능
- Spring Boot Monitor 대시보드에서 서비스 선택

---

## 🎨 대시보드 커스터마이징

### 즐겨찾기 설정

```
1. 자주 보는 대시보드 열기
2. 우측 상단 ⭐ 클릭
3. Home 화면 → Starred 섹션에 표시
```

### 시간 범위 변경

```
우측 상단 시계 아이콘 클릭
- Last 15 minutes (실시간 모니터링)
- Last 1 hour (트렌드 확인)
- Last 6 hours (장기 추세)
```

### Auto Refresh 설정

```
우측 상단 새로고침 아이콘
- 5s, 10s, 30s, 1m 등 선택
- 실시간 모니터링 시 유용
```

---

## 🐛 문제 해결

### "No data" 표시되는 경우

**1. Prometheus 데이터소스 확인**

```
Configuration → Data Sources → Prometheus
- URL: http://prometheus:9090
- Access: Server (default)
- Save & Test 클릭
```

**2. Prometheus 타겟 확인**

```
http://localhost:9090/targets
모든 타겟이 UP 상태인지 확인
```

**3. 시간 범위 확인**

```
대시보드 우측 상단 시간 선택기
- Last 1 hour로 변경
```

### 대시보드가 느린 경우

**1. 시간 범위 축소**

```
Last 24 hours → Last 1 hour
```

**2. Auto Refresh 간격 증가**

```
5s → 30s 또는 1m
```

**3. 패널 수 줄이기**

```
불필요한 패널 숨기기 또는 제거
```

---

## 📱 유용한 팁

### 1. Kiosk 모드 (TV 모니터용)

```
URL에 ?kiosk 추가:
http://localhost:3000/d/rYdddlPWk/node-exporter-full?kiosk

메뉴 없이 전체 화면 대시보드
```

### 2. Snapshot 공유

```
대시보드 우측 상단 Share → Snapshot
현재 상태 캡처하여 URL로 공유
```

### 3. 알림 설정

```
패널 Edit → Alert 탭
CPU > 80% 등 조건 설정
Email/Slack 알림
```

### 4. 여러 대시보드 순환 재생

```
Playlists → New Playlist
- Node Exporter (30초)
- Spring Boot (30초)
- JVM (30초)
TV 모니터에 표시
```

---

## ✅ 체크리스트

완료 후 확인:

### Grafana 접속

- [ ] http://localhost:3000 접속
- [ ] admin/admin 로그인
- [ ] 비밀번호 변경 Skip

### 대시보드 Import

- [ ] Node Exporter Full (1860)
- [ ] Spring Boot Monitor (11378)
- [ ] JVM Micrometer (4701)

### 메트릭 확인

- [ ] Node Exporter에서 CPU 사용률 확인
- [ ] 메모리 사용량 확인
- [ ] 실시간 데이터 업데이트 확인

### 추가 설정

- [ ] 즐겨찾기 등록
- [ ] Auto Refresh 10s 설정
- [ ] 시간 범위 Last 15 minutes

---

## 🎉 완료!

이제 로컬 환경에서 실시간 모니터링이 가능합니다!

**다음 단계**:

1. 마이크로서비스에 Actuator 추가
2. 운영 서버에 Node Exporter 설치
3. 커스텀 대시보드 생성

**문서 참고**:

- `docs/import-dashboards.md` - 상세 Import 가이드
- `docs/dashboard-strategy.md` - 대시보드 전략
- `docs/troubleshooting.md` - 문제 해결
