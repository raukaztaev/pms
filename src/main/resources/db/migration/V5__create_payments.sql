create table payments (
    id uuid primary key,
    order_id uuid not null references orders(id),
    user_id uuid not null references users(id),
    amount numeric(12,2) not null,
    status varchar(32) not null,
    created_at timestamp not null
);
