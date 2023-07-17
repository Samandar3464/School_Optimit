package com.example.entity;

import com.example.enums.Gender;
import com.example.enums.Position;
import com.example.model.request.UserRegisterDto;
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

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String fatherName;

    @NotBlank
    @Size(min = 9, max = 9)
    private String phoneNumber;

    @NotBlank
    @Size(min = 6)
    private String password;

    @Email
    private String email;

    private int inn;

    private int inps;

    @Column(columnDefinition = "TEXT")
    private String biography;

    private boolean married;

    private LocalDate birthDate;

    private LocalDateTime registeredDate;

    private boolean isBlocked;

    private String fireBaseToken;

    private Integer verificationCode;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL)
    private Attachment profilePhoto;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    private boolean deleted;




    @OneToOne(mappedBy = "classLeader")
    private StudentClass studentClass;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Subject> subjects;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<DailyLessons> dailyLessons;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Salary> salaries;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Achievement> achievements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
    private List<WorkExperience> workExperiences;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacher")
    private List<TeachingHours> teachingHours;



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
        return isBlocked;
    }

    public static User from(UserRegisterDto userRegisterDto) {
        return User.builder()
                .name(userRegisterDto.getName())
                .surname(userRegisterDto.getSurname())
                .fatherName(userRegisterDto.getFatherName())
                .phoneNumber(userRegisterDto.getPhoneNumber())
                .registeredDate(LocalDateTime.now())
                .gender(userRegisterDto.getGender())
                .birthDate(userRegisterDto.getBirthDate())
                .isBlocked(true)
                .email(userRegisterDto.getEmail() == null ? null : userRegisterDto.getEmail())
                .inn(userRegisterDto.getInn() == 0 ? 0 : userRegisterDto.getInn())
                .inps(userRegisterDto.getInps() == 0 ? 0 : userRegisterDto.getInps())
                .biography(userRegisterDto.getBiography() == null ? null : userRegisterDto.getBiography())
                .married(userRegisterDto.isMarried())
                .deleted(false)
                .build();
    }
}
