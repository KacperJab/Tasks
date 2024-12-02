import React from 'react'
import logo from '../images/sparkbit-logo.svg'
import {Link} from "react-router-dom";
import PersonalMenuContainer from "./PersonalMenuContainer";
import UserService, {Role} from "../services/UserService";

const Navbar = () => {
    return (
        <nav className="navbar">
            <div className="navbar__menu">
                <Link to="/">
                    <img className="navbar__logo" src={logo} alt=""/>
                </Link>
                <div className="navbar__buttons">
                    <Link className="navbar__button" to="/tasks">
                        TASKS
                    </Link>
                    {UserService.hasRole(Role.ADMIN) && <Link className="navbar__button" to="/users">
                        USERS
                    </Link>}
                    <Link className="navbar__button" to="/about">
                        ABOUT
                    </Link>
                </div>
            </div>
            <PersonalMenuContainer />
        </nav>
    )
}

export default Navbar