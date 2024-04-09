CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username varchar(25) NOT NULL,
    email varchar NOT NULL,
    password varchar(200) NOT NULL,
    date_joined date NOT NULL,
);

CREATE TABLE records (
    record_id SERIAL PRIMARY KEY
    name varchar NOT NULL,
    length float,
    file_location url NOT NULL,
    creator_id int4 NOT NULL REFERENCES users(id)
);

CREATE TABLE collections (
    collection_id SERIAL PRIMARY KEY,
    name varchar(50) NOT NULL,
    user_id int4 NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE collection_items (
    item_id SERIAL PRIMARY KEY,
    collection_id NOT NULL REFERENCES collections(id) ON DELETE CASCADE,
    record_id NOT NULL REFERENCES records(id)
);