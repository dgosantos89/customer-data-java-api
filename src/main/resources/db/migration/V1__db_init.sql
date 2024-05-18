create table consents
(
    id varchar primary key not null,
    client_id varchar,
    status varchar,
    consent_permission varchar,
    creation_date_time timestamp,
    status_update_date_time timestamp,
    expiration_date_time timestamp

);

create table accounts
(
    id uuid NOT NULL,
    bank varchar,
    account_number varchar,
    creation_date_time timestamp,
    status_update_date_time timestamp
);