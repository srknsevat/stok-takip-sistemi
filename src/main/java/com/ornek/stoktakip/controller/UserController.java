package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String userList(Model model) {
        List<User> users = userService.getActiveUsers();
        model.addAttribute("users", users);
        return "users/list";
    }
    
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.UserRole.values());
        return "users/create";
    }
    
    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, @RequestParam String password) {
        try {
            userService.createUser(user.getUsername(), user.getEmail(), password, 
                                user.getFirstName(), user.getLastName(), user.getRole());
            return "redirect:/users?success=created";
        } catch (Exception e) {
            return "redirect:/users/create?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("roles", User.UserRole.values());
            return "users/edit";
        }
        return "redirect:/users?error=user_not_found";
    }
    
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        try {
            user.setId(id);
            userService.updateUser(user);
            return "redirect:/users?success=updated";
        } catch (Exception e) {
            return "redirect:/users/edit/" + id + "?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/users?success=deleted";
        } catch (Exception e) {
            return "redirect:/users?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/activate/{id}")
    public String activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return "redirect:/users?success=activated";
        } catch (Exception e) {
            return "redirect:/users?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return "redirect:/users?success=deactivated";
        } catch (Exception e) {
            return "redirect:/users?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/change-password/{id}")
    public String changePassword(@PathVariable Long id, @RequestParam String newPassword) {
        try {
            userService.changePassword(id, newPassword);
            return "redirect:/users?success=password_changed";
        } catch (Exception e) {
            return "redirect:/users?error=" + e.getMessage();
        }
    }
    
    // API Endpoints
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<User>> getUsersApi() {
        List<User> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserApi(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createUserApi(@RequestBody User user, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        try {
            User createdUser = userService.createUser(user.getUsername(), user.getEmail(), password,
                                                    user.getFirstName(), user.getLastName(), user.getRole());
            response.put("success", true);
            response.put("user", createdUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUserApi(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            response.put("success", true);
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUserApi(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.deleteUser(id);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
