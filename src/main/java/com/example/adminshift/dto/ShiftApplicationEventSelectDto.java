package com.example.adminshift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftApplicationEventSelectDto {
    private Integer eventId;
    private String displayName;
}