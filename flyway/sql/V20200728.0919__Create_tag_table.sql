create table Tag (
    id varchar(26) PRIMARY KEY,
    name text NOT NULL,
    color text NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL
);

create table StockTag (
    stock_id varchar(26),
    tag_id varchar(26),
    created_at timestamp NOT NULL,

    foreign key (stock_id) references Stock(id),
    foreign key (tag_id) references Tag(id)
)
