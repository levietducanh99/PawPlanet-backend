# 🔧 Hướng Dẫn Fix Connection Pool - Heroku Essential

## ❌ Vấn Đề Trước Đó

Heroku Essential plan chỉ cho phép **20 connections**, nhưng ứng dụng đang mở:
- **HikariCP mặc định**: 10 connections
- **Flyway runtime**: 2-3 connections
- **JPA + Spring**: 2-3 connections
- **Swagger/OpenAPI**: 1-2 connections

➡️ **Tổng: 15-18 connections** → Vượt quá giới hạn khi có nhiều request

---

## ✅ Giải Pháp Đã Áp Dụng

### 1. **HikariCP Limits** (application.yml)

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5      # Local dev: 5 connections
      minimum-idle: 1
      idle-timeout: 30000
      max-lifetime: 600000
      connection-timeout: 30000
```

### 2. **Production Config** (application-prod.yml)

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 2      # Production: CHỈ 2 connections
      minimum-idle: 0           # Không giữ idle connection
      idle-timeout: 10000       # 10s timeout
      leak-detection-threshold: 2000  # Phát hiện leak sớm
```

### 3. **Tắt Flyway Runtime**

Flyway giờ **KHÔNG tự chạy** khi app start → tránh giữ connection.

---

## 🚀 Cách Sử Dụng

### **Local Development**

```bash
# Chạy migration thủ công (nếu cần)
mvn flyway:migrate

# Start ứng dụng
mvn spring-boot:run
```

### **Heroku Production**

#### **Bước 1: Set Profile**

```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod --app pawplanet
```

#### **Bước 2: Deploy**

```bash
git add .
git commit -m "Fix connection pool for Heroku Essential"
git push heroku main
```

#### **Bước 3: Chạy Migration Thủ Công**

```bash
# Chỉ chạy khi CẦN migrate database
heroku run ./mvnw flyway:migrate --app pawplanet
```

---

## 📊 Monitor Connection Pool

### **Kiểm tra logs HikariCP**

```bash
heroku logs --tail --app pawplanet | grep -i hikari
```

Bạn sẽ thấy:
```
HikariPool-1 - Pool stats (total=2, active=1, idle=1, waiting=0)
```

### **Kiểm tra số connections trên database**

```bash
heroku pg:ps --app pawplanet
```

Nên thấy **< 5 connections** thay vì 17-18.

---

## ⚠️ Lưu Ý Quan Trọng

### ✅ **Local Dev** (application.yml)
- `maximum-pool-size: 5` - Đủ để dev
- `flyway.enabled: false` - Chạy manual: `mvn flyway:migrate`
- `show-sql: false` - Tránh spam log

### ✅ **Production** (application-prod.yml)
- `maximum-pool-size: 2` - **KHÔNG TĂNG** trên Essential plan
- `flyway.enabled: false` - **BẮT BUỘC** tắt
- `leak-detection-threshold: 2000` - Phát hiện leak sớm

### ❌ **KHÔNG Bao Giờ**
- Tăng `maximum-pool-size` > 5 trên Heroku Essential
- Bật `flyway.enabled: true` trên production
- Bật `show-sql: true` trên production

---

## 🐛 Troubleshooting

### **"Connection timeout" error**

➡️ Database đang quá tải hoặc connection pool quá nhỏ.

**Fix:**
```bash
# Restart dyno để giải phóng connections
heroku ps:restart --app pawplanet
```

### **"Too many connections"**

➡️ Có nhiều dynos hoặc app khác dùng chung DB.

**Check:**
```bash
heroku pg:info --app pawplanet
```

### **Migration không chạy**

➡️ Flyway đã bị tắt, phải chạy manual.

**Fix:**
```bash
heroku run ./mvnw flyway:migrate --app pawplanet
```

---

## 📝 Checklist Deploy

- [ ] `SPRING_PROFILES_ACTIVE=prod` đã set
- [ ] `application-prod.yml` có trong commit
- [ ] `maximum-pool-size: 2` trong production config
- [ ] `flyway.enabled: false` trong cả 2 files
- [ ] Deploy lên Heroku
- [ ] Chạy `heroku run ./mvnw flyway:migrate` (nếu cần)
- [ ] Test API: `https://pawplanet.herokuapp.com/health`
- [ ] Monitor logs: `heroku logs --tail`

---

## 🎯 Kết Quả Mong Đợi

- **Số connections trên DB**: < 5 (thay vì 17-18)
- **Response time**: Không thay đổi
- **Errors**: Không có "too many connections"
- **App restart**: Nhanh hơn (không chờ Flyway)

---

**✅ Done!** Connection pool giờ đã được quản lý đúng cách cho Heroku Essential plan.

