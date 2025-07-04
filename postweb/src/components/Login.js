import { Container, Row, Col, Alert, Button, Form, Image } from "react-bootstrap";
import { useContext, useState } from "react";
import Apis, { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { MyDispatcherContext } from "../configs/MyContexts";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../lib/firebase";


const Login = () => {
    const info = [
        { title: "Email", field: "username", type: "text" },
        { title: "Mật khẩu", field: "password", type: "password" }
    ];
    const [user, setUser] = useState({});
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();
    const dispatch = useContext(MyDispatcherContext);

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const login = async (e) => {
        e.preventDefault();
        setMsg(null);

        try {
            setLoading(true);
            let res = await Apis.post(endpoints["login"], { ...user });
            console.log("Login response:", res.data);

            cookie.save("token", res.data.token);
            let u = await authApis().get(endpoints["current-user"]);
            console.info("Current user:", u.data);

            await signInWithEmailAndPassword(auth, user.username, user.password);
            console.log("Đăng nhập Firebase thành công");


            dispatch({
                type: "login",
                payload: u.data
            });

            console.log("Đăng nhập thành công, chuyển trang");
            nav("/");
        } catch (ex) {
            console.error("Lỗi đăng nhập:", ex);


            if (ex.response && ex.response.data) {
                setMsg(ex.response.data);
            } else {
                setMsg("Có lỗi xảy ra! Vui lòng thử lại.");
            }

        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-5">
            <Row className="justify-content-center align-items-center">
                <Col md={6} className="d-none d-md-block">
                    <Image
                        src="https://i.pinimg.com/736x/62/ae/dd/62aedd94f6ac535869260adb780000ef.jpg"
                        fluid
                    />
                </Col>

                <Col md={6}>
                    <h1 className="text-center mb-4" style={{ color: "#393E46", fontSize: "2.5rem", fontWeight: "bold", letterSpacing: "1.5px" }}>
                        ALUMNI NETWORK
                    </h1>
                    <div className="p-4 border rounded shadow">
                        <h1 className="text-center text-success mb-4">ĐĂNG NHẬP</h1>

                        {msg && <Alert variant="danger">{msg}</Alert>}

                        <Form onSubmit={login}>
                            {info.map(i => (
                                <Form.Control
                                    value={user[i.field] || ""}
                                    onChange={e => setState(e.target.value, i.field)}
                                    className="mt-3 mb-1"
                                    key={i.field}
                                    type={i.type}
                                    placeholder={i.title}
                                    required
                                />
                            ))}

                            {loading ? <MySpinner /> : <Button type="submit" variant="success" className="mt-3 w-100">Đăng nhập</Button>}
                        </Form>

                        <div className="text-center mt-3">
                            <Button variant="outline-primary" onClick={() => nav("/register")}>Tạo tài khoản mới</Button>
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
};

export default Login;