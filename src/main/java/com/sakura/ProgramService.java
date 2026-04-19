package com.sakura;

import java.io.IOException;
import java.time.LocalDateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ProgramService {
    private final ProgramRepository repository;

    public ProgramService(ProgramRepository repository) {
        this.repository = repository;
    }
    
    public void fetchSendaiPrograms() {
        String url = "https://tv.yahoo.co.jp/listings/34/";
        try {
            // 1. サイトに接続してHTMLを取得
            Document doc = Jsoup.connect(url).get();
            
            // 2. 番組の枠（各放送局の縦列など）を特定
            // Yahoo!テレビの番組枠は 'section' タグなどに分かれていることが多いです
            Elements programElements = doc.select(".y-tv-listing__program"); 

            for (Element el : programElements) {
                // タイトルを取得
                String title = el.select(".y-tv-listing__program-title").text();
                
                if (!title.isEmpty()) {
                    Program program = new Program();
                    program.setTitle(title);
                    // 一旦、放送局名は親要素などから推測するか、固定でテスト
                    program.setStationName("仙台地上波"); 
                    program.setStartTime(LocalDateTime.now());
                    program.setDescription(el.select(".y-tv-listing__program-content").text());
                    
                    repository.save(program);
                }
            }
            System.out.println("✅ データ保存完了！");
        } catch (IOException e) {
            System.err.println("エラー発生: " + e.getMessage());
        }
    }
}

