# Bug Report for Account Endpoints

## Summary
- [GET /accounts/v1/accounts/{accountId}](#get-accountsv1accountsaccountid)
- [GET /accounts/v1/accounts](#get-accountsv1accounts)
- [Priority Explanation](#priority-explanation) 

## GET /accounts/v1/accounts/{accountId}

### TC003: A short account ID returns error
**Description:** The endpoint does not validate the format of the Account ID, allowing short IDs to pass validation.

**Steps to Reproduce:**
1. Provide a valid authentication token.
2. Make a GET request to `/accounts/v1/accounts/87caf37b-f70f-440c-bacd-3b9399ca5d7`.

**Expected Results:** An error message "Internal Server Error" with status code 500 should be returned, containing "Internal Server Error: Invalid UUID string: {invalid Id}".

**Actual Results:** The endpoint returns a success response with status code 404.
   
**Priority:** Low
  
**Comments:** The endpoint should validate the Account ID format to prevent invalid requests.

### TC009: Request with a not existent consent ID scope token
**Description:** The endpoint does not validate if the Consent ID exists before processing the request.

**Steps to Reproduce:**
1. Provide an authentication token with a non-existent consent ID scope.
2. Make a GET request to `/accounts/v1/accounts`.
   
**Expected Results:** An error message "Not Found" with status code 404 should be returned, containing "Consent Id {consentId} not found".

**Actual Results:** The endpoint returns a success response with status code 200.

**Priority:** High

**Comments:** The endpoint should validate the existence of the Consent ID to prevent unauthorized access.

### TC010: Request with a consentId in AWAITING_AUTHORISATION status
**Description:** The endpoint does not validate the status of the Consent ID.

**Steps to Reproduce:**
1. Provide an authentication token with a consentId in `AWAITING_AUTHORISATION` status.
2. Make a GET request to `/accounts/v1/accounts`.

**Expected Results:** An error message "Forbidden" with status code 403 should be returned, containing "Consent Id {consentId} is not in the right status".

**Actual Results:** The endpoint returns a success response with status code 200.
   
**Priority:** High 

**Comments:** The endpoint should validate the status of the Consent ID to prevent unauthorized access.

### TC011: Request with a consentId in REJECTED status
**Description:** The endpoint does not validate the status of the Consent ID.

**Steps to Reproduce:**
1. Provide an authentication token with a consentId in `REJECTED` status.
2. Make a GET request to `/accounts/v1/accounts`.

**Expected Results:** An error message "Forbidden" with status code 403 should be returned, containing "Consent Id {consentId} is not in the right status".
   
**Actual Results:** The endpoint returns a success response with status code 200.
   
**Priority:** High

**Comments:** The endpoint should validate the status of the Consent ID to prevent unauthorized access.

### TC012: Request with an expired consent returns error
**Description:** The endpoint does not validate the expiration date of the Consent ID.

**Steps to Reproduce:**
1. Provide an authentication token with an expired consent.
2. Make a GET request to `/accounts/v1/accounts`.

**Expected Results:** An error message "Forbidden" with status code 403 should be returned, containing "Consent expired".
   
**Actual Results:** The endpoint returns a success response with status code 200.

**Priority:** Medium

**Comments:** The endpoint should validate the expiration date of the Consent ID to prevent access after expiration.

### TC013: Request without a valid consentId returns error
**Description:** The endpoint does not validate if the Consent Id is present in the request.

**Steps to Reproduce:**
1. Provide an authentication token.
2. Make a GET request to /accounts/v1/accounts without including a valid Consent Id.
   
**Expected Results:** An error message "Forbidden" with status code 403 should be returned, containing "Consent ID not present on the request".
   
**Actual Results:** The endpoint returns a success response with status code 200.
   
**Priority:** High
   
**Comments:** The endpoint should validate if the Consent Id is present in the request to prevent unauthorized access.

## GET /accounts/v1/accounts

### TC010: Request with an expired consent returns error
**Description:** The endpoint does not validate the expiration date of the Consent ID.

**Steps to Reproduce:**
1. Provide an authentication token with an expired consent.
2. Make a GET request to /accounts/v1/accounts.

**Expected Results:** An error message "Forbidden" with status code 403 should be returned, containing "Consent expired".
   
**Actual Results:** The endpoint returns a success response with status code 200.

**Priority:** Medium
   
**Comments:** The endpoint should validate the expiration date of the Consent ID to prevent access after expiration.

## Priority Explanation

In assigning priorities to the identified bugs, the severity of their impact on system functionality, data security, and compliance requirements was thoroughly evaluated.

**High Priority:** Bugs categorized as high priority represent potential risks to data integrity and security. These issues could lead to unauthorized access to sensitive information, violating compliance regulations and jeopardizing data confidentiality.

**Medium Priority:** Bugs classified as medium priority are not as urgent as high-priority issues but still require attention. Because when someone make a request with an expired consent it was supposed that at least once was authenticated, so it's a big problem but a bit less than the high priority bugs.

**Low Priority:** Bugs marked as low priority involve enhancements or optimizations that contribute to improving the user experience but do not pose immediate risks to data security or compliance.

By prioritizing bug resolution based on these criteria, we aim to address critical risks first while maintaining a balance between security, functionality, and user experience.
