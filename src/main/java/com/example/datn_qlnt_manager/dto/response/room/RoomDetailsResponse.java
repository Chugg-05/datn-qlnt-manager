package com.example.datn_qlnt_manager.dto.response.room;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDetailsResponse {
    //  1.tòa nhà(tên, địa chỉ, chủ(tên, số điện thoại)))
    String buildingName;
    String buildingAddress;
    String ownerName;
    String ownerPhone;

    // 2. phòng (đầy đủ thông tin)
    String roomCode;
    BigDecimal acreage;
    Integer maximumPeople;
    RoomType roomType;
    RoomStatus status;
    String description;

    // 4. hợp đồng (thông tin cơ bản, thời hạn hợp đồng)
    String contractCode;
    String representativeName;
    String representativePhone;
    LocalDate dob;
    String identityCardNumber;
    BigDecimal deposit;
    BigDecimal roomPrice;
    ContractStatus contractStatus;
    LocalDate  startDate;
    LocalDate  endDate;

    // 3. Số thành viên trong phòng
    int memberInRoomCount;

    // 5. Số tài sản trong phòng
    int assetInRoomCount;

    // 6. Số dịch vụ trong phòng đang sử dụng
    int serviceInRoomCount;

    // 7. Số phương tiện trong phòng
    int vehicleInRoomCount;

    @Builder

    public RoomDetailsResponse(String buildingName, String buildingAddress, String ownerName, String ownerPhone, String roomCode, BigDecimal acreage, Integer maximumPeople, RoomType roomType, RoomStatus status, String description, String contractCode, String representativeName, String representativePhone, LocalDate dob, String identityCardNumber, BigDecimal deposit, BigDecimal roomPrice, ContractStatus contractStatus, LocalDate startDate, LocalDate endDate, int memberInRoomCount, int assetInRoomCount, int serviceInRoomCount, int vehicleInRoomCount) {
        this.buildingName = buildingName;
        this.buildingAddress = buildingAddress;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.roomCode = roomCode;
        this.acreage = acreage;
        this.maximumPeople = maximumPeople;
        this.roomType = roomType;
        this.status = status;
        this.description = description;
        this.contractCode = contractCode;
        this.representativeName = representativeName;
        this.representativePhone = representativePhone;
        this.dob = dob;
        this.identityCardNumber = identityCardNumber;
        this.deposit = deposit;
        this.roomPrice = roomPrice;
        this.contractStatus = contractStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberInRoomCount = memberInRoomCount;
        this.assetInRoomCount = assetInRoomCount;
        this.serviceInRoomCount = serviceInRoomCount;
        this.vehicleInRoomCount = vehicleInRoomCount;
    }
}
