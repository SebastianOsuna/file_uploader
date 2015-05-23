package com.sebastianosuna.file_uploader;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploaderConfig {
	
	private UploaderType type;
	
	private String path;
	
	private String s3BucketName;
	
	private String s3Endpoint;
	
	private String s3AccessKey;
	
	private String s3SecretKey;
	
	private Collection<String> supportedMimes;
	
	private String maxFileSize;
	
	private boolean reducedRedundancy = false;
	
	public Collection<String> getSupportedMimes() {
		return supportedMimes;
	}
	
	public UploaderType getType() {
		return type;
	}

	public String getPath() {
		return path;
	}
	
	public boolean isReducedRedundancy() {
		return reducedRedundancy;
	}

	public String getS3BucketName() {
		return s3BucketName;
	}

	public String getS3Endpoint() {
		return s3Endpoint;
	}

	public String getS3AccessKey() {
		return s3AccessKey;
	}

	public String getS3SecretKey() {
		return s3SecretKey;
	}
	
	public int getMaxFileSizeInKB() {
		// No max file size
		if(maxFileSize == null) {
			return -1;
		}
		Pattern p = Pattern.compile("(\\d+)(KB|MB|GB)");
		Matcher m = p.matcher(maxFileSize);
		m.find();
		// Get unit
		int unit = Integer.parseInt(m.group(1));
		// Get magnitude
		String magnitude = m.group(2);
		if(magnitude.equals("MB")) {
			return unit*1024;
		} else if (magnitude.equals("GB")) {
			return unit*1024*1024;
		} else {
			return unit;
		}
	}

	public enum UploaderType {
		LOCAL("local"), S3("s3");
		
		private String value;
		
		private UploaderType(String value) {
			this.value = value;
		}
	}

}
