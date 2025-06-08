* LƯU Ý: Không tự ý thêm bất cứ gì vào file này (chỉ đọc).

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