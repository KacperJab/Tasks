import { combineReducers } from 'redux';
import userReducer from "./UserReducer";
import {taskApi} from "../../api/TaskApi";
import {filterSlice} from "./filterReducer";

const rootReducer =  combineReducers({
    users: userReducer,
    filters: filterSlice.reducer,
    [taskApi.reducerPath]: taskApi.reducer
});

export type StateType = ReturnType<typeof rootReducer>;

export default rootReducer