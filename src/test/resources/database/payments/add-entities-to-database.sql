INSERT INTO users (id, email, first_name, last_name, password, is_deleted)
values (1, 'test@gmail.com', 'Ihor', 'Boyko', 'some_encrypted_password', false);

INSERT INTO users (id, email, first_name, last_name, password, is_deleted)
values (2, 'test2@gmail.com', 'Ihor2', 'Boyko2', 'some_encrypted_password', false);

INSERT INTO cars (id, model, brand, car_type, inventory, daily_fee, is_deleted)
values (1, 'BWM', 'X5', 'SUV', 5, 90, false);

INSERT INTO rentals (id, rental_date,return_date, actual_return_date, car_id, user_id, is_active, is_deleted)
values (1, '2023-11-21', '2023-11-23', null, 1, 1, true, false);
