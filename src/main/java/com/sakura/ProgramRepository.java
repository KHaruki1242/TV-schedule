package com.sakura;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//ProgramRepository.java
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
	 
    // 💡 これを一行書き足してください！
    List<Program> findByStationName(String stationName);

    // 💡 既存のメソッド（これも使えます）
    List<Program> findByStationNameAndStartTimeBetweenOrderByStartTimeAsc(
            String stationName, LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    void deleteByStationName(String stationName);

    List<Program> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);
}