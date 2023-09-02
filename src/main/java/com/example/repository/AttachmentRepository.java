package com.example.repository;

import com.example.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

<<<<<<< HEAD
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    Optional<Attachment> findByName(String name);
    Optional<Attachment> findAllById(Integer id);
=======
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    Optional<Attachment> findByNewName(String newName);
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
}
