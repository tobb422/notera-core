create table Stock (
    id varchar(26) NOT NULL PRIMARY KEY,
    title text NOT NULL,
    url text NOT NULL,
    image text NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL

    index idx_id(id)
);
