import {useEffect, useState} from "react";
import {TaskIn, TaskOut} from "../state/reducers/taskReducer";
import {useAddTaskMutation, useUpdateTaskMutation} from "../api/TaskApi";

const CreateEditTask = (onCloseAction: any, currentTask?: TaskIn) => {

    const [taskTitle, setTaskTitle] = useState(currentTask ? currentTask.title : "")
    const [taskDescription, setTaskDescription] = useState(currentTask ? currentTask.description: "");

    const [addTask] = useAddTaskMutation()
    const [updateTask] = useUpdateTaskMutation()

    const handleCancel = () => {
        onCloseAction();
    }

    const handleCreate = () => {
        let task: TaskOut = {
            title: taskTitle,
            description: taskDescription,
            done: false,
            important: false,
        }
        addTask(task);
        onCloseAction();
    }

    const handleEdit = () => {
        let task: TaskOut = {
            title: taskTitle,
            description: taskDescription,
            done: currentTask!.done,
            important: currentTask!.important,
        }
        updateTask({id: currentTask!.id, task})
        onCloseAction();
    }

    const descriptionPlaceholder = "Description"

    const titlePlaceholder = "Input text"

    const header = currentTask ? "Edit Task" : "Create task";

    const submitButonText = currentTask ? "EDIT" : "CREATE";

    useEffect(() => {
        setTaskTitle(currentTask ? currentTask.title : "")
        setTaskDescription(currentTask ? currentTask.description : "")
    }, [currentTask])

    return (
        <div className="create-edit-task">
            <h1 className="create-edit-task__header">{header}</h1>
            <div className="create-edit-task__body">
                <p>Are you sure you want to {submitButonText.toLowerCase()} this task?</p>
            </div>
            <div className="create-edit-task__form">
                <label className="create-edit-task__title-label">
                    Title
                </label>
                <input
                    className="create-edit-task__title"
                    id="form-title"
                    name="form-title"
                    placeholder={titlePlaceholder}
                    required={true}
                    value={taskTitle}
                    onChange={(e) => setTaskTitle(e.target.value)}
                />
                <textarea
                    className="create-edit-task__description"
                    id="form-description"
                    name="form-description"
                    placeholder={descriptionPlaceholder}
                    value={taskDescription}
                    required={true}
                    onChange={(e) => setTaskDescription(e.target.value)}
                >
                </textarea>
            </div>
            <div className="create-edit-task__buttons">
                <button className="create-edit-task__cancel" onClick={handleCancel}>
                    CANCEL
                </button>
                <button type="submit" className="create-edit-task__create" onClick={currentTask ? handleEdit : handleCreate}>
                    {submitButonText}
                </button>
            </div>
        </div>
    )
}

export default CreateEditTask;