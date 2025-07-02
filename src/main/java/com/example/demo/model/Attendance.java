package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi personel
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    @Column(name = "timestamp")
    // Giriş veya çıkış zamanı
    private LocalDateTime timestamp;

    // Giriş mi, çıkış mı?
    private boolean isEntry; // true = giriş, false = çıkış


}
