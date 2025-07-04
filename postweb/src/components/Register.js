import { Container, Row, Col, Alert, Button, Form, Image, Toast } from "react-bootstrap";
import { useRef, useState } from "react";
import Apis, { endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";
import { createUserWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../lib/firebase";
import { doc, setDoc } from "firebase/firestore";


const Register = () => {
    const fields = [
        { title: "Tên đầy đủ", field: "name", type: "text" },
        { title: "Giới thiệu bản thân", field: "bio", type: "text" },
        { title: "Giới tính", field: "gender", type: "select", options: ["MALE", "FEMALE"] },
        { title: "Ngày sinh", field: "birthday", type: "date" },
        { title: "Mã sinh viên", field: "studentCode", type: "text" },
        { title: "Email sinh viên", field: "username", type: "text" },
        { title: "Mật khẩu", field: "password", type: "password" },
        { title: "Xác nhận mật khẩu", field: "confirm", type: "password" }
    ];

    const [user, setUser] = useState({ role: "ROLE_ALUMNI", active: false });
    const avatar = useRef();
    const coverPhoto = useRef();
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const register = async (e) => {
        e.preventDefault();
        if (user.password !== user.confirm) {
            setMsg("Mật khẩu KHÔNG khớp");
        } else {
            let form = new FormData();
            for (let key in user) {
                if (key !== "confirm") {
                    form.append(key, user[key]);
                }
            }
            form.append("avatar", avatar.current.files[0]);
            form.append("coverPhoto", coverPhoto.current.files[0]);

            try {
                setLoading(true);

                await Apis.post(endpoints["register"], form, {
                    headers: { "Content-Type": "multipart/form-data" }
                });

                const fire = await createUserWithEmailAndPassword(auth, user.username, user.password);
                await setDoc(doc(db, "users", fire.user.uid), {
                    username: user.username,
                    name: user.name,
                    avatar: "https://i.pinimg.com/236x/69/66/3c/69663c8db71f7a7ff349b2745a419dab.jpg",
                    id: fire.user.uid,
                    blocked: [],
                });

                await setDoc(doc(db, "userChats", fire.user.uid), {
                    chats: [],
                });

                nav("/");
            } catch (err) {
                console.error(err);
                setMsg("Đăng ký thất bại. Vui lòng thử lại.");
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <Container className="mt-5">
            <Row className="justify-content-center align-items-center">
                {/* Bên trái: Ảnh minh họa */}
                <Col md={6} className="d-none d-md-block text-center">
                    <Image src="https://i.pinimg.com/736x/9f/d7/89/9fd7892f00423f0293e3813d34059892.jpg" fluid />
                    <h2 className="mt-4 text-primary">Tham gia cộng đồng Alumni ngay!</h2>
                    <p className="fs-5 text-muted">Kết nối, chia sẻ và xây dựng mạng lưới cựu sinh viên.</p>
                </Col>

                {/* Bên phải: Form đăng ký */}
                <Col md={5}>
                    <div className="p-4 border rounded shadow bg-white">
                        <h1 className="text-center text-success mb-4">ĐĂNG KÝ CỰU SINH VIÊN</h1>

                        {msg && <Alert variant="danger">{msg}</Alert>}

                        <Form onSubmit={register}>
                            {fields.map(f => (
                                f.type === "select" ? (
                                    <Form.Select
                                        key={f.field}
                                        value={user[f.field] || ""}
                                        onChange={e => setState(e.target.value, f.field)}
                                        className="mb-3"
                                        required
                                    >
                                        <option value="">-- Chọn {f.title.toLowerCase()} --</option>
                                        {f.options.map(opt => (
                                            <option key={opt} value={opt}>
                                                {opt === "MALE" ? "Nam" : "Nữ"}
                                            </option>
                                        ))}
                                    </Form.Select>
                                ) : (
                                    <Form.Control
                                        key={f.field}
                                        value={user[f.field] || ""}
                                        onChange={e => setState(e.target.value, f.field)}
                                        type={f.type}
                                        placeholder={f.title}
                                        className="mb-3"
                                        required
                                    />
                                )
                            ))}

                            <Form.Label>Ảnh đại diện</Form.Label>
                            <Form.Control ref={avatar} type="file" className="mb-3" required />

                            <Form.Label>Ảnh bìa</Form.Label>
                            <Form.Control ref={coverPhoto} type="file" className="mb-4" required />

                            {loading ? <MySpinner /> : <Button type="submit" variant="success" className="w-100">Đăng ký</Button>}
                        </Form>
                    </div>
                </Col>
            </Row>
        </Container>
    );
};

export default Register;