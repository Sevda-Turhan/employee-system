package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data//Getter ve Setter anatosyonlarıyla birlikte kullanılmaz çakışma olabilir.
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String department;
    @Column(unique = true)
    private String personelNo;
    private String role;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;
    @OneToOne
    private User user;



    @Lob
    @Column(length = 65535)  // TEXT tipi için maksimum uzunluk
    private String qrCodeData;



}
