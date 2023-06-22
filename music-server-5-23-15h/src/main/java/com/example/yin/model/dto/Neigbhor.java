package com.example.yin.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Neigbhor {
    Integer userId;
    Double relate;
    Integer score;
}
