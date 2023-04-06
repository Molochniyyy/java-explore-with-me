package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class StatisticClient {
    protected final RestTemplate restTemplate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatisticClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ViewStatsDto[] getStatArray(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = getParametersMap(start, end, uris, unique);
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<ViewStatsDto[]> responseEntity = restTemplate.getForEntity(path, ViewStatsDto[].class, parameters);
        return responseEntity.getBody();
    }

    private static Map<String, Object> getParametersMap(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        return Map.of("start", start.format(DATE_TIME_FORMATTER), "end", end.format(DATE_TIME_FORMATTER), "uris", uris, "unique", unique);
    }

    public List<ViewStatsDto> getStatList(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = getParametersMap(start, end, uris, unique);
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<ViewStatsDto>> responseEntity = restTemplate.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        }, parameters);
        return responseEntity.getBody();
    }

    // Данный метод - альтернатива двум предыдущим методам getStatArray и getStatList.
    public ResponseEntity<Object> getStatObject(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = getParametersMap(start, end, uris, unique);
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    // две взаимозаменяемых версии метода postStat (используются оба)
    public ResponseEntity<?> postStatMonolith(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHitDto hitDtoRequest = new EndpointHitDto(null, app, uri, ip, timestamp.format(DATE_TIME_FORMATTER));
        HttpEntity<EndpointHitDto> httpEntity = new HttpEntity<>(hitDtoRequest);
        return restTemplate.postForEntity("/hit", httpEntity, ResponseEntity.class);
    }

    public ResponseEntity<Object> postStat(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHitDto hitDtoRequest = new EndpointHitDto(null, app, uri, ip, timestamp.format(DATE_TIME_FORMATTER));
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, hitDtoRequest);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, null);

        ResponseEntity<Object> responseEntity;
        try {
            if (parameters != null) {
                responseEntity = restTemplate.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                responseEntity = restTemplate.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(responseEntity);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
