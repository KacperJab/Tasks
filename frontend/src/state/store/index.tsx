import { ThunkDispatch } from "redux-thunk"
import rootReducer from "../reducers/index";
import {configureStore} from "@reduxjs/toolkit";
import {taskApi} from "../../api/TaskApi";

export const store = configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware().concat(taskApi.middleware),
})

export default store;

export type AppDispatch = ThunkDispatch<any, any, any>
