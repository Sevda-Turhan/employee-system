package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeLoginController {



    private final EmployeeService employeeService;
    public EmployeeLoginController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

//    @GetMapping("/employee-login")
//    public String showEmployeeLogin() {
//        return "employee-login";
//    }

    @PostMapping("/employee-login")
    public String employeeLogin(@RequestParam String personelNo, Model model, HttpSession session) {
        Employee employee = employeeService.findByPersonelNo(personelNo);
        if (employee == null) {
            model.addAttribute("errorMessage", "Personel numarası bulunamadı.");
            return "login";  // login.html sayfasına geri dön, hata mesajı göster
        }
        session.setAttribute("loggedInEmployee", employee);
        return "redirect:/employee/view/" + employee.getId();
    }


}
