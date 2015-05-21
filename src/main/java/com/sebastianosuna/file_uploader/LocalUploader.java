package com.sebastianosuna.file_uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalUploader extends Uploader {
	
	/**
	 * Root directory path to upload the files.
	 */
	private String path;
	
	/**
	 * Max file size.
	 */
	private int maxFileSize = -1;

	public LocalUploader(UploaderConfig config) throws UploaderException {
		// Root directory path must provided
		if(config.getPath() == null) {
			throw new UploaderException("Must specify an upload path.");
		}
		this.path = config.getPath();
		this.maxFileSize = config.getMaxFileSizeInKB();
		this.supportedMimes = config.getSupportedMimes();
	}

	@Override
	public void upload(InputStream stream, String... fileNameParts) throws UploaderException {
		if(fileNameParts.length == 0) {
			throw new UploaderException("Invalid file name.");
		}
		if(!acceptedMIME(fileNameParts[fileNameParts.length - 1])) {
			throw new UploaderException("Unsupported MIME type.");
		}
		// Build file path
		String filePath = path + (path.endsWith("/") ? "" : "/") + buildPathFromParts(fileNameParts);
		
		// Check directory exists or creates it
		checkDirectory(filePath);
		
		try {
			// Create new file
			File out = new File(filePath);
			if(!out.exists() && !out.createNewFile()) {
				throw new UploaderException("Couldn't create destination file.");
			}
			// Write file content
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buffer = new byte[1024];
			int kilobytesRead = 0;
			while(stream.read(buffer) > 0) {
				fos.write(buffer);
				kilobytesRead++;
				if(maxFileSize > -1 && kilobytesRead > maxFileSize) {
					fos.close();
					stream.close();
					out.delete();
					throw new UploaderException("Maximum file size reached.");
				}
			}
			// Close streams
			fos.close();
			stream.close();
			String publishedPath = buildPathFromParts(fileNameParts);
		} catch (IOException e) {
			throw new UploaderException("Couldn't create destination file.\n" + e.getMessage());
		}
	}

	/**
	 * Checks if the parent directory of the given file path exists. If it doesn't exists, it tries to create
	 * the directory.
	 * @param filePath Path to the file directory to check
	 * @throws UploaderException if the parent File isn't a directory or couldn't be created.
	 */
	private void checkDirectory(String filePath) throws UploaderException {
		File file = new File(filePath);
		File f = file.getParentFile();
		if(f.isDirectory() || (!f.exists() && f.mkdirs())) {
			return;
		} else {
			throw new UploaderException("Given path isn't a valid directory");
		}
	}

}
