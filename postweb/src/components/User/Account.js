import React, { useState } from 'react';
import { MyUserContext } from "../../configs/MyContexts";
import { useContext } from 'react';
import cookie from 'react-cookies';
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { getAuth, updatePassword } from "firebase/auth";
import { useUserStore } from "../../lib/userStore";

const Account = () => {
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const user = useContext(MyUserContext);
    const [loading, setLoading] = useState(false);


    const handleChangePassword = async (e) => {
        e.preventDefault();
        setMessage(null);

        if (newPassword !== confirmPassword) {
            setMessage('Mật khẩu mới và xác nhận không khớp.');
            return;
        }

        try {
            setLoading(true);
            //authApis().get(endpoints["current-user"]);
            const res = await authApis().put(endpoints['change-password'], {
                password: newPassword
            });

            if (res.status === 200 || res.status === 204) {
                setMessage('Đổi mật khẩu thành công!');
            } else {
                setMessage('Có lỗi xảy ra khi đổi mật khẩu.');
            }

            //FIRE BASSE =========
            const auth = getAuth();
            const firebaseUser = auth.currentUser;
            await updatePassword(firebaseUser, newPassword);
            setMessage('Đổi mật khẩu firebase thành công!');
            //===================================

        } catch (err) {
            console.error(err);
            if (err.response && err.response.data && err.response.data.message) {
                setMessage(err.response.data.message);
            } else {
                setMessage('Lỗi hệ thống, vui lòng thử lại sau.');
            }
        } finally {
            setLoading(false);
        }
    };


    return (
        <div style={{
            maxWidth: "500px",
            margin: "auto",
            padding: "24px",
            borderRadius: "12px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
            backgroundColor: "#fff",
            fontFamily: "Arial, sans-serif"
        }}>
            <h2 style={{ textAlign: "center", marginBottom: "24px" }}>Thay đổi mật khẩu</h2>

            <form onSubmit={handleChangePassword} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>

                <input
                    type="password"
                    placeholder="Mật khẩu mới"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    required
                    style={inputStyle}
                />
                <input
                    type="password"
                    placeholder="Xác nhận mật khẩu mới"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    style={inputStyle}
                />
                <button type="submit" style={buttonStyle}>Đổi mật khẩu</button>
                {message && <p style={{ color: 'green', textAlign: 'center' }}>{message}</p>}
            </form>
        </div>
    );
};


const inputStyle = {
    padding: "10px 14px",
    borderRadius: "8px",
    border: "1px solid #ccc",
    fontSize: "16px"
};


const buttonStyle = {
    padding: "12px",
    backgroundColor: "#4CAF50",
    color: "#fff",
    fontWeight: "bold",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "16px",
    transition: "0.3s",
};

export default Account;
