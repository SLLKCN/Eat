package com.example.eat.model.dto.param.diary;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DateDto {
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;
}
