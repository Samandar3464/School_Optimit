package com.example.entity;

import com.example.model.request.TopicRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany
    private List<Attachment> lessonFiles;

    @ElementCollection
    private List<String> useFullLinks;

    private LocalDateTime creationDate;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subject subject;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Level level;

    public static Topic toEntity(TopicRequest dto) {
        return Topic
                .builder()
                .name(dto.getName())
                .useFullLinks(dto.getUseFullLinks())
                .creationDate(LocalDateTime.now())
                .build();
    }
}
