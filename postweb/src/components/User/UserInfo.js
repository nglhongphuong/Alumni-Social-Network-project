import { useContext } from 'react';
import { MyUserContext } from "../../configs/MyContexts";
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
import { useParams } from 'react-router-dom';


const UserInfo = () => {
    const defaultCover = "https://i.pinimg.com/736x/e0/42/0e/e0420e450a592b6a1f9bb4124f4867f1.jpg";
    const defaultAvatar = "https://i.pinimg.com/736x/97/c9/00/97c900fdd932d1517c10f6735ba95dfc.jpg";
    const { userId } = useParams();
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [user, setUser] = useState(null);
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

    const loadUserInfo = async () => {
             let u = await authApis().get(`${endpoints["user-info"]}${userId}`);
            setUser(u.data);
    }

    const loadPosts = async () => {
        try {
            setLoading(true);
       
   
            const token = cookie.load('token');
            const res = await Apis.get(`${endpoints['post']}?order=desc&userId=${userId}&page=${page}`, {
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

    useEffect(() => {
        loadUserInfo();
        if (page > 0)
            loadPosts();
        
    }, [ page]);

    useEffect(() => {
        setPage(1);
        setPosts([]);
    }, []);



    if (!user) return <p>Không tìm thấy thông tin người dùng!</p>;

    return (
        <div style={{ maxWidth: "900px", margin: "0 auto", padding: "0 12px" }}>
            <Card className="mb-4 shadow">
                <div style={{ position: 'relative' }}>
                    <Card.Img
                        variant="top"
                        src={user.coverPhoto || defaultCover}
                        alt="Cover"
                        style={{ height: '250px', objectFit: 'cover' }}
                    />

                </div>

                <Card.Body>
                    <Row className="align-items-center">
                        <Col md={3} className="text-center" style={{ position: 'relative' }}>
                            <img
                                src={user.avatar || defaultAvatar}
                                alt="Avatar"
                                style={{
                                    width: '150px',
                                    height: '150px',
                                    borderRadius: '50%',
                                    objectFit: 'cover',
                                    border: '4px solid white',
                                    marginTop: '-100px'
                                }}
                            />

                        </Col>

                        <Col md={9}>
                            <div className="d-flex justify-content-between align-items-start flex-wrap">
                                <h3>{user.name}</h3>

                            </div>
                            <p className="mb-1"><strong>Giới tính:</strong> {user.gender === 'MALE' ? 'Nam' : 'Nữ'}</p>
                            <p className="mb-1"><strong>Ngày sinh:</strong> {moment(user.birthday).format('DD/MM/YYYY')}</p>
                            <p className="mb-1"><strong>Email:</strong> {user.username}</p>
                            <p className="mb-0"><strong>Bio:</strong> {user.bio}</p>
                            <p className="mb-0"><strong>Role:</strong> {user.role}</p>
                        </Col>
                    </Row>
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
                                />
                            </Card.Header>
                            <Card.Body>
                                <Card.Text>{p.content}</Card.Text>
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

export default UserInfo;