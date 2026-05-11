-- segment
create table segment (
    segment_id serial primary key,
    name varchar(255) not null unique
);

-- size
create table size (
    size_id serial primary key,
    name varchar(255) not null unique
);

-- color
create table color (
    color_id serial primary key,
    name varchar(255) not null unique,
    hex varchar(6) not null unique
);

-- product
create table product (
    product_id serial primary key,
    name varchar(255) not null unique,
    description text,
    current_price int not null check (current_price > 0),
    segment_id int not null references segment(segment_id) on delete restrict
);

-- variant
create table variant (
	variant_id serial primary key,
    product_id int not null references product(product_id) on delete cascade,
    size_id int not null references size(size_id) on delete restrict,
    color_id int not null references color(color_id) on delete restrict,
    quantity int not null default 0 check (quantity >= 0),
    unique (product_id, size_id, color_id)
);

-- image
create table image (
    image_id serial primary key,
    product_id int not null references product(product_id) on delete cascade,
    color_id int not null references color(color_id) on delete restrict,
    path varchar(255) not null,
    is_main boolean not null default false
);

-- city
create table city (
    city_uuid varchar(36) primary key,
    name varchar(255) not null unique
);

-- address
create table address (
    address_id serial primary key,
    address varchar(255) not null unique,
    city_uuid varchar(36) not null references city(city_uuid) on delete restrict
);

-- users
create table users (
    user_id serial primary key,
    username varchar(255) not null unique,
    password_hash varchar(60) not null,
    role varchar(255) not null check (role in ('customer', 'admin'))
);

-- product_item
create table product_item (
    product_item_id serial primary key,
    variant_id int not null references variant(variant_id) on delete restrict,
    is_sold boolean default false
);

-- purchase_status
create table purchase_status (
    purchase_status_id serial primary key,
    name varchar(255) not null unique
);

insert into purchase_status(name) values
('ожидание передачи в пункт отправки'),
('ожидание отправки'),
('в пути к получателю'),
('ожидание получения'),
('получен');

-- purchase
create table purchase (
    purchase_id serial primary key,
    user_id int not null references users(user_id) on delete restrict,
    status_id int not null references purchase_status(purchase_status_id) on delete restrict,
    target_address_id int not null references address(address_id) on delete restrict
);

-- purchase_item
create table purchase_item (
    purchase_item_id serial primary key,
    purchase_id int not null references purchase(purchase_id) on delete cascade,
    product_item_id int not null references product_item(product_item_id) on delete restrict,
    price int not null check (price > 0)
);

-- purchase_status_history
create table purchase_status_history (
    purchase_status_history_id serial primary key,
    purchase_id int not null references purchase(purchase_id) on delete cascade,
    previous_status_id int not null references purchase_status(purchase_status_id) on delete restrict,
    changed_at timestamptz not null
);

-- session
create table session (
    session_id serial primary key,
    username varchar(255) not null,
    role varchar(60) not null check (role in ('customer', 'admin')),
    token varchar(255) not null unique,
    expires_at timestamp not null
);