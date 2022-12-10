package com.karansaklani20.multiwordle.words.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.karansaklani20.multiwordle.words.dto.AddWordRequest;
import com.karansaklani20.multiwordle.words.models.Word;
import com.karansaklani20.multiwordle.words.services.WordService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/words")
@AllArgsConstructor
public class WordController {
    private WordService wordService;

    @PostMapping("/")
    public Word addWord(@RequestBody AddWordRequest wordRequest) throws Exception {
        return this.wordService.addWord(wordRequest);
    }

    @GetMapping("/find")
    public List<Word> findAll() throws Exception {
        return this.wordService.getAllWords();
    }
}
