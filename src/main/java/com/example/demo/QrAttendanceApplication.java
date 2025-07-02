package com.example.demo;

import com.example.demo.service.QRCodeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrAttendanceApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(QrAttendanceApplication.class, args);
//    }
public static void main(String[] args) throws Exception {
    var context = SpringApplication.run(QrAttendanceApplication.class, args);

    QRCodeService qrService = context.getBean(QRCodeService.class);
    byte[] qrImage = qrService.generateQRCodeImage("Test QR Code", 250, 250);

//    System.out.println("QR Code oluşturuldu, byte uzunluğu: " + qrImage.length);
}



}

