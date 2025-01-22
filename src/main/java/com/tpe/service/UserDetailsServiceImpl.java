package com.tpe.service;

import com.tpe.domain.Role;
import com.tpe.domain.User;
import com.tpe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService { //UserDetailsService bunu implemente ettik

    @Autowired
    private UserRepository userRepository;
    //1-UserDetailsService -> implemente ederek yaptik
    //2-UserDetails --> loadUserByUsername
    //3-GrantedAuthority --> buildGrantedAuthorities

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ////bu metod ile DB deki kendi userımızı bulup securitye UserDetails vereceğiz

        //bizim userimiz nedir security bilmez
        User user = userRepository.findByUserName(username).
                orElseThrow(()-> new UsernameNotFoundException("User not found by username"));

    //UserDetails'i extend eder burdan bilgileri veririz
    return new org.springframework.security.core.userdetails.User(user.getUserName(),
                                                               user.getPassword(),
                                                               buildGrantedAuthorities(user.getRoles()));


    }

    //userimizin rolleri var bunlari --> GrantedAuthority dondermemiz gerek
    private List<SimpleGrantedAuthority> buildGrantedAuthorities(Set<Role> roles){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles){
            authorities.add(new SimpleGrantedAuthority(role.getType().name())); //enum oldugu icin name() bu string olarak ismini almamizi gerekiyor
            //new SimpleGrantedAuthority("ROLE_ADMIN") --> burda GrantedAuthority haline getirdik
        }
        return authorities;
    }
}
