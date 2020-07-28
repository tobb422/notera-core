create table stocks (
    id varchar(26) PRIMARY KEY,
    user_id varchar(26),
    title text NOT NULL,
    url text NOT NULL,
    image text NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,

    foreign key (user_id) references users(id) on delete cascade
);
