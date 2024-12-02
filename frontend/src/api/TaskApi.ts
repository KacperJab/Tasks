import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import {TaskIn, TaskOut} from "../state/reducers/taskReducer";
import {config} from "../constants";
import UserService from "../services/UserService";

export type TaskApiArgs = {
    userId?: string,
    important?: boolean,
    open?: boolean
}

export const taskApi = createApi({
    reducerPath: "taskApi",
    tagTypes: ["Tasks"],
    baseQuery: fetchBaseQuery({
        baseUrl: config.url.API_URL,
        prepareHeaders: (headers) => {
            const token = UserService.getToken();
            if (token) {
                headers.set('authorization', `Bearer ${token}`)
            }
            headers.set('Content-type', 'application/json; charset=UTF-8',);
            return headers;
        }
    }),
    endpoints: (builder) => ({
        getTasks: builder.query< TaskIn[], TaskApiArgs >({
            query: ({userId, important, open}) => ({
                url: (userId === undefined) ? `/tasks` : `users/${userId}/tasks`,
                method: "GET",
                params: {important, open}
            }),
            transformResponse:
                (res: TaskIn[]) =>
                    res.sort((t_1: TaskIn, t_2: TaskIn) => new Date(t_2.createdOn).getTime() - new Date(t_1.createdOn).getTime()),
            providesTags: (result) =>
                result
                    ? [...result.map(({ id }) => ({ type: 'Tasks' as const, id })), 'Tasks']
                    : ['Tasks'],
        }),
        getTask: builder.query<TaskIn, string>({
            query: (id) => ({
                url: `/tasks/${id}`,
                method: "GET"
            }),
            providesTags: ["Tasks"]
        }),
        addTask: builder.mutation<TaskIn, TaskOut>({
            query: (task) => ({
                url: "/tasks",
                method: "POST",
                body: task
            }),
            invalidatesTags: ["Tasks"]
        }),
        updateTask: builder.mutation<TaskIn, {id: string, task: TaskOut}>({
            query: ({id, task}) => ({
                url: `/tasks/${id}`,
                method: "PUT",
                body: task
            }),
            invalidatesTags: (result, error, arg) => [{type: "Tasks", id: arg.id}]
        }),
        deleteTask: builder.mutation<any, string>({
            query: (id) => ({
                url: `/tasks/${id}`,
                method: "DELETE",
            }),
            invalidatesTags: (result, error, arg) => [{type: "Tasks", id: arg}]
        }),
    })
})

export const { useGetTasksQuery, useAddTaskMutation, useUpdateTaskMutation, useDeleteTaskMutation } = taskApi