package com.example.adminshift.entity;


import java.io.Serializable;

import jakarta.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class ShiftRequestId implements Serializable {

    private String userId;

    private Integer eventId;

}