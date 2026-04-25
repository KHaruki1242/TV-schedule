package com.sakura;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    @Transactional
    public void fetchTbcWeekly() {
        // 1. 既存のデータを一旦削除
        programRepository.deleteAll();
        programRepository.flush();

        // 2. 本日を起点に7日間分ループ
        LocalDate startDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (int i = 0; i < 7; i++) {
            LocalDate targetDate = startDate.plusDays(i);
            String dateStr = targetDate.format(formatter);
            String url = "https://www.tbc-sendai.co.jp/tpg/week_view.php?d=" + dateStr;

            try {
                System.out.println("📅 " + dateStr + " のTBC番組表を解析中...");
                
                Document doc = Jsoup.connect(url)
                                    .userAgent("Mozilla/5.0")
                                    .timeout(10000)
                                    .get();

                // あなたが見つけた最強のセレクタ！
                Elements rows = doc.select("tr"); // 行単位で回す

                for (Element row : rows) {
                    Element timeEl = row.selectFirst("td[id^=asa], td[id^=hiru], td[id^=yoru]"); // 時刻セル
                    Element titleEl = row.selectFirst("td#bangumi"); // 番組名セル

                    if (timeEl != null && titleEl != null) {
                        String timeStr = timeEl.text().trim();
                        String titleStr = titleEl.text().trim();

                        if (titleStr.length() > 2) {
                            Program program = new Program();
                            program.setStationName("TBC東北放送");
                            program.setTitle(titleStr);
                            program.setStartTime(targetDate.atStartOfDay()); // 日付判別用
                            program.setDescription(timeStr); // ★ここに時刻（4:55など）を入れる
                            programRepository.save(program);
                        }
                    }
                }
                System.out.println("✅ " + dateStr + " 分の保存完了");

            } catch (Exception e) {
                System.err.println("❌ " + dateStr + " の取得でエラー: " + e.getMessage());
            }
        }
        
        programRepository.flush();
        System.out.println("🎉 全日程の取得が完了しました！現在のDB件数: " + programRepository.count());
    }
}