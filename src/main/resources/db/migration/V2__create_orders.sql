create table orders (
    id uuid primary key,
    organizer_id uuid not null references users(id),
    product_name varchar(255) not null,
    source_platform varchar(255) not null,
    min_target_amount numeric(12,2) not null,
    current_total numeric(12,2) not null default 0,
    status varchar(32) not null,
    deadline timestamp not null,
    created_at timestamp not null,
    version bigint
);
