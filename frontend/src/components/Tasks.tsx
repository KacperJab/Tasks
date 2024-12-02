import React, { useState} from 'react'
import TaskList from "./TaskList";
import TasksHeader from "./TasksHeader";
import addTaskImg from "../images/addTask.svg"
import Modal from "./Modal";
import CreateEditTask from "./CreateEditTask";

const Tasks = () => {

    const [modalCreateTaskVisible, setModalCreateTaskVisible] = useState(false);

    const showModal = () => setModalCreateTaskVisible(true);

    const hideModal = () => setModalCreateTaskVisible(false);

    return (
        <div className="tasks" >
            <TasksHeader />
            { TaskList() }
            <button className="tasks__add-task-btn" onClick={() => showModal()}>
                <img src={addTaskImg} alt="add task" />
            </button>
            <div>
                <Modal visible={modalCreateTaskVisible} onCloseAction={hideModal} content={CreateEditTask(hideModal)}/>
            </div>
        </div>
    );
}

export default Tasks
