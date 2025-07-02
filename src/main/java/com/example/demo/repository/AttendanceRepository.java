package com.example.demo.repository;

import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // Belirli bir gün içinde kaç farklı personel giriş yapmış sorgusu
    @Query("SELECT COUNT(DISTINCT a.employee) FROM Attendance a WHERE a.isEntry = true AND DATE(a.timestamp) = :date")
    long countDistinctByDate(@Param("date") LocalDate date);

    // Bugünkü giriş kayıtlarını çekmek için (isteğe bağlı)
    List<Attendance> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

}
