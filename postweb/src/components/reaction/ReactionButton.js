import React, { useState } from "react";
import './ReactionButton.css';

const REACTIONS = ["like", "love", "haha", "wow", "sad", "angry"];
const EMOJI_MAP = {
  like: "👍",
  love: "❤️",
  haha: "😂",
  wow: "😮",
  sad: "😢",
  angry: "😡"
};

const ReactionButton = ({ currentReaction, onReact, onRemoveReaction }) => {
  const [showOptions, setShowOptions] = useState(false);

  const handleMainButtonClick = () => {
    if (currentReaction) {
      onRemoveReaction();  // nếu đã react → xoá khi bấm
    }
  };

  const handleReact = (type) => {
    onReact(type);         // chọn emoji → thêm hoặc thay đổi reaction
    setShowOptions(false); // ẩn menu
  };

const active = REACTIONS.includes(currentReaction?.toLowerCase?.());

  const buttonStyle = {
    backgroundColor: active ? "#dc3545" : "white",
    color: active ? "white" : "#dc3545",
    borderColor: "#dc3545"
  };

  return (
    <div
      className="reaction-wrapper position-relative"
      onMouseEnter={() => setShowOptions(true)}
      onMouseLeave={() => setShowOptions(false)}
      style={{ display: "inline-block" }}
    >
      <button
        className="btn btn-sm"
        style={buttonStyle}
        onClick={handleMainButtonClick}
      >
        {EMOJI_MAP[currentReaction?.toLowerCase()] || "👍"} Thích
      </button>

      {showOptions && (
        <div
          className="reaction-options shadow-sm p-2 bg-white rounded-3 position-absolute z-3"
          style={{ whiteSpace: "nowrap", top: "100%", left: 0 }}
        >
          {REACTIONS.map((r, idx) => (
            <span
              key={r + idx}
              className="reaction-emoji px-1"
              style={{ cursor: "pointer", fontSize: "1.5rem" }}
              onClick={() => handleReact(r)}
              title={r.charAt(0).toUpperCase() + r.slice(1)}
            >
              {EMOJI_MAP[r]}
            </span>
          ))}
        </div>
      )}
    </div>
  );
};

export default ReactionButton;
