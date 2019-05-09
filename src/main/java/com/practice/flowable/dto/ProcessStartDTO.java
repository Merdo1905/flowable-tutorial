package com.practice.flowable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStartDTO {

    String employee;
    Integer numberOfHolidays;
    String description;
    String processName;
    //OMG where are my getter-setters
}
