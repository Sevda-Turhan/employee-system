package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.QRCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final QRCodeService qrCodeService;


      @PostMapping("/addWithQr")
      public ResponseEntity<?> addEmployeeWithQr(@RequestBody Employee employee) {
          try {
              Employee savedEmployee = employeeService.ekleEmployee(employee);
              return ResponseEntity.ok(savedEmployee);
          } catch (IllegalArgumentException ex) {
              // Kullanıcıya özel hata mesajı gönder
              return ResponseEntity.status(HttpStatus.CONFLICT).body(
                      Map.of("message", ex.getMessage())  // JSON formatlı mesaj
              );
          } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                      Map.of("message", "❌ Bir hata oluştu, lütfen tekrar deneyin.")
              );
          }


      }


    // 2. Tüm personelleri getir
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // 3. QR verisine göre personel getir
    @GetMapping("/qr/{code}")
    public Employee getByQRCodeData(@PathVariable String code) {
        return employeeService.getByQrCodeData(code);
    }

    @GetMapping("/qr-image/{id}")
    public ResponseEntity<String> getQrCodeImage(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null || employee.getQrCodeData() == null) {
            return ResponseEntity.notFound().build();
        }
        // Base64 stringini doğrudan döndürüyoruz
        return ResponseEntity.ok(employee.getQrCodeData());
    }


    @PostMapping("/generateQrCodesForExisting")
    public ResponseEntity<String> generateQrCodesForExisting() {
        try {
            employeeService.generateQrCodesForExistingEmployees();
            return ResponseEntity.ok("QR kodları başarıyla oluşturuldu.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("QR kodları oluşturulurken hata oluştu: " + e.getMessage());
        }

      }

    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "employee-profile"; // Thymeleaf dosyası
    }

    @PostMapping("/employees/add")
    public String addEmployee(Employee employee, Model model) {
        try {
            employeeService.saveNewEmployee(employee);
            model.addAttribute("successMessage", "Personel başarıyla eklendi.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Personel eklenirken hata oluştu: " + e.getMessage());
        }
        return "redirect:/employees";  // veya ilgili listeleme sayfası
    }




}











