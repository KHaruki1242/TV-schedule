package com.sakura;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName; // 放送局 (TBC, 仙台放送など)
    private String title;       // 番組名
    private LocalDateTime startTime;
    private String description; // 番組内容
}