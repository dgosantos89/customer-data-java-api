# OpenAPI Documentation Review

The primary advantage of using Swagger (OpenAPI) is its ability to provide live, interactive documentation and easy to implement and maintain on micronaut service. However, if it is treated as static documentation, it requires constant updates to remain accurate. Below are several issues identified in the current OpenAPI documentation:

## Issues Identified

### 1. Server URL
- **Problem**: The server URLs are missing the `http://` prefix.
- **Suggestion**: Insert on the server URL `http://` prefix.

### 2. Documentation Exposure
- **Problem**: The documentation could be exposed on the server for easy access and updates.
- **Suggestion**: Configure a route for serving the Swagger UI documentation.

### 3. Mixed Languages
- **Problem**: The documentation mixes Portuguese and English.
- **Suggestion**: Maintain a single language throughout the documentation for consistency and clarity. Preferably, use English for wider accessibility.

### 4. Unimplemented Permissions
- **Problem**: The request body schema mentions a permission (`CREDIT_CARD_READ`) that is not implemented.
- **Suggestion**: Remove or update the schema to reflect only the implemented permissions.

### 5. Error Response Examples
- **Problem**: Error response examples are not provided.
- **Suggestion**: Add examples of error responses for better understanding and handling of errors.

### 6. Response Schema Mismatch
- **Problem**: The `ResponseAccountData` and `ResponseConsentData` objects do not reflect the actual response schema.
- **Suggestion**: Update the schema to match the actual data structure returned by the API.

### 7. Authorization Header
- **Problem**: The authorization header is not consistently included in the requests.
- **Suggestion**: Ensure the authorization header is specified for all endpoints requiring authentication.

### 8. Authentication and Authorization Information
- **Problem**: Lack of detailed information about authentication and authorization mechanisms.
- **Suggestion**: Add a section explaining how to obtain and use access tokens, including example requests.
