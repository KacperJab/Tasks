import React, {useEffect} from 'react'
import {useAppDispatch, useAppSelector} from "../state/hooks";
import {UserIn} from "../state/reducers/UserReducer";
import User from "./User";
import Pagination from "./Pagination";
import { fetchUsers } from "../state/actions";
import UsersTable from "./UsersTable";
import RenderOnCondition from "./RenderOnCondition";
import UserService, {Role} from "../services/UserService";
import {cleanTasks} from "../state/actions/actionCreators";

const Users = () => {

    const usersState = useAppSelector(state => state.users)

    const dispatch = useAppDispatch()

    const usersCount = usersState.usersCount;

    const elementsPerPage = 10;

    const usersComponents = usersState.users.map((user: UserIn) => {
        return User(user);
    })

    const userTable = UsersTable(usersComponents)

    useEffect(() => {
            dispatch(cleanTasks())
    }, [])

    return (
        <RenderOnCondition if={UserService.hasRole(Role.ADMIN)} showErrorPage={true}>
            <div className="users" >
                <label className="users__placeholder">
                    Users
                </label>
                <Pagination totalElements={usersCount} elementsPerPage={elementsPerPage} getElementsFunction={fetchUsers}>
                    {userTable}
                </Pagination>
            </div>
        </RenderOnCondition>

    )
}

export default Users
