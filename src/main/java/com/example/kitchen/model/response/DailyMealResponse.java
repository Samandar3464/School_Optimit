package com.example.kitchen.model.response;

import com.example.entity.Branch;
import com.example.enums.WeekDays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyMealResponse {

    private Integer id;

    private String name;

<<<<<<< HEAD
    private Integer photoId;
=======
    private String photo;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private WeekDays day;

    private String time;

    private Branch branch;
}
