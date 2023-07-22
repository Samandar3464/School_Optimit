package com.example.config;


import com.example.entity.*;
import com.example.enums.Gender;
import com.example.kitchen.entity.Measurement;
import com.example.kitchen.repository.MeasurementRepository;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentTypeRepository paymentTypeRepository;
    private final LevelRepository levelRepository;
    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;
    private final MeasurementRepository measurementRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final StudentClassRepository studentClassRepository;
    private final TypeOfWorkRepository typeOfWorkRepository;
    private final SubjectRepository subjectRepository;
    private final BalanceRepository balanceRepository;
    private  final SubjectLevelRepository subjectLevelRepository;

    @Value("${spring.sql.init.mode}")
    private String initMode;

    @Override
    public void run(String... args) {


        if (initMode.equals("always")) {

            PaymentType xisobdanXisobga = PaymentType.builder().name("Xisobdan xisobga").build();
            PaymentType karta = PaymentType.builder().name("Karta orqali").build();
            PaymentType elektron = PaymentType.builder().name("Elektron to'lov").build();
            PaymentType naqt = PaymentType.builder().name("Naqt").build();

            paymentTypeRepository.saveAll(List.of(karta, elektron, xisobdanXisobga, naqt));


            Level level1 = new Level(1, 1);
            Level level2 = new Level(2, 2);
            Level level3 = new Level(3, 3);
            Level level4 = new Level(4, 4);
            Level level5 = new Level(5, 5);
            Level level6 = new Level(6, 6);
            Level level7 = new Level(7, 7);
            Level level8 = new Level(8, 8);
            Level level9 = new Level(9, 9);
            Level level10 = new Level(10, 10);
            Level level11 = new Level(11, 11);

            levelRepository.saveAll(List.of(level1, level2, level3, level4, level5, level6, level7, level8, level9, level10, level11));

            Permission p1 = permissionRepository.save(new Permission(1, "ROLE_ACCESS"));
            Permission p2 = permissionRepository.save(new Permission(2, "ADD"));
            Permission p3 = permissionRepository.save(new Permission(3, "read"));

            Role supper_admin = Role.builder().id(1).name("SUPER_ADMIN").permissions(List.of(p1, p2, p3)).active(true).build();

            roleRepository.save(supper_admin);

            User superAdmin = User.builder()
                    .name("Super Admin")
                    .surname("Admin")
                    .fatherName("Admin")
                    .phoneNumber("906163464")
                    .birthDate(LocalDate.parse("1998-05-13"))
                    .gender(Gender.ERKAK)
                    .registeredDate(LocalDateTime.now())
                    .verificationCode(0)
                    .password(passwordEncoder.encode("111111"))
                    .isBlocked(true)
                    .deleted(false)
                    .role(supper_admin)
                    .build();
            userRepository.save(superAdmin);

        }
    }
}
