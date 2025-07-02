package com.example.demo.service;

import com.example.demo.model.Attendance;
import com.example.demo.model.Employee;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public long getTodayEntryCount() {
        LocalDate today = LocalDate.now();
        return attendanceRepository.countDistinctByDate(today);
    }


    public void recordEntry(Long employeeId, boolean isEntry) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee bulunamadı"));

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setTimestamp(LocalDateTime.now());
        attendance.setEntry(isEntry); // true = giriş, false = çıkış

        attendanceRepository.save(attendance);
    }

    public List<Attendance> getTodayAttendanceRecords() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay(); // 00:00
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1); // 23:59:59.999999999

        return attendanceRepository.findByTimestampBetween(start, end);
    }





}
