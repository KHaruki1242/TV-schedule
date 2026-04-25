package com.sakura;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        if ("tbc".equals(stationCode)) {
            programService.fetchTbcWeekly();
        }
        // 更新が終わったら一覧画面へリダイレクト
        return "redirect:/tv";
    }

    @GetMapping("/tv")
    public String showTvGuide(@RequestParam(required = false) String date, Model model) {
        LocalDate targetDate;
        if (date != null && date.length() == 8) {
            targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else {
            targetDate = LocalDate.now(); // 指定がなければ今日
        }

        // その日の 00:00:00 〜 23:59:59 の範囲を指定
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        // DBからその日だけのデータを取得
        List<Program> programs = programRepository.findByStartTimeBetweenOrderByStartTimeAsc(startOfDay, endOfDay);
        
        model.addAttribute("programs", programs);
        model.addAttribute("selectedDate", targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        return "tv-guide";
    }
}