package com.example.yin.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPearsonScoreDto {
    Integer userId;
    Double personScore;

}
