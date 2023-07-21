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
    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;
    private final MeasurementRepository measurementRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final StudentClassRepository studentClassRepository;


    @Value("${spring.sql.init.mode}")
    private String initMode;

    @Override
    public void run(String... args) {


        if (initMode.equals("always")) {

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

            Business business = Business.builder()
                    .name("Demo business")
                    .address("Demo")
                    .description("Demo")
                    .phoneNumber("Demo")
                    .active(true)
                    .delete(false)
                    .build();
            Business save = businessRepository.save(business);

            Branch branch = Branch.builder()
                    .name("Demo branch")
                    .business(save)
                    .delete(false)
                    .build();
            Branch branch1 = branchRepository.save(branch);

            Measurement measurement = Measurement.builder()
                    .name("KG")
                    .branch(branch1)
                    .active(true)
                    .build();
             measurementRepository.save(measurement);

            Measurement measurement1 = Measurement.builder()
                    .name("Liter")
                    .branch(branch1)
                    .active(true)
                    .build();
            measurementRepository.save(measurement1);


            PaymentType xisobdanXisobga = PaymentType.builder().name("Xisobdan xisobga").build();
            PaymentType karta = PaymentType.builder().name("Karta orqali").build();
            PaymentType elektron = PaymentType.builder().name("Elektron to'lov").build();
            PaymentType naqt = PaymentType.builder().name("Naqt").build();

            paymentTypeRepository.saveAll(List.of(karta, elektron, xisobdanXisobga, naqt));


            RoomType roomType = RoomType.builder()
                    .name("O'quv xona")
                    .active(true)
                    .branch(branch1)
                    .build();

            RoomType roomType1 = roomTypeRepository.save(roomType);

            Room room = Room.builder()
                    .active(true)
                    .roomNumber(1)
                    .branch(branch1)
                    .roomType(roomType1)
                    .build();
            Room room1 = roomRepository.save(room);

            StudentClass studentClass = StudentClass.builder()
                    .className("1-A sinf")
                    .startDate(LocalDate.parse("2023-05-05"))
                    .endDate(LocalDate.parse("2024-05-05"))
                    .active(true)
                    .room(room1)
                    .createdDate(LocalDateTime.now())
                    .branch(branch1)
                    .build();

            studentClassRepository.save(studentClass);
        }


    }
}
