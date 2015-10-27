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

The current API does not require authentication.

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
