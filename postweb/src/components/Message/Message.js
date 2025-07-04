import React, { useState, useRef, useEffect } from "react";
import EmojiPicker from "emoji-picker-react";
import "./Message.css";
import avatar from "../../img/avatar.webp";
import cover from "../../img/background.webp";

import 'bootstrap-icons/font/bootstrap-icons.css';
import { arrayUnion, doc, getDoc, onSnapshot, updateDoc } from "firebase/firestore";
import { db } from "../../lib/firebase";
import { useChatStore } from "../../lib/chatStore";
import { useUserStore } from "../../lib/userStore";

const Message = () => {
  const [showEmoji, setShowEmoji] = useState(false);
  const [message, setMessage] = useState("");

  const emojiRef = useRef();
  const endRef = useRef(null);
  const [chat, setChat] = useState();
  const { chatId, user } = useChatStore();
  const { currentUser } = useUserStore();


  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth" });
  })

  const handleEmojiClick = (emojiData) => {
    setMessage(prev => prev + emojiData.emoji);
    console.log(message);
  };

  useEffect(() => {
    const unSub = onSnapshot(
      doc(db, "chats", chatId), (res) => {
        setChat(res.data())
      })
    return () => {
      unSub();
    }
  }, [chatId]);

  console.log(chat);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (emojiRef.current && !emojiRef.current.contains(event.target)) {
        setShowEmoji(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);


  const handleSend = async () => {
    if (message === "") return;
    try {

      await updateDoc(doc(db, "chats", chatId), {
        messages: arrayUnion({
          senderId: currentUser.id,
          message,
          createdAt: new Date(),
        })
      });

      const userIDs = [currentUser.id, user.id];

      userIDs.forEach(async (id) => {




        const userChatRef = doc(db, "userChats", id);
        const userChatsSnapshot = await getDoc(userChatRef);
        if (userChatsSnapshot.exists()) {
          const userChatsData = userChatsSnapshot.data();
          const chatIndex = userChatsData.chats.findIndex((c) => c.chatId === chatId);

          userChatsData.chats[chatIndex].lastMessage = message;
          userChatsData.chats[chatIndex].isSeen = id === currentUser.id ? true : false;
          userChatsData.chats[chatIndex].updatedAt = Date.now();

          await updateDoc(userChatRef, {
            chats: userChatsData.chats,
          });
        }
      });

      setMessage("");

    } catch (err) {
      console.log(err);
    }

  };

  return (
    <div className="mess">
      <div className="top">
        <div className="user">
          <img src={user.avatar} alt="User avatar" />
          <div className="texts">
            <span>{user.name}</span>
            <p>{user.username}</p>
          </div>
        </div>
        <div className="icon">
          <i className="bi bi-telephone" title="Gọi điện"></i>
          <i className="bi bi-camera-video" title="Gọi video"></i>
          <i className="bi bi-info-circle" title="Thông tin"></i>
        </div>
      </div>

      <div className="center">
        {chat?.messages?.map((mess) => (
          <>
            <div className={mess.senderId === currentUser?.id ? "message own" : "message"} key={mess?.createdAt}>
              <div className="texts">
                <p>{mess.message}</p>
                <span>
                  {new Date(mess.createdAt?.seconds * 1000).toLocaleString([], {
                    hour: '2-digit',
                    minute: '2-digit',
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric'
                  })}
                </span>

              </div>
            </div>


          </>
        ))}


        <div ref={endRef}></div>
      </div>


      <div className="bottom">
        <div className="icons">
          <i className="bi bi-image" title="Gửi ảnh"></i>
          <i className="bi bi-camera" title="Mở camera"></i>
          <i className="bi bi-mic" title="Ghi âm"></i>
        </div>

        <input
          type="text"
          placeholder="Type a message..."
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />

        <div className="emoji">
          <i
            className="bi bi-emoji-smile"
            title="Chèn emoji"
            onClick={() => setShowEmoji(!showEmoji)}
          ></i>

          {showEmoji && (
            <div className="emoji-picker" ref={emojiRef}>
              <EmojiPicker onEmojiClick={handleEmojiClick} height={350} />
            </div>
          )}
        </div>


        <button className="sendButton" onClick={handleSend}>Send</button>
      </div>
    </div>
  );
};

export default Message;
