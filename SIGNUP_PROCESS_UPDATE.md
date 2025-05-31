# Cập nhật Signup Process - Company Selection

## Tổng quan thay đổi

Đã cập nhật signup process để hỗ trợ việc chọn công ty khi đăng ký với role EMPLOYER và tự động tạo join request.

## Các file đã thay đổi

### 1. Model & Request
- **SignUpRequest.java**: Thêm trường `companyId` cho việc chọn công ty
- **CompanySelectionResponse.java**: DTO mới cho dropdown chọn công ty

### 2. Repository
- **CompanyRepository.java**: Thêm method `findByActiveTrue()` để lấy danh sách công ty đang hoạt động

### 3. Service Layer
- **CompanyService.java**: Thêm method `getCompaniesForSelection()`
- **CompanyServiceImpl.java**: Implement method lấy danh sách công ty cho selection
- **AuthServiceImpl.java**: 
  - Thêm validation cho company selection khi role = EMPLOYER
  - Tự động tạo join request sau khi tạo user thành công

### 4. Controller
- **CompanyController.java**: Thêm endpoint `/api/v1/companies/simple` để lấy danh sách công ty

## API Endpoints mới

### GET /api/v1/companies/simple
- **Mục đích**: Lấy danh sách công ty đơn giản cho dropdown selection
- **Authentication**: Không cần
- **Response**: List<CompanySelectionResponse> với id, name, logoUrl, industryType

## Luồng hoạt động mới

### Đăng ký với role CANDIDATE
1. User điền form đăng ký
2. Gửi request không cần companyId
3. Tạo user và login thành công

### Đăng ký với role EMPLOYER
1. User chọn role EMPLOYER
2. Frontend gọi `/api/v1/companies/simple` để lấy danh sách công ty
3. User chọn công ty từ dropdown
4. Gửi signup request với companyId
5. Backend validate company tồn tại
6. Tạo user trong Keycloak và database
7. **Tự động tạo join request** với status PENDING
8. Login user và trả về auth response

## Validation Rules

1. **Role EMPLOYER**: Bắt buộc phải có companyId
2. **Company exists**: companyId phải tồn tại trong database
3. **Duplicate join request**: Kiểm tra và chỉ tạo join request mới nếu chưa có hoặc status không phải PENDING

## Business Logic

- Khi user đăng ký với role EMPLOYER, hệ thống tự động tạo join request với status PENDING
- Admin của company sẽ cần approve join request này để user trở thành member
- Nếu đã có join request PENDING, không tạo thêm request mới
- User creation vẫn thành công ngay cả khi join request creation thất bại (logged as warning)

## Testing

Xem file `test_signup_with_company.md` để có hướng dẫn test chi tiết các scenarios:
- Đăng ký CANDIDATE (không cần company)
- Đăng ký EMPLOYER (cần chọn company)
- Validation errors (missing companyId, invalid companyId)
- Kiểm tra join request được tạo

## Frontend Integration

Frontend cần:
1. Thêm dropdown chọn công ty khi user chọn role EMPLOYER
2. Gọi API `/api/v1/companies/simple` để populate dropdown
3. Include companyId trong signup request khi role = EMPLOYER
4. Handle validation errors appropriately 