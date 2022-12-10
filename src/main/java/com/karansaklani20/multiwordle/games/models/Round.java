package com.karansaklani20.multiwordle.games.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.words.models.Word;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rounds")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Word word;
    @OneToOne
    private Game game;
    @OneToOne
    private GameUser winner;
    private Boolean completed;
    private Boolean currentRound;
    private Integer nRound;

}
