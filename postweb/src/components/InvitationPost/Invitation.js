import { useEffect, useState } from "react";
import { Button, Card, Col, Form, Row, Spinner, Table } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "../layouts/MySpinner";
import { useNavigate } from "react-router-dom";


const Invitation = () => {
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [invitation, setInvitation] = useState([]);
    const navigate = useNavigate();
    const [filters, setFilters] = useState({
        title: "",
        recipientScope: "",
        startDate: "",
        endDate: ""
    });

    const loadInvitation = async () => {
        setLoading(true);
        try {
            let url = `${endpoints['invitation']}?page=${page}`;
            if (filters.title) url += `&title=${filters.title}`;
            if (filters.recipientScope) url += `&recipientScope=${filters.recipientScope}`;
            if (filters.startDate) url += `&startDate=${filters.startDate}`;
            if (filters.endDate) url += `&endDate=${filters.endDate}`;

            let res = await authApis().get(url);

            if (res.data.length === 0)
                setPage(0);
            else {
                if (page === 1)
                    setInvitation(res.data);
                else
                    setInvitation(prev => [...prev, ...res.data]);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const deleteInvitation = async (id) => {
        if (window.confirm("Bạn có chắc muốn xoá lời mời này không?")) {
            try {
                await authApis().delete(`${endpoints['invitation']}${id}`);
                setInvitation(prev => prev.filter(inv => inv.id !== id));
                alert("Đã xoá lời mời!");
            } catch (err) {
                console.error("Lỗi xoá lời mời:", err);
                alert("Không thể xoá lời mời!");
            }
        }
    };



    useEffect(() => {
        if (page > 0) loadInvitation();
    }, [page]);

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const handleSearch = () => {
        setPage(1);
        setInvitation([]);
        loadInvitation();
    };

    const loadMore = () => {
        if (!loading && page > 0) setPage(page + 1);
    };

    return (
        <div className="container-fluid">
            <Row className="justify-content-center mt-1">
                <Col xs={12} md={8} lg={8} xl={8}>

                    <h3 className="mb-4 text-center">Danh sách lời mời</h3>
                    <Form className="mb-3">
                        <Row>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>Tiêu đề</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="title"
                                        value={filters.title}
                                        onChange={handleFilterChange}
                                        placeholder="Nhập tiêu đề..."
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>Loại người nhận</Form.Label>
                                    <Form.Select
                                        name="recipientScope"
                                        value={filters.recipientScope}
                                        onChange={handleFilterChange}
                                    >
                                        <option value="">Tất cả</option>
                                        <option value="ROLE_GROUP">Nhóm quyền</option>
                                        <option value="INDIVIDUAL">Cá nhân</option>
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row className="mt-3">
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>Từ ngày</Form.Label>
                                    <Form.Control
                                        type="date"
                                        name="startDate"
                                        value={filters.startDate}
                                        onChange={handleFilterChange}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>Đến ngày</Form.Label>
                                    <Form.Control
                                        type="date"
                                        name="endDate"
                                        value={filters.endDate}
                                        onChange={handleFilterChange}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row className="mt-3 align-items-center">
                            <Col xs={6} className="text-start">
                                <Button variant="primary" onClick={handleSearch}>
                                    Tìm kiếm
                                </Button>
                            </Col>
                            <Col xs={6} className="text-end">
                                <Button variant="success" onClick={() => navigate("/new-invitation")}>
                                    + Tạo lời mời
                                </Button>
                            </Col>
                        </Row>

                    </Form>

                    <Table bordered hover responsive>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Nội dung</th>
                                <th>Loại</th>
                                <th>Ngày tạo</th>
                                <th>Cập nhật</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {invitation.length > 0 ? (
                                invitation.map((item, index) => (
                                    <tr key={item.id}>
                                        <td>{item.id}</td>
                                        <td>{item.title}</td>
                                        <td>{item.content}</td>
                                        <td>{item.recipientScope}</td>
                                        <td>{item.createdAt}</td>
                                        <td>{item.updatedAt}</td>
                                        <td>
                                            <div className="d-flex flex-column gap-2">
                                                {/* <Button
                                                    variant="warning"
                                                    size="sm"
                                                    onClick={() => console.log("Edit", item.id)}
                                                >
                                                    Sửa
                                                </Button> */}
                                                <Button
                                                    variant="danger"
                                                    size="sm"
                                                  onClick={() => deleteInvitation(item.id)}

                                                >
                                                    Xoá
                                                </Button>
                                            </div>
                                        </td>

                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="7" className="text-center">Không có lời mời.</td>
                                </tr>
                            )}
                        </tbody>

                    </Table>
                    {loading && <MySpinner />}
                    {!loading && page > 0 && (
                        <div className="text-center">
                            <Button onClick={loadMore} disabled={loading}>
                                {loading ? "Đang tải..." : "Tải thêm"}
                            </Button>
                        </div>
                    )}

                </Col>
            </Row>
        </div>
    );

}

export default Invitation;

