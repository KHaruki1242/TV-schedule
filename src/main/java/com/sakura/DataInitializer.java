/*package com.sakura;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProgramRepository repository;

    public DataInitializer(ProgramRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            Program p1 = new Program();
            p1.setStationName("仙台放送");
            p1.setTitle("サンプル番組 A");
            p1.setStartTime(LocalDateTime.now());
            p1.setDescription("仙台の最新ニュースをお届けします。");
            repository.save(p1);

            Program p2 = new Program();
            p2.setStationName("TBC");
            p2.setTitle("サンプル番組 B");
            p2.setStartTime(LocalDateTime.now().plusHours(1));
            p2.setDescription("宮城のスポーツ情報。");
            repository.save(p2);

            System.out.println("✅ ダミーデータを投入しました");
        }
    }
}*/