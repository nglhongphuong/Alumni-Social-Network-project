import React, { useEffect, useState } from "react";
import { Bar, Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

import { saveAs } from "file-saver";
import { Document, Packer, Paragraph, Table, TableCell, TableRow, TextRun } from "docx";

import Apis, { authApis, endpoints } from "../../configs/Apis";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend
);

const months = [
  "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
  "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
];

const fillDataByMonth = (rawData, valueKey, selectedYear) => {
  const monthMap = new Array(12).fill(0);
  rawData
    .filter(item => item.year === selectedYear)
    .forEach(item => {
      const monthIndex = item.month - 1;
      monthMap[monthIndex] = item[valueKey] || 0;
    });
  return monthMap;
};

const Statistics = () => {
  const [allUserData, setAllUserData] = useState([]);
  const [allPostData, setAllPostData] = useState([]);
  const [allSurveyData, setAllSurveyData] = useState([]);
  const [allInvitationData, setAllInvitationData] = useState([]);
  const [yearList, setYearList] = useState([]);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res1 = await authApis().get(endpoints.stats + "users");
        const res2 = await authApis().get(endpoints.stats + "posts");
        const res3 = await authApis().get(endpoints.stats + "survey-posts");
        const res4 = await authApis().get(endpoints.stats + "invitation-posts");

        setAllUserData(res1.data);
        setAllPostData(res2.data);
        setAllSurveyData(res3.data);
        setAllInvitationData(res4.data);

        const allYears = [
          ...res1.data,
          ...res2.data,
          ...res3.data,
          ...res4.data
        ].map(item => item.year);

        const uniqueYears = Array.from(new Set(allYears)).sort((a, b) => b - a);
        setYearList(uniqueYears);
      } catch (err) {
        console.error("Lỗi khi tải dữ liệu thống kê: ", err);
      }
    };

    fetchData();
  }, []);

  const userData = fillDataByMonth(allUserData, "totalUsers", selectedYear);
  const postData = fillDataByMonth(allPostData, "totalPosts", selectedYear);
  const surveyData = fillDataByMonth(allSurveyData, "totalSurveyPosts", selectedYear);
  const invitationData = fillDataByMonth(allInvitationData, "totalInvitationPosts", selectedYear);

  const makeChartData = (label, data, color) => ({
    labels: months,
    datasets: [
      {
        label,
        data,
        backgroundColor: color,
        borderColor: color,
        borderWidth: 1,
        fill: false,
      }
    ]
  });

  // --- Xuất CSV ---
  const exportCSV = () => {
    const header = ["Tháng", "Người dùng mới", "Bài viết", "Bài khảo sát", "Bài mời"];
    const rows = months.map((month, i) => [
      month,
      userData[i],
      postData[i],
      surveyData[i],
      invitationData[i]
    ]);

    let csvContent = "data:text/csv;charset=utf-8,";
    csvContent += header.join(",") + "\n";
    rows.forEach(row => {
      csvContent += row.join(",") + "\n";
    });

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `ThongKe_${selectedYear}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // --- Xuất Word ---
  const exportWord = async () => {
    const rows = months.map((month, i) => new TableRow({
      children: [
        new TableCell({ children: [new Paragraph(month)] }),
        new TableCell({ children: [new Paragraph(userData[i].toString())] }),
        new TableCell({ children: [new Paragraph(postData[i].toString())] }),
        new TableCell({ children: [new Paragraph(surveyData[i].toString())] }),
        new TableCell({ children: [new Paragraph(invitationData[i].toString())] }),
      ],
    }));

    const table = new Table({
      rows: [
        new TableRow({
          children: [
            new TableCell({ children: [new Paragraph("Tháng")] }),
            new TableCell({ children: [new Paragraph("Người dùng mới")] }),
            new TableCell({ children: [new Paragraph("Bài viết")] }),
            new TableCell({ children: [new Paragraph("Bài khảo sát")] }),
            new TableCell({ children: [new Paragraph("Bài mời")] }),
          ],
        }),
        ...rows
      ],
    });

    const doc = new Document({
      sections: [{
        children: [
          new Paragraph({
            text: `Báo cáo thống kê năm ${selectedYear}`,
            heading: "Heading1",
          }),
          table,
        ],
      }],
    });

    const blob = await Packer.toBlob(doc);
    saveAs(blob, `BaoCao_ThongKe_${selectedYear}.docx`);
  };

  return (
    <div style={{ padding: "2rem", maxWidth: "1000px", margin: "auto" }}>
      <h2 style={{ textAlign: "center" }}>📊 Thống Kê Theo Năm</h2>

      <div style={{ textAlign: "center", margin: "1rem 0" }}>
        <label htmlFor="year-select">Chọn năm: </label>
        <select
          id="year-select"
          value={selectedYear}
          onChange={(e) => setSelectedYear(Number(e.target.value))}
        >
          {yearList.map(year => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </select>
      </div>

      <div style={{ margin: "1rem 0", textAlign: "center" }}>
        <button
          onClick={exportCSV}
          style={{
            marginRight: "1rem",
            padding: "10px 20px",
            borderRadius: "8px",
            border: "none",
            backgroundColor: "#4caf50",
            color: "white",
            fontWeight: "600",
            fontSize: "16px",
            cursor: "pointer",
            transition: "background-color 0.3s ease"
          }}
          onMouseEnter={e => e.currentTarget.style.backgroundColor = "#45a049"}
          onMouseLeave={e => e.currentTarget.style.backgroundColor = "#4caf50"}
        >
          Xuất CSV
        </button>
        <button
          onClick={exportWord}
          style={{
            padding: "10px 20px",
            borderRadius: "8px",
            border: "none",
            backgroundColor: "#2196f3",
            color: "white",
            fontWeight: "600",
            fontSize: "16px",
            cursor: "pointer",
            transition: "background-color 0.3s ease"
          }}
          onMouseEnter={e => e.currentTarget.style.backgroundColor = "#1976d2"}
          onMouseLeave={e => e.currentTarget.style.backgroundColor = "#2196f3"}
        >
          Xuất Word
        </button>
      </div>


      <div style={{ margin: "2rem 0" }}>
        <h3>Người dùng mới theo tháng</h3>
        <Bar data={makeChartData("Người dùng", userData, "#4bc0c0")} />
      </div>

      <div style={{ margin: "2rem 0" }}>
        <h3>Bài viết theo tháng</h3>
        <Bar data={makeChartData("Bài viết", postData, "#36a2eb")} />
      </div>

      <div style={{ margin: "2rem 0" }}>
        <h3>Bài khảo sát theo tháng</h3>
        <Line data={makeChartData("Khảo sát", surveyData, "#ff6384")} />
      </div>

      <div style={{ margin: "2rem 0" }}>
        <h3>Bài mời tham gia theo tháng</h3>
        <Bar data={makeChartData("Bài mời", invitationData, "#ff9f40")} />
      </div>
    </div>
  );
};

export default Statistics;
