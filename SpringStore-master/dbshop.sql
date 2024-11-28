
CREATE SCHEMA shop;


ALTER SCHEMA shop OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;



CREATE TABLE shop.authorities (
    username character varying(50) NOT NULL,
    authority character varying(50) NOT NULL
);


ALTER TABLE shop.authorities OWNER TO postgres;


CREATE TABLE shop.order_items (
    id bigint NOT NULL,
    order_id bigint,
    product_id bigint
);


ALTER TABLE shop.order_items OWNER TO postgres;



CREATE SEQUENCE shop.order_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shop.order_items_id_seq OWNER TO postgres;



ALTER SEQUENCE shop.order_items_id_seq OWNED BY shop.order_items.id;




CREATE TABLE shop.orders (
    id bigint NOT NULL,
    username character varying(50)
);


ALTER TABLE shop.orders OWNER TO postgres;



CREATE SEQUENCE shop.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shop.orders_id_seq OWNER TO postgres;


ALTER SEQUENCE shop.orders_id_seq OWNED BY shop.orders.id;



CREATE TABLE shop.products (
    id bigint NOT NULL,
    title character varying(255),
    price integer
);


ALTER TABLE shop.products OWNER TO postgres;


CREATE SEQUENCE shop.products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shop.products_id_seq OWNER TO postgres;



ALTER SEQUENCE shop.products_id_seq OWNED BY shop.products.id;




CREATE TABLE shop.users (
    username character varying(50) NOT NULL,
    password character varying(100) NOT NULL,
    enabled boolean NOT NULL
);

CREATE TABLE shop.analytics (
                           id SERIAL PRIMARY KEY,
                           product_id INT REFERENCES shop.products(id) ON DELETE CASCADE,
                           total_sales INT DEFAULT 0,
                           total_revenue DECIMAL(10, 2) DEFAULT 0.00,
                           last_sold TIMESTAMP
);

ALTER TABLE shop.users OWNER TO postgres;



ALTER TABLE ONLY shop.order_items ALTER COLUMN id SET DEFAULT nextval('shop.order_items_id_seq'::regclass);




ALTER TABLE ONLY shop.orders ALTER COLUMN id SET DEFAULT nextval('shop.orders_id_seq'::regclass);



ALTER TABLE ONLY shop.products ALTER COLUMN id SET DEFAULT nextval('shop.products_id_seq'::regclass);



INSERT INTO shop.authorities (username, authority) VALUES
                                                       ('Tim', 'ROLE_ADMIN'),
                                                       ('Renat', 'ROLE_USER'),
                                                       ('Dina', 'ROLE_ADMIN');
INSERT INTO shop.order_items (id, order_id, product_id) VALUES
                                                            (1, 1, 2),
                                                            (2, 1, 4),
                                                            (3, 1, 5),
                                                            (4, 2, 2),
                                                            (5, 2, 4),
                                                            (6, 3, 2),
                                                            (7, 3, 4),
                                                            (8, 4, 2),
                                                            (9, 4, 4),
                                                            (29, 13, 4),
                                                            (30, 13, 2),
                                                            (31, 13, 3),
                                                            (37, 16, 4),
                                                            (38, 16, 2),
                                                            (39, 17, 3),
                                                            (40, 17, 2);
INSERT INTO shop.orders (id, username) VALUES
                                           (1, 'Renat'),
                                           (2, 'Tim'),
                                           (3, 'Renat'),
                                           (4, 'Tim'),
                                           (13, 'Renat'),
                                           (16, 'Tim'),
                                           (17, 'Tim');

INSERT INTO shop.users (username, password, enabled) VALUES
                                                         ('Dina', '{noop}cola', true),
                                                         ('Tim', '{noop}sir', true),
                                                         ('Renat', '{noop}far', true);




SELECT setval('shop.order_items_id_seq', 40, true);

-- Устанавливаем значение для последовательности shop.orders_id_seq
SELECT setval('shop.orders_id_seq', 17, true);

-- Устанавливаем значение для последовательности shop.products_id_seq
SELECT setval('shop.products_id_seq', 5, true);

--
-- Name: order_items order_items_pkey; Type: CONSTRAINT; Schema: shop; Owner: postgres
--

ALTER TABLE ONLY shop.order_items
    ADD CONSTRAINT order_items_pkey PRIMARY KEY (id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: shop; Owner: postgres
--

ALTER TABLE ONLY shop.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);




ALTER TABLE ONLY shop.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);



ALTER TABLE ONLY shop.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);




ALTER TABLE ONLY shop.authorities
    ADD CONSTRAINT authorities_username_fkey FOREIGN KEY (username) REFERENCES shop.users(username);



ALTER TABLE ONLY shop.order_items
    ADD CONSTRAINT order_items_order_id_fkey FOREIGN KEY (order_id) REFERENCES shop.orders(id);




ALTER TABLE ONLY shop.order_items
    ADD CONSTRAINT order_items_product_id_fkey FOREIGN KEY (product_id) REFERENCES shop.products(id);


DELETE FROM shop.order_items WHERE product_id IN (2, 4, 5, 3);

DELETE FROM shop.products WHERE id IN (2, 4, 5, 3);

INSERT INTO shop.products (id, title, price, img_url) VALUES
                                                          (1, 'Теннисная ракетка', 250, 'im1.jpg'),
                                                          (2, 'Упаковка теннисных мячей', 50, 'im1.jpg'),
                                                          (3, 'Теннисная обувь', 300, 'im1.jpg'),
                                                          (4, 'Теннисная сумка', 120, 'im1.jpg'),
                                                          (5, 'Теннисные носки', 20, 'im1.jpg'),
                                                          (6, 'Теннисные шорты', 70, 'im1.jpg'),
                                                          (7, 'Теннисная футболка', 90, 'im1.jpg'),
                                                          (8, 'Теннисная кепка', 30, 'im1.jpg'),
                                                          (9, 'Теннисная лента для рукоятки', 15, 'im1.jpg'),
                                                          (10, 'Теннисная сетка', 500, 'im1.jpg');

ALTER TABLE shop.products ADD COLUMN quantity int;

CREATE TABLE shop.order_items (
                             id BIGSERIAL PRIMARY KEY,         -- Уникальный идентификатор для OrderItem
                             order_id BIGINT NOT NULL,         -- Ссылка на заказ
                             product_id BIGINT NOT NULL,       -- Ссылка на продукт
                             FOREIGN KEY (order_id) REFERENCES shop.orders (id) ON DELETE CASCADE, -- Внешний ключ на таблицу заказов
                             FOREIGN KEY (product_id) REFERENCES shop.products (id) ON DELETE CASCADE -- Внешний ключ на таблицу продуктов
);

CREATE TABLE shop.images (
                        id BIGSERIAL PRIMARY KEY,                   -- Уникальный идентификатор изображения
                        name VARCHAR(255) NOT NULL,                -- Название изображения
                        file_name VARCHAR(255) NOT NULL,           -- Имя файла
                        size BIGINT NOT NULL,                      -- Размер файла в байтах
                        file_type VARCHAR(255) NOT NULL,           -- MIME-тип файла (например, image/png)
                        bytes BYTEA NOT NULL,                      -- Бинарное содержимое файла
                        product_id BIGINT,                         -- Внешний ключ для связи с таблицей продуктов
                        CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES shop.products (id) ON DELETE CASCADE
);
CREATE TABLE shop.ProductDetails (
                                     id SERIAL PRIMARY KEY,               -- Уникальный идентификатор записи
                                     product_name VARCHAR(255) NOT NULL,   -- Название товара
                                     description TEXT NOT NULL,           -- Описание товара
                                     brand VARCHAR(100),                  -- Бренд товара
                                     price INTEGER,                        -- Цена (целочисленное значение)
                                     warranty_period INT,                 -- Гарантийный срок в месяцах
                                     product_id INT,                      -- Внешний ключ к продукту
                                     CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES shop.products(id)  -- Внешний ключ
);

