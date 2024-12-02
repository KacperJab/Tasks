import {Action} from "../actions/actionCreators"
import {ActionTypes} from "../actionTypes";

export type TaskIn = {
    id: string;
    title: string;
    createdOn: Date;
    description: string;
    done: boolean;
    important: boolean
}

export type TaskOut = {
    title: string;
    description: string;
    done: boolean;
    important: boolean
}

const _tasks: TaskIn[] = []

const initialState = {
    tasks: _tasks
}

const tasks = (state  = initialState, action: Action) => {
    switch (action.type) {
        case ActionTypes.GET_TASKS:
            return {
                ...state,
                tasks: action.payload,
            }
        case ActionTypes.ADD_TASK:
            return {
                ...state,
                tasks: [
                    ...state.tasks,
                    action.payload
                ],
            }
        case ActionTypes.SET_FILTERS:
            return {
                ...state,
                tasks: [
                    ...state.tasks
                ]
            }
        case ActionTypes.UPDATE_TASK:
            const updatedTask = action.payload;
                return {
                    ...state,
                    tasks: [
                        ...state.tasks.map((t: TaskIn) => {
                            if (t.id === updatedTask.id) {
                                return updatedTask;
                            }
                            return t;
                        })
                    ]
                }
        case ActionTypes.DELETE_TASK:
            let taskId = action.payload;
            return {
                ...state,
                tasks: [
                    ...state.tasks.filter((t: TaskIn) => {
                        return t.id !== taskId;
                    })
                ]
            }
        case ActionTypes.GET_USER_TASKS:
            return {
                ...state,
                tasks: action.payload
            }
        case ActionTypes.CLEAN_TASKS:
            return {
                ...state,
                tasks: []
            }
        case ActionTypes.CLEAN_STATE:
            return {
                tasks: []
            }
        default:
            return state
    }
}

export default tasks;