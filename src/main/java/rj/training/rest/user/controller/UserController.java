package rj.training.rest.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import rj.training.rest.user.User;
import rj.training.rest.user.repository.UserRepository;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    // Endpoint to set a custom response header
    @GetMapping("/customHeader")
    public ResponseEntity<Map<String, String>> getWithCustomHeader() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Custom header response");
        return ResponseEntity.ok()
                .header("X-Custom-Header", "CustomHeaderValue")
                .body(response);
    }
    

    // Endpoint that redirects to final destination
    @GetMapping("/temporary-redirect")
    public ResponseEntity<Void> temporaryRedirect() {
        return ResponseEntity.status(HttpStatus.FOUND) // 302 redirect
                .header("Location", "/api/users/final-destination")
                .build();
    }

    // Final destination endpoint
    @GetMapping("/final-destination")
    public ResponseEntity<String> finalDestination() {
        return ResponseEntity.ok("You have reached the final destination!");
    }

    // Endpoint to set a cookie in the response
    @GetMapping("/setCookie")
    public ResponseEntity<Map<String, String>> setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("sessionID", "12345");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Cookie has been set");
        return ResponseEntity.ok(responseBody);
    }

    // Endpoint to demonstrate a redirect
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirectToAllUsers() {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/api/users")).build();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedUser.setId(id); // Ensures the ID is set for the update
        User savedUser = userRepository.save(updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/form")
    public ResponseEntity<User> createUserWithFormParams(@RequestParam String name, @RequestParam String email) {
        User user = new User(null, name, email);
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUser);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        response.put("fileName", file.getOriginalFilename());
        response.put("message", "File uploaded successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadWithDescription")
    public ResponseEntity<Map<String, String>> uploadFileWithDescription(@RequestParam("file") MultipartFile file,
                                                                         @RequestParam("description") String description) {
        Map<String, String> response = new HashMap<>();
        response.put("fileName", file.getOriginalFilename());
        response.put("description", description);
        response.put("message", "File uploaded with description successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> getUserByName(@RequestParam String name) {
        List<User> filteredUsers = userRepository.findAll().stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .toList();
        return ResponseEntity.ok(filteredUsers);
    }
}
