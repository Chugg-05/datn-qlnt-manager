package com.example.datn_qlnt_manager.dto.request.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data

public class TerminateContractRequest {
    @NotNull(message = "INVALID_END_DATE_BLANK")
    private LocalDate newEndDate;
}
