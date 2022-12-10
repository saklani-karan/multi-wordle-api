package com.karansaklani20.multiwordle.words.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.karansaklani20.multiwordle.words.models.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    Word findByValue(String word);

    @Query(value = "SELECT * FROM WORDS WHERE id NOT IN (:wordIds)", nativeQuery = true)
    List<Word> getWordsExcludingIds(@Param("wordIds") List<Long> wordIds);
}
