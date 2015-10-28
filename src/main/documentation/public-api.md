# IHTSDO-MLDS API

## Schema

All access to the API is over HTTPS to the `mlds.ihtsdotools.org` domain. All data is sent and received as JSON.

```
$ curl -i 'https://mlds.ihtsdotools.org/affiliates/check?member=IHTSDO&match=spectra'
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

{"error":{"message":"Missing mandatory parameter: match"}}
```

## Authentication

The public APIs do not require authentication to be supplied.

### Basic Authentication

The APIs that do require authentication support Basic Authentication using MLDS credentials.

Ensure that all communication is through `https` to ensure the credentials aren't revealed.

```
$ curl -u USER:PASSWORD -i 'https://mlds.ihtsdotools.org/app/rest/releasePackages'
```

---

# Affiliate API

## Affiliate Check

Check that an affiliate is in good standing with IHTSDO.

```
GET /affiliates/check?member=IHTSDO&match=keyword
```

### Parameters

| Name | Type | Optional | Description |
| ---- | ---- | -------- | ----------- |
| member | string | Mandatory | Identification of the member country that the affiliate has been accepted by. It is either a two-letter country code or `IHTSDO` to indicate non-aligned countries. The country codes are a subset of the ISO 3166-1 alpha-2 codes. Valid options: AU BE BN CA CL CZ DK EE ES GB HK IHTSDO IL IN IS LT MT MY NL NZ PL PT SE SG SI SK US UY. |
| match | string | Mandatory | Search keyword within the affiliate record. This will match against a number of identifying fields: organization name, first name, last name, street address, email, alternative email, and third email. |
| affiliateId | string | Optional | Limit the search to a specific Affilate record. |

The check API requires a unique match to be found from the database of Affiliates. Affiliates are filtered by the member, optionally by affiliate id, and a keyword text match against the identifying fields of the affiliate.

Once a single affiliate has been identified the affiliate's application must have been approved andfor the affiliate to be considered in good standing.

If seccusseful then the API will return with a `matched: true`.

If there is no match then the API will return with `matched: false`.

### Examples

A successful match against a Swedish affilate based on a match against the `abc@test.com` email address. The affiliate was uniquely identified and the account was in good standing.

```
$ curl -i 'https://mlds.ihtsdotools.org/affiliates/check?member=SE&match=abc@test.com'
HTTP/1.1 200 OK
Content-Type: application/json

{"matched":true}
```

An unsuccessful match against an IHTSDO affilate based a match against the keyword hospital. No affiliate was unique identified as many affiliates contained `hospital` in the searched fields.

```
$ curl -i 'https://mlds.ihtsdotools.org/affiliates/check?member=SE&match=abc@test.com'
HTTP/1.1 200 OK
Content-Type: application/json

{"matched":false}
```

A successful match that limited the search to a single specified affiliate. The 'hospital' keyword matched, as before, however, by limiting the search to a single affiliate the result was unique.

```
$ curl -i 'https://mlds.ihtsdotools.org/affiliates/check?member=IHTSDO&match=hospital&affiliateId=123'
HTTP/1.1 200 OK
Content-Type: application/json

{"matched":true}
```

---

# Release Packages API

Each member organization can make Release Packages available to their affiliates for downloading. A Release Package has a current version and past versions. A Version contains multiple files that can be individually downloaded by an affiliate.

Release files are not stored in MLDS and instead a URL is provided where the file can be download from on demand. Files are typically stored on the Amazon s3 service.

## Authentication

You can get all release packages without authentication; however, to modify and add release packages requires authentication with a user with staff authorization. 

##  Get all release packages

List all release packages.

```
$ curl -i 'https://mlds.ihtsdotools.org/app/rest/releasePackages'
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
      "clientDownloadUrl": "/app/rest/releasePackages/1911/releaseVersions/1913/releaseFiles/1921/download",
      "downloadUrl": "http://google.com?q=file2"
    }, {
      "releaseFileId": 1919,
      "label": "<p>file1<br/></p>",
      "createdAt": "2015-10-14T19:18:30.628Z",
      "clientDownloadUrl": "/app/rest/releasePackages/1911/releaseVersions/1913/releaseFiles/1919/download",
      "downloadUrl": "http://google.com?q=file1"
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
      "clientDownloadUrl": "/app/rest/releasePackages/5267/releaseVersions/5269/releaseFiles/5271/download",
      "downloadUrl": "http://www.google.com?q=a1"
    }]
  }]
}]
```

## Get a single Release Package

```
GET /app/rest/releasePackages/:releasePackageId'
```

## Get a single Release Version

```
GET /app/rest/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId'
```

### Get a single Release File

```
GET /app/rest/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId/releaseFiles/:releaseFileId'
```

## Create a new Release Package

```
POST /app/rest/releasePackages 
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
POST /app/rest/releasePackages/:releaseVersionId/releaseVersions
```

### Input

| Name | Type | Description |
| ---- | ---- | ----------- |
| name | string | Name of the Release Version |
| description | string | Description of the Release Version. Can be plain text or HTML. |
| publishedAt | date | Optional date that the version should be published. |

```
{
  "name": "First Version",
  "description": "<p><b>First</b> version description <br/></p>",
  "publishedAt": "2015-10-28"
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
