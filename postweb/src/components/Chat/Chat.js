import './Chat.css';
import List from '../List/List';
import Detail from '../Detail/Detail';
import Message from '../Message/Message';
import { onAuthStateChanged } from "firebase/auth";
import { auth } from "../../lib/firebase";
import { useUserStore } from "../../lib/userStore";
import { useChatStore } from '../../lib/chatStore';
import React, { useState, useContext, useEffect } from "react";
import { MyUserContext } from "../../configs/MyContexts";


const Chat = () => {
  const user = useContext(MyUserContext);
  const { currentUser, isLoading, fetchUserInfo } = useUserStore();
 const {chatId } = useChatStore();

  //==== test authauth for firebase 
  useEffect(() => {
    const unSub = onAuthStateChanged(auth, (userFireBase) => {
      fetchUserInfo(userFireBase.uid);
      console.log("user firebase ", userFireBase);
    });

    return () => {
      unSub();
    }
  }, [fetchUserInfo]);

  console.log("currrent: ", currentUser);

  if (isLoading) return <div className="loading">loading...</div>

  return (
    <div className="chat-background">
      <div className="chat-container">
        <List />
      {chatId &&  <Message />}
     {chatId &&   <Detail />}
      </div>
    </div>
  );
};

export default Chat;
