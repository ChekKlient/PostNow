INSERT INTO USER_ROLE VALUES(600, 'ADMIN');
INSERT INTO USER_ROLE VALUES(700, 'USER');

INSERT INTO USER_ADDITIONAL_DATA VALUES (1000,'1998-05-22','Rafał','MEN','Lublin', false,'Kacprzak','111000666', 'https://cdn1.iconfinder.com/data/icons/man-user-human-profile-avatar-business-person/100/09-1User_3-4-512.png');
INSERT INTO USER VALUES (1, true,'rafal@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1000);
INSERT INTO USERS_ROLES VALUES(1, 600);

INSERT INTO USER_ADDITIONAL_DATA VALUES (1002,'1994-01-17','Dada','MEN','Lubartów', true,'Kamiński','111002666', 'https://www.enigmatixmedia.com/pics/demo.png');
INSERT INTO USER VALUES (2, true,'Dada102@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1002);
INSERT INTO USERS_ROLES VALUES(2, 700);

INSERT INTO USER_ADDITIONAL_DATA VALUES (1003,'1979-07-13','Jessica','OTHER','Warszawa', true,'Łukasiewicz','111003666', 'https://www.enigmatixmedia.com/pics/demo.png');
INSERT INTO USER VALUES (3, true,'lukasiewicz.a@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1003);
INSERT INTO USERS_ROLES VALUES(3, 700);

INSERT INTO USER_ADDITIONAL_DATA VALUES (1004,'2003-12-24','Paulina','WOMEN','Krakow', false,'Lechman','111004666', 'https://www.enigmatixmedia.com/pics/demo.png');
INSERT INTO USER VALUES (4, true,'Paulina.03@postnow.com','$2y$12$VpYnhft8IbD8u3jUhq5rZOf4mCuVnMxgbBrVI6kTkAyvd..96LgGC',1004);
INSERT INTO USERS_ROLES VALUES(4, 700);

INSERT INTO POST VALUES (1000, CURRENT_TIMESTAMP, 1, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean velit purus, aliquet id felis non, fermentum laoreet nisi. Proin mi velit, lacinia at imperdiet quis, aliquet congue urna. Vestibulum ut metus leo. Praesent id felis massa. Aenean ac pulvinar dolor. Curabitur ac scelerisque elit. Morbi mattis sem ac nulla convallis, eu rutrum dolor congue. Maecenas pellentesque gravida velit, sodales volutpat diam ultrices id. Duis ac pretium purus. Aenean rhoncus ante non dolor molestie aliquam.', 1);
INSERT INTO POST VALUES (2000, CURRENT_TIMESTAMP, 1, 'Hi!', 2);
INSERT INTO POST VALUES (3000, CURRENT_TIMESTAMP, 0, 'Nam dolor libero, ullamcorper ac convallis eget, rutrum in erat. Nam eu mi ipsum. Integer ligula lacus, commodo a erat in, elementum semper elit. Nunc sodales posuere mi, facilisis faucibus mauris scelerisque vel. Etiam tellus ante, laoreet a ante eu, dignissim interdum tortor. Proin sit amet nulla dignissim, luctus dui at, cursus orci. Donec vitae est eu sapien ornare porta. Maecenas ipsum purus, molestie vitae efficitur vitae, sollicitudin in metus.', 3);
INSERT INTO POST VALUES (4000, CURRENT_TIMESTAMP, 0, 'Maecenas at leo in risus placerat porttitor.', 4);
INSERT INTO POST VALUES (5000, CURRENT_TIMESTAMP, 0, 'Curabitur consequat erat vitae tincidunt gravida. Aenean nibh ipsum, hendrerit nec ligula id, euismod condimentum augue. Quisque vehicula fringilla erat a rhoncus. Aenean laoreet neque vitae ex feugiat tristique.', 2);

INSERT INTO POST_COMMENT VALUES (10000, CURRENT_TIMESTAMP, 'Ut eget eleifend sapien.', 1, 1000);
INSERT INTO POST_COMMENT VALUES (10001, CURRENT_TIMESTAMP, 'Ut eget eleifend sapien.', 3, 1000);
INSERT INTO POST_COMMENT VALUES (10002, CURRENT_TIMESTAMP, 'Ut eget eleifend sapien.', 2, 1000);
INSERT INTO POST_COMMENT VALUES (20000, CURRENT_TIMESTAMP, 'Ut eget eleifend sapien.', 2, 2000);
INSERT INTO POST_COMMENT VALUES (20001, CURRENT_TIMESTAMP, 'Ut eget eleifend sapien.', 4, 2000);
INSERT INTO POST_COMMENT VALUES (20002, CURRENT_TIMESTAMP, 'Ut eget eleifend sapien.', 1, 4000);
