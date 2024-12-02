import {TaskIn} from "../state/reducers/taskReducer";
import TaskView from "./TaskView";
import {useState} from "react";
import Modal from "./Modal";
import DeleteTask from "./DeleteTask";
import CreateEditTask from "./CreateEditTask";
import {useGetTasksQuery, useUpdateTaskMutation} from "../api/TaskApi";
import {useAppSelector} from "../state/hooks";
import {useParams} from "react-router-dom";

const TaskList = () => {

    const [modalDelete, setModalDelete] = useState(false);

    const [currentTask, setTask] = useState<TaskIn>();

    const [modalEditTask, setModalEditTask] = useState(false)

    const filterState = useAppSelector(state => state.filters)

    const setCurrentTask = (task: TaskIn) => {
        setTask(task);
    }

    const showModal = () => setModalDelete(true);

    const hideModalDelete = () => {
        setModalDelete(false);
        setTask(undefined);
    }

    const showModalEdit = () => setModalEditTask(true);

    const hideModalEdit = () => {
        setModalEditTask(false);
        setTask(undefined);
    }

    const {userId} = useParams();

    const {
        data: tasks = []
    } = useGetTasksQuery({userId: userId, important: filterState.important, open: filterState.open})

    const [ updateTask ] = useUpdateTaskMutation()

    const taskComponents = tasks.map((task: TaskIn) =>
        TaskView(task, showModal, setCurrentTask, showModalEdit, updateTask))

    return (
        <div className="task-list">
            { taskComponents }
            <div>
                <Modal visible={modalDelete} onCloseAction={hideModalDelete} content={DeleteTask(hideModalDelete, currentTask!)}/>
            </div>
            <div>
                <Modal visible={modalEditTask} onCloseAction={hideModalEdit} content={CreateEditTask(hideModalEdit, currentTask)}/>
            </div>
        </div>
    )
}

export default TaskList