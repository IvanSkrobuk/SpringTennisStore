-- 1. Создаем схему
CREATE SCHEMA shop;
ALTER SCHEMA shop OWNER TO postgres;

-- 2. Устанавливаем параметры
SET default_tablespace = '';
SET default_with_oids = false;

-- 3. Создаем последовательности
CREATE SEQUENCE shop.order_items_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE shop.order_items_id_seq OWNER TO postgres;

CREATE SEQUENCE shop.orders_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE shop.orders_id_seq OWNER TO postgres;

CREATE SEQUENCE shop.products_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE shop.products_id_seq OWNER TO postgres;

-- 4. Создаем таблицы
CREATE TABLE shop.users (
                            username VARCHAR(50) NOT NULL PRIMARY KEY,
                            password VARCHAR(100) NOT NULL,
                            enabled BOOLEAN NOT NULL
);
ALTER TABLE shop.users OWNER TO postgres;

CREATE TABLE shop.authorities (
                                  username VARCHAR(50) NOT NULL,
                                  authority VARCHAR(50) NOT NULL,
                                  FOREIGN KEY (username) REFERENCES shop.users(username) ON DELETE CASCADE
);
ALTER TABLE shop.authorities OWNER TO postgres;

CREATE TABLE shop.orders (
                             id BIGINT PRIMARY KEY DEFAULT nextval('shop.orders_id_seq'),
                             username VARCHAR(50),
                             FOREIGN KEY (username) REFERENCES shop.users(username) ON DELETE SET NULL
);
ALTER TABLE shop.orders OWNER TO postgres;

CREATE TABLE shop.products (
                               id BIGINT PRIMARY KEY DEFAULT nextval('shop.products_id_seq'),
                               title VARCHAR(255),
                               price INTEGER,
                               quantity INT DEFAULT 0
);
ALTER TABLE shop.products OWNER TO postgres;

CREATE TABLE shop.order_items (
                                  id BIGINT PRIMARY KEY DEFAULT nextval('shop.order_items_id_seq'),
                                  order_id BIGINT NOT NULL,
                                  product_id BIGINT NOT NULL,
                                  FOREIGN KEY (order_id) REFERENCES shop.orders(id) ON DELETE CASCADE,
                                  FOREIGN KEY (product_id) REFERENCES shop.products(id) ON DELETE CASCADE
);
ALTER TABLE shop.order_items OWNER TO postgres;

CREATE TABLE shop.analytics (
                                id SERIAL PRIMARY KEY,
                                product_id INT REFERENCES shop.products(id) ON DELETE CASCADE,
                                total_sales INT DEFAULT 0,
                                total_revenue DECIMAL(10, 2) DEFAULT 0.00,
                                last_sold TIMESTAMP
);

CREATE TABLE shop.images (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             file_name VARCHAR(255) NOT NULL,
                             size BIGINT NOT NULL,
                             file_type VARCHAR(255) NOT NULL,
                             bytes BYTEA NOT NULL,
                             product_id BIGINT,
                             CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES shop.products(id) ON DELETE CASCADE
);

CREATE TABLE shop.ProductDetails (
                                     id SERIAL PRIMARY KEY,
                                     product_name VARCHAR(255) NOT NULL,
                                     description TEXT NOT NULL,
                                     brand VARCHAR(100),
                                     price INTEGER,
                                     warranty_period INT,
                                     product_id INT,
                                     CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES shop.products(id)
);

-- 5. Заполняем таблицы
INSERT INTO shop.users (username, password, enabled) VALUES
                                                         ('Dina', '{noop}cola', true),
                                                         ('Tim', '{noop}sir', true),
                                                         ('Renat', '{noop}far', true);

INSERT INTO shop.authorities (username, authority) VALUES
                                                       ('Tim', 'ROLE_ADMIN'),
                                                       ('Renat', 'ROLE_USER'),
                                                       ('Dina', 'ROLE_ADMIN');

INSERT INTO shop.orders (id, username) VALUES
                                           (1, 'Renat'),
                                           (2, 'Tim'),
                                           (3, 'Renat'),
                                           (4, 'Tim'),
                                           (13, 'Renat'),
                                           (16, 'Tim'),
                                           (17, 'Tim');

INSERT INTO shop.products (id, title, price) VALUES
                                                 (1, 'Теннисная ракетка', 250),
                                                 (2, 'Упаковка теннисных мячей', 50),
                                                 (3, 'Теннисная обувь', 300),
                                                 (4, 'Теннисная сумка', 120),
                                                 (5, 'Теннисные носки', 20),
                                                 (6, 'Теннисные шорты', 70),
                                                 (7, 'Теннисная футболка', 90),
                                                 (8, 'Теннисная кепка', 30),
                                                 (9, 'Теннисная лента для рукоятки', 15),
                                                 (10, 'Теннисная сетка', 500);

INSERT INTO shop.order_items (id, order_id, product_id) VALUES
                                                            (1, 1, 2), (2, 1, 4), (3, 1, 5), (4, 2, 2), (5, 2, 4),
                                                            (6, 3, 2), (7, 3, 4), (8, 4, 2), (9, 4, 4), (29, 13, 4),
                                                            (30, 13, 2), (31, 13, 3), (37, 16, 4), (38, 16, 2),
                                                            (39, 17, 3), (40, 17, 2);

-- 6. Устанавливаем значения последовательностей
SELECT setval('shop.order_items_id_seq', 40, true);
SELECT setval('shop.orders_id_seq', 17, true);
SELECT setval('shop.products_id_seq', 10, true);
