import { useEffect, useState } from "react";
import { Alert, Button, Card, Image } from "react-bootstrap";
import { MyUserContext, MyDispatcherContext } from "../configs/MyContexts";
import { useContext } from 'react';
import Apis, { authApis, endpoints } from "../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "./layouts/MySpinner";
import cookie from 'react-cookies';
import moment from 'moment';
import 'moment/locale/vi';
import ReactionsDisplay from './reaction/ReactionsDisplay';
import ReactionButton from "./reaction/ReactionButton";
import PostDropdownMenu from "./reaction/PostDropdownMenu ";
import Comment from "./reaction/Comment";
import { Link } from "react-router-dom";

const Home = () => {
    moment.locale('vi');
    const defaultAvatar = "https://i.pinimg.com/736x/97/c9/00/97c900fdd932d1517c10f6735ba95dfc.jpg";
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();
    const [newContent, setNewContent] = useState('');
    const [newImage, setNewImage] = useState(null);
    const [visibility, setVisibility] = useState('public');
    const [isCommentLocked, setIsCommentLocked] = useState(false);
    const [postMessage, setPostMessage] = useState('');
    const [userReactions, setUserReactions] = useState({});
    const user = useContext(MyUserContext);
    const loadPosts = async () => {
        try {
            setLoading(true);

            let url = `${endpoints['post']}?order=desc&page=${page}`;

            let content = q.get("content");
            if (content)
                url += `&content=${content}`;

            let username = q.get("username");
            if (username)
                url += `&username=${username}`;

            let order = q.get("order");
            if (order)
                url += `&order=${order}`;

            const token = cookie.load('token');

            let res = await Apis.get(url, {
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

    const handlePostSubmit = async (e) => {
        e.preventDefault();
        setPostMessage('');
        setNewContent('');
        setNewImage('');
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



    useEffect(() => {
        if (page > 0)
            loadPosts();
    }, [page, q]);

    useEffect(() => {
        setPage(1);
        setPosts([]);
    }, [q]);

    const loadMore = () => {
        if (!loading && page > 0)
            setPage(page + 1);
    };
    return (
        <>
            {posts.length === 0 && <Alert variant="info" className="m-2">Không có bài viết nào!</Alert>}

            <div style={{ maxWidth: "700px", margin: "0 auto", padding: "0 12px" }}>

                <div className="d-flex flex-column gap-4">
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

                                <Button type="submit" variant="success" disabled={loading}>
                                    {loading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                            Đang đăng...
                                        </>
                                    ) : "Đăng bài"}
                                </Button>

                            </form>
                        </Card.Body>
                    </Card>

                    {posts
                        .filter(p => p.visibility !== "PRIVATE")
                        .map(p => (
                            <Card key={`post-${p.postId}`} className="shadow-sm">
                                <Card.Header className="d-flex align-items-center bg-white rounded-top-4 justify-content-between">
                                    <div className="d-flex align-items-center">
                                        <Link
                                            to={user?.username === p.username ? "/profile" : `/userInfo/${p.userId}`}
                                            className="d-flex align-items-center text-decoration-none text-dark"
                                        >
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
                                        </Link>
                                    </div>

                                    <PostDropdownMenu
                                        post={p}
                                        handleDeletePost={handleDeletePost}
                                        handleTogglePostAttribute={handleTogglePostAttribute}
                                    />
                                </Card.Header>

                                {p.image && (
                                    <Card.Img
                                        variant="top"
                                        src={p.image}
                                        style={{ maxHeight: '400px', objectFit: 'cover' }}
                                    />
                                )}

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
            </div>
            {page > 0 && posts.length > 0 &&
                <div className="text-center mb-2">
                    <Button onClick={loadMore}>Xem thêm bài viết...</Button>
                </div>
            }

            {loading && <MySpinner />}

        </>
    );
};

export default Home;
