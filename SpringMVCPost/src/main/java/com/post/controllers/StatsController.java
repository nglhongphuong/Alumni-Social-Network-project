/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.service.StatsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */

@Controller
@RequestMapping("/")
public class StatsController {

    @Autowired
    private StatsService statService;

  @GetMapping
public String statsPage(
        @RequestParam(name = "type", defaultValue = "user") String type,
        @RequestParam(name = "period", defaultValue = "month") String period,
        @RequestParam(name = "year", required = false) Integer selectedYear,
        Model model) {

    List<Map<String, Object>> stats = new ArrayList<>();

    switch (type) {
        case "user":
            stats = statService.getUserStats(); // trả về list có month + year
            break;
        case "post":
            stats = statService.getPostStats();
            break;
        case "survey":
            stats = statService.getSurveyPostStats();
            break;
        case "invitation":
            stats = statService.getInvitationPostStats();
            break;
    }

    // ✅ Tìm tất cả các năm có trong dữ liệu để làm dropdown
    Set<Integer> availableYears = stats.stream()
            .filter(m -> m.get("year") != null)
            .map(m -> (Integer) m.get("year"))
            .collect(Collectors.toCollection(TreeSet::new));

    // ✅ Nếu chưa chọn năm, mặc định là năm mới nhất
    if ((selectedYear == null || !availableYears.contains(selectedYear)) && !availableYears.isEmpty()) {
        selectedYear = ((TreeSet<Integer>) availableYears).last();
    }

    // ✅ Nếu lọc theo tháng hoặc quý thì lọc theo năm
    if ("month".equals(period) || "quarter".equals(period)) {
        int finalSelectedYear = selectedYear;
        stats = stats.stream()
                .filter(m -> m.get("year") != null && ((Integer) m.get("year")) == finalSelectedYear)
                .collect(Collectors.toList());
    }

    // 🔽 Gửi dữ liệu xuống view
    model.addAttribute("stats", stats);
    model.addAttribute("type", type);
    model.addAttribute("period", period);
    model.addAttribute("selectedYear", selectedYear);
    model.addAttribute("availableYears", availableYears);

    return "index";
}


}
