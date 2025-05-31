# Test Signup Process với Company Selection

## 1. Lấy danh sách công ty để chọn

```bash
curl -X GET "http://localhost:8080/api/v1/companies/simple" \
  -H "Content-Type: application/json"
```

Response:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Tech Corp",
      "logoUrl": "https://example.com/logo.png",
      "industryType": "Technology"
    },
    {
      "id": 2,
      "name": "Finance Solutions",
      "logoUrl": "https://example.com/logo2.png",
      "industryType": "Finance"
    }
  ]
}
```

## 2. Đăng ký với role CANDIDATE (không cần chọn công ty)

```bash
curl -X POST "http://localhost:8080/api/v1/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phoneNumber": "0912345678",
    "role": "CANDIDATE"
  }'
```

## 3. Đăng ký với role EMPLOYER (cần chọn công ty)

```bash
curl -X POST "http://localhost:8080/api/v1/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "username": "janesmith",
    "email": "jane.smith@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phoneNumber": "0987654321",
    "role": "EMPLOYER",
    "companyId": 1
  }'
```

## 4. Test validation - EMPLOYER không có companyId

```bash
curl -X POST "http://localhost:8080/api/v1/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Bob",
    "lastName": "Wilson",
    "username": "bobwilson",
    "email": "bob.wilson@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phoneNumber": "0911111111",
    "role": "EMPLOYER"
  }'
```

Expected error:
```json
{
  "success": false,
  "message": "Company selection is required for EMPLOYER role"
}
```

## 5. Test validation - companyId không tồn tại

```bash
curl -X POST "http://localhost:8080/api/v1/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Brown",
    "username": "alicebrown",
    "email": "alice.brown@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phoneNumber": "0922222222",
    "role": "EMPLOYER",
    "companyId": 999
  }'
```

Expected error:
```json
{
  "success": false,
  "message": "Selected company not found"
}
```

## Luồng hoạt động

1. **Frontend**: Khi user chọn role "EMPLOYER", gọi API `/api/v1/companies/simple` để lấy danh sách công ty
2. **Frontend**: Hiển thị dropdown cho user chọn công ty
3. **Frontend**: Gửi signup request với `companyId` được chọn
4. **Backend**: Validate company tồn tại
5. **Backend**: Tạo user trong Keycloak và database
6. **Backend**: Tự động tạo join request với status PENDING
7. **Backend**: Trả về auth response với user info

## Kiểm tra join request được tạo

Sau khi đăng ký thành công với role EMPLOYER, có thể kiểm tra join request:

```bash
# Login với admin của company để xem join requests
curl -X GET "http://localhost:8080/api/v1/company-admin/join-requests" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json"
``` 