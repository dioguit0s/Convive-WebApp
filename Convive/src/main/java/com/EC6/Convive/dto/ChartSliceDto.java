package com.EC6.Convive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChartSliceDto {
    private final String label;
    private final long value;
}
