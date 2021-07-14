DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE users(
  id serial PRIMARY KEY NOT NULL,
  username varchar(30) UNIQUE NOT NULL,
  email varchar(100) UNIQUE NOT NULL,
  password varchar(150) NOT NULL,
  name varchar(100) NOT NULL,
  phone VARCHAR(255)
);

