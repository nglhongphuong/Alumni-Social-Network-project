import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Pie, Bar } from "react-chartjs-2";
import {
    Chart as ChartJS,
    ArcElement,
    Tooltip,
    Legend,
    CategoryScale,
    LinearScale,
    BarElement
} from 'chart.js';
import { authApis, endpoints } from "../../configs/Apis";
import { Card, Container, Row, Col, Button } from "react-bootstrap";

import { Document, Packer, Paragraph, Table, TableCell, TableRow } from "docx";
import { saveAs } from "file-saver";

// Đăng ký biểu đồ
ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement);

const SurveyStats = () => {
    const { surveyId } = useParams();
    const navigate = useNavigate();
    const [stats, setStats] = useState([]);

    useEffect(() => {
        const loadStats = async () => {
            try {
                let res = await authApis().get(`${endpoints['survey-post']}${surveyId}/stats`);
                setStats(res.data);
            } catch (err) {
                console.error("Lỗi tải thống kê:", err);
            }
        };

        loadStats();
    }, [surveyId]);

    // --- Xuất CSV ---
    const exportCSV = () => {
        if (!stats.length) return;

        let csvContent = "data:text/csv;charset=utf-8,";

        stats.forEach((question, idx) => {
            csvContent += `"Câu hỏi ${idx + 1}","${question.questionContent}"\n`;
            csvContent += "Lựa chọn,Số phiếu\n";
            question.options.forEach(opt => {
                // Thêm dấu " để tránh lỗi dấu phẩy trong nội dung
                csvContent += `"${opt.optionContent}",${opt.voteCount}\n`;
            });
            csvContent += "\n";
        });

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", `ThongKe_KhaoSat_${surveyId}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    // --- Xuất Word ---
    const exportWord = async () => {
        if (!stats.length) return;

        const sections = stats.map((question, idx) => {
            // Tạo hàng tiêu đề bảng
            const headerRow = new TableRow({
                children: [
                    new TableCell({ children: [new Paragraph("Lựa chọn")] }),
                    new TableCell({ children: [new Paragraph("Số phiếu")] }),
                ],
            });

            // Tạo các hàng dữ liệu
            const dataRows = question.options.map(opt => new TableRow({
                children: [
                    new TableCell({ children: [new Paragraph(opt.optionContent)] }),
                    new TableCell({ children: [new Paragraph(opt.voteCount.toString())] }),
                ],
            }));

            return [
                new Paragraph({ text: `Câu hỏi ${idx + 1}: ${question.questionContent}`, heading: "Heading2", spacing: { after: 300 } }),
                new Table({
                    rows: [headerRow, ...dataRows],
                    width: { size: 100, type: "pct" },
                }),
                new Paragraph({ text: "", spacing: { after: 300 } }) // tạo khoảng cách giữa các bảng
            ];
        }).flat();

        const doc = new Document({
            sections: [
                {
                    children: [
                        new Paragraph({
                            text: `Báo cáo thống kê khảo sát #${surveyId}`,
                            heading: "Heading1",
                            spacing: { after: 400 }
                        }),
                        ...sections
                    ]
                }
            ]
        });

        const blob = await Packer.toBlob(doc);
        saveAs(blob, `BaoCao_ThongKe_KhaoSat_${surveyId}.docx`);
    };

    return (
        <Container className="mt-1">
            <Col md={7} className="mx-auto">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h4 className="mb-5" style={{ marginTop: "15px" }}>
                        📊 Thống kê khảo sát #{surveyId}
                    </h4>
                    <Button variant="secondary" size="sm" onClick={() => navigate(-1)}>← Quay lại</Button>
                </div>

                <div className="mb-3 d-flex gap-2 justify-content-center">
                    <Button variant="success" size="sm" onClick={exportCSV}>
                        Xuất CSV
                    </Button>
                    <Button variant="primary" size="sm" onClick={exportWord}>
                        Xuất Word
                    </Button>
                </div>

                {stats.length === 0 ? (
                    <p className="text-center">Không có dữ liệu thống kê.</p>
                ) : (
                    stats.map((question) => {
                        const labels = question.options.map(opt => opt.optionContent);
                        const data = question.options.map(opt => opt.voteCount);

                        const pieData = {
                            labels,
                            datasets: [
                                {
                                    label: "Số phiếu",
                                    data,
                                    backgroundColor: [
                                        "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"
                                    ]
                                }
                            ]
                        };

                        const barData = {
                            labels,
                            datasets: [
                                {
                                    label: "Số phiếu",
                                    data,
                                    backgroundColor: "#36A2EB"
                                }
                            ]
                        };

                        return (
                            <Card key={question.questionId} className="mb-4 p-3 shadow-sm">
                                <h6 className="text-center mb-3">{question.questionContent}</h6>
                                <Row>
                                    <Col xs={12} md={6} className="mb-3">
                                        <Pie data={pieData} options={{ plugins: { legend: { position: "bottom" } } }} />
                                    </Col>
                                    <Col xs={12} md={6}>
                                        <Bar data={barData} options={{
                                            plugins: { legend: { display: false } },
                                            scales: { y: { beginAtZero: true } }
                                        }} />
                                    </Col>
                                </Row>
                            </Card>
                        );
                    })
                )}
            </Col>
        </Container>
    );
};

export default SurveyStats;
