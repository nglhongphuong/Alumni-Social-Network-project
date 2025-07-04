import React, { useContext, useEffect, useState, useRef } from "react";
import { MyUserContext } from "../../../configs/MyContexts";
import "./Main.css";
import { GoogleGenAI } from "@google/genai";
import ReactMarkdown from "react-markdown";


const Main = () => {
    const user = useContext(MyUserContext);
    const ai = new GoogleGenAI({ apiKey: "AIzaSyBDo3M32xDKc_BPgVZrQT61FYWi_p4kEWc" });

    const [input, setInput] = useState("");
    const [chatHistory, setChatHistory] = useState([]); // lưu lịch sử chat
    const [loading, setLoading] = useState(false);
    const inputRef = useRef(null);
    const chatEndRef = useRef(null); // ref để scroll xuống cuối chat

    // Scroll chat xuống cuối mỗi khi chatHistory hoặc loading thay đổi
    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [chatHistory, loading]);


    // Hàm gọi API Gemini trả lời prompt
    const sendPrompt = async (prompt) => {
        if (!prompt.trim()) return;
        setLoading(true);
        // Cập nhật câu hỏi user vào chat history trước
        setChatHistory((prev) => [...prev, { from: "user", text: prompt }]);
        setInput("");

        try {
            const response = await ai.models.generateContent({
                model: "gemini-2.0-flash",
                contents: prompt,
            });

            const answer = response.text || "Xin lỗi, mình không có câu trả lời.";

            setChatHistory((prev) => [...prev, { from: "gemini", text: answer }]);
        } catch (error) {
            setChatHistory((prev) => [
                ...prev,
                { from: "gemini", text: "Có lỗi xảy ra khi gọi Gemini AI." },
            ]);
            console.error("Error calling Gemini AI:", error);
        } finally {
            setLoading(false);
            // Focus lại input sau khi gửi
            inputRef.current?.focus();
        }
    };

    // Xử lý bấm enter gửi prompt
    const handleKeyDown = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendPrompt(input);
        }
    };

    // Bấm nút gửi
    const handleSendClick = () => {
        sendPrompt(input);
    };

    return (
        <div className="gemini-main">
            <div className="main-nav">
                <p>Gemini</p>
                <img src={user.avatar} alt="avatar" />
            </div>

            <div className="main-container" style={{ display: "flex", flexDirection: "column", flexGrow: 1 }}>
                <div className="greet">
                    <p>
                        <span>Hello {user.name}</span>
                    </p>

                </div>

                {/* Container chat với chiều cao cố định, scroll khi quá dài */}
                <div
                    className="chat-history"

                >
                    {chatHistory.map((msg, idx) => (
                        <div
                            key={idx}
                            className={msg.from === "user" ? "chat-message user" : "chat-message gemini"}
                            style={{
                                textAlign: msg.from === "user" ? "right" : "left",
                                marginBottom: "0.5rem",
                                display: "flex",
                                justifyContent: msg.from === "user" ? "flex-end" : "flex-start",  // align theo chiều ngang
                            }}
                        >
                            <div
                                style={{
                                    display: "inline-block",
                                    padding: "8px 12px",
                                    borderRadius: "15px",
                                    backgroundColor: msg.from === "user" ? "#f9a825" : "#212121",
                                    color: msg.from === "user" ? "#000" : "#fff176",
                                    maxWidth: "70%",
                                    whiteSpace: "pre-wrap",
                                }}
                            >
                                <ReactMarkdown
                                    children={msg.text}
                                    components={{
                                        p: ({ node, ...props }) => <p style={{ margin: 0 }} {...props} />,
                                    }}
                                />

                            </div>
                        </div>
                    ))}

                    {loading && (
                        <div
                            className="chat-message gemini"
                            style={{ textAlign: "left", marginBottom: "0.5rem" }}
                        >
                            <div
                                style={{
                                    display: "inline-block",
                                    padding: "8px 12px",
                                    borderRadius: "15px",
                                    backgroundColor: "#43a047",
                                    color: "#fff",
                                    maxWidth: "70%",
                                }}
                            >
                                Gemini is typing...
                            </div>
                        </div>
                    )}

                    <div ref={chatEndRef} />
                </div>
            </div>

            <div className="main-bottom">
                <div className="search-box">
                    <input
                        ref={inputRef}
                        rows={1}
                        placeholder="Enter a prompt..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={handleKeyDown}
                        style={{ resize: "none", overflow: "hidden" }}
                    />
                    <div className="search-icons">
                        <i className="bi bi-image"></i> {/* Gallery icon */}
                        <i className="bi bi-mic-fill"></i> {/* Microphone icon */}
                        <i
                            className="bi bi-send-fill"
                            onClick={handleSendClick}
                            style={{ cursor: "pointer" }}
                            title="Send"
                        ></i>
                        {" "}
                        {/* Send icon */}
                    </div>
                </div>

                <p className="bottom-info">
                    Gemini may display inaccurate info, so double-check important details.
                </p>
            </div>
        </div>
    );
};

export default Main;
