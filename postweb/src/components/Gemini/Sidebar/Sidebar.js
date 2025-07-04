import React, { useState } from 'react';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './Sidebar.css';

const Sidebar = () => {
    const [extended, setExtended] = useState(true); // Bắt đầu mở rộng

    const toggleSidebar = () => {
        setExtended((prev) => !prev);
    };

    return (
        <div className={`gemini-sidebar ${extended ? 'expanded' : 'collapsed'}`}>
            {/* Top section */}
            <div className="sidebar-top">
                <i onClick={toggleSidebar} className="bi bi-list sidebar-menu-icon"></i>

                <div className="new-chat">
                    <i className="bi bi-plus-circle"></i>
                    {extended && <p>New Chat</p>}
                </div>

                {extended && (
                    <div className="recent">
                        <p className="recent-title">Recent</p>
                        <div className="recent-entry">
                            <i className="bi bi-chat-dots"></i>
                            <p>What is React ...</p>
                        </div>
                        <div className="recent-entry">
                            <i className="bi bi-chat-dots"></i>
                            <p>Explain async JS</p>
                        </div>
                    </div>
                )}
            </div>

            {/* Bottom section */}
            <div className="sidebar-bottom">
                <div className="bottom-item">
                    <i className="bi bi-question-circle"></i>
                    {extended && <p>Help</p>}
                </div>
                <div className="bottom-item">
                    <i className="bi bi-clock-history"></i>
                    {extended && <p>Activity</p>}
                </div>
                <div className="bottom-item">
                    <i className="bi bi-gear"></i>
                    {extended && <p>Setting</p>}
                </div>
            </div>
        </div>
    );
};

export default Sidebar;
