create table notifications (
    id uuid primary key,
    user_id uuid not null references users(id),
    type varchar(100) not null,
    payload text not null,
    channel varchar(32) not null,
    delivered_at timestamp,
    created_at timestamp not null
);
