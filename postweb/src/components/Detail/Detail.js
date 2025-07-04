import "./Detail.css";
import avatar from "../../img/avatar.webp";
import {
  ShareFill,
  Download,
  FileEarmarkTextFill,
  GearFill,
} from "react-bootstrap-icons";
import { useChatStore } from "../../lib/chatStore";
import { useUserStore } from "../../lib/userStore";


const Detail = () => {

  const { chatId, user} = useChatStore();
  const { currentUser } = useUserStore();


  return (
    <div className="detail">
      <div className="user">
        <img src={user?.avatar || avatar} alt="User avatar" />
        <h4>{user?.name }</h4>
        <p>{user.username}</p>
      </div>
      <div className="info">

        <div className="option">
          <div className="title">
            <span><GearFill /> Chat Settings</span>
            <ShareFill className="icon" />
          </div>
        </div>

        <div className="option">
          <div className="title">
            <span><Download /> Shared photos</span>
            <ShareFill className="icon" />
          </div>
          <div className="photos">
            <div className="photoItem">
              <div className="photoDetail">
                <img
                  src="https://i.pinimg.com/736x/5e/55/10/5e5510b5f5c2e2ee2ee1b304758cf762.jpg"
                  alt="anh.pnj"
                />
                <span>photo_2024.png</span>
              </div>
              <Download className="photoDownload" />
            </div>
          </div>
        </div>

        <div className="option">
          <div className="title">
            <span><FileEarmarkTextFill /> Shared Files</span>
            <ShareFill className="icon" />
          </div>
        </div>

        <button>Block User</button>
      </div>
    </div>
  );
};

export default Detail;
