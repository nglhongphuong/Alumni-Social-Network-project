import { useEffect, useState } from "react";
import { Button, Card, Col, Form, Row, Spinner, Table } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "../layouts/MySpinner";
import { useNavigate } from "react-router-dom";

const SurveyPost = () => {
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [survey, setSurvey] = useState([]);
    const [showCreateForm, setShowCreateForm] = useState(false);
    const [newSurvey, setNewSurvey] = useState({
        title: "",
        description: "",
        startDate: "",
        endDate: "",
        status: "draft" // mặc định là draft
    });


    const navigate = useNavigate();
    const [filters, setFilters] = useState({
        title: "",
        userId: "",
        startDate: "",
        endDate: "",
        order: "desc" // Mặc định là mới nhất
    });

    const loadSurvey = async () => {
        setLoading(true);
        try {
            let url = `${endpoints['survey-post']}?page=${page}`;
            if (filters.title) url += `&title=${filters.title}`;
            if (filters.userId) url += `&userId=${filters.userId}`;
            if (filters.startDate) url += `&startDate=${filters.startDate}`;
            if (filters.endDate) url += `&endDate=${filters.endDate}`;
            if (filters.order) url += `&order=${filters.order}`;

            let res = await authApis().get(url);

            if (res.data.length === 0)
                setPage(0);
            else {
                if (page === 1)
                    setSurvey(res.data);
                else
                    setSurvey(prev => [...prev, ...res.data]);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const deleteSurvey = async (id) => {
        if (window.confirm("Bạn có chắc muốn xoá bài khảo sát này không?")) {
            try {
                await authApis().delete(`${endpoints['survey-post']}${id}`);
                setSurvey(prev => prev.filter(inv => inv.id !== id));
                alert("Đã xoá bài khảo sát!");
            } catch (err) {
                console.error("Lỗi xoá bài khảo sát:", err);
                alert("Không thể xoá bài khảo sát!");
            }
        }
    };

    useEffect(() => {
        if (page > 0) loadSurvey();
    }, [page]);

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    const handleSearch = () => {
        setPage(1);
        setSurvey([]);
        loadSurvey();
    };

    const loadMore = () => {
        if (!loading && page > 0) setPage(page + 1);
    };
    const handleCreateChange = (e) => {
        const { name, value } = e.target;
        setNewSurvey(prev => ({ ...prev, [name]: value }));
    };
    const handleCreateSurvey = async () => {
        try {
            const res = await authApis().post(endpoints["survey-post"], newSurvey);
            alert("Tạo khảo sát thành công!");
            setPage(1);
            setSurvey([]);
            await loadSurvey();
            setNewSurvey({
                title: "",
                description: "",
                startDate: "",
                endDate: "",
                status: "draft"
            });
            setShowCreateForm(false);
        } catch (err) {
            console.error("Lỗi tạo khảo sát:", err);
            alert("Không thể tạo khảo sát!");
        }
    };

    return (
        <div className="container-fluid">
            <Row className="justify-content-center mt-1">
                <Col xs={12} md={8} lg={8} xl={8}>
                    <h3 className="mb-4 text-center">Danh sách bài khảo sát</h3>
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
                                    <Form.Label>Sắp xếp</Form.Label>
                                    <Form.Select
                                        name="order"
                                        value={filters.order}
                                        onChange={handleFilterChange}
                                    >
                                        <option value="desc">Mới nhất</option>
                                        <option value="asc">Cũ nhất</option>
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
                                <Button
                                    variant={showCreateForm ? "secondary" : "success"}
                                    onClick={() => setShowCreateForm(!showCreateForm)}
                                >
                                    {showCreateForm ? "Đóng form" : "+ Thêm mới"}
                                </Button>
                            </Col>
                        </Row>


                        {showCreateForm && (
                            <Card className="mt-3 p-3 bg-light">
                                <h5 className="mb-3">Tạo bài khảo sát mới</h5>
                                <Form.Group className="mb-3">
                                    <Form.Label>Tiêu đề</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="title"
                                        value={newSurvey.title}
                                        onChange={handleCreateChange}
                                        placeholder="Nhập tiêu đề..."
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Mô tả</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        name="description"
                                        value={newSurvey.description}
                                        onChange={handleCreateChange}
                                        placeholder="Nhập mô tả..."
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ngày bắt đầu</Form.Label>
                                    <Form.Control
                                        type="date"
                                        name="startDate"
                                        value={newSurvey.startDate}
                                        onChange={handleCreateChange}
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Ngày kết thúc</Form.Label>
                                    <Form.Control
                                        type="date"
                                        name="endDate"
                                        value={newSurvey.endDate}
                                        onChange={handleCreateChange}
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Trạng thái</Form.Label>
                                    <Form.Select
                                        name="status"
                                        value={newSurvey.status}
                                        onChange={handleCreateChange}
                                    >
                                        <option value="draft">Nháp</option>
                                        <option value="publish">Công khai</option>
                                        <option value="closed">Đã đóng</option>
                                    </Form.Select>
                                </Form.Group>

                                <Button variant="success" onClick={handleCreateSurvey}>
                                    Gửi
                                </Button>
                            </Card>
                        )}

                    </Form>

                    <Table bordered hover responsive>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Mô tả</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Cập nhật</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {survey.length > 0 ? (
                                survey.map((item, index) => (
                                    <tr key={item.id}>
                                        <td>{item.id}</td>
                                        <td>{item.title}</td>
                                        <td>{item.description}</td>
                                        <td>{item.status}</td>
                                        <td>{item.createdAt}</td>
                                        <td>{item.updatedAt}</td>
                                        <td>
                                            <div className="d-flex flex-column gap-2">
                                                <Button
                                                    variant="outline-info"
                                                    size="sm"
                                                    onClick={() =>
                                                        navigate(`/survey-detail/${item.id}`, {
                                                            state: {
                                                                title: item.title,
                                                                description: item.description,
                                                                status: item.status,
                                                                startDate: item.startDate,
                                                                endDate: item.endDate,
                                                            }
                                                        })
                                                    }
                                                >
                                                    Chi tiết
                                                </Button>


                                                <Button
                                                    variant="danger"
                                                    size="sm"
                                                    onClick={() => deleteSurvey(item.id)}

                                                >
                                                    Xoá
                                                </Button>
                                            </div>
                                        </td>

                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="7" className="text-center">Không có bài khảo sát.</td>
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
};

export default SurveyPost;
