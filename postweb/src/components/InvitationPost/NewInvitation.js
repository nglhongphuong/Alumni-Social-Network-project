import { useState } from "react";
import { Button, Card, Form, Alert, Spinner } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";

const NewInvitation = () => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [recipientScope, setRecipientScope] = useState("ALL");
  const [username, setUsername] = useState("");
  const [role, setRole] = useState("");
  const [userIds, setUserIds] = useState("");
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setLoading(true);
    const payload = { title, content, recipientScope };

    if (recipientScope === "INDIVIDUAL") {
      payload.username = username;
    } else if (recipientScope === "ROLE_GROUP") {
      payload.role = role;
    } else if (recipientScope === "CUSTOM_GROUP") {
      payload.user = userIds;
    }

    try {
      await authApis().post(`${endpoints['invitation']}`, payload);
      setMessage("Thư mời đã được gửi thành công!");
      setError(null);
      setTitle("");
      setContent("");
      setRecipientScope("ALL");
      setUsername("");
      setRole("");
      setUserIds("");
    } catch (err) {
      setMessage(null);
      setError(err?.response?.data || "Không thể gửi thư mời.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-start" style={{ minHeight: "100vh", marginTop: 0 }}>
      <div style={{ width: "100%", maxWidth: "600px", padding: "20px" }}>
        <Card style={{ border: "none", boxShadow: "none" }}>
          <Card.Body>
            <h2 className="mb-4 text-center">Tạo Thư Mời</h2>

            {message && <Alert variant="success">{message}</Alert>}
            {error && <Alert variant="danger">{error}</Alert>}

            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Tiêu đề</Form.Label>
                <Form.Control
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Nhập tiêu đề thư"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Nội dung</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={4}
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="Nhập nội dung thư"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Đối tượng nhận</Form.Label>
                <div>
                  <Form.Check
                    type="radio"
                    label="Tất cả người dùng"
                    value="ALL"
                    checked={recipientScope === "ALL"}
                    onChange={(e) => setRecipientScope(e.target.value)}
                  />
                  <Form.Check
                    type="radio"
                    label="Cá nhân (gmail)"
                    value="INDIVIDUAL"
                    checked={recipientScope === "INDIVIDUAL"}
                    onChange={(e) => setRecipientScope(e.target.value)}
                  />
                  <Form.Check
                    type="radio"
                    label="Theo vai trò (Role)"
                    value="ROLE_GROUP"
                    checked={recipientScope === "ROLE_GROUP"}
                    onChange={(e) => setRecipientScope(e.target.value)}
                  />
                  <Form.Check
                    type="radio"
                    label="Nhóm tùy chọn (nhập ID người dùng)"
                    value="CUSTOM_GROUP"
                    checked={recipientScope === "CUSTOM_GROUP"}
                    onChange={(e) => setRecipientScope(e.target.value)}
                  />
                </div>
              </Form.Group>

              {recipientScope === "INDIVIDUAL" && (
                <Form.Group className="mb-3">
                  <Form.Label>Gmail</Form.Label>
                  <Form.Control
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="ví dụ: user123"
                  />
                </Form.Group>
              )}

              {recipientScope === "ROLE_GROUP" && (
                <Form.Group className="mb-3">
                  <Form.Label>Vai trò</Form.Label>
                  <Form.Select value={role} onChange={(e) => setRole(e.target.value)}>
                    <option value="">-- Chọn vai trò --</option>
                    <option value="ROLE_ALUMNI">Cựu sinh viên</option>
                    <option value="ROLE_ADMIN">Quản trị viên</option>
                    <option value="ROLE_LECTURE">Giảng viên</option>
                  </Form.Select>
                </Form.Group>
              )}

              {recipientScope === "CUSTOM_GROUP" && (
                <Form.Group className="mb-3">
                  <Form.Label>ID người dùng (phân cách dấu phẩy)</Form.Label>
                  <Form.Control
                    type="text"
                    value={userIds}
                    onChange={(e) => setUserIds(e.target.value)}
                    placeholder="ví dụ: 1,2,3"
                  />
                </Form.Group>
              )}

              <div className="text-center">
                <Button onClick={handleSubmit} disabled={loading}>
                  {loading ? (
                    <>
                      <Spinner animation="border" size="sm" /> Đang gửi...
                    </>
                  ) : (
                    "Gửi Thư Mời"
                  )}
                </Button>
              </div>
            </Form>
          </Card.Body>
        </Card>
      </div>
    </div>
  );
};

export default NewInvitation;
