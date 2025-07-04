import { useEffect, useState } from "react";
import { Button, Card, Col, Row, Alert, Form, InputGroup } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layouts/MySpinner";
import { useLocation, useParams, useNavigate } from "react-router-dom";


const SurveyDetail = () => {
    const { surveyId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const trimDate = (dateTimeString) => {
        if (!dateTimeString) return "";
        const parts = dateTimeString.split(" ")[0].split("-");
        if (parts.length !== 3) return "";
        return `${parts[2]}-${parts[1].padStart(2, "0")}-${parts[0].padStart(2, "0")}`;
    };
    const {
        title: initTitle,
        description: initDesc,
        status: initStatus,
        startDate: initStart,
        endDate: initEnd,
    } = location.state || {};
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [questions, setQuestions] = useState([]);
    const [editSurvey, setEditSurvey] = useState(false);
    const [surveyInfo, setSurveyInfo] = useState({
        title: initTitle || "",
        description: initDesc || "",
        status: initStatus || "",
        startDate: trimDate(initStart) || "",
        endDate: trimDate(initEnd) || "",
    });
    const [showAddForm, setShowAddForm] = useState(false);
    const [newQuestionContent, setNewQuestionContent] = useState("");
    const [newQuestionType, setNewQuestionType] = useState("SINGLE_CHOICE");

    const [addingOptionForQuestionId, setAddingOptionForQuestionId] = useState(null);
    const [newOptionContent, setNewOptionContent] = useState("");

    const formatDateForInput = (dateString) => {
        if (!dateString) return "";
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return ""; // không hợp lệ
        return date.toISOString().split("T")[0]; // trả về yyyy-MM-dd
    };
    const [editingQuestionId, setEditingQuestionId] = useState(null);
    const [editingOptionId, setEditingOptionId] = useState(null);
    const [questionEditData, setQuestionEditData] = useState({
        content: "",
        responseType: "",
    });
    const [optionEditData, setOptionEditData] = useState({
        content: "",
    });


    const loadSurveyDetails = async () => {
        setLoading(true);
        try {
            let url = `${endpoints["survey-post"]}${surveyId}/details?page=${page}`;
            let res = await authApis().get(url);
            if (res.data.length === 0) {
                setPage(0);
            } else {
                if (page === 1) setQuestions(res.data);
                else setQuestions((prev) => [...prev, ...res.data]);
            }
        } catch (err) {
            console.error("Lỗi khi tải chi tiết khảo sát:", err);
        } finally {
            setLoading(false);
        }
    };

    const loadSurveyDetailsAllPages = async () => {
        setLoading(true);
        try {
            let allQuestions = [];
            for (let p = 1; p <= page; p++) {
                let url = `${endpoints["survey-post"]}${surveyId}/details?page=${p}`;
                let res = await authApis().get(url);
                if (res.data.length === 0) {
                    break;  // Nếu trang trống thì dừng
                }
                allQuestions = [...allQuestions, ...res.data];
            }
            setQuestions(allQuestions);
        } catch (err) {
            console.error("Lỗi khi tải chi tiết khảo sát:", err);
        } finally {
            setLoading(false);
        }
    };


    useEffect(() => {
        if (page > 0) loadSurveyDetails();
    }, [page]);

    // Cập nhập thông  tin khảo sát ======== survey-post
    const handleSurveyChange = (e) => {
        const { name, value } = e.target;
        setSurveyInfo((prev) => ({ ...prev, [name]: value }));
    };

    const saveSurveyInfo = async () => {
        setLoading(true);
        try {
            console.log("Data gửi đi:", surveyInfo);
            await authApis().put(`${endpoints["survey-post"]}${surveyId}`, surveyInfo);
            setEditSurvey(false);
        } catch (err) {
            console.error("Lỗi cập nhật survey:", err);
        } finally {
            setLoading(false);
        }
    };


    // Chỉnh sửa câu hỏi ===== CRUD QUESTION 
    const openQuestionEdit = (q) => {
        setEditingQuestionId(q.questionId);
        setQuestionEditData({ content: q.content, responseType: q.responseType });
    };

    const saveQuestionEdit = async (questionId) => {
        setLoading(true);
        try {
            await authApis().put(`${endpoints["survey-question"]}${questionId}`, questionEditData);
            if (page < 1) {
                setPage(1);
            }
            if (page > 1) {
                setPage(1);
                setQuestions([]);
            }
            await loadSurveyDetails();
            setEditingQuestionId(null);
        } catch (err) {
            console.error("Lỗi cập nhật câu hỏi:", err);
        } finally {
            setLoading(false);
        }
    };

    const deleteQuestion = async (questionId) => {
        if (!window.confirm("Bạn có chắc muốn xóa câu hỏi này?")) return;
        setLoading(true);
        try {
            await authApis().delete(`${endpoints["survey-question"]}${questionId}`, questionEditData);
            if (page < 1) {
                setPage(1);
            }
            if (page > 1) {
                setPage(1);
                setQuestions([]);
            }

            await loadSurveyDetails();
        } catch (err) {
            console.error("Lỗi xóa câu hỏi:", err);
        } finally {
            setLoading(false);
        }
    };

    //====== Chỉnh sửa survey option ==============
    const openOptionEdit = (option) => {
        setEditingOptionId(option.optionId);
        setOptionEditData({ content: option.content });
    };

    const saveOptionEdit = async (questionId, optionId) => {
        setLoading(true);
        try {
            await authApis().put(`${endpoints["survey-option"]}${optionId}`, optionEditData);
            if (page < 1) {
                setPage(1);
            }
            if (page > 1) {
                setPage(1);
                setQuestions([]);
            }
            await loadSurveyDetails();
            setEditingOptionId(null);
        } catch (err) {
            console.error("Lỗi cập nhật option:", err);
        } finally {
            setLoading(false);
        }
    };

    const deleteOption = async (questionId, optionId) => {
        if (!window.confirm("Bạn có chắc muốn xóa lựa chọn này?")) return;
        setLoading(true);
        try {
            await authApis().delete(`${endpoints["survey-option"]}${optionId}`, optionEditData);
            if (page < 1) {
                setPage(1);
            }
            if (page > 1) {
                setPage(1);
                setQuestions([]);
            }
            await loadSurveyDetails();
        } catch (err) {
            console.error("Lỗi xóa option:", err);
        } finally {
            setLoading(false);
        }
    };

    //====== THÊM OPTION ==========
    const startAddOption = (questionId) => {
        setAddingOptionForQuestionId(questionId);
        setNewOptionContent("");
    };

    const saveNewOption = async (questionId) => {
        if (!newOptionContent.trim()) return;

        setLoading(true);
        try {
            await authApis().post(`${endpoints["survey-question"]}${questionId}/create-option`, { content: newOptionContent });
            if (page < 1) {
                setPage(1);
            }
            if (page > 1) {
                setPage(1);
                setQuestions([]);
            }
            await loadSurveyDetails();
            setAddingOptionForQuestionId(null);
            setNewOptionContent("");
        } catch (err) {
            console.error("Lỗi thêm lựa chọn:", err);
        } finally {
            setLoading(false);
        }
    };

    // ====== THÊM QUESTION ===========

    const handleAddQuestion = async (e) => {
        e.preventDefault();
        if (!newQuestionContent.trim()) return;

        setLoading(true);
        try {
            await authApis().post(`${endpoints["survey-post"]}${surveyId}/create-question`, { content: newQuestionContent, responseType: newQuestionType });
            if (page < 1) {
                setPage(1);
            }
            if (page > 1) {
                setPage(1);
                setQuestions([]);
            }
            await loadSurveyDetails();

            setNewQuestionContent("");
            setNewQuestionType("SINGLE_CHOICE");
            setShowAddForm(false);
        } catch (err) {
            console.error("Lỗi thêm câu hỏi:", err);
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="container-fluid py-3">
            <Row className="justify-content-center">
                <Col xs={12} md={10} lg={8}>
                    <div className="d-flex justify-content-between mb-3">
                        <Button variant="secondary" onClick={() => navigate(-1)}>
                            &larr; Quay lại
                        </Button>
                        {!editSurvey ? (
                            <Button variant="warning" onClick={() => setEditSurvey(true)}>
                                Chỉnh sửa khảo sát
                            </Button>
                        ) : (
                            <>
                                <Button variant="success" onClick={saveSurveyInfo} disabled={loading}>
                                    Lưu
                                </Button>
                                <Button variant="danger" onClick={() => setEditSurvey(false)} disabled={loading}>
                                    Hủy
                                </Button>
                            </>
                        )}
                    </div>

                    <Card className="mb-4 shadow-sm">
                        <Card.Body>
                            {!editSurvey ? (
                                <>
                                    <h4 className="mb-3 text-primary">{surveyInfo.title || "Không có tiêu đề"}</h4>
                                    <Row>
                                        <Col xs={12} md={6}>
                                            <p>
                                                <strong>Mô tả:</strong> {surveyInfo.description || "Không có mô tả"}
                                            </p>
                                            <p>
                                                <strong>Trạng thái:</strong> {surveyInfo.status || "Không rõ"}
                                            </p>
                                        </Col>
                                        <Col xs={12} md={6}>
                                            <p>
                                                <strong>Ngày bắt đầu:</strong> {surveyInfo.startDate || "Không rõ"}
                                            </p>
                                            <p>
                                                <strong>Ngày kết thúc:</strong> {surveyInfo.endDate || "Không rõ"}
                                            </p>
                                        </Col>
                                    </Row>
                                    <Row className="mt-3">
                                        <Col className="text-end">
                                            <Button
                                                variant="info"
                                                onClick={() => navigate(`/survey-post/${surveyId}/stats`)}
                                            >
                                                Xem thống kê
                                            </Button>
                                        </Col>
                                    </Row>

                                </>
                            ) : (
                                <Form>
                                    <Form.Group className="mb-2" controlId="formTitle">
                                        <Form.Label>Tiêu đề</Form.Label>
                                        <Form.Control
                                            type="text"
                                            name="title"
                                            value={surveyInfo.title}
                                            onChange={handleSurveyChange}
                                        />
                                    </Form.Group>
                                    <Form.Group className="mb-2" controlId="formDescription">
                                        <Form.Label>Mô tả</Form.Label>
                                        <Form.Control
                                            as="textarea"
                                            rows={2}
                                            name="description"
                                            value={surveyInfo.description}
                                            onChange={handleSurveyChange}
                                        />
                                    </Form.Group>
                                    <Form.Group className="mb-2" controlId="formStatus">
                                        <Form.Label>Trạng thái</Form.Label>
                                        <Form.Select
                                            name="status"
                                            value={surveyInfo.status}
                                            onChange={handleSurveyChange}
                                        >
                                            <option value="">-- Chọn trạng thái --</option>
                                            <option value="PUBLISH">PUBLISH</option>
                                            <option value="CLOSED">CLOSED</option>
                                            <option value="DRAFT">DRAFT</option>
                                        </Form.Select>
                                    </Form.Group>

                                    <Row>
                                        <Col>
                                            <Form.Group controlId="formStartDate">
                                                <Form.Label>Ngày bắt đầu</Form.Label>
                                                <Form.Control
                                                    type="date"
                                                    name="startDate"
                                                    value={formatDateForInput(surveyInfo.startDate)}
                                                    onChange={handleSurveyChange}
                                                />

                                            </Form.Group>
                                        </Col>
                                        <Col>
                                            <Form.Group controlId="formEndDate">
                                                <Form.Label>Ngày kết thúc</Form.Label>
                                                <Form.Control
                                                    type="date"
                                                    name="endDate"
                                                    value={formatDateForInput(surveyInfo.endDate)}
                                                    onChange={handleSurveyChange}
                                                />

                                            </Form.Group>
                                        </Col>
                                    </Row>
                                </Form>
                            )}
                        </Card.Body>
                    </Card>

                    {loading && <MySpinner />}

                    {questions.length === 0 && !loading && (
                        <Alert variant="info">Không có câu hỏi nào trong khảo sát này.</Alert>
                    )}

                    {questions.map((q) => (
                        <Card className="mb-3 shadow-sm" key={q.questionId}>
                            <Card.Body>
                                <div className="d-flex justify-content-between align-items-center">
                                    {editingQuestionId === q.questionId ? (
                                        <div className="flex-grow-1">
                                            <Form.Control
                                                type="text"
                                                value={questionEditData.content}
                                                onChange={(e) =>
                                                    setQuestionEditData((prev) => ({ ...prev, content: e.target.value }))
                                                }
                                                className="mb-2"
                                            />
                                            <Form.Select
                                                value={questionEditData.responseType}
                                                onChange={(e) =>
                                                    setQuestionEditData((prev) => ({ ...prev, responseType: e.target.value }))
                                                }
                                            >
                                                <option value="SINGLE_CHOICE">SINGLE_CHOICE</option>
                                                <option value="MULTIPLE_CHOICE">MULTIPLE_CHOICE</option>
                                            </Form.Select>
                                        </div>
                                    ) : (
                                        <h5>
                                            {q.content} <small className="text-muted">({q.responseType})</small>
                                        </h5>
                                    )}
                                    <div>
                                        {editingQuestionId === q.questionId ? (
                                            <>
                                                <Button
                                                    size="sm"
                                                    variant="success"
                                                    onClick={() => saveQuestionEdit(q.questionId)}
                                                    disabled={loading}
                                                    className="me-1"
                                                >
                                                    Lưu
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="secondary"
                                                    onClick={() => setEditingQuestionId(null)}
                                                    disabled={loading}
                                                >
                                                    Hủy
                                                </Button>
                                            </>
                                        ) : (
                                            <>
                                                <Button
                                                    size="sm"
                                                    variant="warning"
                                                    onClick={() => openQuestionEdit(q)}
                                                    className="me-1"
                                                >
                                                    Sửa
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="danger"
                                                    onClick={() => deleteQuestion(q.questionId)}
                                                    className="me-1"
                                                    disabled={loading}
                                                >
                                                    Xóa
                                                </Button>
                                            </>
                                        )}
                                    </div>
                                </div>

                                <hr />

                                {(q.options || []).map((opt) =>
                                    editingOptionId === opt.optionId ? (
                                        <InputGroup className="mb-2" key={opt.optionId}>
                                            <Form.Control
                                                type="text"
                                                value={optionEditData.content}
                                                onChange={(e) => setOptionEditData({ content: e.target.value })}
                                            />
                                            <Button
                                                variant="success"
                                                onClick={() => saveOptionEdit(q.questionId, opt.optionId)}
                                                disabled={loading}
                                            >
                                                Lưu
                                            </Button>
                                            <Button
                                                variant="secondary"
                                                onClick={() => setEditingOptionId(null)}
                                                disabled={loading}
                                            >
                                                Hủy
                                            </Button>
                                        </InputGroup>
                                    ) : (
                                        <div
                                            key={opt.optionId}
                                            className="d-flex justify-content-between align-items-center mb-2"
                                        >
                                            <div>{opt.content}</div>
                                            <div>
                                                <Button
                                                    size="sm"
                                                    variant="warning"
                                                    onClick={() => openOptionEdit(opt)}
                                                    className="me-1"
                                                    disabled={loading}
                                                >
                                                    Sửa
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="danger"
                                                    onClick={() => deleteOption(q.questionId, opt.optionId)}
                                                    disabled={loading}
                                                >
                                                    Xóa
                                                </Button>
                                            </div>
                                        </div>
                                    )
                                )}

                                {/* Form nhập option mới hiển thị khi đang thêm option cho câu hỏi này */}
                                {addingOptionForQuestionId === q.questionId && (
                                    <div className="d-flex align-items-center mb-2">
                                        <Form.Control
                                            type="text"
                                            placeholder="Nhập nội dung lựa chọn mới"
                                            value={newOptionContent}
                                            onChange={(e) => setNewOptionContent(e.target.value)}
                                            disabled={loading}
                                            className="me-2"
                                        />
                                        <Button
                                            size="sm"
                                            variant="success"
                                            onClick={() => saveNewOption(q.questionId)}
                                            disabled={loading || !newOptionContent.trim()}
                                            className="me-1"
                                        >
                                            Lưu
                                        </Button>
                                        <Button
                                            size="sm"
                                            variant="secondary"
                                            onClick={() => setAddingOptionForQuestionId(null)}
                                            disabled={loading}
                                        >
                                            Hủy
                                        </Button>
                                    </div>
                                )}

                                {addingOptionForQuestionId !== q.questionId && (
                                    <Button
                                        size="sm"
                                        variant="outline-success"
                                        onClick={() => startAddOption(q.questionId)}
                                    >
                                        + Thêm lựa chọn mới
                                    </Button>
                                )}

                            </Card.Body>
                        </Card>
                    ))}

                    {page !== 0 && (
                        <div className="text-center my-3">
                            <Button variant="outline-primary" onClick={() => setPage((prev) => prev + 1)} disabled={loading}>
                                {loading ? "Đang tải..." : "Tải thêm câu hỏi"}
                            </Button>
                        </div>
                    )}

                    <div className="mt-4">
                        {!showAddForm ? (
                            <Button variant="success" onClick={() => setShowAddForm(true)}>
                                + Thêm câu hỏi mới
                            </Button>
                        ) : (
                            <Card className="p-3">
                                <Form onSubmit={handleAddQuestion}>
                                    <Form.Group className="mb-2">
                                        <Form.Label>Nội dung câu hỏi</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={newQuestionContent}
                                            onChange={(e) => setNewQuestionContent(e.target.value)}
                                            placeholder="Nhập nội dung câu hỏi"
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-2">
                                        <Form.Label>Loại phản hồi</Form.Label>
                                        <Form.Select
                                            value={newQuestionType}
                                            onChange={(e) => setNewQuestionType(e.target.value)}
                                        >
                                            {/* <option value="TEXT">Text</option> */}
                                            <option value="SINGLE_CHOICE">Single Choice</option>
                                            <option value="MULTIPLE_CHOICE">Multiple Choice</option>

                                        </Form.Select>
                                    </Form.Group>

                                    <div className="d-flex gap-2">
                                        <Button type="submit" variant="primary" disabled={loading}>
                                            {loading ? "Đang thêm..." : "Thêm"}
                                        </Button>
                                        <Button variant="secondary" onClick={() => setShowAddForm(false)} disabled={loading}>
                                            Hủy
                                        </Button>
                                    </div>
                                </Form>
                            </Card>
                        )}
                    </div>

                </Col>
            </Row>

        </div>
    );
};

export default SurveyDetail;
