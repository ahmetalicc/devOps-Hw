package org.sau.devopsproject2.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.sau.devopsproject2.Config.AWSClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
public class DocumentService {
    private final AmazonS3 amazonS3;
    private final AWSClientConfig awsClientConfig;
    private final String s3BucketUrl; // The base URL of your S3 bucket

    // Constructor
    public DocumentService(AmazonS3 amazonS3, AWSClientConfig awsClientConfig, @Value("${aws.s3.bucket}") String s3BucketUrl) {
        this.amazonS3 = amazonS3;
        this.awsClientConfig = awsClientConfig;
        this.s3BucketUrl = s3BucketUrl;
    }


    public void uploadFileToS3(String bucketName, String key, File file) {
        // Set ACL to public read
        PutObjectRequest request = new PutObjectRequest(bucketName, key, file)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(request);
    }

    public String generateImageUrl(String bucketName, String key) {
        // Generate a presigned URL for the uploaded object
        Date expiration = new Date(System.currentTimeMillis() + 360000000); // 1 hour from now
        URL url = amazonS3.generatePresignedUrl(bucketName, key, expiration);

        return url.toString();
    }
    public void deleteFileFromS3(String bucketName, String key) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName,key));
}

}