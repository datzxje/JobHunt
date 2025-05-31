# Company Management Guide

## Tổng quan

Hệ thống đã được cập nhật để cho phép thêm companies trực tiếp vào database mà không cần user constraint. Admin chỉ có thể edit companies, không thể tạo mới thông qua API.

## Thay đổi đã thực hiện

### 1. Database Schema
- **Migration V7**: Cho phép `user_id` trong bảng `companies` có thể null
- **Company Entity**: Cập nhật `@JoinColumn(name = "user_id", nullable = true)`

### 2. API Changes
- **Removed**: POST `/api/v1/companies` (create company endpoint)
- **Updated**: PUT `/api/v1/companies/{id}` (chỉ cho phép edit)
- **Updated**: DELETE `/api/v1/companies/{id}` (chỉ cho phép delete nếu user sở hữu)

### 3. Business Logic Updates
- Khi admin edit company lần đầu, tự động assign làm owner
- Chỉ owner mới có thể edit/delete company
- Companies có thể tồn tại mà không có owner

## Cách thêm Companies vào hệ thống

### 1. Thêm trực tiếp vào Database

```sql
-- Chạy migration để cập nhật schema
-- File: V7__update_companies_user_id_nullable.sql sẽ tự động chạy khi restart app

-- Thêm sample companies
-- Chạy file: sample_companies.sql
```

### 2. Sử dụng sample data

```bash
# Kết nối vào PostgreSQL database
psql -h localhost -U your_username -d jobhunt_db

# Chạy script sample companies
\i /path/to/sample_companies.sql
```

### 3. Manual Insert Example

```sql
INSERT INTO companies (
    name, 
    email, 
    industry_type, 
    tax_id, 
    active,
    created_at,
    updated_at
) VALUES (
    'Your Company Name',
    'contact@yourcompany.com',
    'Technology',
    'TAX123456789',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

## Admin Company Management Workflow

### 1. Xem danh sách companies có sẵn

```bash
curl -X GET "http://localhost:8080/api/v1/companies/simple" \
  -H "Content-Type: application/json"
```

### 2. Admin edit company (lần đầu sẽ auto-assign ownership)

```bash
curl -X PUT "http://localhost:8080/api/v1/companies/1" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Company Name",
    "email": "updated@company.com",
    "phoneNumber": "0248888888",
    "websiteUrl": "https://updated-company.com",
    "establishmentYear": 2015,
    "teamSize": "100-500",
    "industryType": "Technology",
    "about": "Updated company description",
    "country": "Vietnam",
    "city": "Ho Chi Minh City",
    "address": "Updated address",
    "taxId": "0123456789"
  }'
```

### 3. Xem company details

```bash
curl -X GET "http://localhost:8080/api/v1/companies/1" \
  -H "Content-Type: application/json"
```

## User Signup với Company Selection

### 1. User chọn role EMPLOYER và company

```bash
curl -X POST "http://localhost:8080/api/v1/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phoneNumber": "0912345678",
    "role": "EMPLOYER",
    "companyId": 1
  }'
```

### 2. Join request được tự động tạo với status PENDING

### 3. Admin của company approve join request

```bash
curl -X PUT "http://localhost:8080/api/v1/company-admin/join-requests/1" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "APPROVED",
    "reviewNote": "Welcome to the team!"
  }'
```

## Security & Permissions

### Company Ownership Rules
1. **No Owner**: Company có thể tồn tại mà không có owner
2. **First Edit**: Admin đầu tiên edit company sẽ trở thành owner
3. **Edit Permission**: Chỉ owner mới có thể edit company
4. **Delete Permission**: Chỉ owner mới có thể delete (set active = false)

### API Access Control
- **GET endpoints**: Public access (để lấy danh sách companies)
- **PUT/DELETE endpoints**: Require EMPLOYER role và ownership
- **POST endpoint**: Đã bị remove (không thể tạo company qua API)

## Troubleshooting

### 1. Migration Issues
```bash
# Kiểm tra migration status
mvn flyway:info

# Force run migration nếu cần
mvn flyway:migrate
```

### 2. Constraint Errors
```sql
-- Kiểm tra constraint hiện tại
SELECT 
    tc.constraint_name, 
    tc.table_name, 
    kcu.column_name, 
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM 
    information_schema.table_constraints AS tc 
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
WHERE tc.table_name='companies';
```

### 3. Reset Company Ownership
```sql
-- Nếu cần reset ownership của company
UPDATE companies SET user_id = NULL WHERE id = 1;
```

## Best Practices

1. **Backup trước khi thay đổi**: Luôn backup database trước khi chạy migration
2. **Test với sample data**: Sử dụng sample companies để test trước
3. **Monitor logs**: Kiểm tra application logs khi users đăng ký với company selection
4. **Regular cleanup**: Định kỳ kiểm tra và cleanup các join requests cũ 