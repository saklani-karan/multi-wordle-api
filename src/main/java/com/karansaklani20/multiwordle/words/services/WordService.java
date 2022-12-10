package com.karansaklani20.multiwordle.words.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.words.dto.AddWordRequest;
import com.karansaklani20.multiwordle.words.exceptions.UnusedWordsNotFound;
import com.karansaklani20.multiwordle.words.exceptions.WordAlreadyExists;
import com.karansaklani20.multiwordle.words.models.Word;
import com.karansaklani20.multiwordle.words.repository.WordRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class WordService {
    private final WordRepository wordRepository;

    public Word addWord(AddWordRequest wordRequest) {
        log.info("addWord: request received");

        Word word = this.wordRepository.findByValue(wordRequest.getValue());
        if (word != null) {
            log.error("addWord: word with value={} already exists with id={}", wordRequest.getValue(), word.getId());
            throw new WordAlreadyExists(wordRequest.getValue(), word.getId());
        }
        log.info("addWord: word with value={} does not exists", wordRequest.getValue());

        return this.wordRepository.save(Word.builder().value(wordRequest.getValue()).build());
    }

    public List<Word> getAllWords() {
        return this.wordRepository.findAll();
    }

    public Word getRandomUnusedWord(List<Long> wordIds) throws Exception {
        log.info("getRandomUnusedWord: wordIds={}", wordIds.size());

        List<Word> words = new ArrayList<>();
        if (wordIds.size() == 0) {
            words = this.wordRepository.findAll();
        } else {
            words = this.wordRepository.getWordsExcludingIds(wordIds);
        }

        if (words.size() == 0) {
            log.error("getRandomUnusedWord: no unused words found");
            throw new UnusedWordsNotFound();
        }
        log.info("getRandomUnusedWord: unused words found={}", words.size());

        long randomIndex = (long) Math.floor(Math.random() * words.size());
        log.info("getRandomUnusedWord: random index = {}", randomIndex);

        return words.get((int) randomIndex);

    }
}
