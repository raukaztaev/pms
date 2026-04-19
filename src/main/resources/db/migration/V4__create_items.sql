create table items (
    id uuid primary key,
    order_id uuid not null references orders(id),
    user_id uuid not null references users(id),
    name varchar(255) not null,
    link text,
    quantity int not null,
    unit_price numeric(12,2) not null,
    notes text,
    distributed boolean not null default false,
    created_at timestamp not null
);
