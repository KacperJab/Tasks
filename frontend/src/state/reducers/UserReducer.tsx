import {Action} from "../actions/actionCreators";
import {ActionTypes} from "../actionTypes";

export type UserIn = {
    name: string,
    id: string,
    openTasksCount: number,
    openImportantTaskCount: number,
    totalTaskCount: number
}

const _users: UserIn[] = []

const initialState = {
    users: _users,
    usersCount: 0
}

const userReducer = (state  = initialState, action: Action) => {
    switch (action.type) {
        case ActionTypes.GET_USERS:
            return {
                ...state,
                users: action.payload.users,
                usersCount: action.payload.usersCount
            }
        case ActionTypes.CLEAN_STATE:
            return {
                users: [],
                usersCount: 0
            }
        default:
            return state
    }
}

export default userReducer
