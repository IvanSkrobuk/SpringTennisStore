
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
