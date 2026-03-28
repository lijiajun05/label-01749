package com.classmanage.controller;

import com.classmanage.entity.AttendanceRecord;
import com.classmanage.service.AttendanceService;
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

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("考勤管理接口测试")
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendanceService attendanceService;

    @Test
    @DisplayName("分页查询考勤记录")
    @WithMockUser(roles = {"ADMIN"})
    void testListAttendance() throws Exception {
        AttendanceRecord record = new AttendanceRecord();
        record.setId(1L);
        record.setStudentId(1L);
        record.setCourseId(1L);
        record.setStatus(1);
        
        Page<AttendanceRecord> page = new PageImpl<>(List.of(record), PageRequest.of(0, 10), 1);

        when(attendanceService.getPage(anyInt(), anyInt(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/attendances")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}
