package com.example.entity;

import com.example.enums.Gender;
import com.example.enums.Position;
import com.example.model.request.UserRegisterDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @NotBlank
    private String fullName;

    @NotBlank
    @Size(min = 9, max = 9)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Position position;

    @NotBlank
    @Size(min = 6)
    private String password;

    @Email
    private String email;

    private int inn;

    private int inps;

    private String biography;

    private boolean married;

    private double overallSalary;

    private LocalDate birthDate;

    private LocalDateTime registeredDate;

    private boolean isBlocked;

    private String fireBaseToken;

    private Integer verificationCode;

    @Enumerated(EnumType.STRING)
    private Gender gender;



    @OneToOne(cascade = CascadeType.ALL)
    private Attachment profilePhoto;

    @OneToOne
    private StudentClass studentClass;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Achievement> achievements;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Subject> subjects;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<WorkExperience> workExperiences;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<DailyLessons> dailyLessons;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        for (Role role : roles) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            role.getPermissions().forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));
        }
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
        return isBlocked;
    }

    public static User from(UserRegisterDto userRegisterDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(userRegisterDto.getBirthDate(), formatter);
        return User.builder()
                .fullName(userRegisterDto.getFullName())
                .phoneNumber(userRegisterDto.getPhoneNumber())
                .inn(userRegisterDto.getInn())
                .inps(userRegisterDto.getInps())
                .biography(userRegisterDto.getBiography())
                .birthDate(birthDate)
                .registeredDate(LocalDateTime.now())
                .isBlocked(true)
                .gender(userRegisterDto.getGender())
                .isBlocked(true)
                .build();
    }
}
