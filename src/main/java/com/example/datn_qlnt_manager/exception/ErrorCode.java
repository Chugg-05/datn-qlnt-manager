package com.example.datn_qlnt_manager.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    // Code: 500
    INTERNAL_SERVER_ERROR(500, "Uncategorized error.", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_FAILED(500, "Upload failed.", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PROCESSING_ERROR(500, "Failed to process JSON data.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Code: 401
    UNAUTHORIZED(401, "Unauthenticated: Invalid or expired JWT token.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_FORMAT(401, "Invalid token format.", HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE(401, "Token signature is invalid.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(401, "Token has expired.", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLISTED(401, "Token has been blacklisted (user logged out).", HttpStatus.UNAUTHORIZED),

    // Code: 403
    FORBIDDEN(403, "You don't have permission.", HttpStatus.FORBIDDEN),
    CANT_REMOVE_USER(403, "You can't remove another user.", HttpStatus.FORBIDDEN),
    ACCOUNT_HAS_BEEN_LOCKED(403, "User account is locked.", HttpStatus.FORBIDDEN),

    // Code: 400
    BAD_REQUEST(400, "Invalid request.", HttpStatus.BAD_REQUEST),
    CANNOT_SEND_EMAIL(400, "Cannot send email.", HttpStatus.BAD_REQUEST),
    EMAIL_SENDING_FAILED(400, "Email sending failed.", HttpStatus.BAD_REQUEST),
    INVALID_KEY(400, "Invalid key.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(400, "Invalid token.", HttpStatus.BAD_REQUEST),
    USER_NOT_DELETED(400, "User not deleted.", HttpStatus.BAD_REQUEST),
    USER_NOT_LOCKED(400, "User is not locked.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_BLANK(400, "Username or email must not be blank.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(400, "Must be a valid email with domain.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_OR_PASSWORD(400, "Invalid email or password.", HttpStatus.BAD_REQUEST),
    INVALID_FULL_NAME(400, "Your first name must be at least {min} characters.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(400, "Your password must be at least {min} characters.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_BLANK(400, "Password must not be blank.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_LENGTH(400, "Password must be at least 6 characters long.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_UPPERCASE(400, "Password must contain at least one uppercase letter.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_LOWERCASE(400, "Password must contain at least one lowercase letter.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_NUMBER(400, "Password must contain at least one digit.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_SPECIAL_CHARACTERS(
            400, "Password must contain at least one special character(@#$%&*!).", HttpStatus.BAD_REQUEST),
    INVALID_GENDER_BLANK(400, "Gender cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_DOB(400, "Your age must be at least {min}.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE(400, "Your phone must be at least {min}.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_BLANK(400, "Phone number is not blank.", HttpStatus.BAD_REQUEST),
    PASSWORDS_CONFIRM_NOT_MATCH(400, "Password confirm not match.", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD(400, "New password same as old.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER_FORMAT(
            400,
            "Phone number must be 10-11 digits, starting with 0 or +84. Example: 0974 xxx xxx",
            HttpStatus.BAD_REQUEST),
    INVALID_ROLE_NAME_AND_PERMISSION_BLANK(400, "Name cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_ROLE_NAME_AND_PERMISSION_FORMAT(
            400,
            "Names must only contain uppercase letters, numbers, and the '_' character to separate words.",
            HttpStatus.BAD_REQUEST),
    INVALID_DESCRIPTION_BLANK(400, "Description cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_PERMISSION_BLANK(400, "Permission cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_OTP_BLANK(400, "OTP code cannot be left blank.", HttpStatus.BAD_REQUEST),
    INVALID_OTP_FORMAT(400, "OTP code is not in correct format.", HttpStatus.BAD_REQUEST),

    REFRESH_TOKEN_INVALID(400, "Refresh token invalid.", HttpStatus.BAD_REQUEST),
    BUILDING_ID_REQUIRED(400, "Building ID can not be blank.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED(400, "Refresh token expired.", HttpStatus.BAD_REQUEST),
    INVALID_OTP_CODE(400, "OTP code is incorrect or expired.", HttpStatus.BAD_REQUEST),
    INVALID_ISSUER(400, "Invalid issuer.", HttpStatus.BAD_REQUEST),
    INVALID_AUDIENCE(400, "Invalid audience.", HttpStatus.BAD_REQUEST),

    INVALID_FLOORS_NUMBER_FOR_RENT(
            400, "Number of floors for rent exceeds actual number of floors.", HttpStatus.BAD_REQUEST),
    ACTUAL_FLOOR_NUMBER_IS_INVALID(400, "The actual number of floors must be at least {min}.", HttpStatus.BAD_REQUEST),
    INVALID_RENTAL_FLOOR_NUMBER(400, "Number of floors for rent must be greater than 0.", HttpStatus.BAD_REQUEST),
    INVALID_BUILDING_CODE_BLANK(400, "Building Code cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_BUILDING_NAME_BLANK(400, "Building Name cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_ADDRESS_BLANK(400, "Address cannot be blank", HttpStatus.BAD_REQUEST),
    INVALID_ACTUAL_NUMBER_OF_FLOORS_BLANK(400, "Actual Number Of Floors cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_NUMBER_OF_FLOORS_FOR_RENT_BLANK(400, "Number Of Floors For Rent cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_BUILDING_TYPE_BLANK(400, "Building Type cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_BUILDING_STATUS_BLANK(400, "Building Status cannot be blank.", HttpStatus.BAD_REQUEST),

    FLOOR_NAME_INVALID(400, "Floor name can not be blank.", HttpStatus.BAD_REQUEST),
    MAX_ROOM_INVALID(400, "Maximum room must not be null.", HttpStatus.BAD_REQUEST),
    FLOOR_TYPE_INVALID(400, "Floor type must not be null.", HttpStatus.BAD_REQUEST),
    STATUS_TYPE_INVALID(400, "Floor status must not be null.", HttpStatus.BAD_REQUEST),
    MAX_ROOM_AT_LEAST(400, "Maximum number of rooms must be at least 1.", HttpStatus.BAD_REQUEST),
    MAX_ROOM_SEARCH(400, "Maximum room must be >= 0.", HttpStatus.BAD_REQUEST),
    MAX_ROOM_AT_MOST(400, "Each floor must not have more than 100 rooms.", HttpStatus.BAD_REQUEST),
    FLOOR_ROOM_LIMIT_REACHED(400, "This floor is full, no new rooms can be added.", HttpStatus.BAD_REQUEST),

    INVALID_CUSTOMER_CODE_BLANK(400, "Customer code cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_ID_NUMBER_BLANK(400, "Identity card number cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_ID_NUMBER_FORMAT(400, "Incorrect ID number format.", HttpStatus.BAD_REQUEST),
    INVALID_TENANT_STATUS_BLANK(400, "Tenant status cannot be null.", HttpStatus.BAD_REQUEST),
    TENANT_CANNOT_BE_DELETED(400, "This tenant cannot be deleted.", HttpStatus.BAD_REQUEST),
    TENANT_CANNOT_BE_TOGGLED(400, "This tenant cannot be toggled.", HttpStatus.BAD_REQUEST),

    INVALID_VEHICLE_STATUS_BLANK(400, "Vehicle status cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_VEHICLE_TYPE_BLANK(400, "Vehicle type cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_LICENSE_PLATE_BLANK(400, "License plate cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_REGISTRATION_DATE_BLANK(400, "Registration date cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_TENANT_ID_BLANK(400, "Tenant id cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_REGISTRATION_DATE(400, "Registration date cannot be in the future.", HttpStatus.BAD_REQUEST),
    INVALID_LICENSE_PLATE(
            400, "License plate is not in correct format (EX: 29B1-12345 or 29AN-12345).", HttpStatus.BAD_REQUEST),
    // Service Room Validation
    ROOM_ID_REQUIRED(400, "Room ID is required.", HttpStatus.BAD_REQUEST),
    SERVICE_ID_REQUIRED(400, "Service ID is required.", HttpStatus.BAD_REQUEST),
    START_DATE_REQUIRED(400, "Start date is required.", HttpStatus.BAD_REQUEST),
    START_DATE_MUST_BE_TODAY_OR_FUTURE(400, "Start date must be today or in the future.", HttpStatus.BAD_REQUEST),
    TOTAL_PRICE_REQUIRED(400, "Total price is required.", HttpStatus.BAD_REQUEST),
    TOTAL_PRICE_MUST_BE_NON_NEGATIVE(400, "Total price must be >= 0.", HttpStatus.BAD_REQUEST),
    STATUS_REQUIRED(400, "Service room status is required.", HttpStatus.BAD_REQUEST),

    INVALID_DEFAULT_SERVICE_APPLIES_TO_NULL(400, "Default Service Applies To cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_PRICES_APPLY_NULL(400, "Prices Apply cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_START_APPLYING_NULL(400, "Start Applying cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_DEFAULT_SERVICE_STATUS_NULL(400, "Default service status cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_BUILDING_ID_NULL(404, "Building ID cannot be null.", HttpStatus.NOT_FOUND),
    INVALID_SERVICE_ID_NULL(404, "Service ID cannot be null.", HttpStatus.NOT_FOUND),
    PRICES_APPLY_INVALID(400, "Prices Apply must be >= 0.", HttpStatus.BAD_REQUEST),
    DUPLICATE_SERVICE(400, "Service already exist for this building and application type.", HttpStatus.BAD_REQUEST),

    INVALID_NUMBER_OF_PEOPLE(400, "Number of people must be at least { min }.", HttpStatus.BAD_REQUEST),
    INVALID_START_DATE_BLANK(400, "Start date cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_END_DATE_BLANK(400, "End date cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_DEPOSIT_BLANK(400, "Deposit cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_DEPOSIT(400, "Deposit must be greater than 0.", HttpStatus.BAD_REQUEST),
    INVALID_TENANTS_BLANK(400, "Tenants cannot be blank.", HttpStatus.BAD_REQUEST),
    INVALID_TENANTS(400, "At least one tenant must be selected.", HttpStatus.BAD_REQUEST),
    INVALID_CONTRACT_STATUS_BLANK(400, "Contract status cannot be blank.", HttpStatus.BAD_REQUEST),
    NUMBER_OF_PEOPLE_EXCEEDS_LIMIT(400, "Number of people exceeds the limit of the room.", HttpStatus.NOT_FOUND),
    END_DATE_BEFORE_START_DATE(400, "End date cannot be before start date.", HttpStatus.NOT_FOUND),
    TENANTS_EXCEEDS_NUMBER_OF_PEOPLE(400, "The number of tenants exceeds the number of people in the room.", HttpStatus.NOT_FOUND),
    CANNOT_DELETE_CONTRACT(400, "This room is still under contract and cannot be deleted.", HttpStatus.NOT_FOUND),
    CANNOT_REACTIVATE_EXPIRED_CONTRACT(400, "Expired contracts cannot be reactivated.", HttpStatus.BAD_REQUEST),
    CANNOT_TOGGLE_CONTRACT_STATUS(400, "Cannot transfer current contract status.", HttpStatus.BAD_REQUEST),
    OWNER_ID_REQUIRED(400, "Owner ID is required.", HttpStatus.BAD_REQUEST),
    IS_REPRESENTATIVE_REQUIRED(400, "Is Representative is required.", HttpStatus.BAD_REQUEST),
    TENANT_ALREADY_IN_CONTRACT(400, "This tenant already has a lease. Information cannot be changed.", HttpStatus.BAD_REQUEST),
    CANNOT_ADD_MORE_FLOORS(400, "Cannot add more floors than building's real number of floors.", HttpStatus.BAD_REQUEST),

    // Code: 404
    USER_NOT_FOUND(404, "User not found.", HttpStatus.NOT_FOUND),
    BUILDING_ID_NOT_FOUND(404, "Building ID not found.", HttpStatus.NOT_FOUND),
    CHAT_NOT_FOUND(404, "Chat not found.", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(404, "Message not found.", HttpStatus.NOT_FOUND),
    API_ENDPOINT_NOT_FOUND(404, "API endpoint not found.", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(404, "Permission not found.", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(404, "Role not found.", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(404, "Email not found.", HttpStatus.NOT_FOUND),
    BUILDING_NOT_FOUND(404, "Building not found.", HttpStatus.NOT_FOUND),
    FLOOR_NOT_FOUND(404, "floor not found.", HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND(404, "Room not found", HttpStatus.NOT_FOUND),
    TENANT_NOT_FOUND(404, "Tenant not found", HttpStatus.NOT_FOUND),
    ASSET_TYPE_NAME_INVALID(404, "Asset type can not be blank.", HttpStatus.NOT_FOUND),
    ASSET_GROUP_INVALID(404, "Asset group must not be null.", HttpStatus.NOT_FOUND),
    DESCRIPTION_INVALID(404, "Description asset type can not be blank.", HttpStatus.NOT_FOUND),
    ASSET_TYPE_NOT_FOUND(404, "Asset type not found", HttpStatus.NOT_FOUND),
    ASSET_NOT_FOUND(404, "Asset not found", HttpStatus.NOT_FOUND),
    VEHICLE_NOT_FOUND(404, "Vehicle not found.", HttpStatus.NOT_FOUND),
    METER_NOT_FOUND(404, "Meter not found.", HttpStatus.NOT_FOUND),
    SERVICE_NOT_FOUND(404, "service not found.", HttpStatus.NOT_FOUND),
    CONTRACT_NOT_FOUND(404, "Contract not found.", HttpStatus.NOT_FOUND),
    DEFAULT_SERVICE_NOT_FOUND(404, "Default Service not found.", HttpStatus.NOT_FOUND),


    // Code: 409
    OTP_ALREADY_SENT(409, "OTP already sent.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED(409, "User has been deleted.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_LOCKED(409, "User has been locked.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(409, "Email already existed.", HttpStatus.CONFLICT),
    PHONE_NUMBER_EXISTED(409, "Phone already existed.", HttpStatus.CONFLICT),
    FlOOR_EXISTED(409, "Floor already existed.", HttpStatus.CONFLICT),
    FLOOR_ALREADY_EXISTS(409, "Floor name already exists in this building.", HttpStatus.CONFLICT),
    PERMISSION_EXISTED(409, "Permission already existed.", HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(409, "Role already existed.", HttpStatus.BAD_REQUEST),
    BUILDING_CODE_EXISTED(409, "Building Code already existed.", HttpStatus.CONFLICT),
    BUILDING_NAME_EXISTED(409, "Building Name already existed.", HttpStatus.CONFLICT),
    ROOM_CODE_EXISTED(409, "Ma phong already existed.", HttpStatus.BAD_REQUEST),
    ID_NUMBER_EXISTED(409, "ID card number already existed.", HttpStatus.CONFLICT),
    ASSET_TYPE_EXISTED(409, "Asset Type already existed.", HttpStatus.BAD_REQUEST),
    LICENSE_PLATE_EXISTED(409, "License plate already exists", HttpStatus.BAD_REQUEST),
    DUPLICATE_ASSET_NAME(409,"Asset name already exists", HttpStatus.BAD_REQUEST),
    ROOM_EXISTED_SERVICE(409, "Room already has this service", HttpStatus.BAD_REQUEST),
    SERVICE_ROOM_NOT_FOUND(409, "Service room not found.", HttpStatus.NOT_FOUND),
    ROOM_ALREADY_HAS_CONTRACT(409,"This room is under contract.", HttpStatus.BAD_REQUEST),
    ;

    final int code;
    final String message;
    final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}