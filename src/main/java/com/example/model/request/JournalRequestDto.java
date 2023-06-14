package com.example.model.request;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JournalRequestDto {

    private Integer id;

    private Integer studentClassId;

    private Integer branchId;

}
