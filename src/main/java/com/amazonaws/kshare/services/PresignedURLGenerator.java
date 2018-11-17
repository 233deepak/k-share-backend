package com.amazonaws.kshare.services;

import java.net.URL;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

public class PresignedURLGenerator {
	
	public static final String CLIENTREGION = "us-east-2";
	public static final String BUCKETNAME = "k-share-files";
	
	public static URL generateURL(String fileName ,String fileType) {
        String objectKey = fileName;
        URL url = null ;

        try {            
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(CLIENTREGION)
                    .build();
    
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest = 
                    new GeneratePresignedUrlRequest(BUCKETNAME, objectKey)
                    .withMethod(HttpMethod.PUT)
                    .withContentType(fileType)
                    .withExpiration(expiration);
             url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    
            System.out.println("Pre-Signed URL: " + url.toString());
        }
        catch(AmazonServiceException e) {
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            e.printStackTrace();
        }
		return url;
	}

}
