import React, { useState } from 'react'
import user from '../images/user-circle.svg'
import PersonalMenu from "./PersonalMenu";

const PersonalMenuContainer = () => {

    const [isShown, setIsShown] = useState(false);

    const handleClick = () => {
        setIsShown(current => !current);
    };

    return (
        <div>
            <ellipse className="personal-menu">
                <button className="personal-menu__button" onClick={handleClick}>
                    <img src={user} alt=""/>
                </button>
            </ellipse>
            {isShown && (<PersonalMenu/>)}
        </div>

    )
}

export default PersonalMenuContainer

