package com.tpe.controller;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.service.StudentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/*
clienttan 3 sekilde veri alinir:
1- requestin BODY(JSON)
2-requestin URL query param
3-requestin URL path param
*/

@RestController //request'ler buradan eslestirilecegini belirtiyoruz, requestler bu classtaki methodlarla eslestirilecek ve responselar hazirlanacak
//@ResponseBody : metodun dönüş değerini JSON formatında cevap olarak hazırla
//RequestBody   : requestin icindeki(body) JSON formatinda olan datayi methodlari parametresinde kullanabilmemizi saglar
// :obje<->JSON'a donusturulmesi isini, Jackson yapar: objenin JSON'a Veya JSON'in objeye donusturulmesini jackson kutuphanesi sagliyor
//jackson kutuphanesi spring-web-starter ile otomatik olarak gelir
@RequestMapping("/students") //https://localhost:8080/students...
@RequiredArgsConstructor //sadece final olan field'lari const. olusturur
/*
@NoArgsConstructor -- default parametresiz const olusturur
@AllArgsConstructor-- tum fieldlari parametre olarak alan const. olusturur
@RequiredArgsConstructor -- zorunlu (final) olan field lari parametre olarak alan const. olusturur
*/
public class StudentController {

    Logger logger = LoggerFactory.getLogger(StudentController.class);//hangi classta kullanilacaksa o class verilir


    @Autowired
    private StudentService service;

    //bunlarini kullanmamiza gerek yok bunu @RequiredArgsConstructor ile bunu cozmus olduk
//    @Autowired //tek oldugu icin bunu yazmaya gerek yok ama okunabilirlik icin yazdim simdilik
//    public StudentController(StudentService service) {
//        this.service = service;
//    }

    //SpringBOOT'u selamlama :)
    //http://localhost:8080/students/greet + GET
    //!! @ResponseBody //bu annatation @RestController icinde oldugu icin burda kullanmama gerek yok
    @GetMapping("/greet")
    public String greet(){
        return "Hello Spring Boot :)";
    }

    //1-tüm öğrencileri listeleyelim : READ
    //Request : http://localhost:8080/students + GET
    //Response : tum ogrencilerin listesini dondurecegiz + 200:OK(HttpStatus Code)yani cevabi destekleme kodu
    @GetMapping
    //@ResponseBody :RestController icerisinde var burada kullanmaya gerek kalmadi
    public ResponseEntity<List<Student>> getAllStudents(){
        //tablodan ogrencilerimizi getirecegiz
        List<Student> allStudents = service.findAllStudents();
        return new ResponseEntity<>(allStudents, HttpStatus.OK); //Durumu : HttpStatus.OK : 200 basarili olarak gonderdik
    }

    // ResponseEntity : response'un bodysini + status kodunu birlikte gonderebilmek icin kullanilir. kutu gibi icine bunlari koyup gonderecegim

    //3-ogrenci ekleme : CREATE

    //Request : http://localhost:8080/students + POST + body(JSON)
    /*
    {
    "name":"Jack",
    "lastname":"Sparrow",
    "email":"jack@mail.com",
    "grade":98
    }
     */
    //Response : ogrenci tabloya eklenecek, basarili mesaji + 201(Created) cevap olarak istemciye bunlar dondurulur
    @PostMapping
    public ResponseEntity<String> createStudent(@Valid @RequestBody Student student){ //ResponseEntity<String> burda String turunde bir deger donderecegim
        try {
            service.saveStudent(student);

            logger.info("yeni ogrenci eklendi : " + student.getName());

            return new ResponseEntity<>("Student is created successfully...",HttpStatus.CREATED); //Durumu : HttpStatus.CREATE : 201

        } catch (Exception e) {
            logger.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST); //400
        }
    }


    //6-query param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/query?id=1 + GET
    //response : student + 200
    @GetMapping("/query")
    public ResponseEntity<Student> getStudent(@RequestParam("id") Long id){
        Student foundStudent = service.getStudentById(id);
        return new ResponseEntity<>(foundStudent,HttpStatus.OK);
    }

    //ÖDEV:(Alternatif)6-path param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/1 + GET
    //response : student + 200
    @GetMapping("/{id}")
    public ResponseEntity<Student> findStudent(@PathVariable("id") Long id){
        Student foundstudent=service.getStudentById(id);
        return new ResponseEntity<>(foundstudent,HttpStatus.OK);
    }

    //8-path param ile id si verilen öğrenciyi silme
    ////request: http://localhost:8080/students/1 + DELETE
    //response: tablodan kayıt silinir,başarılı mesajı + 200
    @DeleteMapping("/{deletedId}")
    public ResponseEntity<String> deleteStudent(@PathVariable("deletedId") Long id){

        service.deleteStudentById(id);

        /*return new ResponseEntity<>("Student is deleted succsesfuly...", HttpStatus.OK);*/
        return ResponseEntity.ok("Student is deleted succsesfuly...");
    }


    //10 - idsi verilen öğrencinin name,lastname ve emailini değiştirme
    //request : http://localhost:8080/students/1 + PUT/PATCH + BODY(JSON)bilgileri JSON olarak alacagiz
    // PUT(yerine koyma tum bilgileri degistirir oncekileri atar yenisini koyar grade girmeyince grade degirini sifirlar)/PATCH(kismi guncelleme yapar verilen yerleri degistirir sadece)
    //response : guncelleme, basarili mesaji + 201
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable("id") Long id, @Valid @RequestBody UpdateStudentDTO studentDTO){

        service.updateStudent(id,studentDTO); //guncellemek istediklerimizi studentDTO icerisinde kapsulleyip gonderiyoruz
        return new ResponseEntity<>("Student is updated successfully...", HttpStatus.CREATED); //201
    }
    //NOT: http://localhost:8080/students/update?name=Ali&lastname=Can&email=ali@mail.com

    //----------------------------------

    //12-tum ogrencileri listeleme : READ
    // 1 | 2 | 3 | 4 ... next
    //12-tüm öğrencileri listeleme : READ
    //pagination(sayfalandırma) -- verileri sayfa sayfa gormek : hız/performans
    //tüm kayıtları page page(sayfa sayfa) gösterelim

    //request :
    //http://localhost:8080/students/page?
    //                               page=3& ---> kacinci sayfa oldugu
    //                               size=20& ---> bir sayfada 20 sonuc
    //                               sort=name& ----> siralama islemleri
    //                               direction=DESC/ASC + GET ---> siralamainin yonu ve okuma islemi GET olarak yapilir
    //response
    @GetMapping("/page")
    public ResponseEntity<Page<Student>> getAllStudent(@RequestParam("page") int pageNo  /*sayfa no*/,
                                                       @RequestParam("size") int size  /*kac urun olacak sayfada*/,
                                                       @RequestParam("sort") String property  /*string olarak hangi ozellige gore siralama ornegin en ucuza gore*/,
                                                       @RequestParam("direction") Sort.Direction direction /*siralamanin yonu icin sabit degiskenler*/
    ){
        Pageable pageable = PageRequest.of(pageNo,size,Sort.by(direction,property));

        Page<Student> studentPage = service.getAllStudentByPage(pageable);
        return new ResponseEntity<>(studentPage, HttpStatus.OK); //200
    }

    //14-grade ile ogrencileri filtreleyelim
    //requesr : http://localhost:8080/students/grade/100 + GET
    //response : grade = 100 olan ogrencileri listeleyelim +200
    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<Student>> getAllStudentByGrade(@PathVariable("grade") Integer grade){
        List<Student> students = service.getStudentByGrade(grade);
        return ResponseEntity.ok(students);
    }

    //ÖDEVVV:
    //JPA in metodlarını türetme
    //JPQL/SQL ile custom sorgu
    //16-lastname ile öğrencileri filtreleyelim
    // request:http://localhost:8080/students/lastname?lastname=Potter + GET
    //response : lastname e sahip olan öğrenci listesi + 200
    @GetMapping("/lastname")
    public ResponseEntity<List<Student>> getStudentsByLastName(@RequestParam String lastname){

        List<Student> studentList=service.getAllStudentByLastname(lastname);

        return ResponseEntity.ok(studentList);//200
    }

    //Meraklisina ODEV:
    //isim veya soyisme gore filtreleme
    //request:http://localhost:8080/students/search?word=harry + GET
    @GetMapping("/search")
    public ResponseEntity<List<Student>> getAllStudentByNameOrLastName(@RequestParam("word") String word){
        List<Student> studentList = service.getAllStudentByNameOrLastname(word);
        return ResponseEntity.ok(studentList);

    }

    //Meraklisina ODEV: name icinde "...." hecesi gecen ogrencileri filtreleme : herhangi bir kelime icinde geciyorsa filtrele
    //request:http://localhost:8080/students/filter?word=al + GET --> ex:halil, lale



    //17-id'si verilen öğrencinin name,lastname ve grade getirme
    //request:http://localhost:8080/students/info/2 + GET
    //respose : id'si verilen ogrencinin sadece 3 datasini DTO ile getirelim + 200
    @GetMapping("/info/{id}")
    public ResponseEntity<StudentDTO> getStudentInfo(@PathVariable Long id){
        //17-a StudentDTO studentDTO = service.getStudentByIdDto(id); //18-a

        //17-b
        StudentDTO studentDTO=service.getStudentInfoByDTO(id);  //18-b


        return ResponseEntity.ok(studentDTO);
    }

/*
Loglama, bir yazılım veya sistemin çalışırken yaptığı önemli olayları,
 işlemleri ve hataları kaydetmesi anlamına gelir. Loglar,

 bilgisayar programlarının bir çeşit "günlüğü" veya "karne defteri" gibi düşünülebilir.
 Sistem, yaptığı işleri ve karşılaştığı problemleri
 buraya yazar ve bu bilgiler, geliştiriciler veya sistem yöneticileri için çok değerlidir.
*/
    //bir log kaydi olusturalim

    //19-http://localhost:8080/students/welcome + GET
    @GetMapping("/welcome")
    public String welcome(HttpServletRequest request){
        //loger objesini en basta olusturduk
        logger.info("welcome istegi geldi!");
        logger.warn("welcome isteginin path'i: " + request.getServletPath());
        logger.warn("welcome isteginin methodu: " + request.getMethod());
        return "welcome";
    }

    /*
Spring Boot Actuator, bir uygulamanın sağlık durumunu ve çalışma metriklerini izlemek
için kullanılan bir Spring Boot kütüphanesidir. Actuator, bir uygulamanın
arka planda nasıl çalıştığını görmenizi sağlar ve uygulamanın izlenebilirliğini artırır.
Uygulama Sağlık Durumu:

Uygulamanın çalışır durumda olup olmadığını kontrol eder.
Örneğin: "Veritabanına bağlanabiliyor mu? Sunucu çalışıyor mu?"
Metrik Takibi:

Uygulamanın performansı hakkında bilgiler sağlar.
Örneğin: "Kaç kullanıcı sisteme bağlandı? Bellek kullanımı ne durumda?"
Günlük İşleyişin İzlenmesi:

Loglama, yapılandırmalar, güvenlik bilgileri gibi iç detayları görmenizi sağlar.
Sorun Giderme:

Hata durumunda, sistemin hangi noktada sorun yaşadığını anlamanıza yardımcı olur
/actuator/health    Uygulamanın sağlık durumunu gösterir.
/actuator/metrics   Uygulamanın performansıyla ilgili metrikleri listeler.
/actuator/env       Uygulamanın çevre değişkenlerini listeler.
/actuator/loggers   Log seviyelerini ve log yapılandırmalarını kontrol eder.
/actuator/info      Uygulama hakkında bilgi verir (ör. sürüm bilgisi).
*/


}
