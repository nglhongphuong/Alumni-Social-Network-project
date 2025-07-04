import axios from "axios";
import cookie from 'react-cookies'

const BASE_URL = 'http://localhost:8080/SpringMVCPost/api/';

export const endpoints = {
    'register': '/user',
    'login': '/login',
    'current-user': '/secure/profile',
    'post':'/secure/post/',
    'change-password': '/secure/change-password',
    'user-info':  '/secure/user/',
    'get-user': '/secure/user',
    'active-account': '/secure/active-account/',
    'stats': '/secure/stats/',
    'invitation': '/secure/invitation-post/',
    'survey-post': '/secure/survey-post/',
    'survey-question' : '/secure/survey-question/',
    'survey-option': '/secure/survey-option/',
    'survey-response': '/secure/survey-response',
}

export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookie.load('token')}`
        }
    })
}

export default axios.create({
    baseURL: BASE_URL
});