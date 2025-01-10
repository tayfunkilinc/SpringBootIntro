package com.tpe.repository;

import com.tpe.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // burada bunu yamamiza gerek yok JpaRepository bize Bead olusturuyor. ama okunabilirlik icin yazilabilir opsiyonel
public interface StudentRepository extends JpaRepository<Student, Long> {
    //burda spring kullanacagimiz icin SQL sorgularini direk yapabildigimiz icin burdan direk JpaRepository 'den miras alarak bu islemleri otomatik yapacagiz


    //JpaRepositorydeki metodlar Spring tarafından otomatik olarak implemente edilir, bizim implemente etmemize gerek yok


}
