# Open Customer Data Api

### About it

This is a open source api to simulate a data sharing using a consent base concept. Basicly to share any client data you need
to request him to consent the sharing. On this API we only have account information to be shared
and it will require the right permission and status to allow the success of the api call.
The security of the api is done using a bearer token as a plaintext base64. All the structures are detailed bellow

## 💻 Requirments

Antes de começar, verifique se você atendeu aos seguintes requisitos:

- Have Git installed
- Have Docker host installed and running
- Have Gradle installed
- Have java 17 Amazon Coretto installed


## 🚀 Running the API

To run the Api you must clone this repository and run the following command:

Linux e macOS:

```
make run-local
```
The host will be http://localhost:8080 when the docker spins up

## ☕ Using Customer Data Api

The security of the Api consist of a jwt token with a header and payload (the sign part is omitted on purpose).

Header
```
{
  "alg": "none",
  "typ": "JWT"
}

echo -n '{"alg": "none","typ": "JWT"}' | base64
```

Payload
```
{
  "scope": "consents",
  "client_id": "client1"
}

echo -n '{"scope": "consents","client_id": "client1"}' | base64
```
The api supports 2 scopes (consents, accounts)

You must specify each for each call you make


You put together the two parts generate by the commands above and also a dot(.) at the end

```
eyJhbGciOiAibm9uZSIsInR5cCI6ICJKV1QifQ==.eyJzY29wZSI6ICJjb25zZW50cyIsImNsaWVudF9pZCI6ICJjbGllbnQxIn0=.
```

How to create a consent
Permissions allowed:
- ACCOUNTS_READ
- CREDIT_CARD_READ
```
curl --location 'http://localhost:8080/test-api/consents/v1/consents' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <TOKEN>' \
--data '{
    "data": {
        "permissions": "ACCOUNTS_READ",
        "expirationDateTime": "2024-12-21T13:54:31Z"
    }
}'
```

How to update a consent
Permissions allowed:
- AUTHORISED
- REJECTED
```
curl --location --request PUT 'http://localhost:8080/test-api/consents/v1/consents/<CONSENTID>' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <TOKEN>' \
--data '{
    "data": {
        "status": "AUTHORISED"
    }
}'
```
