package com.example.datn_qlnt_manager.dto.request.feedback;

import com.example.datn_qlnt_manager.common.RepresentativeChangeType;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantNewCreationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackChangeRepresentativeRequest {

    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    @NotNull(message = "INVALID_REPRESENTATIVE_CHANGE_TYPE_NULL")
    RepresentativeChangeType changeType;

    //nếu đổi cho người cùng phòng
//    @NotBlank(message = "INVALID_TENANT_ID_BLANK")
    String tenantId;

    //nếu nhượng cho người ngoài
    TenantNewCreationRequest newTenant;

    //xác nhận người gửi có ở lại phòng nữa không
    Boolean stayInRoom;

    String reason;
}
