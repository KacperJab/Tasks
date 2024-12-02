import {TaskIn, TaskOut} from "../reducers/taskReducer";
import {Dispatch} from "react";
import ApiService from "../../services/ApiService";
import UserService, {Role} from "../../services/UserService";
import axios from "axios";
import {Action, addTask, deleteTask, getTasks, getUsers, getUserTasks, updateTask} from "./actionCreators";
import {errorNotification, successNotification} from "../../services/NotificationService";
import {logMessage, notificationMessageError, notificationMessageSuccess} from "../../constants";
import {UserIn} from "../reducers/UserReducer";

export enum TaskEditProperty {
    IMPORTANT,
    DONE,
    TITLE_DESCRIPTION
}

const logError = (error: any) => {
    if (axios.isAxiosError(error)) {
        logMessage( "Axios error: " + error.message);
    }
    else {
        logMessage("Unknown error: " + error);
    }
}

export const fetchTasks = () => async (dispatch: Dispatch<Action>) => {
    try {
        const response = await ApiService.getAxiosClient().get<TaskIn[]>("/tasks"
            , {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+ UserService.getToken()
                }
            }
        );
        dispatch( getTasks(response.data) )
        successNotification(notificationMessageSuccess.TasksFetchedSuccess)
    } catch (error) {
        errorNotification(notificationMessageError.TasksFetchedError)
        logError(error);
    }
};

export const fetchUserTasks = (userId: string) => async (dispatch: Dispatch<Action>) => {
    try {
        const response = await ApiService.getAxiosClient().get<TaskIn[]>("/users/" + userId + "/tasks"
            , {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+ UserService.getToken()
                }
            }
        );
        dispatch( getUserTasks(response.data) )
        successNotification(notificationMessageSuccess.TasksFetchedSuccess)
    } catch (error) {
        errorNotification(notificationMessageError.TasksFetchedError)
        logError(error);
    }
};

export const fetchUsers = () => async (dispatch: Dispatch<Action>) => {
    try {
        const response = await ApiService.getAxiosClient().get<UserIn[]>("/users"
            , {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+ UserService.getToken()
                }
            }
        );
        dispatch( getUsers(response.data, response.data.length) )
    } catch (error) {
        if (UserService.hasRole(Role.ADMIN)) {
            errorNotification(notificationMessageError.UsersFetchedError)
        }
        logError(error);
    }
};

export const addTaskAsync = (task: TaskOut) => async (dispatch: Dispatch<Action>) => {
    try {
        const response = await ApiService.getAxiosClient().post<TaskIn>("/tasks"
            , task, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+ UserService.getToken()
                }
            }
        );
        dispatch( addTask(response.data) )
        successNotification(notificationMessageSuccess.TaskAddedSuccess)
    } catch (error) {
        errorNotification(notificationMessageError.TaskAddedError)
        logError(error);
    }
};

export const updateTaskAsync = (task: TaskIn, property: TaskEditProperty, taskEdited?: TaskOut) => async (dispatch: Dispatch<Action>) => {
    let taskUpdated: TaskOut = {
        title: task.title,
        description: task.description,
        important: task.important,
        done: task.done
    };
    switch (property) {
        case TaskEditProperty.DONE:
            taskUpdated.done = !task.done
            break;
        case TaskEditProperty.IMPORTANT:
            taskUpdated.important = !task.important
            break;
        case TaskEditProperty.TITLE_DESCRIPTION:
            taskUpdated = taskEdited!
            break
        default:
            break;
    }
    try {
        const response = await ApiService.getAxiosClient().put<TaskIn>("/tasks/" + task.id
            , taskUpdated, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+ UserService.getToken()
                }
            }
        );
        successNotification(notificationMessageSuccess.TaskUpdatedSuccess)
        dispatch( updateTask(response.data) );
    } catch (error) {
        errorNotification(notificationMessageError.TaskUpdatedError)
        logError(error);
    }
};

export const deleteTaskAsync = (taskId: string) => async (dispatch: Dispatch<Action>) => {
    try {
        const response = await ApiService.getAxiosClient().delete<string>("/tasks/" + taskId
            , {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+ UserService.getToken()
                }
            }
        );
        if (response.status == 204) {
            dispatch( deleteTask(taskId) )
        }
    } catch (error) {
        errorNotification(notificationMessageError.TaskDeleteError)
        logError(error);
    }
};
