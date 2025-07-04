import { useEffect, useState } from "react";
import { Button, Card, Col, Form, Row, Spinner, Table } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "../layouts/MySpinner";
import avatar from "../../img/avatar.webp";
import cover from "../../img/background.webp";
import { createUserWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../../lib/firebase";
import { doc, setDoc } from "firebase/firestore";



const ManageUser = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [q] = useSearchParams();
    const [activatingUserId, setActivatingUserId] = useState(null);
    const [showLecturerForm, setShowLecturerForm] = useState(false);
    const [newLecturer, setNewLecturer] = useState({ username: "" });
    const [registering, setRegistering] = useState(false);
    const [registerError, setRegisterError] = useState("");


    const registerLecturer = async () => {
        if (!newLecturer.username.trim()) {
            alert("Vui lòng nhập username!");
            return;
        }

        const formData = new FormData();
        formData.append("username", newLecturer.username);
        formData.append("password", "defaultPassword123");
        formData.append("role", "ROLE_LECTURER");
        formData.append("isActive", "true");
        formData.append("birthday", "2000-01-01");
        formData.append("gender", "male");
        formData.append("name", "lecturer test");
        formData.append("bio", "");



        try {
            setRegistering(true);
            setRegisterError("");

            const avatarResponse = await fetch(avatar);
            const avatarBlob = await avatarResponse.blob();
            const avatarFile = new File([avatarBlob], "avatar.webp", { type: avatarBlob.type });

            const coverResponse = await fetch(cover);
            const coverBlob = await coverResponse.blob();
            const coverFile = new File([coverBlob], "background.webp", { type: coverBlob.type });

            formData.append("avatar", avatarFile);
            formData.append("coverPhoto", coverFile);

            await authApis().post(endpoints['register'], formData);


            // Tạo tài khoản trên Firebase Auth
            const fire = await createUserWithEmailAndPassword(auth, newLecturer.username, "@Ou123");

            
            await setDoc(doc(db, "users", fire.user.uid), {
                username: newLecturer.username,
                name: "lecturer test anynomous",
                avatar: "https://res.cloudinary.com/dmz9kuzue/image/upload/v1747407707/qzmsrlnad6mpcpqrowx6.png",
                id: fire.user.uid,
                blocked: [],
            });

            await setDoc(doc(db, "userChats", fire.user.uid), {
                chats: [],
            });

            alert("Đã tạo giảng viên!");
            setNewLecturer({ username: "" });
            setShowLecturerForm(false);
            setPage(1);
            setUsers([]);
            loadUsers();
        } catch (err) {
            if (err.response && err.response.data) {
                setRegisterError(err.response.data);
            } else {
                setRegisterError("Có lỗi xảy ra, vui lòng thử lại sau.");
            }
        } finally {
            setRegistering(false);
        }
    };

    const [filters, setFilters] = useState({
        username: "",
        isActive: "",
        startDate: "",
        endDate: ""
    });

    const loadUsers = async () => {
        setLoading(true);
        try {
            let url = `${endpoints['get-user']}?page=${page}`;

            if (filters.username) url += `&username=${filters.username}`;
            if (filters.isActive !== "") url += `&isActive=${filters.isActive}`;
            if (filters.startDate) url += `&startDate=${filters.startDate}`;
            if (filters.endDate) url += `&endDate=${filters.endDate}`;

            let res = await authApis().get(url);

            if (res.data.length === 0)
                setPage(0);
            else {
                if (page === 1)
                    setUsers(res.data);
                else
                    setUsers(prev => [...prev, ...res.data]);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (page > 0) loadUsers();
    }, [page]);

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const handleSearch = () => {
        setPage(1);
        setUsers([]);
        loadUsers();
    };
    const activateUser = async (userId) => {
        try {
            setActivatingUserId(userId);
            await authApis().put(`${endpoints['active-account']}${userId}`);
            alert("Đã kích hoạt tài khoản!");
            setPage(1);
            setUsers([]);
            loadUsers();
        } catch (err) {
            console.error(err);
            alert("Lỗi kích hoạt!");
        } finally {
            setActivatingUserId(null);
        }
    };

    const loadMore = () => {
        if (!loading && page > 0) setPage(page + 1);
    };

    return (
        <Row className="p-3 justify-content-center">
            <Col md={8}>
                <h3 className="text-center">Quản lý người dùng</h3>

                <Form className="mb-3">

                    <Row>
                        <Col md={3}>
                            <Form.Control
                                type="text"
                                name="username"
                                placeholder="Tìm theo username"
                                value={filters.username}
                                onChange={handleFilterChange}
                            />
                        </Col>
                        <Col md={2}>
                            <Form.Select name="isActive" value={filters.isActive} onChange={handleFilterChange}>
                                <option value="">Trạng thái</option>
                                <option value="true">Hoạt động</option>
                                <option value="false">Vô hiệu hóa</option>
                            </Form.Select>
                        </Col>
                        <Col md={2}>
                            <Form.Control
                                type="date"
                                name="startDate"
                                value={filters.startDate}
                                onChange={handleFilterChange}
                            />
                        </Col>
                        <Col md={2}>
                            <Form.Control
                                type="date"
                                name="endDate"
                                value={filters.endDate}
                                onChange={handleFilterChange}
                            />
                        </Col>
                        <Col md={2}>
                            <Button onClick={handleSearch}>Tìm kiếm</Button>
                        </Col>

                    </Row>
                </Form>
                <>
                    <Button
                        variant={showLecturerForm ? "secondary" : "primary"}
                        className="mt-3"
                        onClick={() => setShowLecturerForm(!showLecturerForm)}
                    >
                        {showLecturerForm ? "Đóng form đăng ký giảng viên" : "Thêm tài khoản giảng viên"}
                    </Button>

                    {showLecturerForm && (
                        <Card className="mt-3 mx-auto" style={{ maxWidth: "500px" }}>
                            <Card.Body>
                                <h5 className="text-center mb-4">Đăng ký tài khoản giảng viên</h5>
                                <Form>
                                    {registerError && (
                                        <div className="alert alert-danger py-2 px-3 mb-3" role="alert">
                                            {registerError}
                                        </div>
                                    )}

                                    <Form.Group className="mb-3" controlId="username">
                                        <Form.Label>Username</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={newLecturer.username}
                                            onChange={(e) =>
                                                setNewLecturer({ ...newLecturer, username: e.target.value })
                                            }
                                        />
                                    </Form.Group>

                                    <div className="d-flex justify-content-end gap-2">
                                        <Button onClick={registerLecturer} disabled={registering}>
                                            {registering && <Spinner animation="border" size="sm" className="me-2" />}
                                            {registering ? "Đang tạo..." : "Tạo tài khoản"}
                                        </Button>
                                        <Button
                                            variant="outline-secondary"
                                            onClick={() => {
                                                setShowLecturerForm(false);
                                                setNewLecturer({ username: "" });
                                                setRegisterError(""); // reset lỗi khi hủy
                                            }}
                                        >
                                            Hủy
                                        </Button>
                                    </div>
                                </Form>

                            </Card.Body>
                        </Card>
                    )}
                </>

                <Table striped bordered hover>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Ngày tạo</th>
                            <th>Role</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map(u => (
                            <tr key={u.id}>
                                <td>{u.id}</td>
                                <td>{u.username}</td>
                                <td>{new Date(u.createdAt).toLocaleString()}</td>
                                <td>{u.role}</td>
                                <td>{u.isActive ? "Hoạt động" : "Vô hiệu hóa"}</td>
                                <td>
                                    {!u.isActive && (
                                        activatingUserId === u.id ? (
                                            <Spinner animation="border" size="sm" />
                                        ) : (
                                            <Button
                                                variant="success"
                                                onClick={() => activateUser(u.id)}
                                            >
                                                Xác nhận
                                            </Button>
                                        )
                                    )}
                                </td>

                            </tr>
                        ))}
                    </tbody>
                </Table>

                {loading && <MySpinner />}

                {page > 0 && !loading && (
                    <div className="text-center">
                        <Button onClick={loadMore}>Tải thêm...</Button>
                    </div>
                )}
            </Col>
        </Row>
    );
};

export default ManageUser;
