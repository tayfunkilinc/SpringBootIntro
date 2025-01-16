package com.tpe.service;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    //9-
    public void deleteStudentById(Long id) {
        //getStudentById(id);
        // repository.deleteById(id); bunu kullanirkende verilen id ye sahip ogrenci var mi kotrol yapilmali (oncesinde getStudentById(id); ile custom exception firlatmak istiyoruz)

        //bu id ile ogrenci var mi?
        Student student = getStudentById(id); //ogrenci yoksa exception firlatir
        repository.delete(student);



    }
    //11-id'si verilen ogrencinin bilgilerini dto'da gelen bilgiler ile degistirelim
    public void updateStudent(Long id, @Valid UpdateStudentDTO studentDTO) {
        Student foundStudent = getStudentById(id); // 1,Jack,Sparrow,jack@mail.com,98,13.1.2025...

        //email unique olmasina engel var mi?
        //DTOdan gelen yeni email       tablodaki emailler
        //1-xx@mail.com                 kimseye ait degilse (existByEmail: false )onceden kullanilmamissa --update () guncellenecek mailin onceden tabloda var oplup olmadigini kontrol etmeliyiz
        //2-harry@mail.com              baska birine ait (existByEmail: true) --> update yapamam cakisma var ConflictException
        //3-jack@mail.com               update edilecek kendisine aitse (existByEmail: true)  --> update yapilir cunku bu bir cakisma degil kendi adresi


        //istek ile gonderilen email tabloda var mi?
        boolean existEmail = repository.existsByEmail(studentDTO.getEmail()); //tabloda var mi ? varsa true yoksa false doner
        boolean selfEmail = foundStudent.getEmail().equals(studentDTO.getEmail()); // burda bulunan Email kendi emailimi bunun konrolunu yapacagiz burdan tru donerse mail kendisine ait olmus olur
        if (existEmail && !selfEmail) {//mail tabloda var ve kendisine ait degilse exception firlatacagim
            //cakisma var
            throw new ConflictException("Email already exists!!...");
        }

        foundStudent.setName(studentDTO.getName());
        foundStudent.setLastname(studentDTO.getLastname());
        foundStudent.setEmail(studentDTO.getEmail());
        repository.save(foundStudent); //save methodu saveOrUpdate gibi calisir : id onceden varsa update, id onceden yoksa save islemi yapar


    }

    //13- gerekli parametreleri(bilgileri) pageblable ile vererek
    // tum ogrencilerin sayfalanmasini ve talep edilen istenen sayfanin dondurulmesini saglayalim
    public Page<Student> getAllStudentByPage(Pageable pageable) {
        Page<Student> studentPage = repository.findAll(pageable);
        return studentPage;
    }
    //15-
    public List<Student> getStudentByGrade(Integer grade) {
        //select * from Student where grade=100
        /*return repository.findAllByGrade(grade);*/

        //2.YOL
        //kok dizinleri kullanarak tam olrak istedigimiz sorguyu yapamassak
        /*return repository.filterStudentsByGrade(grade);*/

        //3.YOL
        return repository.filterStudentsByGradeSQL(grade);
    }

    //18-a id verilen ogrenciyi bulup getirecek --
    public StudentDTO getStudentByIdDto(Long id) {
        Student student = getStudentById(id);

        //Entity -> objeDTO yaptik
        // tablodan gelen entity icindeki 3 datayi alip dto objesi icine yerlestirdik

        // 1- StudentDTO studentDTO = new StudentDTO(student.getName(), student.getLastname(), student.getGrade()); //bunu kisa yoldan hallettik StudentDTO icerinde

        //------------------

        // 2-  StudentDTO studentDTO=new StudentDTO();
        //     studentDTO.setName(student.getName());....

        //-------------------

        //yukaridaki iki secenek zahmetli bunun yerine DTO olusturmak icin constructor parametresine Entity objesi verip donusumu saglayabiliriz
        StudentDTO studentDTO = new StudentDTO(student); //otomatik olarak field'lari set etmis olduk
        return studentDTO;

    }

    //18-b : repository'den dogrudan DTO objesi getirelim
    public StudentDTO getStudentInfoByDTO(Long id) {

        StudentDTO studentDTO = repository.findStudentDtoById(id).
                orElseThrow(()->new ResourceNotFoundException("Student is not found by id: " + id));;
        return studentDTO;
    }

    //ÖDEV:16
    public List<Student> getAllStudentByLastname(String lastName) {
        return repository.findAllByLastnameIgnoreCase(lastName);
    }

    //meraklısına:)
    public List<Student> getAllStudentByNameOrLastname(String word) {
        return repository.findByNameOrLastname(word,word);

    }







}
