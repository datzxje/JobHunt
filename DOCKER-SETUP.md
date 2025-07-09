# JobHunt Backend - Docker Setup

## Quick Start

### 1. Start Infrastructure Services
```bash
cd JobHunt
docker-compose up -d
```

Điều này sẽ khởi động:
- **PostgreSQL** (localhost:5433)
- **Keycloak** (localhost:8180) 
- **Redis** (localhost:6379)

### 2. Configure Environment Variables
Tạo file `.env` trong thư mục `JobHunt/` với nội dung:

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/jobhunt
SPRING_DATASOURCE_USERNAME=jobhunt
SPRING_DATASOURCE_PASSWORD=jobhunt

# JWT Configuration  
JWT_SECRET_KEY=your_super_secret_jwt_key_here_at_least_32_characters_long

# Keycloak Configuration
KEYCLOAK_CLIENT_SECRET=your_keycloak_client_secret_from_admin_console

# Redis Configuration
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=jobhunt123

# Cloudflare R2 Configuration
CLOUDFLARE_R2_ACCESS_KEY=your_cloudflare_r2_access_key
CLOUDFLARE_R2_SECRET_KEY=your_cloudflare_r2_secret_key
CLOUDFLARE_R2_BUCKET_NAME=your_bucket_name
CLOUDFLARE_R2_ENDPOINT=https://your_account_id.r2.cloudflarestorage.com

# Email Configuration (for job expiration notifications)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password
```

### 3. Setup Keycloak (Chỉ lần đầu)
1. Truy cập http://localhost:8180
2. Login: `admin` / `admin`
3. Tạo realm và client theo hướng dẫn Keycloak
4. Copy client secret vào `.env`

### 4. Start Backend
```bash
./mvnw spring-boot:run
```

### 5. Start Frontend (Từ thư mục JobHunt_FE)
```bash
cd ../JobHunt_FE
yarn dev
```

## Services
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/profile/api/v1  
- **Keycloak**: http://localhost:8180
- **PostgreSQL**: localhost:5433
- **Redis**: localhost:6379

## Stop Services
```bash
docker-compose down
```

## Reset Data
```bash
docker-compose down -v  # Xóa tất cả volumes
``` 