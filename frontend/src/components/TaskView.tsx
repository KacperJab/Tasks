import {TaskIn, TaskOut} from "../state/reducers/taskReducer";
import importantFalse from "../images/important-false.svg"
import importantTrue from "../images/important-true.svg"
import deleteImg from "../images/bin.svg"
import editImg from "../images/edit-icon.svg"
import deselected from "../images/deselected.svg"
import completed from "../images/done.svg"
import link from "../images/link.svg"

const TaskView = (task: TaskIn, showModalDelete: any, setTask: any, showModalEdit: any, update: any) => {

    const format: Intl.DateTimeFormatOptions = {
        month: "long",
        day: "numeric",
        year: "numeric"
    }

    const date = new Date(task.createdOn)

    const handleDeleteClick = () => {
        setTask(task);
        showModalDelete();
    }

    const handleEditClick = () => {
        setTask(task);
        showModalEdit(task);
    }

    const handleImportantClick = () => {
        let taskUpdated: TaskOut = {
            title: task.title,
            description: task.description,
            done: task.done,
            important: !task.important,
        };
        update({id: task!.id, task: taskUpdated})
    }

    const handleDoneClick = () => {
        let taskUpdated: TaskOut = {
            title: task.title,
            description: task.description,
            done: !task.done,
            important: task.important,
        };
        update({id: task!.id, task: taskUpdated})
    }

    return (
        <div className="task">
            <img className="task__link-img" src={link} alt="link"/>
            <div className="task__headline">
                {task.title}
            </div>
            <div className="task__created-on">{date.toLocaleString('en-US', format)}</div>
            <div className="task__description">{task.description}</div>
            <div className="task__bottom-menu">
                <div className="task__important-container">
                    <img className="task__important"
                         src={task.important ? importantTrue : importantFalse} alt="important"
                         onClick={() => handleImportantClick()}/>
                </div>
                <div className="task__action-icons">
                    <div className="task__delete-container">
                        <img className="task__delete-img" src={deleteImg} alt="delete"
                            onClick={handleDeleteClick}
                        />
                    </div>
                    <div className="task__edit-container">
                        <img className="task__edit-img" src={editImg} alt="edit"
                             onClick={handleEditClick}/>
                    </div>
                    <div className="task__complete-container">
                        <img className="task__complete-img" src={task.done ? completed : deselected}
                             alt="complete" onClick={() => handleDoneClick()}/>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TaskView