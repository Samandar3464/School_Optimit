package com.example.entity;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String fileOriginalName;

    private long size;

    private String contentType;

    private String name;

    @ManyToOne
    private Branch branch;
=======
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String originName;

    private long size;

    private String newName;

    private String type;

    private String contentType;

    private String path;

    @OneToOne
    private User user;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
}
