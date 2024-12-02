
const UsersTable = (users: any) => {
    return (
        <table className="user-table">
            <thead className="user-table__header">
                <th className="user-table__header__name">User name</th>
                <th className="user-table__header__rest">Open tasks</th>
                <th className="user-table__header__rest">Open important tasks</th>
                <th className="user-table__header__rest">Total tasks</th>
            </thead>
            <tbody>
                {users}
            </tbody>
        </table>
    )
}

export default UsersTable