package com.tpe.service;

import com.tpe.domain.Role;
import com.tpe.domain.RoleType;
import com.tpe.domain.User;
import com.tpe.dto.UserDto;
import com.tpe.exception.ConflictException;
import com.tpe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public void saveUser(@Valid UserDto userDto) {

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        //username set etmeden once bu kullanici db'de varmi diye kontrol ediyoruz
        boolean existUser = userRepository.existsByUserName(userDto.getUserName());
        if (existUser){
            throw new ConflictException("User is already exist!!....");
        }
        user.setUserName(userDto.getUserName());//kontrol sonrasi varsa set ediyoruz

        //user passwordunu  dogrudan degil hash'leyerek kaydetmemiz gerek - bunun icin PasswordEncoder Bean'ini kullanacagiz
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);

        //userin rolunun verilmesi - en az yetkili rol basta atanir sonrasinda yetki arttirimina gidilir
        Set<Role> roleSet = new HashSet<>();
        Role role = roleService.getRoleByType(RoleType.ROLE_STUDENT);
        roleSet.add(role);
        //Not: genellikle alt seviye role verilip gerekirse role degisikligine gidilir
        user.setRoles(roleSet);
        userRepository.save(user);



    }
}
