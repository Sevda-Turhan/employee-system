package com.example.demo.controller;

import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;

    public AdminController(EmployeeService employeeService, AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        model.addAttribute("totalEmployees", employeeService.getTotalEmployeeCount());
        model.addAttribute("departmentCount", employeeService.getDepartmentCount());
        model.addAttribute("todayAttendance", employeeService.getTodayAttendanceCount()); // opsiyonel
        model.addAttribute("todayAttendance", attendanceService.getTodayEntryCount()); // buraya ekledik

        return "admin-dashboard";  // Thymeleaf şablonu
    }



}
