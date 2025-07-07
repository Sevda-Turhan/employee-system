package com.example.demo.service;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.google.zxing.WriterException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class EmployeeService {

    private final QRCodeService qrCodeService;
    public final EmployeeRepository employeeRepository;


    public Employee addEmployee(Employee employee) {
        employee.setQrCodeData("EMP-" + System.currentTimeMillis());
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getByQrCodeData(String qrCodeData) {
        return employeeRepository.findByQrCodeData(qrCodeData);
    }


    //ben bu metodu QR için ekledim kafa karışıklığı olmasın diye ekle olarak  isimlendirdim .
    public Employee ekleEmployee(Employee employee) throws Exception {
        // Aynı çalışan var mı kontrolü
        Optional<Employee> existing = employeeRepository.findByNameAndDepartmentAndRole(
                employee.getName(), employee.getDepartment(), employee.getRole());

        if (existing.isPresent()) {
            throw new IllegalArgumentException("⚠️ Aynı isim ve bölümde kayıtlı personel zaten var.");
        }
        // Önce personeli kaydet (ID oluşacak)
        Employee savedEmployee = employeeRepository.save(employee);

        // Sonra QR kod verisini hazırla
        String qrCodeText = "ID:" + savedEmployee.getId() + ";Name:" + savedEmployee.getName();

        // QR kodu oluştur
        byte[] qrImage = qrCodeService.generateQRCodeImage(qrCodeText, 250, 250);

        // Base64'e çevir
        String qrCodeBase64 = java.util.Base64.getEncoder().encodeToString(qrImage);

        // QR kodu güncelle
        savedEmployee.setQrCodeData(qrCodeBase64);

        // Tekrar kaydet
        return employeeRepository.save(savedEmployee);


    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    //veri tabanından eklene verilerde QR kısmı boş olduğunda hata vermemesi için
    public void generateQrCodesForExistingEmployees() throws Exception {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee emp : employees) {
            if (emp.getQrCodeData() == null || emp.getQrCodeData().isEmpty()) {
                String qrCodeText = "ID:" + emp.getId() + ";Name:" + emp.getName();

                // QR kod oluştur
                byte[] qrImage = qrCodeService.generateQRCodeImage(qrCodeText, 250, 250);

                // Base64'e çevir
                String qrCodeBase64 = Base64.getEncoder().encodeToString(qrImage);

                // QR kodu set et ve kaydet
                emp.setQrCodeData(qrCodeBase64);
                employeeRepository.save(emp);
            }
        }
    }

    public boolean existsByNameAndDepartment(String name, String department) {
        return employeeRepository.findByNameAndDepartment(name, department).isPresent();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElse(null);

    }

    public void save(Employee employee) {
        try {
            // QR kodu tekrar üret
            String qrText = "ID:" + employee.getId() + ";Name:" + employee.getName();
            byte[] qrImage = qrCodeService.generateQRCodeImage(qrText, 250, 250);
            String qrBase64 = Base64.getEncoder().encodeToString(qrImage);
            employee.setQrCodeData(qrBase64);

            employeeRepository.save(employee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteById(Long id) {
        employeeRepository.deleteById(id);

    }

    public boolean existsByNameAndDepartmentExcludeId(String name, String department, Long id) {
        Optional<Employee> emp = employeeRepository.findByNameAndDepartment(name, department);
        return emp.isPresent() && !emp.get().getId().equals(id);
    }

    //dashboard için çekilecek veri
    public long getTotalEmployeeCount() {
        return employeeRepository.count();  // tüm kayıt sayısı
    }

    public long getDepartmentCount() {
        return employeeRepository.findDistinctDepartments().size();  // benzersiz bölüm sayısı
    }

    public long getTodayAttendanceCount() {
        return employeeRepository.countByAttendanceDate(LocalDate.now());
    }

    public Employee findByPersonelNo(String personelNo) {
        return employeeRepository.findByPersonelNo(personelNo).orElse(null);
    }


    public Employee saveNewEmployee(Employee employee) throws WriterException, IOException {
        // 1- Personel numarası üret
        String newPersonelNo = generateUniquePersonelNo();

        employee.setPersonelNo(newPersonelNo);

        // 2- QR Kod üret (personelNo'ya göre)
        String qrBase64 = qrCodeService.generateQRCodeBase64(newPersonelNo, 250, 250);
        employee.setQrCodeData(qrBase64);

        // 3- Kaydet
        return employeeRepository.save(employee);
    }

    private String generateUniquePersonelNo() {
        String lastPersonelNo = employeeRepository.findTopByOrderByIdDesc()
                .map(Employee::getPersonelNo)
                .orElse(null);

        if (lastPersonelNo == null || lastPersonelNo.length() < 4) {
            return "EMP0001";
        }

        try {
            int lastNumber = Integer.parseInt(lastPersonelNo.substring(3));
            int newNumber = lastNumber + 1;
            return String.format("EMP%04d", newNumber);
        } catch (NumberFormatException e) {
            // Eğer personelNo beklenmedik bir formatta ise sıfırdan başla
            return "EMP0001";
        }
    }

    @Transactional
    public void fillMissingPersonelNoAndQRCode() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            boolean updated = false;

            // personelNo boş ise oluştur (örneğin "P" + id)
            if (employee.getPersonelNo() == null || employee.getPersonelNo().trim().isEmpty()) {
                String generatedPersonelNo = "P" + (10000 + employee.getId()); // Örnek: P10001, P10002 ...
                employee.setPersonelNo(generatedPersonelNo);
                updated = true;
            }

            // qrCodeData boş ise personelNo'dan QR kod oluştur
            if (employee.getQrCodeData() == null || employee.getQrCodeData().trim().isEmpty()) {
                try {
                    String base64QRCode = qrCodeService.generateQRCodeBase64(employee.getPersonelNo(), 200, 200);
                    employee.setQrCodeData(base64QRCode);
                    updated = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (updated) {
                employeeRepository.save(employee);
            }
        }
    }


}
