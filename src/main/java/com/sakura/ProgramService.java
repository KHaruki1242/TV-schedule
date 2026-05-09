package com.sakura;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
 // 💡 引数なしで呼ばれた場合、自動的に「今日」を開始日として1週間分回す
    public void fetchOxWeekly() {
        fetchOxWeekly(LocalDate.now());
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
 // 呼び出し例: fetchOxWeekly(LocalDate.of(2026, 5, 9));
    public void fetchOxWeekly(LocalDate startDate) {
        DateTimeFormatter urlFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        
        for (int i = 0; i < 7; i++) {
            // 開始日から順に1日ずつ加算
            LocalDate targetDate = startDate.plusDays(i);
            String dateStr = targetDate.format(urlFormatter);
            
            String url = "https://bangumi.org/epg/td?broad_cast_date=" + dateStr + "&ggm_group_id=19";
            
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(15000)
                        .get();

                // 仙台放送のラインだけをターゲット
                Elements programs = doc.select("#program_line_6 li[s]"); 

                for (Element p : programs) {
                    String title = p.select(".program_title").text().trim();
                    String detail = p.select(".program_detail").text().trim();
                    String startTimeRaw = p.attr("s"); // 例: "202605090500"

                    if (!startTimeRaw.isEmpty() && !title.isEmpty()) {
                    	// --- 時刻文字列の生成 ---
                    	try {
                    	    // 開始日時のパース
                    	    DateTimeFormatter startFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                    	    LocalDateTime startTime = LocalDateTime.parse(startTimeRaw, startFormatter);

                    	    // 💡 修正：余計な文章は一切含まず、時刻（例：05:00）だけを作成
                    	    String displayTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                    	    // --- Repositoryへの保存 ---
                    	    Program program = new Program();
                    	    program.setStationName("仙台放送");
                    	    program.setTitle(title);
                    	    program.setStartTime(startTime);
                    	    // 💡 修正：descriptionには「時刻のみ」をセット
                    	    program.setDescription(displayTime);
                    	    
                    	    programRepository.save(program);
                    	} catch (Exception e) {
                    	    continue; 
                    	}
                    }
                }
                System.out.println("✅ " + targetDate + " (仙台放送) 取得成功");
                Thread.sleep(1000); // サーバー負荷軽減

            } catch (Exception e) {
                System.err.println("❌ " + dateStr + " エラー: " + e.getMessage());
            }
        }
    }
       
}

    