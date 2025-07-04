import { useEffect, useState } from "react";
import { Button, Card, Col, Row, Alert, Form } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layouts/MySpinner";
import { useLocation, useParams, useNavigate } from "react-router-dom";

const ReponseSurvey = () => {
    const { surveyId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();

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
    const [selectedOptions, setSelectedOptions] = useState({}); // {questionId: [optionId, ...]}

    const trimDate = (dateTimeString) => {
        if (!dateTimeString) return "";
        const parts = dateTimeString.split(" ")[0].split("-");
        return `${parts[2]}-${parts[1].padStart(2, "0")}-${parts[0].padStart(2, "0")}`;
    };

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

    useEffect(() => {
        if (page > 0) loadSurveyDetails();
    }, [page]);

    const handleOptionChange = (questionId, optionId, isMultiple) => {
        setSelectedOptions((prev) => {
            const current = prev[questionId] || [];
            if (isMultiple) {
                if (current.includes(optionId)) {
                    return { ...prev, [questionId]: current.filter(id => id !== optionId) };
                } else {
                    return { ...prev, [questionId]: [...current, optionId] };
                }
            } else {
                return { ...prev, [questionId]: [optionId] };
            }
        });
    };

    const handleSubmit = async () => {
        let allOptionIds = [];
        for (const [qId, optionIds] of Object.entries(selectedOptions)) {
            allOptionIds = [...allOptionIds, ...optionIds];
        }

        const payload = {
            optionIds: allOptionIds.join(",")
        };

        try {
            setLoading(true);
            const res = await authApis().post(`${endpoints["survey-response"]}`, payload);
           // alert("Gửi khảo sát thành công!");
            navigate("/survey-view");
        } catch (err) {
            alert("Đã xảy ra lỗi khi gửi khảo sát.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container-fluid py-3">
            <Row className="justify-content-center">
                <Col xs={12} md={10} lg={8}>
                    <h3 className="mb-2">{initTitle}</h3>
                    <p className="text-muted">{initDesc}</p>
                    <p>Thời gian: {trimDate(initStart)} - {trimDate(initEnd)}</p>

                    {loading && <MySpinner />}
                    {questions.length === 0 && !loading && (
                        <Alert variant="info">Không có câu hỏi nào trong khảo sát này.</Alert>
                    )}

                    {questions.map((q) => (
                        <Card className="mb-3 shadow-sm" key={q.questionId}>
                            <Card.Body>
                                <Card.Title>{q.content}</Card.Title>
                                {q.responseType === "SINGLE_CHOICE" && q.options.map((opt) => (
                                    <Form.Check
                                        type="radio"
                                        key={opt.optionId}
                                        name={`question-${q.questionId}`}
                                        label={opt.content}
                                        checked={(selectedOptions[q.questionId] || []).includes(opt.optionId)}
                                        onChange={() => handleOptionChange(q.questionId, opt.optionId, false)}
                                    />
                                ))}
                                {q.responseType === "MULTIPLE_CHOICE" && q.options.map((opt) => (
                                    <Form.Check
                                        type="checkbox"
                                        key={opt.optionId}
                                        label={opt.content}
                                        checked={(selectedOptions[q.questionId] || []).includes(opt.optionId)}
                                        onChange={() => handleOptionChange(q.questionId, opt.optionId, true)}
                                    />
                                ))}
                                {q.responseType === "TEXT" && (
                                    <Alert variant="warning" className="mt-2">
                                        (Câu hỏi tự luận - không yêu cầu chọn lựa)
                                    </Alert>
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

                    {questions.length > 0 && (
                        <div className="text-center my-4">
                            <Button variant="success" onClick={handleSubmit}>
                                Gửi khảo sát
                            </Button>
                        </div>
                    )}
                </Col>
            </Row>
        </div>
    );
};

export default ReponseSurvey;
