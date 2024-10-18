-- Вставка данных в таблицу users
INSERT INTO users (name, email) VALUES
('John Doe', 'john.doe@example.com'),
('Jane Smith', 'jane.smith@example.com'),
('Alice Johnson', 'alice.johnson@example.com');

-- Вставка данных в таблицу requests
INSERT INTO requests (description, requester_id, created) VALUES
('Request for a new item', 1, '2024-09-29 10:00:00'),
('Another request', 2, '2024-09-28 12:30:00'),
('Need an item urgently', 3, '2024-09-27 09:15:00');

-- Вставка данных в таблицу items
INSERT INTO items (name, description, available, request_id, owner_id) VALUES
('Laptop', 'A high-performance laptop', TRUE, 1, 1),
('Camera', 'A DSLR camera for photography', FALSE, 2, 2),
('Tablet', 'A tablet for note-taking', TRUE, 3, 3);

-- Вставка данных в таблицу bookings
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES
('2024-10-01 09:00:00', '2024-10-10 18:00:00', 1, 2, 'APPROVED'),
('2024-10-05 10:00:00', '2024-10-12 16:00:00', 2, 3, 'WAITING'),
('2024-10-02 08:00:00', '2024-10-09 12:00:00', 3, 1, 'REJECTED');

-- Вставка данных в таблицу comments
INSERT INTO comments (text, item_id, author_id, created) VALUES
('Great laptop, very useful!', 1, 2, '2024-09-30 15:00:00'),
('The camera works perfectly.', 2, 3, '2024-09-29 17:30:00'),
('Tablet is great for drawing!', 3, 1, '2024-09-28 14:20:00');
