package ru.miroro.integration.cdek.deliverypoints;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.miroro.api.location.entity.Address;
import ru.miroro.api.location.repository.AddressRepository;
import ru.miroro.integration.cdek.access_token.CdekTokenService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CdekDeliveryPointService {
    private final RestTemplate restTemplate;
    private final CdekTokenService tokenService;
    private final AddressRepository addressRepository;

    @Value("${cdek.auth.url}")
    private String apiBaseUrl;

    public List<Address> getAddresses(String cityUuid) {

        String url = String.format("%s/deliverypoints?city_uuid=%s", apiBaseUrl, cityUuid);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getAccessToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<CdekDeliveryPoint[]> response =
                restTemplate.exchange(url, HttpMethod.GET, request, CdekDeliveryPoint[].class);

        CdekDeliveryPoint[] deliveryPoints = response.getBody();

        if (deliveryPoints == null) {
            return List.of();
        }

        return Arrays.stream(deliveryPoints)
                .map(dp -> dp.getLocation().getAddress())
                .map(addressName -> saveAndReturn(addressName, cityUuid))
                .collect(Collectors.toList());
    }

    private Address saveAndReturn(String addressName, String cityUuid) {

        Address existing = addressRepository.findByName(addressName);
        if (existing != null) {
            return existing;
        }

        try {
            int id = addressRepository.save(addressName, cityUuid);
            return new Address(id, addressName, cityUuid);

        } catch (DuplicateKeyException e) {
            log.debug("address '{}' already exists", addressName);
            return addressRepository.findByName(addressName);
        }
    }
}
