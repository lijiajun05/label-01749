package com.classmanage.controller;

import com.classmanage.entity.GradeInfo;
import com.classmanage.service.GradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("成绩管理接口测试")
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradeService gradeService;

    @Test
    @DisplayName("分页查询成绩列表")
    @WithMockUser(roles = {"ADMIN"})
    void testListGrades() throws Exception {
        GradeInfo grade = new GradeInfo();
        grade.setId(1L);
        grade.setStudentId(1L);
        grade.setCourseId(1L);
        grade.setScore(new BigDecimal("85.5"));
        grade.setGradeType(1);
        
        Page<GradeInfo> page = new PageImpl<>(List.of(grade), PageRequest.of(0, 10), 1);

        when(gradeService.getPage(anyInt(), anyInt(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/grades")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}
