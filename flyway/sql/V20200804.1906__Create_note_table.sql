create table notes (
    id varchar(26) PRIMARY KEY,
    user_id varchar(26),
    stock_id varchar(26),
    content text NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,

    foreign key (user_id) references users(id) on delete cascade,
    foreign key (stock_id) references stocks(id) on delete cascade
);
