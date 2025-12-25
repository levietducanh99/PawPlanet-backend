# PawPlanet Backend - Hướng dẫn chạy Local với .env

## ✅ Spring tự động load .env

Project đã cấu hình **dotenv-java** dependency và `DotenvConfig` class để tự động load file `.env` vào Spring Environment khi khởi động.

### Chạy đơn giản

```powershell
# Chỉ cần chạy - Spring sẽ tự load .env
mvn spring-boot:run

# Hoặc chạy tests
mvn test
```

**Không cần script hay set environment variables thủ công!** Spring tự động đọc `.env` ở project root.

### Cách hoạt động

1. Dependency `io.github.cdimascio:dotenv-java` được thêm vào `pom.xml`
2. Class `DotenvConfig` tự động load `.env` khi Spring khởi động
3. Tất cả biến trong `.env` được nạp vào System properties
4. Spring placeholders như `${DATABASE_URL}` được resolve tự động

### Cấu trúc file .env (CHUẨN HEROKU)

```env
# Heroku-standard format
DATABASE_URL=postgres://username:password@host:port/database
JWT_SECRET=your-secret
```

**Lưu ý:** 
- ✅ Dùng `DATABASE_URL` (chuẩn Heroku) - Spring Boot + HikariCP tự parse
- ❌ KHÔNG cần `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` riêng
- ✅ Format: `postgres://user:pass@host:port/db` (không phải `jdbc:postgresql://`)


### Trong IDE (IntelliJ/VS Code)

1. Mở Run/Debug Configuration
2. Thêm Environment Variables:
   - `DB_URL=jdbc:postgresql://...`
   - `DB_USERNAME=...`
   - `DB_PASSWORD=...`
3. Run hoặc Debug

## Bảo mật quan trọng

- ⚠️ **KHÔNG commit file `.env`** vào Git (đã có trong `.gitignore`)
- Trên Heroku: set Config Vars trong Dashboard → Settings
- File `.env.example` chứa template - bạn có thể chia sẻ file này

## API Health Check

Để test backend hoạt động:

```bash
# Sau khi chạy app
curl http://localhost:8080/api/health
```

Kết quả mong đợi:
```json
{
  "status": "UP",
  "timestamp": "2025-12-26T00:00:00.000Z"
}
```

