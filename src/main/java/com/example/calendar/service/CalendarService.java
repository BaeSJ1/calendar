package com.example.calendar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {
    //1. restTemplate 객체 생성
    private final RestTemplate restTemplate;
    /** 토큰 발급 주소*/
    private final String token_url = "https://oauth2.googleapis.com/token";
    /** 클라이언트 ID*/
    private final String client_id = "823689030404-i0hb7o89540d0abbd0ukmhpfvhbv4utr.apps.googleusercontent.com";
    /** 클라이언트 보안 비번 */
    private final String client_secret = "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";
    private final String grant_type = "refresh_token";
    /** 리프레쉬 토큰  */
    private final String refresh_token = "1//0e1WXR2Pzy0s2CgYIARAAGA4SNwF-L9Ir0XZLlIye3ElFTCMjPWwtubx9vWxlcx7IyuKbjotXGvV_SMPl5Jc_iuTJIGb81_V81qQ";
    private String accessToken; // TTL 1시간

    public void renewToken() {
        //2. Request Body 설정 (body는 x-www-form-urlencoded type)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id",client_id);
        body.add("client_secret", client_secret);
        body.add("grant_type", grant_type);
        body.add("refresh_token", refresh_token);

        //3. header 설정을 위해 HttpHeader 클래스를 생성한 후 HttpEntity 객체에 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(body, headers);

        //4. exchange() 메소드로 api 호출
        ResponseEntity<String> response = restTemplate.exchange(token_url, HttpMethod.POST,entity, String.class);

        ObjectMapper mapper = new ObjectMapper();

        try{
            String responseBody = (String) response.getBody(); //응답 Body 값
            if(responseBody != null){
                JsonNode jsonNode = mapper.readTree(responseBody);
                //5. access token 획득
                accessToken = jsonNode.get("access_token").asText();
            }
        } catch (Exception e){
            log.error("Failed to parse access token", e);
        }
    }

    public Boolean createCalendar(){
        String calendarUrl = "https://www.googleapis.com/calendar/v3/calendars/itnj.sjbae@gmail.com/events";
        //param이나 body값 요청을 통해 동적으로 관리할 수 있도록 dto 추가하면 좋을듯?

        //요청 바디 생성
        Map<String,Object> body = new HashMap<>();
        body.put("summary", "테스트중");

        Map<String,String> start = new HashMap<>();
        start.put("dateTime", "2024-06-21T16:00:00");
        start.put("timeZone", "Asia/Seoul");
        body.put("start", start);

        Map<String,String> end = new HashMap<>();
        end.put("dateTime", "2024-06-21T18:00:00");
        end.put("timeZone", "Asia/Seoul");
        body.put("end",end);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body,headers);

        //요청
        ResponseEntity<String> response = restTemplate.exchange(calendarUrl,HttpMethod.POST, entity, String.class);


        return  response.getStatusCode().equals(200);
    }
}