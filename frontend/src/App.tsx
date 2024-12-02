import React, {useEffect} from 'react';
import Navbar from "./components/Navbar"
import About from "./components/About";
import Tasks from "./components/Tasks";
import {BrowserRouter, Routes, Route, Navigate} from "react-router-dom";
import Users from "./components/Users";
import {ToastContainer} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {notificationDisplayTime} from "./constants";
import {useAppDispatch} from "./state/hooks";
import UserService, {Role} from "./services/UserService";
import { fetchUsers} from "./state/actions";

function App() {

    const dispatch = useAppDispatch()

    useEffect(() => {
        if (UserService.hasRole(Role.ADMIN)) {
            dispatch( fetchUsers() );
        }
    }, []);

    return (
        <BrowserRouter>
            <div className="App">
                <Navbar />
                <Routes>
                    <Route
                        path="/"
                        element={
                            UserService.hasRole(Role.ADMIN) ? (
                                <Navigate replace to="/users" />
                            ) : (
                                <Navigate replace to="/tasks" />
                            )
                        }
                    />
                    <Route path="/about" element={<About />}/>
                    <Route path="/tasks" element={<Tasks />}/>
                    <Route path="/users" element={<Users />}/>
                    <Route path="/users/:userId/tasks" element={<Tasks />}/>
                </Routes>
                <ToastContainer autoClose={notificationDisplayTime} />
            </div>
        </BrowserRouter>

    );

}

export default App;
