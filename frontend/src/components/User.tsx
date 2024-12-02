import {UserIn} from "../state/reducers/UserReducer";
import {Link} from "react-router-dom";

const User = (user: UserIn) => {
    return (
        <tr>
            <td className="user__name-td">
                <Link className="user__name" to={user.id + "/tasks"}>{user.name}</Link>
            </td>
            <td className="user__open-tasks">{user.openTasksCount}</td>
            <td className="user__important-tasks">{user.openImportantTaskCount}</td>
            <td className="user__total-tasks">{user.totalTaskCount}</td>
        </tr>
    )
}

export default User