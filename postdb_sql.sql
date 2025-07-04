use postdb;
-- Bảng chính
CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,        -- Tên người dùng
    birthday DATE,                     -- Ngày sinh nhật
    gender ENUM('MALE', 'FEMALE') NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ROLE_ADMIN', 'ROLE_ALUMNI', 'ROLE_LECTURER') NOT NULL,
    
    bio TEXT,                          -- Tiểu sử / giới thiệu bản thân
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE,   -- Chờ admin xác nhận
    avatar VARCHAR(255),
    cover_photo VARCHAR(255)
);


-- Bảng Alumni (mối quan hệ 1-1 với User)
CREATE TABLE Alumni (
    user_id INT PRIMARY KEY,
    student_code VARCHAR(50) UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- Bảng Admin (mối quan hệ 1-1 với User)
CREATE TABLE Admin (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- Bảng Lecturer (mối quan hệ 1-1 với User)
CREATE TABLE Lecturer (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- Bảng TimeOut (mối quan hệ 1-1 với lecturer)  CURRENT_TIMESTAMP + INTERVAL 24 HOUR
CREATE TABLE TimeOut (
    lecturer_id INT PRIMARY KEY,
    expired_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- Thời gian phiên làm việc hết hạn.
    FOREIGN KEY (lecturer_id) REFERENCES Lecturer(user_id) ON DELETE CASCADE
);

-- Bảng Posts (mối quan hệ 1 - N với User )
CREATE TABLE Post (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_comment_locked BOOLEAN DEFAULT FALSE, -- Khoa binh luan khong
    image VARCHAR(255),
	visibility ENUM('PRIVATE', 'PUBLIC') NOT NULL DEFAULT 'PUBLIC',
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- Bảng comment ( Bảng sinh ra từ mối quan hệ N-N giữa user-post)
CREATE TABLE Comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (post_id) REFERENCES Post(id)
);

-- Bảng Reaction ( 1 người có thể like nhiều bài nhưng 1 like duy nhất với bài post đó)
CREATE TABLE Reaction (
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    reaction_type ENUM('LIKE', 'LOVE', 'HAHA', 'WOW', 'SAD', 'ANGRY') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),  -- chỉ cho 1 reaction/post/user
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (post_id) REFERENCES Post(id)
);

-- Bảng SurveyPost (Mối quan hệ N - 1 với Admin)

CREATE TABLE SurveyPost (
    id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATETIME,
    end_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('DRAFT', 'PUBLISH', 'CLOSED') NOT NULL DEFAULT 'DRAFT',
    FOREIGN KEY (admin_id) REFERENCES Admin(user_id) ON DELETE CASCADE
);

-- Bảng SurveyQuestion (Mối quan hệ N - 1 với Survey Post )
CREATE TABLE SurveyQuestion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    survey_id INT NOT NULL,
    content TEXT NOT NULL,
    response_type ENUM('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TEXT') NOT NULL DEFAULT 'SINGLE_CHOICE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (survey_id) REFERENCES SurveyPost(id) ON DELETE CASCADE
);

-- Bảng Survey Option (Mối quan hệ N - 1 survey question)
CREATE TABLE SurveyOption (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    content VARCHAR(555),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES SurveyQuestion(id) ON DELETE CASCADE
);

-- Bảng ResponseOption sinh ra từ uSER VÀ Survey Option
CREATE TABLE ResponseOption (
    user_id INT NOT NULL,
    option_id INT NOT NULL,
    responded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, option_id),
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES SurveyOption(id) ON DELETE CASCADE
);

-- Bảng Invitation Post (Mối quan hệ N-1 Admin)
CREATE TABLE InvitationPost (
    id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    recipient_scope ENUM('INDIVIDUAL', 'ROLE_GROUP', 'CUSTOM_GROUP', 'ALL') DEFAULT 'ALL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES Admin(user_id) ON DELETE CASCADE
);

-- Bảng InvitationRecipient sinh ra từ mối quan hệ nhiều nhiều giữa user và inviation post
CREATE TABLE InvitationRecipient (
    user_id INT NOT NULL,
    invitation_post_id INT NOT NULL,
    PRIMARY KEY (user_id, invitation_post_id),
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (invitation_post_id) REFERENCES InvitationPost(id) ON DELETE CASCADE
);

INSERT INTO User (username, name, birthday, gender, password, role, is_active, avatar, cover_photo, bio)
VALUES 
('2251010077phuong@ou.edu.vn', 'Phương Mai', '2002-07-15', 'FEMALE', '$2a$10$G5UfJIJSx.0EEiQ4IZgHmuw98uZH0SvmT94fqr.nchW3QCc8RKzjW', 'ROLE_ADMIN', TRUE, 'phuong.jpg', 'phuong_cover.jpg', 'Yêu thiên văn, đam mê kết nối cộng đồng OU.'),
('nguyenluhongphuong@gmail.com', 'Lữ Hồng Phương', '1990-03-12', 'MALE', '123456', 'ROLE_LECTURER', TRUE, 'tuan.jpg', 'tuan_cover.jpg', 'Thầy Phương siêu ngầu, thích React, hay mời trà sữa sinh viên.'),
('2251010062mai@ou.edu.vn', 'Mai Trúc', '2002-09-09', 'FEMALE', '123456', 'ROLE_ALUMNI', TRUE, 'mai.jpg', 'mai_cover.jpg', 'Cựu sinh viên ngành Marketing, đam mê tổ chức sự kiện.'),
('nam@ou.edu.vn', 'Văn Nam', '2001-05-25', 'MALE', '123456', 'ROLE_ALUMNI', TRUE, 'nam.jpg', 'nam_cover.jpg', 'Từng là leader câu lạc bộ Truyền Thông, hiện đang làm Designer.'),
('hoa@ou.edu.vn', 'Cô Hoa', '1987-11-30', 'FEMALE', '123456', 'ROLE_LECTURER', TRUE, 'hoa.jpg', 'hoa_cover.jpg', 'Giảng viên tâm lý học, thích nói chuyện nhẹ nhàng với sinh viên.');

-- Admin: phuong (id = 1)
INSERT INTO Admin (user_id) VALUES (1);

-- Lecturers: tuan (2), hoa (5)
INSERT INTO Lecturer (user_id) VALUES (2), (5);

-- Alumni: mai (3), nam (4)
INSERT INTO Alumni (user_id, student_code)
VALUES (3, 'A001'), (4, 'A002');

INSERT INTO Post (user_id, content, image, visibility)
VALUES 
(3, 'Hôm nay trời đẹp quá!', NULL, 'PUBLIC'),
(4, 'Mình mới tốt nghiệp nè!', 'grad.jpg', 'PUBLIC'),
(2, 'Nhớ giữ sức khoẻ khi ôn thi nhé!', NULL, 'PUBLIC');

INSERT INTO SurveyPost (admin_id, title, description, start_date, end_date, status)
VALUES 
(1, 'Khảo sát hoạt động ngoại khoá', 'Giúp nhà trường lên kế hoạch tốt hơn', NOW(), NOW() + INTERVAL 7 DAY, 'PUBLISH');

-- Question cho survey id = 1
INSERT INTO SurveyQuestion (survey_id, content, response_type)
VALUES (1, 'Bạn có muốn tham gia hoạt động ngoài trời không?', 'SINGLE_CHOICE');

-- Option cho question id = 1
INSERT INTO SurveyOption (question_id, content)
VALUES 
(1, 'Có'),
(1, 'Không'),
(1, 'Tuỳ thời tiết');

-- mai chọn "Có", nam chọn "Không"
INSERT INTO ResponseOption (user_id, option_id)
VALUES 
(3, 1),  -- mai chọn 'Có'
(4, 2);  -- nam chọn 'Không'

-- Tạo một lời mời bởi admin id = 1
INSERT INTO InvitationPost (admin_id, title, content, recipient_scope)
VALUES (1, 'Tham gia chương trình thiện nguyện', 'Chúng ta sẽ đi đến Bình Phước dịp Tết.', 'ALL');

-- Gửi đến user id: 2, 3, 4
INSERT INTO InvitationRecipient (user_id, invitation_post_id)
VALUES 
(2, 1),
(3, 1),
(4, 1);

INSERT INTO Comment (user_id, post_id, content)
VALUES 
(2, 1, 'Bài viết hay quá em ơi!'),
(3, 1, 'Trời hôm nay thật đẹp thiệt!'),
(4, 2, 'Chúc mừng bạn tốt nghiệp nhé! 🎉'),
(3, 3, 'Dạ em cảm ơn thầy, em sẽ cố gắng ạ!'),
(4, 3, 'Bài viết hữu ích quá!');

INSERT INTO Reaction (user_id, post_id, reaction_type)
VALUES 
(3, 1, 'LIKE'),
(4, 1, 'LOVE'),
(2, 2, 'WOW'),
(3, 2, 'HAHA'),
(4, 3, 'SAD');
