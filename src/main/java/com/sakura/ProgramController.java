package com.sakura;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProgramController {

    private final ProgramRepository repository;

    public ProgramController(ProgramRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/tv")
    public String showTvGuide(Model model) {
        // 全データを取得してモデルに渡す
        model.addAttribute("programs", repository.findAll());
        return "tv-guide";
    }
}