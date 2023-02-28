package com.karansaklani20.multiwordle.games.dto;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListGamesResponse {
    private List<GameInfo> gamesData;
    private Integer totalPages;
    private long totalElements;
    private Pageable pageInfo;

}
