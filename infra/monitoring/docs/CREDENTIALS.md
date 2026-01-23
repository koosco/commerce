# 🔐 모니터링 스택 접속 정보

## 로컬 개발 환경

### Grafana
```
URL:      http://localhost:3000
Username: admin
Password: admin123
```

### Prometheus
```
URL: http://localhost:9090
인증: 없음 (로컬 환경)
```

### Node Exporter
```
URL: http://localhost:9100/metrics
인증: 없음
```

---

## 운영 환경

### Grafana
```
URL:      https://monitoring.yourdomain.com (또는 서버 IP:3000)
Username: admin
Password: [.env.prod 파일에서 설정]
```

**⚠️ 보안 주의사항**:
- 운영 환경에서는 반드시 강력한 비밀번호 사용
- `.env.prod` 파일은 Git에 커밋하지 않음 (.gitignore에 포함됨)
- 정기적으로 비밀번호 변경 권장

### Prometheus
```
URL: http://server-ip:9090 (리버스 프록시를 통해 접근)
인증: Nginx/Traefik에서 설정
```

---

## 비밀번호 변경 방법

### Grafana 비밀번호 변경

#### 방법 1: UI에서 변경
```
1. Grafana 로그인
2. 좌측 하단 프로필 아이콘 클릭
3. "Change password" 선택
4. 현재 비밀번호: admin123
5. 새 비밀번호 입력
6. Save
```

#### 방법 2: 환경 변수 변경
```bash
# docker-compose.override.yml 수정
environment:
  - GF_SECURITY_ADMIN_PASSWORD=new_password

# 컨테이너 재시작
docker-compose restart grafana
```

#### 방법 3: CLI로 변경
```bash
# Grafana 컨테이너 내부에서
docker exec -it grafana grafana-cli admin reset-admin-password new_password
```

---

## API 토큰 생성 (자동화용)

### Grafana API Key
```
1. Grafana → Configuration → API Keys
2. "New API Key" 클릭
3. Name: monitoring-automation
4. Role: Admin (또는 필요한 권한)
5. Add 클릭
6. 생성된 키 안전하게 보관
```

**사용 예시**:
```bash
# API로 대시보드 목록 조회
curl -H "Authorization: Bearer YOUR_API_KEY" \
     http://localhost:3000/api/search

# 데이터소스 테스트
curl -H "Authorization: Bearer YOUR_API_KEY" \
     http://localhost:3000/api/datasources/1
```

---

## 보안 체크리스트

### 필수 보안 설정
- [ ] 기본 비밀번호 변경 (admin → 강력한 비밀번호)
- [ ] `.env.prod` 파일 Git 제외 (.gitignore 확인)
- [ ] HTTPS 설정 (운영 환경)
- [ ] 방화벽 규칙 설정 (필요한 포트만 오픈)
- [ ] 정기적인 비밀번호 변경 (3개월마다)

### 권장 보안 설정
- [ ] 2FA (Two-Factor Authentication) 활성화
- [ ] Session timeout 설정
- [ ] IP 화이트리스트 설정
- [ ] 감사 로그 활성화
- [ ] 정기적인 보안 업데이트

---

## 사용자 관리

### 새 사용자 추가
```
1. Grafana → Configuration → Users
2. "New user" 클릭
3. 정보 입력:
   - Name: 사용자 이름
   - Email: 이메일
   - Username: 로그인 ID
   - Password: 초기 비밀번호
4. Create user
```

### 권한 관리
```
Viewer: 대시보드만 볼 수 있음
Editor: 대시보드 수정 가능
Admin: 모든 설정 가능
```

---

## 비상 접근

### Admin 비밀번호 분실 시

**방법 1: 컨테이너 재생성**
```bash
# 환경 변수로 비밀번호 재설정
docker-compose down
# docker-compose.override.yml에서 비밀번호 변경
docker-compose up -d
```

**방법 2: Grafana CLI 사용**
```bash
# 컨테이너 내부에서 비밀번호 리셋
docker exec -it grafana grafana-cli admin reset-admin-password newpassword
docker-compose restart grafana
```

**방법 3: SQLite 데이터베이스 직접 수정** (마지막 수단)
```bash
# Grafana 데이터베이스 백업
docker cp grafana:/var/lib/grafana/grafana.db ./grafana.db.backup

# SQLite로 비밀번호 리셋
# (복잡하므로 방법 1, 2 권장)
```

---

## 접속 문제 해결

### "Invalid username or password"
```
1. 비밀번호 확인: admin123 (로컬), .env.prod 확인 (운영)
2. Caps Lock 확인
3. 브라우저 캐시 삭제
4. 시크릿/프라이빗 모드로 접속 시도
```

### "Connection refused"
```
1. 컨테이너 실행 확인: docker-compose ps
2. 포트 확인: curl http://localhost:3000
3. 로그 확인: docker-compose logs grafana
4. 방화벽 확인: sudo ufw status
```

### "Too many login attempts"
```
# 5분 대기 또는 Grafana 재시작
docker-compose restart grafana
```

---

## 환경별 설정 파일

### 로컬 환경
```
파일: docker-compose.override.yml
비밀번호: admin123 (하드코딩)
보안: 낮음 (개발 환경)
```

### 운영 환경
```
파일: .env.prod
비밀번호: ${GF_ADMIN_PASSWORD} (환경 변수)
보안: 높음 (강력한 비밀번호 + HTTPS)
```

---

## 참고 자료

- [Grafana Authentication](https://grafana.com/docs/grafana/latest/setup-grafana/configure-security/)
- [API Keys](https://grafana.com/docs/grafana/latest/developers/http_api/auth/)
- [User Management](https://grafana.com/docs/grafana/latest/administration/user-management/)
