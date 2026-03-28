package com.classmanage.controller;

import com.classmanage.dto.request.LoginRequest;
import com.classmanage.dto.response.LoginResponse;
import com.classmanage.dto.response.UserInfoResponse;
import com.classmanage.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("认证接口测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("获取RSA公钥")
    void testGetPublicKey() throws Exception {
        when(authService.getPublicKey()).thenReturn("test-public-key");

        mockMvc.perform(get("/api/auth/public-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("test-public-key"));
    }

    @Test
    @DisplayName("用户登录成功")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("encrypted-password");

        LoginResponse response = new LoginResponse();
        response.setToken("jwt-token");
        response.setUserId(1L);
        response.setUsername("admin");
        response.setRealName("管理员");
        response.setRoleCode("ADMIN");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }


    @Test
    @DisplayName("登录参数校验-用户名为空")
    void testLoginWithEmptyUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取当前用户信息")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetCurrentUserInfo() throws Exception {
        UserInfoResponse userInfo = new UserInfoResponse();
        userInfo.setId(1L);
        userInfo.setUsername("admin");
        userInfo.setRealName("管理员");
        userInfo.setRoleCode("ADMIN");

        when(authService.getCurrentUserInfo()).thenReturn(userInfo);

        mockMvc.perform(get("/api/auth/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("用户登出")
    @WithMockUser(username = "admin")
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
