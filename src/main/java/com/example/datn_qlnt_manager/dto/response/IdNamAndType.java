package com.example.datn_qlnt_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdNamAndType {

    private String id;
    private String name;
    private String type;
}
