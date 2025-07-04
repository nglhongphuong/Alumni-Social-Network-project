import React, { useState, useContext, useEffect } from "react";
import { Button, Form, ListGroup, Spinner } from "react-bootstrap";
import moment from "moment";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/MyContexts";
import MySpinner from "../layouts/MySpinner";

moment.locale("vi");

const Comment = ({ postId, isCommentLocked, postUser }) => {
    const defaultAvatar = "https://i.pinimg.com/736x/97/c9/00/97c900fdd932d1517c10f6735ba95dfc.jpg";
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [showComments, setShowComments] = useState(false);  // thêm state để toggle
    const user = useContext(MyUserContext);
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingContent, setEditingContent] = useState("");

    const loadComments = async () => {
        try {
            setLoading(true);
            const res = await authApis().get(endpoints["post"], { params: { postId } });
            const updatedPost = res.data[0];
            setComments(updatedPost.comments || []);
        } catch (err) {
            console.error("Lỗi tải bình luận:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (showComments) {
            loadComments();  
        }
    }, [postId, showComments]);

    const uploadComment = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;

        setSubmitting(true);
        try {
            await authApis().post(`${endpoints["post"]}${postId}/comment`, { content: newComment });
            setNewComment("");
            await loadComments(); 
        } catch (err) {
            console.error("Lỗi gửi bình luận:", err);
        } finally {
            setSubmitting(false);
        }
    };

    const handleDelete = async (commentId) => {
        if (!window.confirm("Bạn có chắc chắn muốn xoá bình luận này không?")) return;

        try {
            await authApis().delete(`${endpoints["post"]}${postId}/comment/${commentId}`);
            await loadComments();
        } catch (err) {
            console.error("Lỗi xoá bình luận:", err);
        }
    };
    const handleEditStart = (commentId, content) => {
        setEditingCommentId(commentId);
        setEditingContent(content);
    };

    const handleEditCancel = () => {
        setEditingCommentId(null);
        setEditingContent("");
    };

    const handleEditSave = async (commentId) => {
        if (!editingContent.trim()) return;

        try {
            setSubmitting(true);
            await authApis().put(`${endpoints["post"]}${postId}/comment/${commentId}`, { content: editingContent });
            setEditingCommentId(null);
            setEditingContent("");
            await loadComments();
        } catch (err) {
            console.error("Lỗi sửa bình luận:", err);
        } finally {
            setSubmitting(false);
        }
    };

   return (
    <div className="mt-3">
      <Button
        variant="outline-primary"
        size="sm"
        onClick={() => setShowComments(!showComments)}
      >
        {showComments ? "Đóng bình luận" : "Bình luận"}
      </Button>

      {showComments && (
        <>
          {!isCommentLocked && (
            <Form onSubmit={uploadComment} className="mt-3">
              <Form.Group>
                <Form.Control
                  as="textarea"
                  rows={2}
                  placeholder="Nhập bình luận..."
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                />
              </Form.Group>
              <Button
                type="submit"
                variant="primary"
                size="sm"
                disabled={submitting}
                className="mt-2"
              >
                {submitting ? "Đang gửi..." : "Gửi"}
              </Button>
            </Form>
          )}

          {loading ? (
            <div className="text-center mt-3"><Spinner animation="border" /></div>
          ) : (
            <ListGroup variant="flush" className="mt-3">
              {comments.length === 0 ? (
                <div className="text-muted">Chưa có bình luận nào.</div>
              ) : (
                comments.map((cmt) => (
                  <ListGroup.Item key={cmt.commentId}>
                    <div className="d-flex justify-content-between align-items-start" >
                      <div className="d-flex">
                        <img
                          src={cmt.avatar || defaultAvatar}
                          alt="avatar"
                          className="me-2 rounded-circle"
                          width={40}
                          height={40}
                        />
                        <div>
                          <strong>{cmt.name}</strong>

                          {editingCommentId === cmt.commentId ? (
                            <>
                              <Form.Control
                                as="textarea"
                                rows={2}
                                value={editingContent}
                                onChange={(e) => setEditingContent(e.target.value)}
                                disabled={submitting}
                              />
                              <div className="mt-1">
                                <Button
                                  variant="success"
                                  size="sm"
                                  onClick={() => handleEditSave(cmt.commentId)}
                                  disabled={submitting}
                                >
                                  Lưu
                                </Button>{" "}
                                <Button
                                  variant="secondary"
                                  size="sm"
                                  onClick={handleEditCancel}
                                  disabled={submitting}
                                >
                                  Hủy
                                </Button>
                              </div>
                            </>
                          ) : (
                            <>
                              <div>{cmt.content}</div>
                              <small className="text-muted">
                                {moment(cmt.createdAt, "DD-MM-YYYY HH:mm:ss").fromNow()}
                              </small>
                            </>
                          )}
                        </div>
                      </div>

                      {(user.username === cmt.user || user.username === postUser || user.role === "ROLE_ADMIN") && editingCommentId !== cmt.commentId && (
                        <div>
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => handleDelete(cmt.commentId)}
                            className="me-1"
                          >
                            Xoá
                          </Button>
                          {user.username === cmt.user && (
                            <Button
                              variant="outline-primary"
                              size="sm"
                              onClick={() => handleEditStart(cmt.commentId, cmt.content)}
                            >
                              Sửa
                            </Button>
                          )}
                        </div>
                      )}
                    </div>
                  </ListGroup.Item>
                ))
              )}
            </ListGroup>
          )}
          {loading && <MySpinner />}
        </>
      )}
    </div>
  );
};

export default Comment;
