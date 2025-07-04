import {
  collection,
  query,
  where,
  getDocs,
  serverTimestamp,
  updateDoc,
  arrayUnion,
  doc,
  setDoc
} from "firebase/firestore";

import "./AddUser.css";
import { db } from "../../../lib/firebase";
import { MyUserContext } from "../../../configs/MyContexts";
import React, { useState, useContext } from "react";
import { useUserStore } from "../../../lib/userStore";

const AddUser = () => {
    const [userFire, setUserFire] = useState(null);
    const user = useContext(MyUserContext);
    const {currentUser} = useUserStore();

    const handleAdd = async () => {
        const chatRef = collection(db, "chats");
        const userChatRef = collection(db, "userChats");

        try {
            const newChatRef = doc(chatRef);

            await setDoc(newChatRef, {
                createdAt: serverTimestamp(),
                messages: [],
            });

            await updateDoc(doc(userChatRef, userFire.id),{
                chats: arrayUnion({
                    chatId: newChatRef.id,
                    lastMessage: "",
                    receiverId: currentUser.id,
                    updatedAt: Date.now(),

                }),
            });

               await updateDoc(doc(userChatRef, currentUser.id),{
                chats: arrayUnion({
                    chatId: newChatRef.id,
                    lastMessage: "",
                    receiverId: userFire. id,
                    updatedAt: Date.now(),
                    
                })
            });

        }catch(err) {
            console.log(err);
        }
    };

    const handlerSearch = async (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);
        const username = formData.get("username");


        try {
            const userRef = collection(db, "users");
            const q = query(userRef, where("username", "==", username));

            const querySnapshot = await getDocs(q); 

            if (!querySnapshot.empty) {
                setUserFire(querySnapshot.docs[0].data());
            } else {
                setUserFire(null);
            }
        } catch (err) {
            console.error("Lỗi tìm user:", err);
        }
    };

    return (
        <div className="addUser">
            <form onSubmit={handlerSearch}>
                <input type="text" placeholder="Username" name="username" />
                <button>Search</button>
            </form>

            {userFire && (
                <div className="user">
                    <div className="detail">
                        <img
                            src={userFire.avatar || "https://i.pinimg.com/736x/28/9e/fc/289efca5fb188ba7914d2b46fb028ded.jpg"}
                            alt="avatar"
                        />
                        <span>{userFire.name}</span>
                    </div>
                    <button onClick={handleAdd}>Add User</button>
                </div>
            )}
        </div>
    );
};

export default AddUser;
