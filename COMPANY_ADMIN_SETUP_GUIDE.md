# Company + Admin Setup Guide

## Tổng quan

Endpoint mới để tạo company cùng với admin user trong một transaction duy nhất, đảm bảo company luôn có admin ngay từ đầu.

## Endpoint

**POST** `/api/v1/admin/setup-company`

- **Authentication**: Không cần (dành cho system admin setup)
- **Content-Type**: `application/json`
- **Method**: Atomic transaction tạo cả company và admin user

## Request Body

```json
{
  // Company Information
  "companyName": "TechCorp Vietnam",
  "companyEmail": "contact@techcorp.vn", 
  "companyPhone": "0248888888",
  "companyWebsite": "https://techcorp.vn",
  "establishmentYear": 2015,
  "teamSize": "100-500",
  "industryType": "Technology",
  "companyAbout": "Leading technology company in Vietnam",
  "country": "Vietnam",
  "city": "Ho Chi Minh City", 
  "address": "123 Nguyen Hue Street, District 1",
  "taxId": "0123456789",

  // Admin User Information
  "adminFirstName": "Nguyen",
  "adminLastName": "Van Admin",
  "adminUsername": "admin_techcorp",
  "adminEmail": "admin@techcorp.vn",
  "adminPassword": "securepassword123",
  "adminPhoneNumber": "0987654321",
  "adminProfilePictureUrl": "https://example.com/avatar.jpg"
}
```

## Complete cURL Example

```bash
curl -X POST "http://localhost:8080/api/v1/admin/setup-company" \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "TechCorp Vietnam",
    "companyEmail": "contact@techcorp.vn",
    "companyPhone": "0248888888", 
    "companyWebsite": "https://techcorp.vn",
    "establishmentYear": 2015,
    "teamSize": "100-500",
    "industryType": "Technology",
    "companyAbout": "TechCorp is a leading technology company in Vietnam, specializing in software development and digital transformation.",
    "country": "Vietnam",
    "city": "Ho Chi Minh City",
    "address": "123 Nguyen Hue Street, District 1, Ho Chi Minh City", 
    "taxId": "0123456789",
    "adminFirstName": "Nguyen",
    "adminLastName": "Van Admin",
    "adminUsername": "admin_techcorp",
    "adminEmail": "admin@techcorp.vn",
    "adminPassword": "securepassword123",
    "adminPhoneNumber": "0987654321"
  }'
```

## Response

### Success Response:
```json
{
  "success": true,
  "data": {
    "id": "1",
    "logoUrl": null,
    "coverUrl": null,
    "name": "TechCorp Vietnam",
    "email": "contact@techcorp.vn",
    "phoneNumber": "0248888888",
    "websiteUrl": "https://techcorp.vn",
    "establishmentYear": 2015,
    "teamSize": "100-500",
    "industryType": "Technology",
    "about": "TechCorp is a leading technology company...",
    "country": "Vietnam",
    "city": "Ho Chi Minh City",
    "address": "123 Nguyen Hue Street, District 1, Ho Chi Minh City",
    "taxId": "0123456789",
    "active": true,
    "averageRating": 0.0,
    "totalReviews": 0,
    "createdAt": "2024-12-01T01:50:00Z",
    "updatedAt": "2024-12-01T01:50:00Z"
  }
}
```

### Error Response:
```json
{
  "success": false,
  "message": "Admin email already exists"
}
```

## Validation Rules

### Company Fields:
- **companyName**: Required, not blank
- **companyEmail**: Required, valid email format
- **establishmentYear**: Required, max 2025
- **teamSize**: Required, not blank
- **industryType**: Required, not blank
- **country**: Required, not blank
- **city**: Required, not blank
- **address**: Required, not blank
- **taxId**: Required, not blank, unique

### Admin User Fields:
- **adminFirstName**: Required, not blank
- **adminLastName**: Required, not blank
- **adminUsername**: Required, not blank, unique
- **adminEmail**: Required, valid email format, unique
- **adminPassword**: Required, min 6 characters
- **adminPhoneNumber**: Required, valid Vietnamese phone format (09|03)xxxxxxxx

## What Happens Behind the Scenes

### 1. **Validation**
- Check admin email and username uniqueness
- Check company tax ID uniqueness
- Validate all required fields

### 2. **Keycloak User Creation**
- Create admin user in Keycloak
- Set password (non-temporary)
- Assign EMPLOYER role
- Set emailVerified = true (admin pre-verified)

### 3. **Database User Creation**
- Create User record in database
- Link with Keycloak ID
- Set role as EMPLOYER

### 4. **Company Creation**
- Create Company record
- Set `user` field (owner/creator) = admin user
- Set `adminUser` field (current admin) = admin user
- Set active = true

### 5. **Company Member Creation**
- Create CompanyMember record
- Set role = ADMIN
- Set department = "Management"
- Set status = ACTIVE

## Business Logic

### Company-Admin Relationship:
```
Company:
├── user (OneToOne) ──────────► Admin User (owner/creator)
├── adminUser (ManyToOne) ────► Admin User (current admin)
└── members (OneToMany) ──────► CompanyMember(role=ADMIN)
```

### Admin User Capabilities:
- **Full Company Management**: Edit company profile, settings
- **Team Management**: Manage join requests, team members
- **Role Assignment**: Promote/demote other members
- **Admin Transfer**: Transfer admin rights to other members

## Multiple Companies Setup

Để setup nhiều companies:

```bash
# Company 1: TechCorp
curl -X POST "http://localhost:8080/api/v1/admin/setup-company" \
  -H "Content-Type: application/json" \
  -d '{"companyName": "TechCorp Vietnam", "adminEmail": "admin@techcorp.vn", ...}'

# Company 2: FinanceHub  
curl -X POST "http://localhost:8080/api/v1/admin/setup-company" \
  -H "Content-Type: application/json" \
  -d '{"companyName": "FinanceHub VN", "adminEmail": "admin@financehub.vn", ...}'

# Company 3: EduTech
curl -X POST "http://localhost:8080/api/v1/admin/setup-company" \
  -H "Content-Type: application/json" \
  -d '{"companyName": "EduTech Solutions", "adminEmail": "admin@edutech.vn", ...}'
```

## Admin Login After Setup

Sau khi setup thành công, admin có thể login:

```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@techcorp.vn",
    "password": "securepassword123"
  }'
```

## Verify Company Admin Dashboard Access

```bash
# Get current user company (should return the company)
curl -X GET "http://localhost:8080/api/v1/companies/me" \
  -H "Authorization: Bearer <access_token>"

# Access admin dashboard endpoints
curl -X GET "http://localhost:8080/api/v1/company-admin/team-members" \
  -H "Authorization: Bearer <access_token>"

curl -X GET "http://localhost:8080/api/v1/company-admin/join-requests" \
  -H "Authorization: Bearer <access_token>"
```

## Advantages

### 1. **Atomic Operation**
- Company và admin được tạo trong 1 transaction
- Rollback hoàn toàn nếu có lỗi
- Đảm bảo consistency

### 2. **No Orphaned Records**
- Company luôn có admin ngay từ đầu
- Không có company "không chủ"
- Admin có full access ngay lập tức

### 3. **Ready to Use**
- Admin có thể login ngay
- Company dashboard hoạt động ngay
- Có thể bắt đầu manage team ngay

### 4. **Secure Setup**
- Validation đầy đủ trước khi tạo
- Keycloak integration hoàn chỉnh
- Proper role assignment

## Use Cases

1. **System Initial Setup**: Tạo companies đầu tiên cho hệ thống
2. **Enterprise Onboarding**: Onboard enterprise customers với dedicated admin
3. **Migration**: Migrate từ system cũ với full company data
4. **Demo Setup**: Tạo demo data với companies và admins sẵn sàng

Endpoint này đảm bảo logic "admin của công ty được cấp tài khoản cùng lúc với công ty" một cách hoàn hảo! 