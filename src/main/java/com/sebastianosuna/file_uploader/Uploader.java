package com.sebastianosuna.file_uploader;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.Collection;

public abstract class Uploader {
	
	protected Collection<String> supportedMimes; 

	/**
	 * Writes the content of the input stream into the file referenced by the file name parts passed as 
	 * parameters.<br/>
	 * The file name parts given are concatenated with "/", so each part represents a new nested directory.
	 * The last part is expected to be the actual file name, that is, it should include the file extension.
	 * @param stream Input stream with the file contents.
	 * @param fileNameParts 
	 * @throws UploaderException
	 */
	public abstract void upload(InputStream stream, String... fileNameParts) throws UploaderException;
	
	protected String buildPathFromParts(String... fileNameParts) {
		String filePath = "";
		for(int i = 0; i < fileNameParts.length; i++) {
			filePath += fileNameParts[i];
			if((i+1) < fileNameParts.length) {
				filePath += "/";
			}
		}
		return filePath;
	}
	
	protected boolean acceptedMIME(String fileName) {
		if(supportedMimes == null) {
			return true;
		}
		String mime = URLConnection.guessContentTypeFromName(fileName);
		return supportedMimes.contains(mime);
	}
	
}
