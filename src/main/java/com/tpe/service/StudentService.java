package com.tpe.service;

import com.tpe.domain.Student;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

//normalde interfaceden yararlanilir fakat simdilik kolay olsun diye direk biz olusturacagiz
@Service
@RequiredArgsConstructor
public class StudentService {

    @Autowired
    private StudentRepository repository;

    //2-tablodan tum kayitlari getirme
    public List<Student> findAllStudents() {
        return repository.findAll();
    }

    //4
    public void saveStudent(@Valid Student student) {
        //student ayni email ile daha once tabloya eklenmis mi? kontrol etmemiz gerekiyor
        //select * from t_student where email=student.getEmail.... geriye donen bir deger varsa bu maili bir kullaniyor demektir

        boolean existsStudent = repository.existsByEmail(student.getEmail()); //kayit yoksa 0, kayit varsa 0dan buyuk deger donderir
        if (existsStudent){
            // bu email daha once kullanilmis, ---> hata firlatalim
            throw new ConflictException("Email Already Exist!");
        }
        repository.save(student); // insert into.. ile bize otomatik olarak bizim icin kayit islemi yapiyor


    }
    //7-
    public Student getStudentById(Long id) {
       Student student = repository.findById(id).
               orElseThrow(()->new ResourceNotFoundException("Student is not found by id: " + id));
       return student;
    }
}
