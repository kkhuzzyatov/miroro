insert into users(
	username, 
	password_hash, 
	role
) values(
	'khuzzyatov',
	'kkhuzzyatov',
	'admin'
);

-- =========================
-- SEGMENT
-- =========================
INSERT INTO segment(name) VALUES
('Блузки'),
('Брюки');

-- =========================
-- SIZE (единая таблица)
-- =========================
INSERT INTO size(name) VALUES
('S 40-44'),
('M 46-48'),
('L 50-52'),
('XL 54-56'),
('XXL 58-60'),
('50'),
('52'),
('54'),
('56'),
('58'),
('60');

-- =========================
-- COLOR
-- =========================
INSERT INTO color(name, hex) VALUES
('желтый', 'FFD700'),
('зеленый', '2ECC71'),
('черный', '000000'),
('золото', 'D4AF37'),
('бежевый', 'F5F5DC'),
('серый', '808080'),
('красный', 'FF0000'),
('леопардовый', 'C19A6B');

-- =========================
-- PRODUCT
-- =========================
INSERT INTO product(name, description, current_price, segment_id)
VALUES
('Блузка нарядная с плиссированными рукавами', 'Блузка нарядная', 2800, 1),
('Брюки нарядные блестящие', 'Брюки нарядные', 2600, 2),
('Пиджак укороченный', 'Пиджак укороченный', 3500, 1),
('Брюки палаццо', 'Брюки палаццо', 3000, 2),
('Платье поло', 'Платье поло', 3200, 1),
('Блузка с поясом', 'Блузка с поясом', 2900, 1);

-- =========================
-- VARIANT (PRODUCT 1 - Блузка)
-- =========================

-- желтый (S, M, L, XL, XXL) -> M (46–48) позже исключён из product_item
INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(1, 1, 1, 10),
(1, 2, 1, 0),
(1, 3, 1, 8),
(1, 4, 1, 12),
(1, 5, 1, 6);

-- зеленый
INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(1, 2, 2, 15),
(1, 3, 2, 20),
(1, 4, 2, 18),
(1, 5, 2, 11);

-- черный
INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(1, 2, 3, 14),
(1, 3, 3, 9),
(1, 4, 3, 22),
(1, 5, 3, 7);

-- =========================
-- VARIANT (PRODUCT 2 - Брюки)
-- =========================

-- золото
INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(2, 6, 4, 10),
(2, 7, 4, 14),
(2, 8, 4, 9),
(2, 9, 4, 20),
(2, 10, 4, 6),
(2, 11, 4, 12);

-- бежевый
INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(2, 6, 5, 8),
(2, 7, 5, 11),
(2, 8, 5, 16),
(2, 9, 5, 13),
(2, 10, 5, 9),
(2, 11, 5, 5);

-- =========================
-- VARIANT: PRODUCT 3 (Пиджак)
-- =========================

-- серый (S–XXL)
INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(3, 1, (SELECT color_id FROM color WHERE name='серый'), 10),
(3, 2, (SELECT color_id FROM color WHERE name='серый'), 12),
(3, 3, (SELECT color_id FROM color WHERE name='серый'), 9),
(3, 4, (SELECT color_id FROM color WHERE name='серый'), 11),
(3, 5, (SELECT color_id FROM color WHERE name='серый'), 7);

-- черный (S–XXL)
INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(3, 1, (SELECT color_id FROM color WHERE name='черный'), 14),
(3, 2, (SELECT color_id FROM color WHERE name='черный'), 16),
(3, 3, (SELECT color_id FROM color WHERE name='черный'), 13),
(3, 4, (SELECT color_id FROM color WHERE name='черный'), 15),
(3, 5, (SELECT color_id FROM color WHERE name='черный'), 10);

-- =========================
-- VARIANT: PRODUCT 4 (Брюки палаццо)
-- =========================

INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(4, 6, (SELECT color_id FROM color WHERE name='черный'), 10),
(4, 7, (SELECT color_id FROM color WHERE name='черный'), 12),
(4, 8, (SELECT color_id FROM color WHERE name='черный'), 9),
(4, 9, (SELECT color_id FROM color WHERE name='черный'), 11),
(4, 10, (SELECT color_id FROM color WHERE name='черный'), 8),
(4, 11, (SELECT color_id FROM color WHERE name='черный'), 6);

-- =========================
-- VARIANT: PRODUCT 5 (Платье поло)
-- =========================

INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(5, 1, (SELECT color_id FROM color WHERE name='красный'), 10),
(5, 2, (SELECT color_id FROM color WHERE name='красный'), 12),
(5, 3, (SELECT color_id FROM color WHERE name='красный'), 9),
(5, 4, (SELECT color_id FROM color WHERE name='красный'), 8);

-- =========================
-- VARIANT: PRODUCT 6 (Блузка с поясом)
-- =========================

INSERT INTO variant(product_id, size_id, color_id, quantity) VALUES
(6, 1, (SELECT color_id FROM color WHERE name='леопардовый'), 7),
(6, 2, (SELECT color_id FROM color WHERE name='леопардовый'), 10),
(6, 3, (SELECT color_id FROM color WHERE name='леопардовый'), 9),
(6, 4, (SELECT color_id FROM color WHERE name='леопардовый'), 6);

-- =========================
-- IMAGE
-- =========================

-- Блузка
INSERT INTO image(product_id, color_id, path, is_main) VALUES
(1, 1, '/img/product_1/Блузка_нарядная_желтый_0.jpg', true),
(1, 1, '/img/product_1/Блузка_нарядная_желтый_1.jpg', false),
(1, 1, '/img/product_1/Блузка_нарядная_желтый_2.jpg', false),

(1, 2, '/img/product_1/Блузка_нарядная_зеленый_0.jpg', true),
(1, 2, '/img/product_1/Блузка_нарядная_зеленый_1.jpg', false),
(1, 2, '/img/product_1/Блузка_нарядная_зеленый_2.jpg', false),

(1, 3, '/img/product_1/Блузка_нарядная_черный_0.jpg', true),
(1, 3, '/img/product_1/Блузка_нарядная_черный_1.jpg', false),
(1, 3, '/img/product_1/Блузка_нарядная_черный_2.jpg', false);

-- Брюки
INSERT INTO image(product_id, color_id, path, is_main) VALUES
(2, 4, '/img/product_2/Брюки_нарядные_золото_0.jpg', true),
(2, 4, '/img/product_2/Брюки_нарядные_золото_1.jpg', false),

(2, 5, '/img/product_2/Брюки_нарядные_бежевый_0.jpg', true),
(2, 5, '/img/product_2/Брюки_нарядные_бежевый_1.jpg', false),
(2, 5, '/img/product_2/Брюки_нарядные_бежевый_2.jpg', false);

-- Пиджак
INSERT INTO image(product_id, color_id, path, is_main) VALUES
(3, (SELECT color_id FROM color WHERE name='серый'), '/img/product_3/Пиджак_укороченный_серый_0.jpg', true),
(3, (SELECT color_id FROM color WHERE name='серый'), '/img/product_3/Пиджак_укороченный_серый_1.jpg', false),

(3, (SELECT color_id FROM color WHERE name='черный'), '/img/product_3/Пиджак_укороченный_черный_0.jpg', true),
(3, (SELECT color_id FROM color WHERE name='черный'), '/img/product_3/Пиджак_укороченный_черный_1.jpg', false),
(3, (SELECT color_id FROM color WHERE name='черный'), '/img/product_3/Пиджак_укороченный_черный_2.jpg', false),
(3, (SELECT color_id FROM color WHERE name='черный'), '/img/product_3/Пиджак_укороченный_черный_3.jpg', false);

-- Брюки палаццо
INSERT INTO image(product_id, color_id, path, is_main) VALUES
(4, (SELECT color_id FROM color WHERE name='черный'), '/img/product_4/Брюки_палаццо_черный_0.jpg', true),
(4, (SELECT color_id FROM color WHERE name='черный'), '/img/product_4/Брюки_палаццо_черный_1.jpg', false);

-- Платье поло
INSERT INTO image(product_id, color_id, path, is_main) VALUES
(5, (SELECT color_id FROM color WHERE name='красный'), '/img/product_5/Платье_поло_красный_0.jpg', true);

-- Блузка с поясом
INSERT INTO image(product_id, color_id, path, is_main) VALUES
(6, (SELECT color_id FROM color WHERE name='леопардовый'), '/img/product_6/Блузка_с_поясом_леопардовый_0.jpg', true);

-- =====================================================
-- PRODUCT_ITEM SEED GENERATION
-- =====================================================

-- =====================================================
-- PRODUCT 1: БЛУЗКА (variant_id 1–11)
-- =====================================================

-- 1 (yellow S)
INSERT INTO product_item(variant_id, is_sold)
SELECT 1, (gs % 12 = 0)
FROM generate_series(1, 12) gs;

-- 2 (yellow M) -> EXCLUDED (NO ITEMS)

-- 3 (yellow L)
INSERT INTO product_item(variant_id, is_sold)
SELECT 3, (gs % 11 = 0)
FROM generate_series(1, 26) gs;

-- 4 (yellow XL)
INSERT INTO product_item(variant_id, is_sold)
SELECT 4, (gs % 9 = 0)
FROM generate_series(1, 33) gs;

-- 5 (yellow XXL)
INSERT INTO product_item(variant_id, is_sold)
SELECT 5, (gs % 10 = 0)
FROM generate_series(1, 40) gs;

-- 6 (green M)
INSERT INTO product_item(variant_id, is_sold)
SELECT 6, (gs % 8 = 0)
FROM generate_series(1, 18) gs;

-- 7 (green L)
INSERT INTO product_item(variant_id, is_sold)
SELECT 7, (gs % 7 = 0)
FROM generate_series(1, 25) gs;

-- 8 (green XL)
INSERT INTO product_item(variant_id, is_sold)
SELECT 8, (gs % 13 = 0)
FROM generate_series(1, 32) gs;

-- 9 (green XXL)
INSERT INTO product_item(variant_id, is_sold)
SELECT 9, (gs % 6 = 0)
FROM generate_series(1, 39) gs;

-- 10 (black M)
INSERT INTO product_item(variant_id, is_sold)
SELECT 10, (gs % 10 = 0)
FROM generate_series(1, 19) gs;

-- 11 (black L)
INSERT INTO product_item(variant_id, is_sold)
SELECT 11, (gs % 9 = 0)
FROM generate_series(1, 26) gs;

-- 12 (black XL)
INSERT INTO product_item(variant_id, is_sold)
SELECT 12, (gs % 12 = 0)
FROM generate_series(1, 33) gs;

-- 13 (black XXL)
INSERT INTO product_item(variant_id, is_sold)
SELECT 13, (gs % 14 = 0)
FROM generate_series(1, 40) gs;

-- =====================================================
-- PRODUCT 2: БРЮКИ (variant_id 14–25)
-- =====================================================

-- gold 50
INSERT INTO product_item(variant_id, is_sold)
SELECT 14, (gs % 9 = 0)
FROM generate_series(1, 18) gs;

-- gold 52
INSERT INTO product_item(variant_id, is_sold)
SELECT 15, (gs % 8 = 0)
FROM generate_series(1, 25) gs;

-- gold 54
INSERT INTO product_item(variant_id, is_sold)
SELECT 16, (gs % 7 = 0)
FROM generate_series(1, 32) gs;

-- gold 56
INSERT INTO product_item(variant_id, is_sold)
SELECT 17, (gs % 11 = 0)
FROM generate_series(1, 39) gs;

-- gold 58
INSERT INTO product_item(variant_id, is_sold)
SELECT 18, (gs % 10 = 0)
FROM generate_series(1, 46) gs;

-- gold 60
INSERT INTO product_item(variant_id, is_sold)
SELECT 19, (gs % 6 = 0)
FROM generate_series(1, 50) gs;

-- beige 50
INSERT INTO product_item(variant_id, is_sold)
SELECT 20, (gs % 7 = 0)
FROM generate_series(1, 18) gs;

-- beige 52
INSERT INTO product_item(variant_id, is_sold)
SELECT 21, (gs % 9 = 0)
FROM generate_series(1, 25) gs;

-- beige 54
INSERT INTO product_item(variant_id, is_sold)
SELECT 22, (gs % 11 = 0)
FROM generate_series(1, 32) gs;

-- beige 56
INSERT INTO product_item(variant_id, is_sold)
SELECT 23, (gs % 8 = 0)
FROM generate_series(1, 39) gs;

-- beige 58
INSERT INTO product_item(variant_id, is_sold)
SELECT 24, (gs % 6 = 0)
FROM generate_series(1, 46) gs;

-- beige 60
INSERT INTO product_item(variant_id, is_sold)
SELECT 25, (gs % 10 = 0)
FROM generate_series(1, 50) gs;

-- =====================================================
-- PRODUCT 3: ПИДЖАК УКОРOЧЕННЫЙ
-- =====================================================

-- gray S
INSERT INTO product_item(variant_id, is_sold)
SELECT 26, (gs % 9 = 0)
FROM generate_series(1, 18) gs;

-- gray M
INSERT INTO product_item(variant_id, is_sold)
SELECT 27, (gs % 7 = 0)
FROM generate_series(1, 25) gs;

-- gray L
INSERT INTO product_item(variant_id, is_sold)
SELECT 28, (gs % 11 = 0)
FROM generate_series(1, 32) gs;

-- gray XL
INSERT INTO product_item(variant_id, is_sold)
SELECT 29, (gs % 8 = 0)
FROM generate_series(1, 39) gs;

-- gray XXL
INSERT INTO product_item(variant_id, is_sold)
SELECT 30, (gs % 6 = 0)
FROM generate_series(1, 46) gs;


-- black S
INSERT INTO product_item(variant_id, is_sold)
SELECT 31, (gs % 10 = 0)
FROM generate_series(1, 18) gs;

-- black M
INSERT INTO product_item(variant_id, is_sold)
SELECT 32, (gs % 9 = 0)
FROM generate_series(1, 25) gs;

-- black L
INSERT INTO product_item(variant_id, is_sold)
SELECT 33, (gs % 7 = 0)
FROM generate_series(1, 32) gs;

-- black XL
INSERT INTO product_item(variant_id, is_sold)
SELECT 34, (gs % 12 = 0)
FROM generate_series(1, 39) gs;

-- black XXL
INSERT INTO product_item(variant_id, is_sold)
SELECT 35, (gs % 11 = 0)
FROM generate_series(1, 46) gs;


-- =====================================================
-- PRODUCT 4: БРЮКИ ПАЛАЦЦО (black only)
-- =====================================================

INSERT INTO product_item(variant_id, is_sold)
SELECT 36, (gs % 8 = 0)
FROM generate_series(1, 18) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 37, (gs % 9 = 0)
FROM generate_series(1, 25) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 38, (gs % 7 = 0)
FROM generate_series(1, 32) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 39, (gs % 10 = 0)
FROM generate_series(1, 39) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 40, (gs % 6 = 0)
FROM generate_series(1, 46) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 41, (gs % 11 = 0)
FROM generate_series(1, 50) gs;


-- =====================================================
-- PRODUCT 5: ПЛАТЬЕ ПОЛО (red)
-- =====================================================

INSERT INTO product_item(variant_id, is_sold)
SELECT 42, (gs % 9 = 0)
FROM generate_series(1, 18) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 43, (gs % 7 = 0)
FROM generate_series(1, 25) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 44, (gs % 10 = 0)
FROM generate_series(1, 32) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 45, (gs % 8 = 0)
FROM generate_series(1, 39) gs;


-- =====================================================
-- PRODUCT 6: БЛУЗКА С ПОЯСОМ (leopard)
-- =====================================================

INSERT INTO product_item(variant_id, is_sold)
SELECT 46, (gs % 6 = 0)
FROM generate_series(1, 18) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 47, (gs % 7 = 0)
FROM generate_series(1, 25) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 48, (gs % 9 = 0)
FROM generate_series(1, 32) gs;

INSERT INTO product_item(variant_id, is_sold)
SELECT 49, (gs % 8 = 0)
FROM generate_series(1, 39) gs;