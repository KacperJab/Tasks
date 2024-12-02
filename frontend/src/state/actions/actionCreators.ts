import {TaskIn} from "../reducers/taskReducer";
import {ActionTypes} from "../actionTypes";
import {UserIn} from "../reducers/UserReducer";

type AddTask = {
    type: ActionTypes.ADD_TASK;
    payload: TaskIn;
}

type DeleteTask = {
    type: ActionTypes.DELETE_TASK;
    payload: string;
}

type UpdateTask = {
    type: ActionTypes.UPDATE_TASK;
    payload: TaskIn;
}

type GetTasks = {
    type: ActionTypes.GET_TASKS;
    payload: TaskIn[];
}

type SetFilter = {
    type: ActionTypes.SET_FILTERS,
    payload: string
}

type GetUsers = {
    type: ActionTypes.GET_USERS,
    payload: {
        users: UserIn[],
        usersCount: number
    }
}

type GetUserTasks = {
    type: ActionTypes.GET_USER_TASKS;
    payload: TaskIn[];
}

type CleanState = {
    type: ActionTypes.CLEAN_STATE;
}

type CleanTasks = {
    type: ActionTypes.CLEAN_TASKS;
}

export const addTask = (task: TaskIn): AddTask => {
    return {
        type: ActionTypes.ADD_TASK,
        payload: task
    }
}

export const cleanState = (): CleanState => {
    return {
        type: ActionTypes.CLEAN_STATE,
    }
}

export const cleanTasks = (): CleanTasks => {
    return {
        type: ActionTypes.CLEAN_TASKS,
    }
}

export const getTasks = (tasks: TaskIn[]): GetTasks => {
    return {
        type: ActionTypes.GET_TASKS,
        payload: tasks
    }
}

export const getUserTasks = (tasks: TaskIn[]): GetUserTasks => {
    return {
        type: ActionTypes.GET_USER_TASKS,
        payload: tasks
    }
}

export const deleteTask = (taskId: string): DeleteTask => {
    return {
        type: ActionTypes.DELETE_TASK,
        payload: taskId
    }
}

export const updateTask = (task: TaskIn): UpdateTask => {
    return {
        type: ActionTypes.UPDATE_TASK,
        payload: task
    }
}

export const setFilter = (filter: string): SetFilter => {
    return {
        type: ActionTypes.SET_FILTERS,
        payload: filter
    }
}

export const getUsers = (users: UserIn[], count: number): GetUsers => {
    return {
        type: ActionTypes.GET_USERS,
        payload: {
            users: users,
            usersCount: count
        }
    }
}

export type Action = AddTask | GetTasks | SetFilter | UpdateTask | DeleteTask | GetUsers | GetUserTasks | CleanState | CleanTasks