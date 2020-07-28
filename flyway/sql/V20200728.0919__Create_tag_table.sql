create table tags (
    id varchar(26) PRIMARY KEY,
    user_id varchar(26),
    name text NOT NULL,
    color text NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,

    foreign key (user_id) references users(id) on delete cascade
);

create table stock_tags (
    stock_id varchar(26),
    tag_id varchar(26),
    created_at timestamp NOT NULL,

    foreign key (stock_id) references stocks(id) on delete cascade,
    foreign key (tag_id) references tags(id) on delete cascade
)
