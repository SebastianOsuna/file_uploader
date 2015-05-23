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
	
	/**
	 * Build path from string parts. The strings are concatenated using "/" to represent a path.
	 * @param fileNameParts Single parts of the path.
	 * @return Full path
	 */
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
	
	/**
	 * Checks whether the give filename has an accepted MIME type based on the file extension.<br/><br/>
	 * If the uploader doesn't have a list of accepted MIME types, then all extensions are considered as accepted.
	 * @param fileName File name containing the extension
	 * @return True if the file is of an accepted MIME type; False otherwise.
	 */
	protected boolean acceptedMIME(String fileName) {
		if(supportedMimes == null) {
			return true;
		}
		String mime = URLConnection.guessContentTypeFromName(fileName);
		return supportedMimes.contains(mime);
	}
	
}
