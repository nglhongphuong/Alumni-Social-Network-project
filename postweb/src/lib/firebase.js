// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import {getAuth} from "firebase/auth";
import {getFirestore} from "firebase/firestore";


const firebaseConfig = {
  apiKey: "AIzaSyAnDL33IIn0fRIoFhp_RLHDVJsWaBT5uRE",
  authDomain: "postchat-500ba.firebaseapp.com",
  projectId: "postchat-500ba",
  storageBucket: "postchat-500ba.firebasestorage.app",
  messagingSenderId: "184211836104",
  appId: "1:184211836104:web:805961d71e74964aa3a9fd",
  measurementId: "G-T023NWK805"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

export const auth = getAuth()
export const db = getFirestore()