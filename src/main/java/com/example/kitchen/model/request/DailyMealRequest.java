package com.example.kitchen.model.request;

import com.example.enums.WeekDays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyMealRequest {

    private Integer id;

    private String name;

<<<<<<< HEAD
    private Integer photoId;
=======
    private MultipartFile photo;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private WeekDays day;

    private String time;

    private Integer branchId;
}
