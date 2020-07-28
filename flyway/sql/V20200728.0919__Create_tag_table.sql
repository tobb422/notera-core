create table tags (
    id varchar(26) PRIMARY KEY,
    name text NOT NULL,
    color text NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL
);

create table stock_tags (
    stock_id varchar(26),
    tag_id varchar(26),
    created_at timestamp NOT NULL,

    foreign key (stock_id) references stocks(id),
    foreign key (tag_id) references tags(id)
)
