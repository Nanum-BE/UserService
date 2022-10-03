package com.nanum.utils.s3;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nanum.utils.s3.dto.S3UploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploaderService {

    private final AmazonS3Client amazonS3Client;

    public S3UploadDto upload(MultipartFile multipartFile, String bucket, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
        String originalFilename = multipartFile.getOriginalFilename();
        return upload(uploadFile, bucket, dirName, originalFilename);
    }

    // S3로 파일 업로드하기
    private S3UploadDto upload(File uploadFile, String bucket, String dirName, String originalFilename) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, bucket, fileName); // s3로 업로드
        removeNewFile(uploadFile);

        return S3UploadDto.builder()
                .imgUrl(uploadImageUrl)
                .saveName(fileName)
                .originName(originalFilename)
                .build();
    }

    // S3로 업로드
    public String putS3(File uploadFile, String bucket, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    /**
     * @param multipartFile 로컬에 파일 저장하기
     */
    public Optional<File> convert(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return Optional.empty();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        //파일 업로드
        File file = new File(System.getProperty("user.dir") + storeFileName);

        multipartFile.transferTo(file);

        return Optional.of(file);
    }

    public Optional<File> converts(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        //파일 업로드
        File file = new File(System.getProperty("user.dir") + storeFileName);
        return Optional.of(file);

    }

    /**
     * @param originalFilename 원본 파일 이름
     * @return 파일 이름
     * @description 파일 이름이 이미 업로드된 파일들과 겹치지 않게 UUID를 사용한다.
     */
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /**
     * @param originalFilename 원본 파일 이름
     * @return 파일 확장자
     * @description 사용자가 업로드한 파일에서 확장자를 추출한다.
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}