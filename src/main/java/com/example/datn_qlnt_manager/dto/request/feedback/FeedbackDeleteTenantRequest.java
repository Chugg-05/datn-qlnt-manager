package com.example.datn_qlnt_manager.dto.request.feedback;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.api.client.util.DateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackDeleteTenantRequest {

    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    List<String> tenantId;


    @NotNull(message = "INVALID_END_DATE_BLANK")
    Date endDate;
}
