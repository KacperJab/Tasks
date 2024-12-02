const dev = {
    url: {
        API_URL: "http://localhost:8080/api"
    }
}

const prod = {
    url: {
        API_URL: "https://task-list-kacper-jablonski.sparkbit.pl/api"
    }
}

const prodLog = () => {

}

const devLog = (message: string) => {
    console.log(message);
}

export const notificationMessageSuccess = {
    TaskAddedSuccess: "Task added successfully",
    TaskUpdatedSuccess: "Task updated successfully",
    TasksFetchedSuccess: "Tasks loaded successfully",
    UsersFetchedSuccess: "Users loaded successfully",
    LoginSuccess: "Login successful"
}

export const notificationMessageError = {
    TaskAddedError: "Error occurred while adding task",
    TaskUpdatedError: "Error occurred while updating task",
    TasksFetchedError: "Error occurred while fetching task",
    UsersFetchedError: "Error occurred while fetching users",
    TaskDeleteError: "Error occurred while deleting task"
}

export const notificationDisplayTime = 2000;

export const config = process.env.NODE_ENV === "development" ? dev : prod

export const logMessage = process.env.NODE_ENV === "development" ? devLog : prodLog

