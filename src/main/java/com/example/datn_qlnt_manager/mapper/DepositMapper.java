package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.response.deposit.DepositResponse;
import com.example.datn_qlnt_manager.entity.Deposit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepositMapper {

    DepositResponse toDepositResponse(Deposit deposit);
}
