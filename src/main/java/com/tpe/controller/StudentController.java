package com.tpe.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // requestler bu classtaki methodlarla eslestirilecek ve responselar hazirlanacak
//@ResponseBody :metodun dönüş değerini JSON formatında cevap olarak govdeye eklenmesini saglar.
//@ResponseBody :metodun dönüş değerini JSON formatında cevap olarak hazırlar
// :obje<->Jackson : objenin JSON'a Veya JSON'in objeye donusturulmesini jackson kutuphanesi sagliyor
@RequestMapping("/students") //https://localhost:8080/students...
public class StudentController {
}
