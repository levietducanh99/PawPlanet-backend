# ⚡ Quick Deploy Checklist - Heroku Production

## 🎯 Trước Khi Deploy

- [ ] **application-prod.yml** đã được tạo với `maximum-pool-size: 2`
- [ ] **application.yml** đã set `flyway.enabled: false`
- [ ] **application.yml** đã set `show-sql: false`
- [ ] Tất cả changes đã commit: `git add .` && `git commit -m "Fix connection pool"`

---

## 🚀 Heroku Setup (Lần Đầu)

```bash
# 1. Login to Heroku
heroku login

# 2. Create app (nếu chưa có)
heroku create pawplanet  # hoặc tên app của bạn

# 3. Add PostgreSQL addon
heroku addons:create heroku-postgresql:essential-0  # hoặc :mini

# 4. Set production profile
heroku config:set SPRING_PROFILES_ACTIVE=prod --app pawplanet

# 5. Set JWT secret
heroku config:set JWT_SECRET=your-production-secret-key-here --app pawplanet

# 6. Verify config
heroku config --app pawplanet
```

**Expected output:**
```
DATABASE_URL:             postgres://u39o0uu0fuvqjr:pf3a6...@c683rl2u9g20vq...
JWT_SECRET:               your-production-secret-key-here
SPRING_PROFILES_ACTIVE:   prod
```

---

## 📦 Deploy

```bash
# 1. Add Heroku remote (nếu chưa có)
heroku git:remote -a pawplanet

# 2. Push to Heroku
git push heroku main
```

**Watch logs:**
```bash
heroku logs --tail --app pawplanet
```

**Expected in logs:**
```
HikariPool-1 - configuration:
HikariPool-1 - maximumPoolSize......................2
HikariPool-1 - minimumIdle..........................0
```

---

## 🗄️ Database Migration

```bash
# Run Flyway migration (ONE-OFF dyno)
heroku run ./mvnw flyway:migrate --app pawplanet
```

**Expected output:**
```
Successfully validated 1 migration
Creating Schema History table [public].[flyway_schema_history] ...
Current version of schema [public]: << Empty Schema >>
Migrating schema [public] to version 1 - init schema
Successfully applied 1 migration to schema [public]
```

---

## ✅ Verify Deployment

### 1. Health Check
```bash
curl https://pawplanet.herokuapp.com/health
```

**Expected:**
```json
{"status":"UP","timestamp":"2025-12-28T..."}
```

### 2. Swagger UI
Truy cập: **https://pawplanet.herokuapp.com/swagger-ui.html**

### 3. Database Connections
```bash
heroku pg:ps --app pawplanet
```

**Expected:** `< 5 connections` (thay vì 17-18)

### 4. Monitor Logs
```bash
heroku logs --tail --app pawplanet | grep -i hikari
```

**Expected:**
```
HikariPool-1 - Pool stats (total=2, active=1, idle=1, waiting=0)
```

---

## 🐛 Common Issues

### ❌ "Connection timeout"
**Cause:** Database quá tải hoặc connection pool quá nhỏ.

**Fix:**
```bash
heroku ps:restart --app pawplanet
heroku pg:info --app pawplanet  # Check DB stats
```

### ❌ "Too many connections"
**Cause:** Nhiều dynos hoặc `maximum-pool-size` quá cao.

**Fix:**
```bash
# Check current connections
heroku pg:ps --app pawplanet

# Restart app
heroku ps:restart --app pawplanet

# Verify SPRING_PROFILES_ACTIVE=prod
heroku config:get SPRING_PROFILES_ACTIVE --app pawplanet
```

### ❌ "Migration failed: table already exists"
**Cause:** Database đã có schema, nhưng Flyway chưa baseline.

**Fix:**
```bash
heroku run ./mvnw flyway:baseline --app pawplanet
heroku run ./mvnw flyway:migrate --app pawplanet
```

### ❌ "Application failed to start"
**Cause:** DATABASE_URL không được set hoặc sai format.

**Fix:**
```bash
# Check DATABASE_URL
heroku config:get DATABASE_URL --app pawplanet

# If empty, add PostgreSQL addon
heroku addons:create heroku-postgresql:essential-0 --app pawplanet
```

---

## 📊 Monitor Production

### View logs
```bash
heroku logs --tail --app pawplanet
```

### Check dyno status
```bash
heroku ps --app pawplanet
```

### Database info
```bash
heroku pg:info --app pawplanet
```

### Database connection count
```bash
heroku pg:ps --app pawplanet
```

### Restart app
```bash
heroku ps:restart --app pawplanet
```

---

## 🔄 Update Production

```bash
# 1. Make changes locally
# 2. Test locally: mvn spring-boot:run
# 3. Commit changes
git add .
git commit -m "Your change description"

# 4. Push to Heroku
git push heroku main

# 5. Watch deployment
heroku logs --tail --app pawplanet
```

---

## 📝 Post-Deploy Checklist

- [ ] Health endpoint returns `UP`: https://pawplanet.herokuapp.com/health
- [ ] Swagger UI accessible: https://pawplanet.herokuapp.com/swagger-ui.html
- [ ] Database connections < 5: `heroku pg:ps`
- [ ] HikariCP shows `maximumPoolSize=2` in logs
- [ ] No "too many connections" errors in logs
- [ ] Flyway migration completed (if needed)
- [ ] API endpoints working correctly

---

**✅ Done!** Your app is now running on Heroku with optimized connection pool.

**📖 For detailed explanation:** See [HEROKU-CONNECTION-FIX.md](HEROKU-CONNECTION-FIX.md)

