# Flyway Migration Guide

## Understanding Flyway in This Project

### What is Flyway?

Flyway is a database migration tool that:
- **Tracks database changes** using versioned SQL scripts
- **Ensures schema consistency** across environments (local, staging, production)
- **Prevents conflicts** by validating checksums of migration files

### Current Configuration

**Location**: `src/main/resources/db/migration/V1__init_schema.sql`

**Schemas managed**:
- `auth` - User authentication and profiles
- `social` - Posts, comments, likes
- `pet` - Pet profiles and adoption
- `encyclopedia` - Animal species and breed information
- `notification` - User notifications

### ❓ Common Question: "Do I need to recreate tables every time?"

**NO!** Flyway is smart:

1. **First run**: Creates tables and schemas from `V1__init_schema.sql`
2. **Subsequent runs**: Checks the `flyway_schema_history` table
3. **If migration already applied**: Skips it ✅
4. **If file changed**: Shows checksum error ❌

### 🔧 Flyway Checksum Mismatch Error

**Error message**:
```
Migration checksum mismatch for migration version 1
-> Applied to database : -335742784
-> Resolved locally    : 1492926869
```

**What this means**:
- The `V1__init_schema.sql` file was **modified** after being applied to the database
- Flyway detected the change by comparing checksums

**Solutions**:

#### Option 1: Repair (Recommended for local dev)
```bash
# Run this PowerShell script
.\scripts\flyway-repair.ps1

# Or run Maven command directly
mvn flyway:repair
```

This updates the schema history table to match your current migration file.

#### Option 2: Create New Migration (Production approach)
Instead of modifying `V1__init_schema.sql`, create a new file:
```
V2__add_new_column.sql
V3__alter_table_structure.sql
```

### 🛡️ Best Practices

#### Development Environment
- ✅ Modify existing migrations freely
- ✅ Run `flyway:repair` after changes
- ✅ Use `validate-on-migrate: false` in `application.yml`

#### Production Environment
- ❌ **NEVER** modify existing migrations
- ✅ Always create new versioned migrations
- ✅ Keep `validate-on-migrate: true`

### 🔄 Typical Workflow

#### When Starting Fresh
```bash
# 1. Drop all schemas (if needed)
# 2. Run application
mvn spring-boot:run
# Flyway creates everything automatically
```

#### When Database Already Exists
```bash
# Application validates and runs without recreating
mvn spring-boot:run
```

#### After Modifying Migration File
```bash
# 1. Repair the schema history
mvn flyway:repair

# 2. Run application
mvn spring-boot:run
```

### 📋 Configuration Files

**application.yml**:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Don't auto-create tables, use Flyway

  flyway:
    enabled: true
    validate-on-migrate: false  # Allow checksum changes (dev only)
    baseline-on-migrate: true   # Work with existing databases
```

**pom.xml** (Flyway Maven Plugin):
```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <!-- Database credentials from pom.xml -->
        <!-- For production, use environment variables -->
    </configuration>
</plugin>
```

### 🚀 Common Commands

```bash
# Check current migration status
mvn flyway:info

# Repair schema history after file changes
mvn flyway:repair

# Validate migrations
mvn flyway:validate

# Clean database (⚠️ DANGEROUS - deletes everything)
mvn flyway:clean
```

### 🔐 Security Note

⚠️ **IMPORTANT**: The `pom.xml` currently contains hard-coded database credentials for convenience. 

**For production**:
1. Remove credentials from `pom.xml`
2. Use environment variables or profiles
3. Never commit credentials to Git

### 📚 References

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot + Flyway](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

