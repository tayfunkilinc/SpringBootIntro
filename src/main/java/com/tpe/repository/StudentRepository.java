package com.tpe.repository;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;

@Repository // burada bunu yamamiza gerek yok JpaRepository bize Bead olusturuyor. ama okunabilirlik icin yazilabilir opsiyonel
public interface StudentRepository extends JpaRepository<Student, Long> {
    //JpaRepositorydeki metodlar Spring tarafından otomatik olarak implemente edilir, bizim implemente etmemize gerek yok

    //5
    boolean existsByEmail(String email);

    //15-a
    List<Student> findAllByGrade(Integer grade); //select * from Student where grade=100

    //15-b --kendi sorgumuz ile islemlerimizi yapmak
    //JPQL : javaca --->JPA in db ile iletisim dili HQL benzeri bir deger
    @Query("FROM Student WHERE grade=:pGrade")//burdan sorgumu alip kendi sorgumuzuda bu sekilde kendi sorgularimizida calistirabiliriz
    List<Student> filterStudentsByGrade(@Param("pGrade") Integer grade);
    //@Param methodun parametresinde verilen degeri pGrade icerisine alir ve bu degiskeni sorgu icerisinde kullanabiliriz

    //15-c --SQL sorgusu ile yapmak icin : value ozelligi ile SQL ifadesi String olarak verilir ve nativeQuery aktif edilir
    @Query(value = "SELECT * FROM student WHERE grade=:pGrade", nativeQuery = true)
    List<Student> filterStudentsByGradeSQL(@Param("pGrade") Integer grade);

    //18-c : JPQL ile tablodan gelen Entity objesini DTO'nun contructor'i ile dogrudan DTO objesine donustureiliriz
    @Query("SELECT new com.tpe.dto.StudentDTO(s) FROM Student s WHERE s.id=:pId")
    Optional<StudentDTO> findStudentDtoById(@Param("pId") Long id);

    //ODEV:16
    List<Student> findAllByLastnameIgnoreCase(String lastName); //spring implemente eder

    //select * from Student where name=:pWord OR lastname=:pWord1
    List<Student> findByNameOrLastname(String word, String word1);


    //existsBy bu kok kelimeden sonra Email, Grade, Name, LastName gibi fieldlari sonuna ekleyerek bu sekilde override edebiliyoruz
    //burda spring kullanacagimiz icin SQL sorgularini direk yapabildigimiz icin burdan direk JpaRepository 'den miras alarak bu islemleri otomatik yapacagiz



}
