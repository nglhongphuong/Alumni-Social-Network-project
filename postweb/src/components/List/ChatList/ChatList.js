import "./ChatList.css";
import 'bootstrap-icons/font/bootstrap-icons.css';
import avatar from "../../../img/avatar.webp";
import AddUser from "../AddUser/AddUser";
import { useUserStore } from "../../../lib/userStore";
import { useChatStore } from "../../../lib/chatStore";
import { MyUserContext } from "../../../configs/MyContexts";
import React, { useState, useContext, useEffect } from "react";
import { arrayUnion, doc, getDoc, onSnapshot, updateDoc } from "firebase/firestore";
import { db } from "../../../lib/firebase";

const ChatList = () => {
  const [addMode, setAddMode] = useState(false);
  const [input, setInput] = useState("");

  const user = useContext(MyUserContext);
  const { currentUser } = useUserStore();
  const { chatId, changeChat } = useChatStore();

  const [chats, setChats] = useState([]);

  useEffect(() => {
    const unSub = onSnapshot(doc(db, "userChats", currentUser.id), async (res) => {
      const items = res.data().chats;
      const promisses = items.map(async (item) => {
        const userDocRef = doc(db, "users", item.receiverId);
        const userDocSnap = await getDoc(userDocRef);

        const userFire = userDocSnap.data();
        return { ...item, userFire };
      })
      const chatData = await Promise.all(promisses);
      setChats(chatData.sort((a, b) => b.updatedAt - a.updatedAt));
    });


    return () => {
      unSub()
    }
  }, [currentUser.id]);

  console.log("hahahaha", chats);

  const toggleAddMode = () => {
    setAddMode(prev => !prev);
  };

  const handleSelect = async (chat) => {

    const userChats = chats.map(item => {
      const {userFire, ...rest} = item;
      return rest;
    })

    const chatIndex = userChats.findIndex( item => item.chatId === chat.chatId)
    userChats[chatIndex].isSeen = true;

    const userChatRef = doc(db, "userChats", currentUser.id);
    try{
      await updateDoc(userChatRef,{
        chats: userChats,
      });

       changeChat(chat.chatId, chat.userFire);

    }catch(err){
      console.log(err);
    }

    // const userChatRef = doc(db, "userChats", currentUser.id);
    // const userChatsSnapshot = await getDoc(userChatRef);
    // if (userChatsSnapshot.exists()) {
    //   const userChatsData = userChatsSnapshot.data();
    //   const chatIndex = userChatsData.chats.findIndex((c) => c.chatId === chatId);

    //   userChatsData.chats[chatIndex].isSeen = true;
     
    //   await updateDoc(userChatRef, {
    //     chats: userChatsData.chats,
    //   });
    // }

  }

  const filteredChats = chats.filter( c => c.userFire.name.toLowerCase().includes(input.toLowerCase()));


  return (
    <div className="chatList">
      <div className="searchContainer">
        <div className="searchBar">
          <i className="bi bi-search"></i>
          <input type="text" placeholder="Search" onChange={(e) => setInput(e.target.value)} />
        </div>
        <button className="addChatBtn" title={addMode ? "Close add mode" : "Add new chat"} onClick={toggleAddMode}>
          <i className={addMode ? "bi bi-dash" : "bi bi-plus"}></i>
        </button>
      </div>

      {/* Nếu cần hiển thị phần thêm chat khi addMode = true thì thêm ở đây */}
      {addMode && (<AddUser />
      )}
      {filteredChats.map((chat) => (
        <div className="item" key={chat.chatId}
          onClick={() => handleSelect(chat)}
          style={{
            backgroundColor: chat?.isSeen ? "transparent" : "#c2b6a4",
          }}

        >
          <img src={chat.userFire.avatar} alt="User avatar" />
          <div className="texts">
            <span>{chat.userFire.name}</span>
            <p>{chat.lastMessage}</p>
          </div>
        </div>
      ))}



    </div>
  );
};

export default ChatList;
