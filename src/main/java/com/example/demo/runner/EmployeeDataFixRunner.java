package com.example.demo.runner;

import com.example.demo.service.EmployeeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDataFixRunner  implements CommandLineRunner {
    private final EmployeeService employeeService;

    public EmployeeDataFixRunner(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void run(String... args) throws Exception {
        employeeService.fillMissingPersonelNoAndQRCode();
        System.out.println("Eksik personelNo ve QR kodlar güncellendi.");
    }
}
