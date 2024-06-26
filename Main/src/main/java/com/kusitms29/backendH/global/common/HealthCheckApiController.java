package com.kusitms29.backendH.global.common;

import com.kusitms29.backendH.domain.chat.entity.ChatContent;
import com.kusitms29.backendH.domain.chat.entity.Room;
import com.kusitms29.backendH.domain.chat.repository.RoomRepository;
import com.kusitms29.backendH.domain.user.entity.User;
import com.kusitms29.backendH.domain.user.service.UserReader;
import com.kusitms29.backendH.infra.config.TranslateConfig;
import com.kusitms29.backendH.infra.config.auth.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api")
public class HealthCheckApiController {
    private MongoTemplate mongoTemplate;
    private RoomRepository roomRepository;
    private UserReader userReader;
    private static final String GOOGLE_AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    private static final String KAKAO_AUTH_ENDPOINT = "https://kauth.kakao.com/oauth/authorize";
    private static final String KAKAO_TOKEN_ENDPOINT = "https://kauth.kakao.com/oauth/token";

    @Value("${app.google.client.id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${app.google.client.secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${app.google.callback.url}")
    private String GOOGLE_REDIRECT_URI;

    @Value("${app.kakao.client.id}")
    private String KAKAO_CLIENT_ID;

    @Value("${app.kakao.client.secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${app.kakao.callback.url}")
    private String KAKAO_REDIRECT_URI;
    private Properties promptMap = new Properties();
    private final TranslateConfig translateConfig;
    public HealthCheckApiController(UserReader userReader, MongoTemplate mongoTemplate, RoomRepository roomRepository, TranslateConfig translateConfig) {
        this.mongoTemplate = mongoTemplate;
        this.roomRepository = roomRepository;
        this.translateConfig = translateConfig;
        this.userReader = userReader;
    }
    @GetMapping("google")
    public ResponseEntity<String> googleOauth(HttpServletRequest request) throws IOException {
        String code = extractCode(request);

        if (code == null) {
            String authUrl = GOOGLE_AUTH_ENDPOINT +
                    "?client_id=" + GOOGLE_CLIENT_ID +
                    "&redirect_uri=" + GOOGLE_REDIRECT_URI +
                    "&response_type=code" +
                    "&scope=email%20profile";

            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, authUrl).build();
        } else {
            RestTemplate restTemplate = new RestTemplate();
            String accessToken = restTemplate.postForObject(GOOGLE_TOKEN_ENDPOINT +
                    "?client_id=" + GOOGLE_CLIENT_ID +
                    "&client_secret=" + GOOGLE_CLIENT_SECRET +
                    "&redirect_uri=" + GOOGLE_REDIRECT_URI +
                    "&code=" + code +
                    "&grant_type=authorization_code", null, String.class);

            // Access Token을 이용한 추가 처리 로직 작성
            // ...

            return new ResponseEntity<>(accessToken, HttpStatus.OK);
        }
    }

    private String extractCode(HttpServletRequest request) {
        String fullUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        if (queryString != null && queryString.contains("code=")) {
            return queryString.split("code=")[1].split("&")[0];
        }

        return null;
    }
    @GetMapping("/oauth/google")
    public void googleOauth(HttpServletResponse response) throws IOException {
        String authUrl = GOOGLE_AUTH_ENDPOINT +
                "?client_id=" + GOOGLE_CLIENT_ID +
                "&redirect_uri=" + GOOGLE_REDIRECT_URI +
                "&response_type=code" +
                "&scope=email%20profile";

        response.sendRedirect(authUrl);
    }

    @GetMapping("/oauth/google/callback")
    public ResponseEntity<String> googleOauthCallback(@RequestParam(name = "code") String code) {
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = restTemplate.postForObject(GOOGLE_TOKEN_ENDPOINT +
                "?client_id=" + GOOGLE_CLIENT_ID +
                "&client_secret=" + GOOGLE_CLIENT_SECRET +
                "&redirect_uri=" + GOOGLE_REDIRECT_URI +
                "&code=" + code +
                "&grant_type=authorization_code", null, String.class);

        // Access Token을 이용한 추가 처리 로직 작성
        // ...

        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @GetMapping("/oauth/kakao")
    public void kakaoOauth(HttpServletResponse response) throws IOException {
        String authUrl = KAKAO_AUTH_ENDPOINT +
                "?client_id=" + KAKAO_CLIENT_ID +
                "&redirect_uri=" + KAKAO_REDIRECT_URI +
                "&response_type=code";

        response.sendRedirect(authUrl);
    }

    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<String> kakaoOauthCallback(@RequestParam(name = "code") String code) {
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = restTemplate.postForObject(KAKAO_TOKEN_ENDPOINT +
                "?grant_type=authorization_code" +
                "&client_id=" + KAKAO_CLIENT_ID +
                "&client_secret=" + KAKAO_CLIENT_SECRET +
                "&redirect_uri=" + KAKAO_REDIRECT_URI +
                "&code=" + code, null, String.class);

        // Access Token을 이용한 추가 처리 로직 작성
        // ...

        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }
    @RequestMapping("/")
    public Long MeetUpServer(@UserId Long userId) {
        return userId;
    }
    @PostMapping("/chat")
    public void chat(@UserId Long userId, @RequestBody ChatReq chatReq){
        User user = userReader.getByUserId(userId);
        Query query = new Query();
        query.addCriteria(Criteria.where("roomName").is(chatReq.roomName()));
        Room room =  mongoTemplate.findOne(query, Room.class);

            ChatContent chatContent = ChatContent.createChatContent(user.getUserName(), chatReq.content(), room, "https://sync-content-bucket-01.s3.ap-northeast-2.amazonaws.com/94f1a566-0072-45e9-944f-707a6e21bbbf.png");
//            room.addChatContent(chatContent);
            roomRepository.save(room);

    }
    @GetMapping("/prompt")
    public ResponseEntity<String> prompt(@RequestParam(name = "string") String string) {
        String translationPrompt = translateConfig.getPromptValue(string);
//        System.out.println("Prompt Map: " + );
        return new ResponseEntity<>(translationPrompt, HttpStatus.OK);
    }

    private void loadPromptMap() {
        try (InputStream inputStream = getClass().getResourceAsStream("/prompt.properties")) {
            promptMap.load(inputStream);
            System.out.println("Prompt Map: " + promptMap);
        } catch (IOException e) {
            // log.error("Error loading prompt map", e);
        }
    }
}