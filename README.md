<h1 align="center">Welcome to imageanalyze 👋</h1>
<p>
</p>

> A simple Spring Boot application that will take images and send them off for image object detection.

## Prerequisites


* Spring-boot
* Postgresql
* Java 17


## Setup 
```
Enviornment variables:

BUCKET_NAME="bucket_name"
PROJECT_ID="Google Vision Project ID"
MINIO_BUCKET_NAME="MINIO bucket name"
MINIO_SECRET_KEY="S3 Secret Key"
MINIO_ACCESS_KEY="S3 Access Key"
MINIO_HOST="S3 host"
GOOGLE_APPLICATION_CREDENTIALS="path_to_file.json"
```

## Usage

```sh
To run:

mvn spring-boot:run
```

```
API:

GET /images
• Returns HTTP 200 OK with a JSON response containing all image metadata.
GET /images?objects="dog,cat"

• Returns a HTTP 200 OK with a JSON response body containing only images that have 
the detected objects specified in the query parameter.

GET /images/{imageId}
• Returns HTTP 200 OK with a JSON response containing image metadata for the 
specified image.

POST /images
• Send a JSON request body including an image file or URL, an optional label for the 
image, and an optional field to enable object detection.

• Returns a HTTP 200 OK with a JSON response body including the image data, its label 
(generate one if the user did not provide it), its identifier provided by
```


## Author

👤 **Jeremiah**

* Github: [@jeremiahjp](https://github.com/jeremiahjp)

