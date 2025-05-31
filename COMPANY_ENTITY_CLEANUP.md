# Company Entity Cleanup Documentation

## Tổng quan

Đã cleanup Company entity để loại bỏ các fields trùng lặp và consolidate thành một cấu trúc entity rõ ràng hơn.

## Vấn đề trước đây

### 1. **Social Network Fields bị trùng lặp:**
```java
// Duplicate social fields
private String facebookUrl;      // vs socialFacebook
private String twitterUrl;       // vs socialTwitter  
private String linkedinUrl;      // vs socialLinkedin
private String googlePlusUrl;    // không có duplicate
private String socialInstagram;  // chỉ có 1
```

### 2. **Contact Information Fields bị trùng lặp:**
```java
// Duplicate contact fields
private String phoneNumber;      // vs contactPhone
private String email;            // vs contactEmail
private String websiteUrl;       // vs contactWebsite
private String address;          // vs contactAddress
```

### 3. **User Relationships gây nhầm lẫn:**
```java
private User adminUser;          // ManyToOne - current admin
private User user;               // OneToOne - owner/creator
```

## Giải pháp đã thực hiện

### 1. **Consolidated Social Network Fields:**
```java
// Kept consolidated social fields
@Column(name = "facebook_url")
private String facebookUrl;

@Column(name = "twitter_url")
private String twitterUrl;

@Column(name = "linkedin_url")
private String linkedinUrl;

@Column(name = "google_plus_url")
private String googlePlusUrl;

@Column(name = "social_instagram")
private String socialInstagram;
```

**Removed:** `socialFacebook`, `socialTwitter`, `socialLinkedin`

### 2. **Consolidated Contact Information:**
```java
// Kept original contact fields
@Column(nullable = false)
private String email;

@Column(name = "phone_number")
private String phoneNumber;

@Column(name = "website_url")
private String websiteUrl;

private String address;
```

**Removed:** `contactPhone`, `contactEmail`, `contactWebsite`, `contactAddress`

### 3. **Clarified User Relationships:**
```java
// Company Owner/Creator (the user who initially manages the company)
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = true)
private User user;

// Current Admin User (can be different from owner, used for company admin operations)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "admin_user_id")
private User adminUser;
```

## Database Migration

### Migration V8: Cleanup duplicate columns
```sql
-- Drop duplicate social network columns
ALTER TABLE companies DROP COLUMN IF EXISTS social_facebook;
ALTER TABLE companies DROP COLUMN IF EXISTS social_twitter;
ALTER TABLE companies DROP COLUMN IF EXISTS social_linkedin;

-- Drop duplicate contact information columns
ALTER TABLE companies DROP COLUMN IF EXISTS contact_phone;
ALTER TABLE companies DROP COLUMN IF EXISTS contact_email;
ALTER TABLE companies DROP COLUMN IF EXISTS contact_website;
ALTER TABLE companies DROP COLUMN IF EXISTS contact_address;
```

## Updated Models

### 1. **CompanyRequest.java**
- Added `socialInstagram` field với validation
- Kept consolidated fields only

### 2. **CompanyResponse.java** 
- Added `socialInstagram` field
- Fixed `teamSize` type từ `Integer` → `String` để match entity

### 3. **CompanyMapper.java**
- Updated mappings để ignore tất cả relationship fields
- Added proper mappings cho `adminUser`, `joinRequests`, `members`

## Final Entity Structure

```java
@Entity
@Table(name = "companies")
public class Company {
  // Basic Info
  private Long id;
  private String logoUrl;
  private String coverUrl;
  private String name;
  private String email;
  private String phoneNumber;
  private String websiteUrl;
  private Integer establishmentYear;
  private String teamSize;
  private String industryType;
  private String about;

  // Social Networks (consolidated)
  private String facebookUrl;
  private String twitterUrl;
  private String linkedinUrl;
  private String googlePlusUrl;
  private String socialInstagram;

  // Contact Info (consolidated)
  private String country;
  private String city;
  private String address;
  private Double latitude;
  private Double longitude;
  private String taxId;

  // User Relationships (clarified)
  private User user;           // Owner/Creator
  private User adminUser;      // Current Admin

  // Collections
  private Set<Job> jobs;
  private Set<Review> reviews;
  private Set<CompanyJoinRequest> joinRequests;
  private Set<CompanyMember> members;

  // Meta
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}
```

## Benefits

### 1. **Reduced Confusion**
- Không còn duplicate fields
- Clear naming convention
- Proper separation of concerns

### 2. **Database Optimization**
- Reduced number of columns
- Cleaner schema
- Better performance

### 3. **Code Maintainability**
- Easier to understand entity structure
- Less mapping complexity
- Consistent field usage

### 4. **API Consistency**
- Request/Response models match entity
- No confusion về field nào được sử dụng
- Clear validation rules

## Breaking Changes

### Potential Impact:
1. **Frontend**: Nếu frontend đang sử dụng duplicate fields
2. **API Calls**: Cần update request/response handling
3. **Database**: Migration sẽ drop unused columns

### Migration Path:
1. **Backup database** trước khi chạy migration
2. **Test thoroughly** với new entity structure  
3. **Update frontend** để sử dụng consolidated fields
4. **Verify API responses** match new structure

## Testing

### Recommended Tests:
```bash
# Test company creation
curl -X POST "/api/v1/auth/signup" -d '{"role": "EMPLOYER", "companyId": 1}'

# Test company update
curl -X PUT "/api/v1/companies/1" -d '{"socialInstagram": "https://instagram.com/company"}'

# Test company response
curl -X GET "/api/v1/companies/1"
```

## Future Considerations

1. **Migration từ old data**: Nếu cần preserve old data, có thể tạo data migration script
2. **Additional social platforms**: Có thể thêm TikTok, YouTube, etc.
3. **Contact preferences**: Có thể thêm preferred contact method
4. **Multi-admin support**: Enhance admin system với roles/permissions 