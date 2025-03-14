package com.book_viewer.book.s3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // ✅ Upload an image to S3
    public String uploadImage(MultipartFile file) {
        String fileName = "books/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
//                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));

            return getImageUrl(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    // ✅ Get an image URL from S3
    public String getImageUrl(String fileName) {
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder().bucket(bucketName).key(fileName).build())
                .toExternalForm();

    }

    // ✅ Fetch images with pagination
    public List<String> getImages(int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;

        ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix("books/")  // Only fetch book images
                .maxKeys(startIndex + pageSize) // Fetch more, but return only requested
                .build());

        List<S3Object> allImages = response.contents();

        return allImages.stream()
                .skip(startIndex)  // Skip previous pages
                .limit(pageSize)   // Take only requested page size
                .map(obj -> getImageUrl(obj.key()))
                .collect(Collectors.toList());
    }
}