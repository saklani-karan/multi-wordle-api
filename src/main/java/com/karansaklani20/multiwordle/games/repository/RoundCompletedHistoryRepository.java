package com.karansaklani20.multiwordle.games.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karansaklani20.multiwordle.games.models.RoundCompletedHistory;

public interface RoundCompletedHistoryRepository extends JpaRepository<RoundCompletedHistory, Long> {
}
