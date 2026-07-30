package com.example.userShiftRequest.validation;

import com.example.userShiftRequest.dto.ShiftRequestDto;

public class ShiftRequestValidator {

    public boolean isValid(ShiftRequestDto dto) {

        if (dto.getAvailable() == null
                || dto.getAvailable().isBlank()) {

            return false;
        }

        if ("○".equals(dto.getAvailable())) {

            if (dto.getStartTime() == null
                    || dto.getStartTime().isBlank()
                    || dto.getEndTime() == null
                    || dto.getEndTime().isBlank()) {

                return false;
            }
        }

        return true;
    }
}