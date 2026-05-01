package com.sakura;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProgramController {

    private final ProgramService programService;
    private final ProgramRepository programRepository;

    // コンストラクタ（変数名を統一しました）
    public ProgramController(ProgramService programService, ProgramRepository programRepository) {
        this.programService = programService;
        this.programRepository = programRepository;
    }
    
    // ルートパス ("/") でホーム画面（局選択）を表示
    @GetMapping("/")
    public String home() {
        // tv-guide.htmlをホーム画面として兼用する場合、
        // 最初はデータが空の状態で表示されることになります
        return "redirect:/tv"; 
    }

    @PostMapping("/tv/update")
    public String updatePrograms(@RequestParam String stationCode) {
        String stationName = "TBC東北放送"; // デフォルト

        if ("tbc".equals(stationCode)) {
            programService.fetchTbcWeekly();
            stationName = "TBC東北放送";
        }
           
        // 💡 修正：更新した局のパラメータを付けてリダイレクトする
        return "redirect:/tv?station=" + java.net.URLEncoder.encode(stationName, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    @GetMapping("/tv")
    public String showTvGuide(
            @RequestParam(required = false) String date, 
            @RequestParam(required = false, defaultValue = "TBC東北放送") String station, 
            Model model) {

        // 1. フォーマット定義
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 2. 日付のパース（try-catchをスッキリさせました）
        LocalDate targetDate;
        try {
            targetDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date, formatter) : LocalDate.now();
        } catch (Exception e) {
            targetDate = LocalDate.now();
        }

        // 3. DBから取得
        List<Program> allStationPrograms = programRepository.findByStationName(station);

        // 4. フィルタリング
        final LocalDate finalTargetDate = targetDate; // Lambda用の定数化
        List<Program> filteredPrograms = allStationPrograms.stream()
                .filter(p -> p.getStartTime().toLocalDate().equals(finalTargetDate))
                .sorted(Comparator.comparing(Program::getStartTime))
                .collect(Collectors.toList());

        // 5. モデルへの詰め込み
        model.addAttribute("programs", filteredPrograms);
        model.addAttribute("currentStation", station);
        // HTML側の th:classappend で比較しやすいようにハイフンありの文字列で渡す
        model.addAttribute("selectedDate", targetDate.format(formatter));
        
        return "tv-guide";
    }
    
}