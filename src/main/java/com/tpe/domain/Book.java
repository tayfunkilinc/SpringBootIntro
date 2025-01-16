package com.tpe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;


    @JsonProperty("bookName") //eklemis oldugumuz field sadece JSON formatinda bu fieldin isminin belirtilen sekilde gosterilmeisni saglar
    private String name;

    @ManyToOne
    @JsonIgnore //bu filedi JSON formatinda ignore et (goz ardi et) -- burda surekli birbirini cagirmasin diye ignore ettik
    private Student student;
    //kitabi kaydederken bu kitap hangi ogrenciye ait
    //bu ogrenciyi bulup set etmem lazim



}
