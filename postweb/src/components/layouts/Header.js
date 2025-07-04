import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import Button from 'react-bootstrap/Button';
import { MyUserContext, MyDispatcherContext } from "../../configs/MyContexts";
import { useContext, useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import './HeaderStyle.css';
import cookie from 'react-cookies';
import { getAuth, signOut } from "firebase/auth";


const Header = () => {

  const defaultAvatar = "https://i.pinimg.com/736x/97/c9/00/97c900fdd932d1517c10f6735ba95dfc.jpg";
  const [posts, setPosts] = useState([]);
  const [kw, setKw] = useState('');
  const nav = useNavigate();
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyDispatcherContext);

  const loadPosts = async () => {
    try {
      const token = cookie.load('token');
      let url = `${endpoints['post']}?order=desc`;
      let res = await Apis.get(url, {
        headers: {
          Authorization: `Bearer ${token}`
        },
        params: { content: kw }
      });
      setPosts(res.data);
      // console.log("Danh sách posts:", res.data);
    } catch (err) {
      console.error("Failed to load posts:", err);
    }
  }

  useEffect(() => {
    loadPosts();
  }, []);

  const search = (e) => {
    e.preventDefault();
    nav(`/?content=${kw}`);
  };

 const handleLogout = async () => {
  const auth = getAuth();
  try {
    await signOut(auth); 
    dispatch({ type: "logout" }); 
    
    nav("/");
  } catch (error) {
    console.error("Đăng xuất Firebase thất bại:", error);
  }
};



  return (
    <Navbar expand="lg" className="custom-navbar shadow-sm fixed-top">
      <Container>
        <Navbar.Brand as={Link} to="/" className="text-white fw-bold">
          Alumni Network
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="basic-navbar-nav" className="bg-light" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/" className="text-white">Home</Nav.Link>

            {!user && (
              <>
                <Nav.Link as={Link} to="/register" className="text-white">Register</Nav.Link>
                <Nav.Link as={Link} to="/login" className="text-white">Login</Nav.Link>
              </>
            )}
          </Nav>

          {user && (
            <Form className="search-form mx-auto d-flex align-items-center" onSubmit={search}>
              <Form.Control
                type="search"
                value={kw}
                onChange={e => setKw(e.target.value)}
                placeholder="Tìm bài viết...."
                className="search-input me-2"
              />
              <Button type="submit" variant="outline-light">Tìm</Button>
            </Form>
          )}

          {user && (
            <Nav className="ms-auto d-flex align-items-center">
              <img
                src={user.avatar || defaultAvatar}
                alt="avatar"
                className="user-avatar me-2"
              />

              <NavDropdown
                title={<span className="text-white">{user.name}</span>}
                id="user-nav-dropdown"
                align="end"
                menuVariant="dark"
              >
                <NavDropdown.Item as={Link} to="/profile">Profile</NavDropdown.Item>
                <NavDropdown.Item as={Link} to="/account">Account</NavDropdown.Item>
                <NavDropdown.Item onClick={handleLogout}>
                  Logout
                </NavDropdown.Item>
              </NavDropdown>
            </Nav>
          )}
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
