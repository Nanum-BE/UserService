package com.nanum.utils.s3.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class S3UploadDto {

    private String originName;
    private String saveName;
    private String imgUrl;

}