package com.example.datn_qlnt_manager.dto.request.feedback;

import com.example.datn_qlnt_manager.common.ContractFeedbackType;
import jakarta.validation.constraints.NotBlank;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackTerminateContractRequest {
    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    LocalDate terminateDate;

    LocalDate extendDate;

    @NotNull(message = "CONTRACT_FEEDBACK_TYPE_NOT_BLANK")
    ContractFeedbackType contractFeedbackType;
}
