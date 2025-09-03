package com.example.datn_qlnt_manager.dto.request.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class FeedbackRatingRequest {

    @NotNull(message = "RATING_NOT_FOUND")
    @Min(value = 1, message = "RATING_TOO_LOW")
    @Max(value = 5, message = "RATING_TOO_HIGH")
    Integer rating;
}
