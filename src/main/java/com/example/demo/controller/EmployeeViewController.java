package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeViewController {


    private final EmployeeService employeeService;


    @GetMapping("/qr-view/{id}")
    public String viewQrCodePage(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return "error"; // Basit hata sayfası (oluşturabilirsin)
        }
        model.addAttribute("employee", employee);
        return "qr";  // templates/qr.html dosyasını render eder
    }

    @GetMapping("/view")//HTML'i render eden method
    public String viewEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees"; // => employees.html dosyasını render eder
    }
//HATA AYIKLAMAK İÇİN ÇIKTIYI GÖRMEK İÇİN YAZILDI.
//    @GetMapping("/view")
//    public String viewEmployees(Model model) {
//        List<Employee> list = employeeService.getAllEmployees();
//        System.out.println(">> Personel sayısı: " + list.size()); // DEBUG
//        model.addAttribute("employees", list);
//        return "employees";
//    }

    @PostMapping("/return-form")
    public String addEmployeeWithQrHtml(@ModelAttribute Employee employee) throws Exception {
        employeeService.ekleEmployee(employee);
        return "redirect:/employees/view"; // Listeye geri döner
    }

    // Form sayfasını göster
    @GetMapping("/add-form")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee"; // templates/add-employee.html
    }


    @PostMapping("/add-form")
    public String handleAddForm(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        try {
            employeeService.ekleEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Personel başarıyla eklendi!");
            return "redirect:/employees/view";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/employees/add-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bir hata oluştu, tekrar deneyin.");
            return "redirect:/employees/add-form";
        }
    }


    @GetMapping("/check-duplicate")
    @ResponseBody
    public Map<String, Boolean> checkDuplicate(@RequestParam String name, @RequestParam String department) {
        boolean exists = employeeService.existsByNameAndDepartment(name, department);
        return Collections.singletonMap("exists", exists);
    }


    @GetMapping("/employees/check-duplicate-edit")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateEdit(@RequestParam Long id, @RequestParam String name, @RequestParam String department) {
        boolean exists = employeeService.existsByNameAndDepartmentExcludeId(name, department, id);
        return Map.of("exists", exists);
    }

    // Personel düzenleme formunu göster (GET /employees/edit/{id})
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            return "redirect:/employees/view";
        }
        model.addAttribute("employee", employee);
        return "employee-edit-form"; // Düzenleme formu sayfası
    }

    // Personel düzenle (POST /employees/edit/{id})
    @PostMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable("id") Long id,
                                   @ModelAttribute Employee employee,
                                   RedirectAttributes redirectAttributes) {
        employee.setId(id);
        employeeService.save(employee);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Personel başarıyla güncellendi!");
        return "redirect:/employees/view";
    }


    // Personel sil (GET /employees/delete/{id})
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        employeeService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Personel başarıyla silindi.");
        return "redirect:/employees/view";
    }

    @GetMapping("/view/{id}")
    public String viewEmployee(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            model.addAttribute("errorMessage", "Personel bulunamadı!");
            // employee-view sayfasını döndür, burada hata mesajı gösterilecek
            return "employee-view";
        }
        model.addAttribute("employee", employee);
        return "employees-view";
    }

    @PostMapping("/employees/add")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        try {
            Employee saved = employeeService.saveNewEmployee(employee);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata: " + e.getMessage());
        }
    }

}