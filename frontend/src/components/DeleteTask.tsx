import {TaskIn} from "../state/reducers/taskReducer";
import {useDeleteTaskMutation} from "../api/TaskApi";

const DeleteTask = (closeAction: any, task: TaskIn) => {

    const [deleteTask] = useDeleteTaskMutation();

    const handleDelete = () => {
        deleteTask(task.id);
        closeAction();
    }

    const handleCancel = () => {
        closeAction();
    }

    return (
        <div className="delete-task">
            <h1 className="delete-task__header">Delete task</h1>
            <div className="delete-task__are-yo-sure">
                <p>Are you sure you want to delete this task?</p>
            </div>
            <div className="delete-task__buttons">
                <button className="delete-task__cancel" onClick={handleCancel}>
                    CANCEL
                </button>
                <button className="delete-task__delete" onClick={handleDelete}>
                    DELETE
                </button>
            </div>
        </div>
    )
}

export default DeleteTask