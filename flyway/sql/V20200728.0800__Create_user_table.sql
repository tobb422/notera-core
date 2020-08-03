create table users (
    id varchar(26) PRIMARY KEY,
    uid text NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL
);

create index on users (uid);
