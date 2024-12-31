package com.backend.backend.controller;

import com.backend.backend.HadithApi.HadithApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hadith") // Alle Endpunkte beginnen mit /hadith
public class HadithController {

    private final HadithApi hadithApi = new HadithApi();

    @GetMapping("/books")
    public String getBooks() {
        return hadithApi.getBooks();
    }

    @GetMapping("/chapters")
    public String getChapters(@RequestParam String bookSlug) {
        return hadithApi.getChapters(bookSlug);
    }

    @GetMapping("/search")
    public String getHadith(
            @RequestParam String hadithEnglish,
            @RequestParam String book,
            @RequestParam String chapter) {
        return hadithApi.getHadith(hadithEnglish, book, chapter);
    }
}