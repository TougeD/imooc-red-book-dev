package com.imooc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VlogDTO {

    private String id;
    @NotBlank
    private String vlogerId;
    @NotBlank
    private String url;
    @NotBlank
    private String cover;
    @NotBlank
    private String title;
    @Digits(integer = 10,fraction = 5)
    private Integer width;
    @Digits(integer = 10,fraction = 5)
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
}