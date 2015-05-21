package com.sebastianosuna.file_uploader;

public class UploaderFactory {
	
	private UploaderFactory() {
		
	}
	
	public static Uploader getUploader(UploaderConfig config) throws UploaderException {
		if(config.getType().equals(UploaderConfig.UploaderType.LOCAL)) {
			return new LocalUploader(config);
		} else if(config.getType().equals(UploaderConfig.UploaderType.S3)) {
			return new S3Uploader(config);
		} else {
			return null;
		}
			
	}

}
