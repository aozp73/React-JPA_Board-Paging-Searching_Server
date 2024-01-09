INSERT INTO user_tb(id, email, username, password, role, created_at)
values(1, 'aozp73@naver.com', 'aozp73', '$2a$12$HB0FWwVKeyiF/7w3/6tgyORjEiZzodMe8etYRVBQ8eOC7iIHpzMvK', 'COMMON', now());
INSERT INTO user_tb(id, email, username, password, role, created_at)
values(2, 'po1630@naver.com', 'totoro', '$2a$12$HB0FWwVKeyiF/7w3/6tgyORjEiZzodMe8etYRVBQ8eOC7iIHpzMvK', 'COMMON', now());
INSERT INTO user_tb(id, email, username, password, role, created_at)
values(3, 'comos@naver.com', 'comos', '$2a$12$HB0FWwVKeyiF/7w3/6tgyORjEiZzodMe8etYRVBQ8eOC7iIHpzMvK', 'COMMON', now());

INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(1, 1,'1번째 제목', '1번째 내용', 13, '2023-09-13 12:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(2, 1,'2번째 제목', '2번째 내용', 2, '2023-11-30 08:21:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(3, 2,'3번째 제목', '3번째 내용', 7, '2023-12-13 10:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(4, 2,'4번째 제목', '4번째 내용', 6, '2023-12-21 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(5, 1,'5번째 제목', '5번째 내용', 13, '2023-12-22 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(6, 1,'6번째 제목', '4번째 내용', 21, '2023-12-23 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(7, 1,'7번째 제목', '4번째 내용', 5, '2023-12-24 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(8, 2,'8번째 제목', '번째 내용', 9, '2023-12-25 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(9, 2,'9번째 제목', '9번째 내용', 31, '2024-01-01 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(10, 2,'10번째 제목', '10번째 내용', 20, '2024-01-02 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(11, 3,'11번째 제목', '11번째 내용', 17, '2024-01-04 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(12, 3,'12번째 제목', '12번째 내용', 19, '2024-01-07 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(13, 3,'13번째 제목', '13번째 내용', 23, '2024-01-09 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(14, 1,'14번째 제목', '14번째 내용', 26, '2024-01-10 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(15, 1,'15번째 제목', '15번째 내용', 29, '2024-01-13 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(16, 1,'16번째 제목', '16번째 내용', 4, '2024-01-15 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(17, 2,'17번째 제목', '17번째 내용', 43, '2024-01-16 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(18, 2,'18번째 제목', '18번째 내용', 13, '2024-01-18 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(19, 2,'19번째 제목', '19번째 내용', 2, '2024-01-21 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(20, 3,'20번째 제목', '20번째 내용', 8, '2024-02-03 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(21, 3,'21번째 제목', '21번째 내용', 13, '2024-02-05 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(22, 3,'22번째 제목', '22번째 내용', 2, '2024-02-08 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(23, 1,'23번째 제목', '23번째 내용', 20, '2024-02-08 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(24, 1,'24번째 제목', '24번째 내용', 21, '2024-02-13 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(25, 1,'25번째 제목', '25번째 내용', 15, '2024-02-15 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(26, 2,'26번째 제목', '26번째 내용', 5, '2024-02-15 14:34:56');
INSERT INTO board_tb(id, user_id, title, content, views, created_at)
values(27, 2,'27번째 제목', '27번째 내용', 3, '2024-02-16 14:34:56');


INSERT INTO comment_tb(id, user_id, board_id, content, created_at)
values(1, 1, 27, '잘 부탁드려요!', now());
INSERT INTO comment_tb(id, user_id, board_id, content, created_at)
values(2, 2, 27, '반가워요!', now());
INSERT INTO comment_tb(id, user_id, board_id, content, created_at)
values(3, 3, 27, '좋은 날씨네요!', now());
INSERT INTO comment_tb(id, user_id, board_id, content, created_at)
values(4, 1, 26, '안녕하세요 ~', now());
INSERT INTO comment_tb(id, user_id, board_id, content, created_at)
values(5, 2, 26, '안녕하세요 ~', now());
