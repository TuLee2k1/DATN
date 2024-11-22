package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.com.dto.ServicePayDto;
import poly.com.dto.response.ServicePayResponse;
import poly.com.model.ServicePay;
import poly.com.repository.ServicePayRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicePayService {
    private final ServicePayRepository servicePayRepository;

    public List<ServicePayResponse> getAllServicePays() {
        return servicePayRepository.findAll().stream().map(this::convertToServicePayResponse).collect(Collectors.toList());
    }

    public ServicePayResponse getServicePayById(Long id) {
        return servicePayRepository.findById(id).map(this::convertToServicePayResponse).orElseThrow(() ->
        new RuntimeException("Không tìm thấy dịch vụ"));
    }

    public ServicePayResponse saveServicePay(ServicePayDto servicePayDto) {
        ServicePay servicePay = new ServicePay();
        servicePay.setServiceTitle(servicePayDto.getServiceTitle());
        servicePay.setServiceDesc(servicePayDto.getServiceDesc());
        servicePay.setServicePrice(servicePayDto.getServicePrice());
        servicePay.setServiceDuration(servicePayDto.getServiceDuration());
        servicePay.setServiceType(servicePayDto.getServiceType());
        servicePay.setServiceImage(servicePayDto.getServiceImage());
        servicePay.setServiceStatus(servicePayDto.getServiceStatus());
        return convertToServicePayResponse(servicePayRepository.save(servicePay));
    }

    public ServicePayResponse updateServicePay(Long id, ServicePayDto servicePayDto) {
        ServicePay servicePay = servicePayRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));
        servicePay.setServiceTitle(servicePayDto.getServiceTitle());
        servicePay.setServiceDesc(servicePayDto.getServiceDesc());
        servicePay.setServicePrice(servicePayDto.getServicePrice());
        servicePay.setServiceDuration(servicePayDto.getServiceDuration());
        servicePay.setServiceType(servicePayDto.getServiceType());
        servicePay.setServiceImage(servicePayDto.getServiceImage());
        servicePay.setServiceStatus(servicePayDto.getServiceStatus());
        servicePay.setUpdatedAt(servicePay.getUpdatedAt());
        return convertToServicePayResponse(servicePayRepository.save(servicePay));
    }

    public void deleteServicePay(Long id) {
        servicePayRepository.deleteById(id);
    }

    private ServicePayResponse convertToServicePayResponse(ServicePay servicePay) {
        return ServicePayResponse.builder()
                .id(servicePay.getId())
                .serviceTitle(servicePay.getServiceTitle())
                .serviceDesc(servicePay.getServiceDesc())
                .serviceDuration(servicePay.getServiceDuration())
                .servicePrice(servicePay.getServicePrice())
                .serviceType(servicePay.getServiceType())
                .serviceImage(servicePay.getServiceImage())
                .serviceStatus(servicePay.getServiceStatus())
                .createdAt(servicePay.getCreatedAt())
                .updatedAt(servicePay.getUpdatedAt())
                .build();
    }
}
