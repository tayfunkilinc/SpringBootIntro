package com.tpe.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //methodlarda yetkilendirme yapmamizi sagliyor -- methodun yetkisi varmi diye kontrol et diyoruz,
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //spring boot config ayarlarina filtre eklemek icin burda config ayarlarimi yapacagim

    @Autowired
    private UserDetailsService userDetailsService; //UserDetailsServiceImpl buda bu bean buraya enjekte edilmis oldu -- burda direk interfaceden refere alaraka olusturdugumuz enjekte ettik

    //filtreyi configure etmek icin yapmam gerekenler
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().//Rest API: session yok : JSON'da bilgiler olmadigi icin yani saklanacak bir sey olmadigi icin csrf disable yapildi
        authorizeHttpRequests().
                antMatchers("index.html", "/register", "/login"). //en azindan giris sayfalarina izin ver
                permitAll().
                anyRequest().
                authenticated().
                and().
                httpBasic();


    }

    @Bean
    private DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    //passwordEncoder kullanicini passwordunu hashleyerek saklamak icin bize gerekli
    //authenticationProvider ise DB deki passwordü requestte gelen ile karşılaştırmak için kuulanır
    @Bean
    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10); //sorluk seviyesi 4-34 arasi  //BCryptPasswordEncoder sifreleme algoritmasi
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    //csrf() yan sekmelerden oturumunuz acik sekmeye kimse ulasamasin diye olusturulmus koruma turu
    /*
http.csrf().disable()
Açıklama: CSRF (Cross-Site Request Forgery) korumasını devre dışı bırakır
Sebep:
API projelerinde genellikle token tabanlı güvenlik kullanıldığından CSRF koruması gereksizdir
Postman gibi test araçlarıyla API testlerini kolaylaştırır
Mobil uygulamalar için API geliştirirken gereksiz bir koruma katmanı oluşturur
authorizeHttpRequests()
Açıklama: HTTP isteklerinin yetkilendirme kurallarını yapılandırmaya başlar
Sebep:
URL bazlı güvenlik kuralları tanımlamak için gereklidir
Hangi URL'lere kimlerin erişebileceğini kontrol etmek için kullanılır
Rol bazlı erişim kontrolü sağlar
antMatchers("index.html", "/register", "/login")
Açıklama: Belirtilen URL patternlerine özel kurallar tanımlar
Sebep:
Bazı sayfalara/endpoint'lere özel erişim kuralları tanımlamak için kullanılır
Genellikle public erişime açık sayfalar için kullanılır
URL pattern'lerini gruplamak için kullanılır
permitAll()
Açıklama: antMatchers'da belirtilen URL'lere herkesin erişimine izin verir
Sebep:
Kayıt ve giriş sayfaları gibi herkese açık olması gereken sayfalar için gereklidir
Kullanıcı authenticate olmadan erişebilmesi gereken kaynaklar için kullanılır
Public API endpoint'leri için gereklidir
anyRequest()
Açıklama: antMatchers'da belirtilmeyen diğer tüm URL'ler için kural tanımlar
Sebep:
Genel güvenlik politikası belirlemek için kullanılır
Özel olarak belirtilmemiş tüm URL'ler için default davranış tanımlar
Güvenlik açıklarını önlemek için gereklidir
authenticated()
Açıklama: Sadece authenticate olmuş kullanıcıların erişimine izin verir
Sebep:
Özel/korumalı kaynaklara erişimi sınırlamak için kullanılır
Kullanıcının giriş yapmış olmasını zorunlu kılar
Yetkisiz erişimleri engeller
and()
Açıklama: Farklı konfigürasyon blokları arasında geçiş yapmak için kullanılır
Sebep:
Akıcı (fluent) API tasarımı için gereklidir
Farklı güvenlik konfigürasyonlarını birleştirmek için kullanılır
Okunabilir kod yapısı sağlar
httpBasic()
Açıklama: Basic Authentication'ı etkinleştirir
Sebep:
Basit kullanıcı adı/şifre authentication'ı için kullanılır
Test aşamasında hızlı geliştirme için uygundur
Postman gibi test araçlarıyla kolay entegrasyon sağlar
Not:

Bu konfigürasyon genellikle geliştirme aşamasında veya basit API'ler için kullanılır
Prodüksiyon ortamında daha güvenli yöntemler (JWT, OAuth2 gibi) tercih edilmelidir
Her projenin ihtiyacına göre bu konfigürasyon özelleştirilebilir

*/
}
