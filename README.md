File Uploader
===
Simple API for file uploading. Upload files locally and to S3.

Tailored made for DropWizard.

# Usage

1. Use the `UploaderConfig` class to configure the Uploader.
```java
UploaderConfig config = new UploaderConfig();
config.setType(UploaderConfig.UploaderType.LOCAL);
config.setPath("/path/to/files/");
```

2. Get an `Uploader` instance using the `UploaderFactory` class.
```java
Uploader uploader = UploaderFactory.getUploader(config);
```

3. Uploade your files!
```java
uploader.upload(inputStream, "namespace", "to", "my", "file.png");
``` 

## Use it with Dropwizard

1. Add `dropwizard-forms` as a dependency in your `pom`.

2. Then add an upload method to your resource.

```java
@POST
@Path("/upload")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public Response upload(@FormDataParam("file") InputStream fileStream, @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
	try {	
		uploader.upload(fileStream, "escorts", "myproject", contentDispositionHeader.getFileName());
		return Response.status(201).build();
	} catch (UploaderException e) {
		return Response.status(500).entity(e.getMessage()).build();
	}
} 
```

3. Setup you Configuration class.
```java
public class MyAppConfig extends Configuration {
	// ...
	
	@JsonProperty
	private UploaderConfig uploader;
	
	public UploaderConfig getUploaderConfig() {
		return uploader;
	}
}
```

4. Configure the uploader from the `.yml` file.
```yml
uploader:
  maxFileSize: 2MB
  supportedMimes:
    - image/png
    - image/jpg
    - image/jpeg
    - image/bmp
  type: s3
  s3BucketName: my-bucket
  s3AccessKey: ...
  s3SecretKey: ...
  s3Endpoint: s3.amazonaws.com
  reducedRedundancy: true
```

# Options

In the current version, there are only two types of uploaders, indicated by the `type` parameter.

```java
// type: [local|s3]
UploaderType.LOCAL, UploaderType.S3
```

You can also limit the accepted maximum file size using the `maxFileSize` parameter.

```
maxFileSize: #[KB|MB|GB]
``` 

*This size is measured in BYTES, so be sure to do you maths correctly.*
*The default unit is kilobytes.*

Also limit the accepted MIME types you'll allow with the `supportedMimes` parameter.

```
supportedMimes:
  - image/png
  - application/pdf
  ...
```

## Local uploader

The local uploader only **requires** an additional parameter: `path`, which is the path were the files will be uploaded to.

## S3 uploader

Off course the S3 uploader will need more info, particularly your S3 credentials and bucket info.

All of this are **required** parameters:

- `s3BucketName`
- `s3AccessKey`
- `s3SecretKey`
- `s3Endpoint`

Aditionally you can upload your files in RDS mode (Reduced redundancy) with `reducedRedundancy`. The default value is **false**.

**WARNING: ** All files uploaded to S3 are public. If you want to control this shoot me PR or ask for it ;)



##

