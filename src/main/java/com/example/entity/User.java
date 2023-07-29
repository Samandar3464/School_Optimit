package com.example.entity;

import com.example.enums.Gender;
import com.example.enums.Position;
import com.example.model.request.UserRegisterDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String surname;

    private String fatherName;

    private String phoneNumber;

    private String password;

    private String email;

    private int workDays;

    private int inn;

    private int inps;

    private String biography;

    private boolean married;

    private LocalDate birthDate;

    private LocalDateTime registeredDate;

    private boolean blocked;

    private String fireBaseToken;

    private Integer verificationCode;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL)
    private Attachment profilePhoto;

    @ManyToOne
    private Role role;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Subject> subjects;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorityList;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static User from(UserRegisterDto userRegisterDto) {
        return User.builder()
                .name(userRegisterDto.getName())
                .surname(userRegisterDto.getSurname())
                .fatherName(userRegisterDto.getFatherName())
                .phoneNumber(userRegisterDto.getPhoneNumber())
//                .birthDate(userRegisterDto.getBirthDate())
                .workDays(userRegisterDto.getWorkDays())
                .inn(userRegisterDto.getInn())
                .inps(userRegisterDto.getInps())
                .biography(userRegisterDto.getBiography())
                .registeredDate(LocalDateTime.now())
                .email(userRegisterDto.getEmail())
                .married(userRegisterDto.isMarried())
                .gender(userRegisterDto.getGender())
                .blocked(false)
                .build();
    }
}
