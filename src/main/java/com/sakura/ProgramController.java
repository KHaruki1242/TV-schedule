package com.sakura;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProgramController {

    private final ProgramService programService; // ← これを追加
    private final ProgramRepository repository;

    // コンストラクタで両方を受け取るように修正
    public ProgramController(ProgramService programService, ProgramRepository repository) {
        this.programService = programService;
        this.repository = repository;
    }

    @GetMapping("/tv")
    public String showTvGuide(Model model) {
        model.addAttribute("programs", repository.findAll());
        return "tv-guide";
    }

    @PostMapping("/tv/update")
    public String updatePrograms() {
        programService.fetchSendaiPrograms(); // これで赤線が消えるはず！
        return "redirect:/tv";
    }
}