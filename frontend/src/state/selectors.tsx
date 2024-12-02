import {VisibilityFilters} from "./actionTypes";
import {TaskIn} from "./reducers/taskReducer";


export const getTasksByVisibilityFilter = (allTodos: TaskIn[], visibilityFilter: string) => {
    switch (visibilityFilter) {
        case VisibilityFilters.OPEN:
            return allTodos.filter(todo => !todo.done);
        case VisibilityFilters.IMPORTANT:
            return allTodos.filter(todo => todo.important);
        case VisibilityFilters.OPEN_IMPORTANT:
            return allTodos.filter(todo => todo.important && !todo.done);
        default:
            return allTodos;
    }
};