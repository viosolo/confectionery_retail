---- 1. ОЧИСТКА (начинаем с зависимых таблиц)
TRUNCATE TABLE product_ingredients, order_products, orders, products, categories, users RESTART IDENTITY CASCADE;

-- 2. ПОЛЬЗОВАТЕЛИ (создаем их ПЕРВЫМИ, чтобы заказы могли на них ссылаться)
INSERT INTO users (id, first_name, last_name, email, password, phone, role, created_at)
VALUES (1, 'Анна', 'Сладкая', 'anna@bakery.com', 'pass123', '+79001112233', 'ADMIN', CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, created_at)
VALUES (2, 'Иван', 'Тестовый', 'ivan@mail.ru', 'pass456', '+79004445566', 'USER', CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, created_at)
VALUES (3, 'Мария', 'Кремова', 'maria@yandex.ru', 'pass789', '+79007778899', 'USER', CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, created_at)
VALUES (4, 'Дмитрий', 'Шоколадов', 'admin@bakery.com', 'admin777', '+79000000000', 'ADMIN', CURRENT_TIMESTAMP);

-- 3. КАТЕГОРИИ
INSERT INTO categories (id, name, slug, description) VALUES (1, 'Зефир', 'zephyr', 'Воздушный натуральный десерт');
INSERT INTO categories (id, name, slug, description) VALUES (2, 'Макаронс', 'macaron', 'Французское миндальное пирожное');
INSERT INTO categories (id, name, slug, description) VALUES (3, 'Эклер', 'eclair', 'Классическое пирожное с кремом');

-- 4. ПРОДУКТЫ (уже привязаны к категориям)
-- Зефир (id 1-6)
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Яблочный (Классика)', 1, 'Яблоко', 2.6, 20, 'Золотой стандарт зефира...', 50, 300);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Кофейный', 1, 'Кофе', 2.7, 15, 'Для тех, кто любит покрепче...', 50, 310);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Черничный', 1, 'Черника', 3.0, 12, 'Лесная ягода в каждом кусочке...', 55, 290);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Вишневый', 1, 'Вишня', 2.9, 18, 'Изысканная терпкость вишни...', 50, 295);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Клубничный', 1, 'Клубника', 3.1, 25, 'Классика летнего настроения...', 50, 305);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Апельсиновый', 1, 'Апельсин', 2.8, 10, 'Солнечный вкус...', 50, 300);

-- Макаронс (id 7-11)
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Зеленый лес', 2, 'Фисташка', 3.0, 50, 'С крошкой ореха', 30, 450);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Лавандовое поле', 2, 'Лаванда', 3.10, 40, 'Нежный аромат', 30, 440);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Соленая карамель', 2, 'Caramel', 2.80, 60, 'Топ продаж', 35, 460);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Малиновый закат', 2, 'Малина', 3.30, 45, 'С кислинкой', 30, 430);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Черничная ночь', 2, 'Черника', 3.10, 30, 'Насыщенный цвет', 30, 440);

-- Эклеры (id 12-16)
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Классический эклер', 3, 'Ваниль', 4.0, 15, 'С заварным кремом', 70, 520);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Шоколадный король', 3, 'Шоколад', 4.10, 10, 'Двойной шоколад', 75, 550);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Тропический', 3, 'Манго', 3.90, 7, 'С нежным муссом', 70, 490);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Ореховый', 3, 'Орех', 4.50, 5, 'С фундуком', 80, 580);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Сливочная карамель', 3, 'Карамель', 4.20, 12, 'С тягучей начинкой', 75, 530);

-- 5. ИНГРЕДИЕНТЫ И СВЯЗИ
INSERT INTO ingredients (name, description) VALUES ('Агар-агар', 'Растительный заменитель желатина');
INSERT INTO ingredients (name, description) VALUES ('Миндальная мука', 'Мелкий помол миндаля для макаронс');
INSERT INTO ingredients (name, description) VALUES ('Сахар', 'Белый кристаллический сахар');
INSERT INTO ingredients (name, description) VALUES ('Мука пшеничная', 'Пшеничная мука высшего сорта');

INSERT INTO product_ingredients (product_id, ingredient_id)
SELECT p.id, (SELECT i.id FROM ingredients i WHERE i.name = 'Агар-агар') FROM products p WHERE p.category_id = 1;

INSERT INTO product_ingredients (product_id, ingredient_id)
SELECT p.id, (SELECT i.id FROM ingredients i WHERE i.name = 'Миндальная мука') FROM products p WHERE p.category_id = 2;

INSERT INTO product_ingredients (product_id, ingredient_id)
SELECT p.id, (SELECT i.id FROM ingredients i WHERE i.name = 'Сахар') FROM products p WHERE p.category_id IN (1, 2, 3);

INSERT INTO product_ingredients (product_id, ingredient_id)
SELECT p.id, (SELECT i.id FROM ingredients i WHERE i.name = 'Мука пшеничная') FROM products p WHERE p.category_id = 3;

-- 6. ЗАКАЗЫ (Пользователи уже существуют, теперь можно добавлять)
INSERT INTO orders (id, order_number, user_id, user_name, user_email, total_amount, status, delivery_address, payment_method, notes, created_at, updated_at)
VALUES (1, 'ORD-2026-001', 3, 'Мария Кремова', 'maria@yandex.ru', 1500.00, 'DELIVERED', 'ул. Пушкина, д. 10', 'CARD', 'Доставлено вовремя', '2026-03-01 10:00:00', '2026-03-01 12:00:00');

INSERT INTO orders (id, order_number, user_id, user_name, user_email, total_amount, status, delivery_address, payment_method, notes, created_at, updated_at)
VALUES (2, 'ORD-2026-002', 2, 'Иван Тестовый', 'ivan@mail.ru', 850.50, 'CONFIRMED', 'пр. Ленина, д. 25', 'CASH', 'Просьба позвонить за час', '2026-03-01 14:30:00', '2026-03-01 14:35:00');

INSERT INTO orders (id, order_number, user_id, user_name, user_email, total_amount, status, delivery_address, payment_method, notes, created_at, updated_at)
VALUES (3, 'ORD-2026-003', 3, 'Мария Кремова', 'maria@yandex.ru', 2100.00, 'PENDING', 'ул. Садовая, д. 5', 'CARD', 'БЕЗ ТРАНЗАКЦИИ', '2026-03-01 16:40:00', '2026-03-01 16:40:00');

INSERT INTO orders (id, order_number, user_id, user_name, user_email, total_amount, status, delivery_address, payment_method, notes, created_at, updated_at)
VALUES (4, 'ORD-2026-004', 2, 'Иван Тестовый', 'ivan@mail.ru', 5000.00, 'PROCESSING', 'ул. Цветочная, д. 1', 'CARD', 'Торт на день рождения', '2026-03-01 17:00:00', '2026-03-01 17:05:00');

-- 7. СОДЕРЖИМОЕ ЗАКАЗОВ (order_products)
INSERT INTO order_products (order_id, product_id) VALUES (1, 1), (1, 7), (2, 12), (3, 2), (4, 13);