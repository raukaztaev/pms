create table order_status_history (
    id uuid primary key,
    order_id uuid not null references orders(id),
    from_status varchar(32),
    to_status varchar(32) not null,
    changed_by uuid not null references users(id),
    changed_at timestamp not null
);
