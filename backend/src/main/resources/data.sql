-- 1. Очистка всех таблиц (ВАЖНО: соблюдаем порядок из-за внешних ключей)
TRUNCATE TABLE product_ingredients, order_products, products, categories RESTART IDENTITY CASCADE;

-- 2. КАТЕГОРИИ
INSERT INTO categories (id, name, slug, description) VALUES (1, 'Зефир', 'zephyr', 'Воздушный натуральный десерт');
INSERT INTO categories (id, name, slug, description) VALUES (2, 'Макаронс', 'macaron', 'Французское миндальное пирожное');
INSERT INTO categories (id, name, slug, description) VALUES (3, 'Эклер', 'eclair', 'Классическое пирожное с кремом');

-- 3. ПРОДУКТЫ: ЗЕФИР (category_id = 1)
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Яблочный (Классика)', 1, 'Яблоко', 2.6, 20, 'Золотой стандарт зефира...', 50, 300);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Кофейный', 1, 'Кофе', 2.7, 15, 'Для тех, кто любит покрепче...', 50, 310);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Черничный', 1, 'Черника', 3.0, 12, 'Лесная ягода в каждом кусочке...', 55, 290);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Вишневый', 1, 'Вишня', 2.9, 18, 'Изысканная терпкость вишни...', 50, 295);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Клубничный', 1, 'Клубника', 3.1, 25, 'Классика летнего настроения...', 50, 305);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Апельсиновый', 1, 'Апельсин', 2.8, 10, 'Солнечный вкус...', 50, 300);

-- 4. ПРОДУКТЫ: МАКАРОНС (category_id = 2)
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Зеленый лес', 2, 'Фисташка', 3.0, 50, 'С крошкой ореха', 30, 450);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Лавандовое поле', 2, 'Лаванда', 3.10, 40, 'Нежный аромат', 30, 440);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Соленая карамель', 2, 'Caramel', 2.80, 60, 'Топ продаж', 35, 460);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Малиновый закат', 2, 'Малина', 3.30, 45, 'С кислинкой', 30, 430);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Черничная ночь', 2, 'Черника', 3.10, 30, 'Насыщенный цвет', 30, 440);

-- 5. ПРОДУКТЫ: ЭКЛЕРЫ (category_id = 3)
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Классический эклер', 3, 'Ваниль', 4.0, 15, 'С заварным кремом', 70, 520);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Шоколадный король', 3, 'Шоколад', 4.10, 10, 'Двойной шоколад', 75, 550);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Тропический', 3, 'Манго', 3.90, 7, 'С нежным муссом', 70, 490);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Ореховый', 3, 'Орех', 4.50, 5, 'С фундуком', 80, 580);
INSERT INTO products (name, category_id, flavor, price, stock_quantity, description, weight, calories) VALUES ('Сливочная карамель', 3, 'Карамель', 4.20, 12, 'С тягучей начинкой', 75, 530);