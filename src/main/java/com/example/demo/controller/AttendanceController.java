package com.example.demo.controller;

import com.example.demo.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // Kart okutulunca çağrılır (örneğin kart id POST edilir)
    @PostMapping("/record")
    public ResponseEntity<String> recordAttendance(@RequestParam Long employeeId, @RequestParam boolean isEntry) {
        attendanceService.recordEntry(employeeId, isEntry);
        return ResponseEntity.ok("Kayıt başarılı");
    }
}
