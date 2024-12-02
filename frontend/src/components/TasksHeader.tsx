import React, {useEffect, useState} from 'react'
import ToggleSwitch from "./Toggle";
import {useAppSelector} from "../state/hooks";
import store from "../state/store";
import {changeImportant, changeOpen} from "../state/reducers/filterReducer";

const TasksHeader = () => {

    const [important, setImportant] = useState(false)
    const [done, setDone] = useState(false)

    const filterState = useAppSelector(state => state.filters)

    const handleClickImportant = () => {
        setImportant(!important);
        store.dispatch(changeImportant())
    };

    const handleClickDone = () => {
        setDone(!done);
        store.dispatch(changeOpen())
    };

    useEffect(() => {
        setDone(filterState.open);
        setImportant(filterState.important);
    })

    return (
        <div className="tasks-header">
            <header className="tasks-header__title">Tasks</header>
            <div className="tasks-header__toggles">
                <ToggleSwitch name="important" label="Important only" func={handleClickImportant} toggled={important}/>
                <ToggleSwitch name="open" label="Open only" func={handleClickDone} toggled={done}/>
            </div>
        </div>
    )
}

export default TasksHeader;