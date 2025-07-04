import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./components/Home";
import Login from "./components/Login";
import Header from "./components/layouts/Header";
import Footer from "./components/layouts/Footer";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from 'react-bootstrap';
import Register from "./components/Register";
import { MyDispatcherContext, MyUserContext } from "./configs/MyContexts";
import { useReducer } from "react";
import MyUserReducer from "./reducers/MyUserReducer"
import cookie from 'react-cookies';
import Apis, { endpoints } from "./configs/Apis";
import { useContext, useEffect, useState } from "react";
import MySpinner from "./components/layouts/MySpinner";
import Sidebar from "./components/layouts/Sidebar";
import Profile from "./components/User/Profile";
import Account from "./components/User/Account";
import UserInfo from "./components/User/UserInfo";
import ManageUser from "./components/ManageUser/ManageUser";
import Statistics from "./components/Stats/Statistics";
import Invitation from "./components/InvitationPost/Invitation";
import NewInvitation from "./components/InvitationPost/NewInvitation";
import SurveyPost from "./components/SurveyPost/SurveyPost";
import SurveyDetail from "./components/SurveyPost/SurveyDetail";
import SurveyStats from "./components/SurveyPost/SurveyStats";
import SurveyView from "./components/ResponseSurvey/SurveyView";
import ReponseSurvey from "./components/ResponseSurvey/ReponseSurvey";
import Chat from "./components/Chat/Chat";
import Detail from "./components/Detail/Detail";
import List from "./components/List/List";
import Gemini from "./components/Gemini/Gemini";


function App() {
  const [user, dispatch] = useReducer(MyUserReducer, null);
  const [checkingAuth, setCheckingAuth] = useState(true);

  useEffect(() => {
    const checkUser = async () => {
      try {
        const token = cookie.load("token");
        if (token) {
          const res = await Apis.get(endpoints["current-user"], {
            headers: { Authorization: `Bearer ${token}` },
          });
          dispatch({ type: "login", payload: res.data });
        }
      } catch (error) {
        console.error("Lỗi xác thực:", error);
      } finally {
        setCheckingAuth(false);
      }
    };
    checkUser();
  }, []);

  if (checkingAuth) return <MySpinner />;

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          {!user ? (
            <Container>
              <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/register" element={<Register />} />
              </Routes>
            </Container>
          ) : (
            <>
              <Header />
              <Sidebar />
              <Container>
                <Routes>
                  <Route path="/" element={<Home />} />
                  <Route path="/profile" element={<Profile />} />
                  <Route path="/userInfo/:userId" element={<UserInfo />} />
                  <Route path="/manage-users" element={<ManageUser />} />
                  <Route path="/account" element={<Account />} />
                  <Route path="/statistics" element={<Statistics />} />
                  <Route path="/invitation" element={<Invitation />} />
                  <Route path="/new-invitation" element={<NewInvitation />} />
                  <Route path="/survey-post" element={<SurveyPost />} />
                  <Route path="/survey-detail/:surveyId" element={<SurveyDetail />} />
                  <Route path="/survey-post/:surveyId/stats" element={<SurveyStats />} />
                  <Route path="/survey-view" element={<SurveyView />} />
                  <Route path="/response-view/:surveyId" element={<ReponseSurvey />} />
                  
                  <Route path="/chat" element={<Chat />} />
                  <Route path="/list" element={<List />} />
                  <Route path="/detail" element={<Detail />} />

                  <Route path="/gemini" element={<Gemini />} />

                </Routes>
              </Container>
              <Footer />
            </>
          )}
        </BrowserRouter>
      </MyDispatcherContext.Provider>
    </MyUserContext.Provider>
  );
}


export default App;