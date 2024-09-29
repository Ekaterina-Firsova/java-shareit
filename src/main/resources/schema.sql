DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	name varchar NOT NULL,
	email varchar NOT NULL UNIQUE,
	CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	description varchar NOT NULL,
	requester_id int8 NOT NULL,
	created timestamp without time zone NOT NULL,
	CONSTRAINT requests_pk PRIMARY KEY (id),
	CONSTRAINT requests_users_fk FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	name varchar NOT NULL,
	description varchar NOT NULL,
	available bool NULL,
	request_id int8 NULL,
	owner_id int8 NULL,
	CONSTRAINT items_pk PRIMARY KEY (id),
	CONSTRAINT items_requests_fk FOREIGN KEY (request_id) REFERENCES public.requests(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT items_users_fk FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	start_date timestamp without time zone NULL,
	end_date timestamp without time zone NULL,
	item_id int8 NOT NULL,
	booker_id int8 NOT NULL,
	status varchar NULL,
	CONSTRAINT bookings_pk PRIMARY KEY (id),
	CONSTRAINT bookings_items_fk FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT bookings_users_fk FOREIGN KEY (booker_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT bookings_status_check CHECK (((status = ANY (
	        ARRAY[
                'WAITING'::text,
                'APPROVED'::text,
                'REJECTED'::text,
                'CANCELED'::text
	        ]))))
);

CREATE TABLE IF NOT EXISTS comments (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	text varchar NULL,
	item_id int8 NOT NULL,
	author_id int8 NOT NULL,
	created timestamp without time zone NULL,
	CONSTRAINT comments_pk PRIMARY KEY (id),
	CONSTRAINT comments_items_fk FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT comments_users_fk FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);