package kr.me.seesaw.controller;


import org.springframework.web.bind.annotation.RestController;
import kr.me.seesaw.entity.User;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kr.me.seesaw.dto.LoginRequest;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {

        Optional<User> userOption = userRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());

        Map<String, Object> response = new HashMap<>();

        if (userOption.isPresent()) {
            User user = userOption.get();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole()
            ));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "입력정보가 올바르지 않습니다.");
            return ResponseEntity.status(401).body(response);
        }
    }


}
