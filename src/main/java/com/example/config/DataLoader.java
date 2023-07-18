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
            Branch save1 = branchRepository.save(branch);

            Measurement measurement = Measurement.builder()
                    .name("KG")
                    .branch(save1)
                    .active(true)
                    .build();
            Measurement save2 = measurementRepository.save(measurement);

            Measurement measurement1 = Measurement.builder()
                    .name("Liter")
                    .branch(save1)
                    .active(true)
                    .build();
            Measurement save3 = measurementRepository.save(measurement1);


            PaymentType xisobdanXisobga = PaymentType.builder().name("Xisobdan xisobga").build();
            PaymentType karta = PaymentType.builder().name("Karta orqali").build();
            PaymentType elektron = PaymentType.builder().name("Elektron to'lov").build();
            PaymentType naqt = PaymentType.builder().name("Naqt").build();

            paymentTypeRepository.saveAll(List.of(karta, elektron, xisobdanXisobga, naqt));


        }


    }
}
