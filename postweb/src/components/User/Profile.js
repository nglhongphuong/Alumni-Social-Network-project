import { useContext } from 'react';
import { MyUserContext, MyDispatcherContext } from "../../configs/MyContexts";
import moment from 'moment';
import { Container, Row, Col, Card, Button, Image } from 'react-bootstrap';
import { PencilSquare } from 'react-bootstrap-icons'; // icon chỉnh sửa
import { useEffect, useState } from "react";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import cookie from 'react-cookies';
import MySpinner from "../layouts/MySpinner";
import PostDropdownMenu from "../reaction/PostDropdownMenu ";
import ReactionsDisplay from '../reaction/ReactionsDisplay';
import ReactionButton from "../reaction/ReactionButton";
import Comment from "../reaction/Comment";
import { Form } from 'react-bootstrap';
import EditProfile from "./EditProfile";

const Profile = () => {
    const defaultCover = "https://i.pinimg.com/736x/e0/42/0e/e0420e450a592b6a1f9bb4124f4867f1.jpg";
    const defaultAvatar = "https://i.pinimg.com/736x/97/c9/00/97c900fdd932d1517c10f6735ba95dfc.jpg";

    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const user = useContext(MyUserContext);
    const dispatch = useContext(MyDispatcherContext);

    const [newContent, setNewContent] = useState('');
    const [newImage, setNewImage] = useState(null);
    const [visibility, setVisibility] = useState('public');
    const [isCommentLocked, setIsCommentLocked] = useState(false);
    const [postMessage, setPostMessage] = useState('');

    const [editingPostId, setEditingPostId] = useState(null);
    const [editContent, setEditContent] = useState('');
    const [editImage, setEditImage] = useState(null);
    const [editVisibility, setEditVisibility] = useState('public');
    const [editIsCommentLocked, setEditIsCommentLocked] = useState(false);
    const [editPostMessage, setEditPostMessage] = useState('');
    const [userReactions, setUserReactions] = useState({});
    const [editingPost, setEditingPost] = useState(null);
    const [editedContent, setEditedContent] = useState("");
    const [editedImage, setEditedImage] = useState(null);

    const [isEditing, setIsEditing] = useState(false);



    const loadPosts = async () => {
        try {
            setLoading(true);

            const token = cookie.load('token');
            const res = await Apis.get(`${endpoints['post']}?order=desc&userId=${user.id}&page=${page}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (res.data.length === 0)
                setPage(0);
            else {
                if (page === 1)
                    setPosts(res.data);
                else
                    setPosts(prev => [...prev, ...res.data]);
            }
            loadUserReactions(res.data);
        } catch (err) {
            console.error("Lỗi load post:", err);
        } finally {
            setLoading(false);
        }
    };
    const loadUserReactions = async (posts) => {
        setUserReactions({});
        const token = cookie.load('token');
        try {
            const results = await Promise.all(posts.map(post =>
                Apis.get(`${endpoints['post']}${post.postId}/reaction`, {
                    headers: { Authorization: `Bearer ${token}` }
                }).then(res => ({ postId: post.postId, reactionType: res.data.reactionType || null }))
                    .catch(() => ({ postId: post.postId, reactionType: null }))
            ));

            const reactionsMap = {};
            results.forEach(({ postId, reactionType }) => {
                reactionsMap[postId] = reactionType;
            });
            setUserReactions(reactionsMap);

        } catch (error) {
            console.error("Error loading user reactions:", error);
        }
    };

    const handleStartEditing = (post) => {
        setEditingPostId(post.postId);
        setEditContent(post.content);
        setEditImage(post.image);
        setEditVisibility(post.visibility);
        setEditIsCommentLocked(post.isCommentLocked);
        setEditPostMessage('');
    };

    const handleCancelEditing = () => {
        setEditingPostId(null);
        setEditPostMessage('');
        setEditImage(null);
    };

    const handleUpdatePost = async (postId) => {
        if (!editContent.trim()) {
            setEditPostMessage('Nội dung không được để trống!');
            return;
        }

        try {
            setLoading(true);
            const token = cookie.load('token');

            const formData = new FormData();
            formData.append("content", editContent);
            if (editImage) formData.append("file", editImage);
            formData.append("visibility", editVisibility);
            formData.append("isCommentLocked", editIsCommentLocked);

            await Apis.put(`${endpoints['post']}${postId}`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    Authorization: `Bearer ${token}`
                }
            });

            // setPosts(posts.map(p => p.postId === postId ? {
            //     ...p,
            //     content: editContent,
            //     image: editImage ? URL.createObjectURL(editImage) : p.image, 
            //     visibility: editVisibility,
            //     isCommentLocked: editIsCommentLocked
            // } : p));

            setEditingPostId(null);
            setEditPostMessage('');
            setEditImage(null);
            loadPosts();
        } catch (err) {
            console.error("Lỗi khi cập nhật bài viết:", err);
            setEditPostMessage("Đã có lỗi xảy ra!");
        } finally {
            setLoading(false);
        }
    };


    const handlePostSubmit = async (e) => {
        e.preventDefault();
        setPostMessage('');
        setNewContent('');
        setNewImage(null);
        setVisibility('public');
        setIsCommentLocked(false);

        if (!newContent.trim()) {
            setPostMessage('Nội dung không được để trống!');
            return;
        }

        try {
            setLoading(true);
            const token = cookie.load('token');

            const formData = new FormData();
            formData.append("content", newContent);
            if (newImage) formData.append("file", newImage);
            formData.append("visibility", visibility);
            formData.append("isCommentLocked", isCommentLocked);
            const res = await Apis.post(endpoints['post'], formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    Authorization: `Bearer ${token}`
                }
            });
            setPage(1);
            setPosts([]);
            loadPosts();
        } catch (err) {
            console.error("Lỗi khi đăng bài:", err);
            setPostMessage("Đã có lỗi xảy ra!");
        } finally {
            setLoading(false);
        }
    };
    const handleTogglePostAttribute = async (postId, attribute, value) => {
        try {
            const token = cookie.load('token');

            const formData = new FormData();
            formData.append(attribute, value);

            await Apis.put(`${endpoints['post']}${postId}`, formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "multipart/form-data"
                }
            });

            setPosts(posts.map(p => p.postId === postId ? { ...p, [attribute]: value } : p));
        } catch (error) {
            const message = error?.response?.data || "Có lỗi xảy ra khi xóa bài viết!";
            alert(message);
        }
    };

    const handleDeletePost = async (postId) => {
        try {
            const token = cookie.load('token');
            await Apis.delete(`${endpoints['post']}${postId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setPosts(posts.filter(p => p.postId !== postId));
        } catch (error) {
            const message = error?.response?.data || "Có lỗi xảy ra khi xóa bài viết!";
            alert(message);
        }
    };
    const handleEditClick = (post) => {
        setEditingPost(post);
        setEditedContent(post.content);
    };

    const handleProfileUpdated = async () => {
        const res = await authApis().put(`${endpoints['current-user']}`);
        dispatch({
            type: "update-profile",
            payload: res.data
        });
        console.log("hahahaha: ", res.data);
        setIsEditing(false);
    };



    useEffect(() => {
        if (user && page > 0)
            loadPosts();
    }, [user, page]);

    useEffect(() => {
        setPage(1);
        setPosts([]);
    }, [user]);



    if (!user) return <p>Không tìm thấy thông tin người dùng!</p>;

    return (
        <div style={{ maxWidth: "900px", margin: "0 auto", padding: "0 12px" }}>
            {!isEditing ? (
                <Card className="mb-4 shadow">
                    <div style={{ height: "250px", overflow: "hidden" }}>
                        <Card.Img
                            variant="top"
                            src={user.coverPhoto || defaultCover}
                            style={{
                                objectFit: "cover",
                                width: "100%",
                                height: "100%",
                            }}
                        />
                    </div>

                    <Card.Body>
                        <Row className="align-items-center">
                            <Col md={3} className="text-center" style={{ position: "relative" }}>
                                <img
                                    src={user.avatar || defaultAvatar}
                                    alt="Avatar"
                                    style={{
                                        width: "150px",
                                        height: "150px",
                                        borderRadius: "50%",
                                        objectFit: "cover",
                                        border: "4px solid white",
                                        marginTop: "-100px",
                                    }}
                                />
                            </Col>

                            <Col md={9}>
                                <div className="d-flex justify-content-between align-items-start flex-wrap">
                                    <h3>{user.name}</h3>
                                    <Button variant="outline-primary" size="sm" onClick={() => setIsEditing(true)}>
                                        <PencilSquare /> Chỉnh sửa thông tin
                                    </Button>
                                </div>
                                <p className="mb-1">
                                    <strong>Giới tính:</strong> {user.gender === "MALE" ? "Nam" : "Nữ"}
                                </p>
                                <p className="mb-1">
                                    <strong>Ngày sinh:</strong>{" "}
                                    {user.birthday ? moment(user.birthday).format("DD/MM/YYYY") : "Chưa có"}
                                </p>
                                <p className="mb-1">
                                    <strong>Email:</strong> {user.username}
                                </p>
                                <p className="mb-1">
                                    <strong>Bio:</strong> {user.bio || "Chưa có mô tả"}
                                </p>
                                <p className="mb-0">
                                    <strong>Vai trò:</strong> {user.role}
                                </p>
                            </Col>
                        </Row>
                    </Card.Body>
                </Card>
            ) : (
                <Card className="p-4 shadow">
                    <h4>Chỉnh sửa thông tin cá nhân</h4>
                    <EditProfile
                        user={user}
                        onUpdate={handleProfileUpdated}
                        onCancel={() => setIsEditing(false)}
                    />
                </Card>
            )}


            <Card className="shadow-sm mb-4">
                <Card.Body>
                    <form onSubmit={handlePostSubmit}>
                        <textarea
                            className="form-control mb-2"
                            placeholder="Hôm nay bạn cảm thấy thế nào?"
                            value={newContent}
                            onChange={(e) => setNewContent(e.target.value)}
                            rows={3}
                        />

                        <input
                            type="file"
                            accept="image/*"
                            className="form-control mb-2"
                            onChange={(e) => setNewImage(e.target.files[0])}
                        />

                        <select
                            className="form-select mb-2"
                            value={visibility}
                            onChange={(e) => setVisibility(e.target.value)}
                        >
                            <option value="public">Công khai</option>
                            <option value="private">Chỉ mình tôi</option>
                        </select>

                        <div className="form-check mb-2">
                            <input
                                type="checkbox"
                                className="form-check-input"
                                id="commentLocked"
                                checked={isCommentLocked}
                                onChange={(e) => setIsCommentLocked(e.target.checked)}
                            />
                            <label className="form-check-label" htmlFor="commentLocked">
                                Khóa bình luận
                            </label>
                        </div>

                        {postMessage && <div className="text-success mb-2">{postMessage}</div>}

                        <Button type="submit" variant="success">Đăng bài</Button>
                    </form>
                </Card.Body>
            </Card>

            <h4 className="mt-5 mb-3">📝 Bài viết đã đăng</h4>
            {posts.length === 0 && <p>Chưa có bài viết nào!</p>}
            <div style={{ maxWidth: "700px", margin: "0 auto", padding: "0 12px" }}>
                <div className="d-flex flex-column gap-4">
                    {posts.map(p => (
                        <Card key={p.postId} className="mb-3 shadow-sm">
                            <Card.Header className="d-flex align-items-center bg-white rounded-top-4 justify-content-between">
                                <div className="d-flex align-items-center">
                                    <Image
                                        src={p.avatar || defaultAvatar}
                                        roundedCircle
                                        width={40}
                                        height={40}
                                        className="me-2"
                                    />
                                    <div>
                                        <strong>{p.name}</strong>
                                        <div className="text-muted" style={{ fontSize: '0.8rem' }}>
                                            {moment(p.createdAt, "DD-MM-YYYY HH:mm:ss").fromNow()}
                                        </div>
                                    </div>
                                </div>

                                <PostDropdownMenu
                                    post={p}
                                    handleDeletePost={handleDeletePost}
                                    handleTogglePostAttribute={handleTogglePostAttribute}
                                    handleEditClick={handleEditClick}
                                />
                            </Card.Header>

                            <Card.Body>
                                {editingPost?.postId === p.postId ? (
                                    <>
                                        {loading && <MySpinner />}

                                        <Form.Control
                                            as="textarea"
                                            rows={3}
                                            value={editedContent}
                                            onChange={(e) => setEditedContent(e.target.value)}
                                            disabled={loading}
                                        />

                                        <Form.Group controlId="formFile" className="mt-2">
                                            <Form.Label>Chọn ảnh mới (nếu có):</Form.Label>
                                            <Form.Control
                                                type="file"
                                                accept="image/*"
                                                onChange={(e) => setEditedImage(e.target.files[0])}
                                                disabled={loading}
                                            />
                                        </Form.Group>

                                        <div className="mt-2 d-flex gap-2">
                                            <Button
                                                variant="primary"
                                                size="sm"
                                                disabled={loading}
                                                onClick={async () => {
                                                    try {
                                                        setLoading(true); // 🔄 Bắt đầu loading
                                                        const formData = new FormData();
                                                        formData.append("content", editedContent);
                                                        if (editedImage) {
                                                            formData.append("file", editedImage);
                                                        }

                                                        await authApis().put(`${endpoints['post']}${p.postId}`, formData);

                                                        setPosts(prev =>
                                                            prev.map(post =>
                                                                post.postId === p.postId
                                                                    ? {
                                                                        ...post,
                                                                        content: editedContent,
                                                                        image: editedImage ? URL.createObjectURL(editedImage) : post.image
                                                                    }
                                                                    : post
                                                            )
                                                        );
                                                        setEditingPost(null);
                                                        setEditedImage(null);
                                                    } catch (err) {
                                                        console.error("Lỗi khi cập nhật bài viết:", err);
                                                    } finally {
                                                        setLoading(false); // ✅ Kết thúc loading
                                                    }
                                                }}
                                            >
                                                Lưu
                                            </Button>
                                            <Button
                                                variant="secondary"
                                                size="sm"
                                                onClick={() => {
                                                    setEditingPost(null);
                                                    setEditedImage(null);
                                                }}
                                                disabled={loading}
                                            >
                                                Hủy
                                            </Button>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        {p.image && (
                                            <Card.Img
                                                variant="top"
                                                src={p.image}
                                                style={{ maxHeight: '400px', objectFit: 'cover' }}
                                            />
                                        )}
                                        <Card.Text>{p.content}</Card.Text>
                                    </>
                                )}


                                <ReactionsDisplay reactions={p.reactions} totalComments={p.totalComments} />
                                <div className="action-buttons">
                                    <ReactionButton
                                        postId={p.postId}
                                        currentReaction={userReactions[p.postId] || null}
                                        onReact={async (type) => {
                                            try {
                                                const token = cookie.load("token");
                                                await Apis.post(`${endpoints['post']}${p.postId}/reaction`, { reactType: type }, {
                                                    headers: { Authorization: `Bearer ${token}` }
                                                });
                                                const res = await authApis().get(`${endpoints['post']}?postId=${p.postId}`);
                                                const updatedPost = res.data[0];
                                                const updatedReactions = updatedPost.reactions;

                                                setPosts(prevPosts =>
                                                    prevPosts.map(post =>
                                                        post.postId === p.postId ? { ...post, reactions: updatedReactions } : post
                                                    )
                                                );
                                                setUserReactions(prev => ({
                                                    ...prev,
                                                    [p.postId]: type
                                                }));
                                            } catch (err) {
                                                console.error("Lỗi khi gửi reaction:", err);
                                            }
                                        }}
                                        onRemoveReaction={async () => {
                                            try {
                                                const token = cookie.load("token");
                                                await Apis.delete(`${endpoints['post']}${p.postId}/reaction`, {
                                                    headers: { Authorization: `Bearer ${token}` }
                                                });
                                                const res = await authApis().get(`${endpoints['post']}?postId=${p.postId}`);
                                                const updatedPost = res.data[0];
                                                const updatedReactions = updatedPost.reactions;

                                                setPosts(prevPosts =>
                                                    prevPosts.map(post =>
                                                        post.postId === p.postId ? { ...post, reactions: updatedReactions } : post
                                                    )
                                                );
                                                setUserReactions(prev => ({
                                                    ...prev,
                                                    [p.postId]: null
                                                }));
                                            } catch (err) {
                                                console.error("Lỗi khi xoá reaction:", err);
                                            }
                                        }}
                                    />
                                    <button
                                        className="share-button"
                                        onClick={() => {
                                            const url = encodeURIComponent(window.location.origin + `/post/${p.postId}`);
                                            const facebookShareUrl = `https://www.facebook.com/sharer/sharer.php?u=${url}`;
                                            window.open(facebookShareUrl, '_blank', 'width=600,height=400');
                                        }}
                                    >
                                        Chia sẻ
                                    </button>
                                </div>
                                <Comment
                                    postId={p.postId}
                                    isCommentLocked={p.isCommentLocked}
                                    postUser={p.username}
                                //xử lý cập nhập reactionDisplay áá
                                />

                            </Card.Body>

                        </Card>
                    ))}
                </div>

                {loading && <MySpinner />}
                {page > 0 && posts.length > 0 && (
                    <div className="text-center mb-3">
                        <Button onClick={() => setPage(page + 1)}>Xem thêm bài viết...</Button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Profile;
