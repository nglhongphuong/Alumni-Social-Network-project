import "./UserInfo.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import avatar from "../../../img/avatar.webp";
import { useUserStore } from "../../../lib/userStore";
import { MyUserContext } from "../../../configs/MyContexts";
import React, { useState, useContext, useEffect } from "react";


const UserInfo = () => {
  const user = useContext(MyUserContext);
  const { currentUser } = useUserStore();

  return (
    <div className="userInfo">
      {/* Bên trái: avatar + tên */}
      <div className="user">
        <img src={user.avatar || avatar} alt="User avatar" />
        <h2>{currentUser.name}</h2>
      </div>

      {/* Bên phải: các icon */}
      <div className="icons">
        <i className="bi bi-three-dots"></i>
        <i className="bi bi-camera-video"></i>
        <i className="bi bi-pencil"></i>
      </div>
    </div>
  );
};

export default UserInfo;
