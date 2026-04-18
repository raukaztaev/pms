create table order_participants (
    order_id uuid not null references orders(id),
    user_id uuid not null references users(id),
    joined_at timestamp not null,
    primary key (order_id, user_id)
);
