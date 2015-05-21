package com.sebastianosuna.file_uploader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.StorageClass;

public class S3Uploader extends Uploader {
	
	private UploaderConfig config;
	
	private int maxFileSize = -1;
	
	public S3Uploader(UploaderConfig config) {
		if(config.getS3AccessKey() == null || config.getS3AccessKey().equals("") ||
				config.getS3SecretKey() == null || config.getS3SecretKey().equals("") ||
				config.getS3Endpoint() == null || config.getS3Endpoint().equals("") ||
				config.getS3BucketName() == null || config.getS3BucketName().equals("")) {
			new UploaderException("Incomplete S3 parameteres.");
		}
		this.maxFileSize = config.getMaxFileSizeInKB();
		this.supportedMimes = config.getSupportedMimes();
		this.config = config;
	}

	@Override
	public void upload(InputStream stream, String... fileNameParts) throws UploaderException {
		if(fileNameParts.length == 0) {
			throw new UploaderException("Invalid file name.");
		}
		if(!acceptedMIME(fileNameParts[fileNameParts.length - 1])) {
			throw new UploaderException("Unsupported MIME type.");
		}
		// Setup AWS S3 SKD
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(config.getS3AccessKey(), config.getS3SecretKey());
        AmazonS3 s3 = new AmazonS3Client(awsCredentials);
        s3.setEndpoint(config.getS3Endpoint());
        
        try {
        	// Create temporal output to check file size
            ByteArrayOutputStream bufferedStream = new ByteArrayOutputStream();
            int kilobytesRead = 0;
            byte[] buffer = new byte[1024];
			while(stream.read(buffer) > 0) {
				bufferedStream.write(buffer);
				kilobytesRead++;
				if(maxFileSize > -1 && kilobytesRead > maxFileSize) {
					bufferedStream.close();
					stream.close();
					throw new UploaderException("Maximum file size reached.");
				}
			}
			bufferedStream.close();
			stream.close();
		
	        String fileKey = buildPathFromParts(fileNameParts);
	        // Object metada needed since we are uploading from a stream
	        ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType(URLConnection.guessContentTypeFromName(fileNameParts[fileNameParts.length - 1]));
	
	        // Connect to S3
	        
	        PutObjectRequest putRequest = new PutObjectRequest(config.getS3BucketName(), fileKey, new ByteArrayInputStream(bufferedStream.toByteArray()), metadata);
	        // Set as public
	        putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
	        if(config.isReducedRedundancy()) {
		        // Store as RRS
		        putRequest.setStorageClass(StorageClass.ReducedRedundancy);
	        }
	        // Upload!
	        PutObjectResult res = s3.putObject(putRequest);
	        String publishedPath = "//" + config.getS3Endpoint() + "/" + config.getS3BucketName() + "/" + fileKey;
        } catch (IOException e) {
			throw new UploaderException("Error reading from input stream: " + e.getMessage());
		}
	}

}
