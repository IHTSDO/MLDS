# IHTSDO-MLDS API

IHTSDO-MLDS provides a limited number of APIs to support integration with other systems. 

## Schema

All access to the API is over HTTPS to the `mlds.ihtsdotools.org` domain. All data is sent and received as JSON.

```
$ curl -i 'https://mlds.ihtsdotools.org/api/affiliates/check?member=IHTSDO&match=spectra'
HTTP/1.1 200 OK
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 27 Oct 2015 19:46:30 GMT

{"matched":true}
```

## Client errors

Client errors on API calls will typically result in a `400 Bad Request` response.

```
HTTP/1.1 400 Bad Request
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 27 Oct 2015 19:54:17 GMT

{"error":"Bad Request","status":400,"message":"Unknown member: 'xz'. Valid options: AU BE BN CA CL CZ DK EE ES GB HK IHTSDO IL IN IS LT MT MY NL NZ PL PT SE SG SI SK US UY "}
```

Error responses can be detected by a status code of 4xx or 5xx. The message value is an optional human description of the problem.

## Authentication

The public APIs do not require authentication to be supplied.

### Basic Authentication

The APIs that do require authentication support Basic Authentication using MLDS credentials.

Ensure that all communication uses `https` to ensure the credentials aren't revealed.

```
$ curl -u USER:PASSWORD -i 'https://mlds.ihtsdotools.org/api/releasePackages'
```

## HTTP Methods

Where possible the API supports appropriate HTTP Methods/Verbs for each resource.

| Method | Description |
| GET | Retrieve a representation of the resource. |
| POST | Create a new resource. |
| PUT | Replace a resource. |
| DELETE | Delete a resource. |

---

# Affiliate API

## Affiliate Check

Check that an affiliate is in good standing with IHTSDO.

Where possible the `affilateId` with confirming matching data from the application can be used, otherwise a single match from all affiliates of a member can be used. 

```
GET /affiliates/check?member=IHTSDO&match=keyword
```

### Parameters

| Name | Type | Optional | Description |
| ---- | ---- | -------- | ----------- |
| member | string | Mandatory | Identification of the member country that the affiliate has been accepted by. It is either a two-letter country code or `IHTSDO` to indicate IHTSDO international. The country codes are a subset of the ISO 3166-1 alpha-2 codes. Valid options: AU BE BN CA CL CZ DK EE ES GB HK IHTSDO IL IN IS LT MT MY NL NZ PL PT SE SG SI SK US UY. |
| match | string | Mandatory | Search keyword within the affiliate record. This will match against a number of identifying fields of the affiliate: organization name, first name, last name, street address, email, alternative email, and third email. |
| affiliateId | string | Optional | Limit the search to a specific Affilate record. |

The check API requires a unique match to be found from the database of Affiliates. Affiliates are filtered by the member, optionally by affiliate id, and a keyword text match against the identifying fields of the affiliate.

Once a single affiliate has been identified the affiliate's application must have been approved for the specified member and for the affiliate's account to be considered in good standing.

If successful then the API will return with a `matched: true`.

If there is no match then the API will return with `matched: false`. 

No additional information is provided to diagnose a failed match. 
 

### Examples

A successful match against a Swedish affiliate based on a match against the `abc@test.com` email address. The affiliate was uniquely identified and the account was in good standing.

```
$ curl -i 'https://mlds.ihtsdotools.org/api/affiliates/check?member=SE&match=abc@test.com'
HTTP/1.1 200 OK
Content-Type: application/json

{"matched":true}
```

An unsuccessful match against an IHTSDO affiliate based a match against the keyword hospital. No affiliate was uniquely identified as many affiliates contained `hospital` in the searched fields.

```
$ curl -i 'https://mlds.ihtsdotools.org/api/affiliates/check?member=SE&match=abc@test.com'
HTTP/1.1 200 OK
Content-Type: application/json

{"matched":false}
```

A successful match that limited the search to a single specified affiliate. The 'hospital' keyword matched, unlike before, however, by limiting the search to a single affiliate the result was unique.

```
$ curl -i 'https://mlds.ihtsdotools.org/api/affiliates/check?member=IHTSDO&match=hospital&affiliateId=123'
HTTP/1.1 200 OK
Content-Type: application/json

{"matched":true}
```

---

# Release Packages API

Each member organization can make Release Packages available to their approved affiliates for downloading. A Release Package has a current version and past versions. A Release version can contain multiple files that can be individually downloaded by an affiliate.

Release files are not stored in MLDS and instead a URL is provided where the file can be download from on demand. Files are typically stored on the Amazon s3 service. The URL to directly access the release file is not made available to affiliate users, who instead use a MLDS based URL that in turn download the file from the original location.

## Authentication

The API provides read access to the released packages without authentication; however, to modify and add release packages requires authentication with a user with staff authorization. 

##  Get all release packages

List all release packages.

```
$ curl -i 'https://mlds.ihtsdotools.org/api/releasePackages'
```

### Response

```json
[{
  "releasePackageId": 1911,
  "createdAt": "2015-10-14T13:49:09.163Z",
  "createdBy": "sweden",
  "member": {
    "key": "SE"
  },
  "name": "Sweden A",
  "description": "<h3>sweden a release..<br/></h3>",
  "releaseVersions": [{
    "releaseVersionId": 1913,
    "createdAt": "2015-10-14T13:50:26.918Z",
    "createdBy": "sweden",
    "name": "sweden a a 1",
    "description": "<p>some kind of version<br/></p>",
    "online": true,
    "publishedAt": null,
    "releaseFiles": [{
      "releaseFileId": 1921,
      "label": "<p>file2<br/></p>",
      "createdAt": "2015-10-14T19:18:39.808Z",
      "clientDownloadUrl": "/api/releasePackages/1911/releaseVersions/1913/releaseFiles/1921/download"
    }, {
      "releaseFileId": 1919,
      "label": "<p>file1<br/></p>",
      "createdAt": "2015-10-14T19:18:30.628Z",
      "clientDownloadUrl": "/api/releasePackages/1911/releaseVersions/1913/releaseFiles/1919/download"
    }]
  }]
}, {
  "releasePackageId": 5267,
  "createdAt": "2015-10-20T14:54:25.733Z",
  "createdBy": "admin",
  "member": {
    "key": "BE"
  },
  "name": "Belgium A",
  "description": "<p>AAAA</p><p><br/></p><p></p>",
  "releaseVersions": [{
    "releaseVersionId": 5269,
    "createdAt": "2015-10-20T14:54:44.829Z",
    "createdBy": "admin",
    "name": "Belgium A 1",
    "description": "<p>A 1<br/></p>",
    "online": true,
    "publishedAt": null,
    "releaseFiles": [{
      "releaseFileId": 5271,
      "label": null,
      "createdAt": "2015-10-20T14:55:01.955Z",
      "clientDownloadUrl": "/api/releasePackages/5267/releaseVersions/5269/releaseFiles/5271/download"
    }]
  }]
}]
```

## Get a single Release Package

```
GET /api/releasePackages/:releasePackageId'
```

## Get a single Release Version

```
GET /api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId'
```

### Get a single Release File

```
GET /api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId/releaseFiles/:releaseFileId'
```

## Create a new Release Package

```
POST /api/releasePackages 
```

### Input

| Name | Type | Description |
| ---- | ---- | ----------- |
| member.key | string | Member organization, either `IHTSDO` or the two letter country code of the member country |
| name | string | Name of the Release Package |
| description | string | Description of the Release Package. Can be plain text or HTML. |

```
{
  "member": {
    "key": "IHTSDO"
  },
  "name": "Another Release",
  "description": "<p>Another Description<br/></p>"
}
```

### Response

```
{
  "releasePackageId": 211920,
  "createdAt": "2015-10-28T20:39:41.965Z",
  "createdBy": "user",
  "member": {
    "key": "SE"
  },
  "name": "Another Release",
  "description": "<p>Another Description<br/></p>",
  "releaseVersions": []
}
```

## Create a new Release Version

```
POST /api/releasePackages/:releaseVersionId/releaseVersions
```

### Input

| Name | Type | Description |
| ---- | ---- | ----------- |
| name | string | Name of the Release Version |
| description | string | Description of the Release Version. Can be plain text or HTML. |
| publishedAt | date | Optional - The publish date of the Released Version. Format: YYYY-MM-DD |

```
{
  "name": "First Version",
  "description": "<p><b>First</b> version description <br/></p>"
}
```

### Response

```
{
  "releaseVersionId": 211924,
  "createdAt": "2015-10-28T20:48:21.796Z",
  "createdBy": "user",
  "name": "First Version",
  "description": "<p><b>First</b> version description <br/></p>",
  "online": false,
  "publishedAt": "2015-10-28",
  "releaseFiles": []
}
```

## Create new Release File

Add a new release file to a Release Version.

```
POST /api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId/releaseFiles
```

### Input

| Name | Type | Description |
| ---- | ---- | ----------- |
| label | string | Short description of the file |
| downloadUrl | string | URL of file content |

```
{
  "label": "<p>Example file</p>",
  "downloadUrl": "http://files.com/example.txt"
}
```

### Response


```
{
  "releaseFileId": 211928,
  "label": "<p>Example file</p>",
  "createdAt": "2015-10-29T14:44:52.682Z",
  "clientDownloadUrl": "/api/releasePackages/211920/releaseVersions/211924/releaseFiles/211928/download",
  "downloadUrl": "http://files.com/example.txt"
}
```

Note that an affiliate download URL is used by affiliates to download the content via MLDS.


## Publish a Release Version Online

To publish a Release Version online the Release Version's online flag should be set to true. To take the Release Version offline the online flag should be set to false.

```
PUT /api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId
```

### Input


| Name | Type | Description |
| ---- | ---- | ----------- |
| name | string | Name of the Release Version |
| description | string | Description of the Release Version. Can be plain text or HTML. |
| online | boolean | True if the release version is available to affiliates to download |


```
{
  "name": "First Version",
  "description": "<p><b>First</b> version description <br/></p>",
  "online": true
}
```

### Response

```
{
  "releaseVersionId": 211924,
  "createdAt": "2015-10-28T20:48:21.796Z",
  "createdBy": "sweden",
  "name": "First Version",
  "description": "<p><b>First</b> version description <br/></p>",
  "online": true,
  "publishedAt": "2015-10-29",
  "releaseFiles": [{
    "releaseFileId": 211928,
    "label": "<p>Example file</p>",
    "createdAt": "2015-10-29T14:44:52.682Z",
    "clientDownloadUrl": "/api/releasePackages/211920/releaseVersions/211924/releaseFiles/211928/download",
    "downloadUrl": "http://files.com/example.txt"
  }]
}
```

## Notify Affiliates of Release V

Affiliates are not notified automatically when a new Release version has been published online. When a notification to affiliates with download access to the Release Package is warranted then the following endpoint can be used. 

```
POST /api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId/notifications
```

### Input

```
{
}
```

### Response
```
{
  "releaseVersionId": 211924,
  "createdAt": "2015-10-28T20:48:21.796Z",
  "createdBy": "sweden",
  "name": "First Version",
  "description": "<p><b>First</b> version description <br/></p>",
  "online": true,
  "publishedAt": "2015-10-29",
  "releaseFiles": [{
    "releaseFileId": 211928,
    "label": "<p>Example file</p>",
    "createdAt": "2015-10-29T14:44:52.682Z",
    "clientDownloadUrl": "/api/releasePackages/211920/releaseVersions/211924/releaseFiles/211928/download",
    "downloadUrl": "http://files.com/example.txt"
  }]
}
```

## Create a Release Package License

A license document can be associated with a Release Package.

```
POST /api/releasePackages/:releasePackageId/license
```

### Input

| Name | Type | Description |
| ---- | ---- | ----------- |
| file | form-data | File name of the posted file contents.

The request should be a `multipart\form-data' post to the server.

```
$ curl -u USER:PASSWORD -i -F "file=@FILE.PDF" 'https://mlds.ihtsdotools.org/api/releasePackages/211920/license'

```
 