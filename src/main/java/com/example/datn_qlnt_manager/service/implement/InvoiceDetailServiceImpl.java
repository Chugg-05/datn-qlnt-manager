package com.example.datn_qlnt_manager.service.implement;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.datn_qlnt_manager.common.InvoiceItemType;
import com.example.datn_qlnt_manager.dto.request.invoiceDetail.InvoiceDetailCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoiceDetail.InvoiceDetailUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.InvoiceDetail;
import com.example.datn_qlnt_manager.entity.ServiceRoom;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.InvoiceMapper;
import com.example.datn_qlnt_manager.repository.InvoiceDetailsRepository;
import com.example.datn_qlnt_manager.repository.InvoiceRepository;
import com.example.datn_qlnt_manager.repository.ServiceRoomRepository;
import com.example.datn_qlnt_manager.service.InvoiceDetailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceDetailServiceImpl implements InvoiceDetailService {

    InvoiceRepository invoiceRepository;
    InvoiceDetailsRepository invoiceDetailsRepository;
    ServiceRoomRepository serviceRoomRepository;
    InvoiceMapper invoiceMapper;

    @Transactional
    @Override
    public InvoiceItemResponse createInvoiceDetail(InvoiceDetailCreationRequest request) {
        Invoice invoice = getInvoice(request.getInvoiceId());

        InvoiceDetail detail;

        switch (request.getInvoiceItemType()) {
            case TIEN_PHONG -> detail = handleRoomCharge(invoice, request);
            case DIEN, NUOC -> detail = handleMeterService(invoice, request);
            case DICH_VU -> detail = handleFixedService(invoice, request);
            case DEN_BU -> detail = handleCompensation(invoice, request);
            default -> throw new AppException(ErrorCode.INVALID_SERVICE_TYPE);
        }

        invoice.getDetails().add(detail);
        recalculateInvoiceTotal(invoice);
        invoiceRepository.save(invoice);

        return invoiceMapper.toItemResponse(detail);
    }

    @Transactional
    @Override
    public InvoiceItemResponse updateInvoiceDetail(String detailId, InvoiceDetailUpdateRequest request) {
        InvoiceDetail detail = getInvoiceDetail(detailId);

        if (request.getNewIndex() != null) {
            if (detail.getOldIndex() != null && request.getNewIndex() < detail.getOldIndex()) {
                throw new AppException(ErrorCode.INVALID_METER_READING);
            }
            detail.setNewIndex(request.getNewIndex());
            if (detail.getOldIndex() != null) {
                detail.setQuantity(detail.getNewIndex() - detail.getOldIndex());
            }
        }

        if (request.getQuantity() != null && detail.getInvoiceItemType() != InvoiceItemType.TIEN_PHONG) {
            detail.setQuantity(request.getQuantity());
        }

        if (request.getUnitPrice() != null) {
            detail.setUnitPrice(request.getUnitPrice());
        }

        if (request.getDescription() != null) {
            detail.setDescription(request.getDescription());
        }

        detail.recalculateAmount();
        invoiceDetailsRepository.save(detail);

        Invoice invoice = detail.getInvoice();

        recalculateInvoiceTotal(invoice);
        invoiceRepository.save(invoice);

        return invoiceMapper.toItemResponse(detail);
    }

    @Override
    public void deleteInvoiceDetail(String detailId) {
        InvoiceDetail detail = getInvoiceDetail(detailId);
        Invoice invoice = detail.getInvoice();

        if (detail.getInvoiceItemType() == InvoiceItemType.TIEN_PHONG) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ROOM_CHARGE);
        }

        invoice.getDetails().removeIf(d -> d.getId().equals(detailId));
        recalculateInvoiceTotal(invoice);

        invoiceRepository.save(invoice);
    }

    private InvoiceDetail handleRoomCharge(Invoice invoice, InvoiceDetailCreationRequest request) {
        // Không cho tạo nhiều lần
        validateDuplicateDetail(invoice, request);

        return InvoiceDetail.builder()
                .invoice(invoice)
                .invoiceItemType(InvoiceItemType.TIEN_PHONG)
                .serviceName("Tiền phòng")
                .quantity(1)
                .unitPrice(invoice.getContract().getRoom().getPrice())
                .amount(invoice.getContract().getRoom().getPrice())
                .description("Tiền phòng tháng " + invoice.getMonth() + "/" + invoice.getYear())
                .build();
    }

    private InvoiceDetail handleMeterService(Invoice invoice, InvoiceDetailCreationRequest request) {
        validateDuplicateDetail(invoice, request);

        ServiceRoom serviceRoom = getServiceRoomIfNeeded(request);
        if (serviceRoom == null || serviceRoom.getService() == null) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }

        int old = invoice.getDetails().stream()
                .filter(d -> d.getInvoiceItemType() == request.getInvoiceItemType())
                .map(InvoiceDetail::getNewIndex)
                .max(Integer::compareTo)
                .orElse(0);

        if (request.getNewIndex() == null || request.getNewIndex() < old) {
            throw new AppException(ErrorCode.INVALID_METER_READING);
        }

        int quantity = request.getNewIndex() - old;

        return InvoiceDetail.builder()
                .invoice(invoice)
                .invoiceItemType(request.getInvoiceItemType())
                .serviceRoom(serviceRoom)
                .serviceName(serviceRoom.getService().getName())
                .oldIndex(old)
                .newIndex(request.getNewIndex())
                .quantity(quantity)
                .unitPrice(serviceRoom.getService().getPrice())
                .amount(serviceRoom.getService().getPrice().multiply(BigDecimal.valueOf(quantity)))
                .description("Dịch vụ " + serviceRoom.getService().getName())
                .build();
    }

    private InvoiceDetail handleFixedService(Invoice invoice, InvoiceDetailCreationRequest request) {
        validateDuplicateDetail(invoice, request);

        ServiceRoom serviceRoom = getServiceRoomIfNeeded(request);
        if (serviceRoom == null || serviceRoom.getService() == null) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }

        int quantity = request.getQuantity() != null ? request.getQuantity() : 1;

        return InvoiceDetail.builder()
                .invoice(invoice)
                .invoiceItemType(InvoiceItemType.DICH_VU)
                .serviceRoom(serviceRoom)
                .serviceName(serviceRoom.getService().getName())
                .quantity(quantity)
                .unitPrice(serviceRoom.getService().getPrice())
                .amount(serviceRoom.getService().getPrice().multiply(BigDecimal.valueOf(quantity)))
                .description("Dịch vụ " + serviceRoom.getService().getName())
                .build();
    }

    private InvoiceDetail handleCompensation(Invoice invoice, InvoiceDetailCreationRequest request) {
        if (request.getUnitPrice() == null || request.getQuantity() == null) {
            throw new AppException(ErrorCode.INVALID_COMPENSATION_DATA);
        }

        String name = StringUtils.hasText(request.getServiceName()) ? request.getServiceName() : "Đền bù thiệt hại";

        return InvoiceDetail.builder()
                .invoice(invoice)
                .invoiceItemType(InvoiceItemType.DEN_BU)
                .serviceName(name)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .amount(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .description(request.getDescription() != null ? request.getDescription() : "Bồi thường tài sản")
                .build();
    }

    private Invoice getInvoice(String invoiceId) {
        return invoiceRepository.findById(invoiceId).orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
    }

    private InvoiceDetail getInvoiceDetail(String detailId) {
        return invoiceDetailsRepository
                .findById(detailId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_DETAIL_NOT_FOUND));
    }

    private void validateDuplicateDetail(Invoice invoice, InvoiceDetailCreationRequest request) {
        InvoiceItemType itemType = request.getInvoiceItemType();
        if (itemType == InvoiceItemType.DEN_BU) return;

        boolean exists;

        if (itemType == InvoiceItemType.TIEN_PHONG) {
            exists = invoice.getDetails().stream()
                    .anyMatch(detail -> detail.getInvoiceItemType() == InvoiceItemType.TIEN_PHONG);
        } else {
            String serviceRoomId = request.getServiceRoomId();
            if (!StringUtils.hasText(serviceRoomId)) {
                throw new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND);
            }

            exists = invoice.getDetails().stream()
                    .anyMatch(detail -> detail.getInvoiceItemType() == itemType
                            && detail.getServiceRoom() != null
                            && serviceRoomId.equals(detail.getServiceRoom().getId()));
        }

        if (exists) {
            throw new AppException(ErrorCode.DUPLICATE_INVOICE_DETAIL);
        }
    }

    private ServiceRoom getServiceRoomIfNeeded(InvoiceDetailCreationRequest request) {
        if (request.getServiceRoomId() == null) return null;
        return serviceRoomRepository
                .findById(request.getServiceRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND));
    }

    private void recalculateInvoiceTotal(Invoice invoice) {
        BigDecimal total = invoice.getDetails().stream()
                .map(InvoiceDetail::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(total);
    }
}
