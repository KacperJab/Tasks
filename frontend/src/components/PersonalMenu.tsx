import React from 'react'
import person from '../images/person.svg'
import logout from '../images/logout.svg'
import UserService from "../services/UserService";
import {useAppDispatch} from "../state/hooks";
import {cleanState} from "../state/actions/actionCreators";

const PersonalMenu = () => {

    const dispatch = useAppDispatch()

    return (
        <div className="personal-menu-box">
            <div className="personal-menu-box__name">
                <img src={person} alt=""/>
                <label>{UserService.getUsername()}</label>
            </div>
            <div className="personal-menu-box__logout" onClick={ () => {
                dispatch(cleanState())
                UserService.doLogout()
                }
            }>
                <img src={logout} alt=""/>
                <label>Logout</label>
            </div>
        </div>
    )
}

export default PersonalMenu
