import React, { useContext } from 'react';
import {
    CDBSidebar,
    CDBSidebarContent,
    CDBSidebarHeader,
    CDBSidebarMenu,
    CDBSidebarMenuItem,
} from 'cdbreact';
import { NavLink } from 'react-router-dom';
import { MyUserContext, MyDispatcherContext } from "../../configs/MyContexts";

const Sidebar = () => {
    const user = useContext(MyUserContext);

    return (
        <div style={{ display: 'flex', height: '100vh', overflow: 'scroll initial', position: 'fixed' }}>
            <CDBSidebar textColor="#fff" backgroundColor="#333">
                <CDBSidebarHeader prefix={<i className="fa fa-bars fa-large"></i>}>
                    <a href="/" className="text-decoration-none" style={{ color: 'inherit' }}>
                        Sidebar
                    </a>
                </CDBSidebarHeader>

                <CDBSidebarContent className="sidebar-content">
                    <CDBSidebarMenu>

                        {user?.role === 'ROLE_ADMIN' ? (
                            <>
                                <NavLink exact to="/manage-users" activeClassName="activeClicked">
                                    <CDBSidebarMenuItem icon="users">Quản lý User</CDBSidebarMenuItem>
                                </NavLink>
                                <NavLink exact to="/invitation" activeClassName="activeClicked">
                                    <CDBSidebarMenuItem icon="envelope">Quản lý thư mời</CDBSidebarMenuItem>
                                </NavLink>
                                <NavLink exact to="/survey-post" activeClassName="activeClicked">
                                    <CDBSidebarMenuItem icon="poll">Quản lý bài khảo sát</CDBSidebarMenuItem>
                                </NavLink>
                                <NavLink exact to="/statistics" activeClassName="activeClicked">
                                    <CDBSidebarMenuItem icon="chart-line">Thống kê</CDBSidebarMenuItem>
                                </NavLink>
                            </>
                        ) : (
                            <>
                                {/* <NavLink exact to="/notifications" activeClassName="activeClicked">
                                    <CDBSidebarMenuItem icon="bell">Thông báo</CDBSidebarMenuItem>
                                </NavLink> */}
                            </>
                        )}
                        <NavLink exact to="/survey-view" activeClassName="activeClicked">
                            <CDBSidebarMenuItem icon="poll">Khảo sát</CDBSidebarMenuItem>
                        </NavLink>
                        <NavLink exact to="/chat" activeClassName="activeClicked">
                            <CDBSidebarMenuItem icon="comments">Tin nhắn</CDBSidebarMenuItem>
                        </NavLink>
                         <NavLink exact to="/gemini" activeClassName="activeClicked">
                            <CDBSidebarMenuItem>✨Gemini</CDBSidebarMenuItem>
                        </NavLink>
                    </CDBSidebarMenu>
                </CDBSidebarContent>
            </CDBSidebar>
        </div>
    );
};

export default Sidebar;
