package com.example.config;


import com.example.entity.*;
import com.example.enums.Gender;
import com.example.kitchen.repository.MeasurementRepository;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoaderTest implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final StudentClassRepository studentClassRepository;
    private final TypeOfWorkRepository typeOfWorkRepository;
    private final SubjectRepository subjectRepository;
    private final BalanceRepository balanceRepository;
    private final LevelRepository levelRepository;
    private final SubjectLevelRepository subjectLevelRepository;

    @Value("${spring.sql.init.mode}")
    private String initMode;

    @Override
    public void run(String... args) {


        if (initMode.equals("always")) {

            Permission p1 = permissionRepository.save(new Permission(1, "ALL"));
            Role teacher1 = Role.builder().id(2).name("TEACHER").permissions(List.of(p1)).active(true).build();
            Role savedRole = roleRepository.save(teacher1);
            Business business = Business.builder()
                    .name("Demo business")
                    .description("Demo")
                    .phoneNumber("Demo")
                    .active(true)
                    .delete(false)
                    .build();
            Business savedBusiness = businessRepository.save(business);

            Branch branch = Branch.builder()
                    .name("Demo branch")
                    .address("Demo")
                    .business(savedBusiness)
                    .delete(false)
                    .build();
            Branch saveBranch = branchRepository.save(branch);
            User teacher = User.builder()
                    .name(" teacher")
                    .surname("teacher")
                    .fatherName("teacher")
                    .phoneNumber("111111112")
                    .birthDate(LocalDate.parse("1998-05-13"))
                    .gender(Gender.ERKAK)
                    .registeredDate(LocalDateTime.now())
                    .verificationCode(0)
                    .password(passwordEncoder.encode("111111"))
                    .blocked(false)
                    .role(savedRole)
                    .branch(saveBranch)
                    .build();
            User savedTeacher = userRepository.save(teacher);



            Balance balance = Balance.builder()
                    .balance(1000000000)
                    .branch(saveBranch)
                    .shotNumber(123123123L)
                    .build();

            Balance savedBalance = balanceRepository.save(balance);

            RoomType roomType = RoomType.builder()
                    .name("O'quv xona")
                    .active(true)
                    .branch(saveBranch)
                    .build();

            RoomType roomType1 = roomTypeRepository.save(roomType);

            Room room = Room.builder()
                    .active(true)
                    .roomNumber(1)
                    .branch(saveBranch)
                    .roomType(roomType1)
                    .build();
            Room room1 = roomRepository.save(room);

            Level savedLavel = levelRepository.getById(6);

            StudentClass studentClass = StudentClass.builder()
                    .className("1-A sinf")
                    .level(savedLavel)
                    .startDate(LocalDate.parse("2023-05-05"))
                    .endDate(LocalDate.parse("2024-05-05"))
                    .active(true)
                    .room(room1)
                    .createdDate(LocalDateTime.now())
                    .branch(saveBranch)
                    .classLeader(savedTeacher)
                    .salaryForClassLeader(500000D)
                    .build();

            studentClassRepository.save(studentClass);

            TypeOfWork typeOfWork = TypeOfWork.builder()
                    .branch(saveBranch)
                    .name("dars berish")
                    .price(50000D)
                    .build();
            typeOfWorkRepository.save(typeOfWork);


            Subject matemetika = Subject.builder()
                    .name("Matemetika")
                    .branch(saveBranch)
                    .build();
            Subject subject = subjectRepository.save(matemetika);

            SubjectLevel subjectLevel = SubjectLevel.builder()
                    .subject(subject)
                    .level(savedLavel)
                    .branch(saveBranch)
                    .teachingHour(10)
                    .priceForPerHour(100000)
                    .build();

            subjectLevelRepository.save(subjectLevel);
        }


    }
}
