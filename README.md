### LƯU Ý: Không tự ý thêm bất cứ gì vào file này (chỉ đọc).

# 📦 Tên dự án: Xây dựng website quản lý nhà trọ - TroHub (BE)

# 🔰 Mục tiêu dự án
 Tóm tắt: Website quản lý nhà trọ, hỗ trợ chủ trọ theo dõi khách thuê, hợp đồng, hóa đơn, báo cáo sự cố, ...

# 🧩 Công nghệ sử dụng
- Java 21
- Spring Boot 3.5.1
- Maven 4.0.0
- Spring Data JPA
- Spring Mail
- Spring Security / JWT
- MySQL
- Redis
- Docker
- Lombok, MapStruct, Mapper
- 

# 🧱 Quy ước tạo file mới trong dự án: tạo các thư để chứa những file không phải global
  ## Tuân thủ các quy tắc đặt tên:
    - class: Invoice
    - folder: invoice 
    - loại: 
     + biến đơn: camelCase, rõ chức năng --VD: fullName
     + danh sách: users
     + boolean: bắt đầu bằng is, has, can
     + method: --VD: lấy danh sách tất cả user = public UserResponse users(){}
     + tên tham số trong method: id của user = userId
     + trong repository: findByFullName()

  ## Quy tắc đặt Path
        VD: user
            -> RequestMapping("/users")
            -> GetMapping -- để trống (getList)
            -> PostMapping -- để trống (create)
            -> Put/PatMapping("/{userId}")
            -> DeleteMapping("/{userId}")
  ## Quy tắc đặt tên method
        VD: user
            -> lấy ds: getUsers
            -> lấy user detail: getUser
            -> thêm user: createUser(update, delete tương tự)

VD: 
├── entity/
│   ├── User.java
│   ├── Role.java
│   ├── Permission.java
│   ├── invoice/
│   │   └── Invoice.java
│   │   └── DetailedInvoice.java

### Cấu hình file application-dev.yaml nếu muốn chạy ở môi trường dev
    1. File application-dev.example.yaml: là file mẫu KHÔNG sửa file này.
    2. Muốn cấu hình thì tạo 1 file mới cùng cấp với file application.yaml tên là 'appication-dev.yaml'.
    3. Copy toàn bộ nội dung từ file 'application-dev.example.yaml' sang 'appication-dev.yaml'
        -> sửa nội dung theo local trên máy.
    4. Muốn chạy với môi trường dev thì vào 'Edit configuration' -> xóa '-Dspring.profiles.active=prod' -> Apply -> Ok.
    
    ** Không bắt buộc chạy với môi trường dev. Nhưng lên chạy để có log DEBUG rõ ràng, chuyên nghiệp hơn.

I. QUY TẮC ĐẶT TÊN NHÁNH
  1. Cấu trúc chung: <type>/<task-name>-<short-description>
    - Các loại type:
      + feature: Tính năng mới --VD: feature/auth-reset-password
      + fix: Sửa lỗi --VD: fix/login-crash
      + refactor: Cải tiến code không đổi Logic --VD: refactor/otp-service-cleanup
      + hotfix: sửa lỗi khẩn cấp
      + chore: Việc phụ trợ(Cấu hình, buld, CI, CD) --VD: chore/add-gitignore
      + docs: Cập nhật tài liệu --VD: docs/add-readme-guideline

II. QUY TẮC ĐẶT TÊN COMMIT
  1. Cấu trúc: <type>: <short summary>
    - Các loại type:
      + feat: Tính năng mới --VD:  add reset password endpoint
      + fix: Sửa lỗi --VD: otp not clearing after verification
      + refactor: Cải tiến code --VD: extract otp logic to separate service
      + style: Format code
      + test: thêm test
      + hotfix: sửa lỗi khẩn cấp
      + chore: linh tinh (update config) --VD: configure lombok and annotation processor
      + docs: Cập nhật tài liệu --VD: add guideline for branch naming

III. Git
 1. Kéo code mới nhất từ nhánh master: trước khi bắt đầu làm
 2. **Không được** push hoặc merge code vào nhánh chính: 
    ❌ git push origin master
    ❌ git merge master