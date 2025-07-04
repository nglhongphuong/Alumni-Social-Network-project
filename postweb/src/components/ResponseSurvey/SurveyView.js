import { useEffect, useState } from "react";
import { Button, Card, Col, Form, Row, Spinner, Table } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "../layouts/MySpinner";
import { useNavigate } from "react-router-dom";

const SurveyView = () => {
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [survey, setSurvey] = useState([]);
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

            const publicSurveys = res.data.filter(s => s.status === "PUBLISH");

            if (publicSurveys.length === 0 && page === 1)
                setPage(0);
            else {
                if (page === 1)
                    setSurvey(publicSurveys);
                else
                    setSurvey(prev => [...prev, ...publicSurveys]);
            }

        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
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
                        </Row>
                    </Form>

                    <Row className="g-3">
                        {survey.length > 0 ? (
                            survey.map((item) => (
                                <Col xs={12} key={item.id}>
                                    <Card
                                        className="shadow-sm border-0 rounded-4"
                                        style={{
                                            backgroundColor: "#f8f9fa", // nền xám nhạt nhẹ nhàng
                                            borderLeft: "#393E46", // viền xanh dương nổi bật bên trái
                                        }}
                                    >
                                        <Card.Body>
                                            <Card.Title className="fs-5 fw-semibold" style={{ color: "#393E46" }}>
                                                {item.title}
                                            </Card.Title>

                                            <Card.Text className="text-dark">{item.description}</Card.Text>
                                            <div className="d-flex justify-content-end">
                                                <Button
                                                    variant="outline-info"
                                                    onClick={() =>
                                                        navigate(`/response-view/${item.id}`, {
                                                            state: {
                                                                title: item.title,
                                                                description: item.description,
                                                                status: item.status,
                                                                startDate: item.startDate,
                                                                endDate: item.endDate,
                                                            },
                                                        })
                                                    }
                                                >
                                                    Thực hiện khảo sát
                                                </Button>
                                            </div>
                                        </Card.Body>
                                    </Card>
                                </Col>
                            ))
                        ) : (
                            <Col>
                                <Card
                                    className="p-3 text-center text-muted border-0"
                                    style={{
                                        backgroundColor: "#f8f9fa",
                                        borderRadius: "1rem",
                                    }}
                                >
                                    Không có bài khảo sát.
                                </Card>
                            </Col>
                        )}
                    </Row>



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

export default SurveyView;