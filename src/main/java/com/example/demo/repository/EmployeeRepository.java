package com.example.demo.repository;

import com.example.demo.model.Employee;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByQrCodeData(String qrCodeData);
    Optional<Employee> findByNameAndDepartmentAndRole(String name, String department, String role);
    Optional<Employee> findByNameAndDepartment(String name, String department);

    @Query("SELECT DISTINCT e.department FROM Employee e")
    List<String> findDistinctDepartments();

    long countByAttendanceDate(LocalDate date);


    Employee findByUser(User user);
    Employee findByPersonelNo(String personelNo);
    Optional<Employee> findTopByOrderByIdDesc();
    boolean existsByPersonelNo(String personelNo);




}
