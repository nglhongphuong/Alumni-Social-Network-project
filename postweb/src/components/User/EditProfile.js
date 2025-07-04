import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";
import cookie from "react-cookies";
import moment from "moment";
import Apis, {authApis, endpoints } from "../../configs/Apis";

const EditProfile = ({ user, onUpdate, onCancel }) => {
  const [avatarFile, setAvatarFile] = useState(null);
  const [coverFile, setCoverFile] = useState(null);
  const [bio, setBio] = useState(user.bio || "");
  const [birthday, setBirthday] = useState(
    user.birthday ? moment(user.birthday).format("YYYY-MM-DD") : ""
  );
  const [gender, setGender] = useState(user.gender || "MALE");
  const [name, setName] = useState(user.name || "");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");


  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");
    try {
      const token = cookie.load("token");
      const formData = new FormData();
      formData.append("name", name);
      formData.append("bio", bio);
      formData.append("birthday", birthday);
      formData.append("gender", gender);
      if (avatarFile) formData.append("avatar", avatarFile);
      if (coverFile) formData.append("coverPhoto", coverFile);

      const res = await  authApis().put(`${endpoints['current-user']}`, formData);

      onUpdate(res.data); // gửi dữ liệu user mới về
    } catch (error) {
      setMessage("Cập nhật thất bại!");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Form.Group>
        <Form.Label>Tên</Form.Label>
        <Form.Control value={name} onChange={(e) => setName(e.target.value)} />
      </Form.Group>
      <Form.Group>
        <Form.Label>Bio</Form.Label>
        <Form.Control
          as="textarea"
          rows={3}
          value={bio}
          onChange={(e) => setBio(e.target.value)}
        />
      </Form.Group>
      <Form.Group>
        <Form.Label>Ngày sinh</Form.Label>
        <Form.Control
          type="date"
          value={birthday}
          onChange={(e) => setBirthday(e.target.value)}
        />
      </Form.Group>
      <Form.Group>
        <Form.Label>Giới tính</Form.Label>
        <Form.Select value={gender} onChange={(e) => setGender(e.target.value)}>
          <option value="MALE">Nam</option>
          <option value="FEMALE">Nữ</option>
        </Form.Select>
      </Form.Group>
      <Form.Group>
        <Form.Label>Ảnh đại diện</Form.Label>
        <Form.Control
          type="file"
          accept="image/*"
          onChange={(e) => setAvatarFile(e.target.files[0])}
        />
      </Form.Group>
      <Form.Group>
        <Form.Label>Ảnh bìa</Form.Label>
        <Form.Control
          type="file"
          accept="image/*"
          onChange={(e) => setCoverFile(e.target.files[0])}
        />
      </Form.Group>

      {message && <p style={{ color: "red" }}>{message}</p>}

      <Button variant="primary" type="submit" disabled={loading}>
        {loading ? "Đang cập nhật..." : "Lưu"}
      </Button>
      <Button variant="secondary" onClick={onCancel} disabled={loading} className="ms-2">
        Hủy
      </Button>
    </Form>
  );
};

export default EditProfile;
