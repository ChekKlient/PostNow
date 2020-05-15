INSERT INTO USER_ROLE VALUES(600, 'ADMIN');
INSERT INTO USER_ROLE VALUES(700, 'USER');

INSERT INTO USER_ADDITIONAL_DATA VALUES (1000,'1998-05-22','Rafał','MEN','Lublin', false,'Kacprzak','111000666', 'https://cdn1.iconfinder.com/data/icons/man-user-human-profile-avatar-business-person/100/09-1User_3-4-512.png');
INSERT INTO USER VALUES (1, true,'rafal@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1000);
INSERT INTO USERS_ROLES VALUES(1, 600);

INSERT INTO USER_ADDITIONAL_DATA VALUES (1002,'1994-01-17','Dada','MEN','Lubartów', false,'Kamiński','111002666', 'https://www.enigmatixmedia.com/pics/demo.png');
INSERT INTO USER VALUES (2, true,'Dada102@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1002);
INSERT INTO USERS_ROLES VALUES(2, 700);

INSERT INTO USER_ADDITIONAL_DATA VALUES (1003,'1979-07-13','Jessica','OTHER','Warszawa', false,'Łukasiewicz','111003666', 'https://www.enigmatixmedia.com/pics/demo.png');
INSERT INTO USER VALUES (3, true,'lukasiewicz.a@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1003);
INSERT INTO USERS_ROLES VALUES(3, 700);

INSERT INTO USER_ADDITIONAL_DATA VALUES (1004,'2003-12-24','Paulina','WOMEN','Krakow', false,'Lechman','111004666', 'https://www.enigmatixmedia.com/pics/demo.png');
INSERT INTO USER VALUES (4, true,'Paulina.03@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1004);
INSERT INTO USERS_ROLES VALUES(4, 700);

INSERT INTO POST VALUES (1000, 1, '2020-05-15', 10, 80, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', 1);
INSERT INTO POST VALUES (2000, 1, '2020-05-15', 30, 70, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', 2);
INSERT INTO POST VALUES (3000, 0, '2020-05-15', 40, 10, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', 3);
INSERT INTO POST VALUES (4000, 0, '2020-05-15', 17, 12, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', 4);
INSERT INTO POST VALUES (5000, 0, '2020-05-15', 56, 17, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', 2);

INSERT INTO POST_COMMENT VALUES (10000, 'Ut eget eleifend sapien.', 1000, 1);
INSERT INTO POST_COMMENT VALUES (20000, 'Ut eget eleifend sapien.', 2000, 2);

