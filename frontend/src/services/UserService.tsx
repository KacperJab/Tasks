import Keycloak from "keycloak-js";
import {logMessage} from "../constants";

const keycloak = new Keycloak({
    url: 'https://junior-academy-keycloak.sparkbit.pl/auth',
    realm: 'task-list-kacper-jablonski',
    clientId: 'task-list'
});

export enum Role {
    ADMIN = "task-list-administrators",
    USER = "task-list-users"
}

const initKeycloak = (onAuthCallback: Function) => {
    keycloak.init(
        {
            onLoad: 'login-required',
        }
    ).then((authenticated) => {
        if (!authenticated) {
            logMessage("user not authenticated")
        }
        onAuthCallback();
    }).catch(console.error)
}

const refreshToken = () => {
    keycloak.updateToken(300).then(() => {
        logMessage('Successfully refreshed token' + keycloak.token);
    }).catch( () => {
        logMessage("Failed to refresh the token, or the session has expired")
        UserService.doLogout();
    })
}

keycloak.onTokenExpired = () => refreshToken();

const doLogin = keycloak.login;

const doLogout = keycloak.logout;

const getToken = () => {
    if (UserService.isLoggedIn()) {
        return keycloak.token;
    }
    else {
        return " ";
    }
}

const isLoggedIn = () => !!keycloak.token;

const getUsername = () => keycloak.tokenParsed?.preferred_username;

const hasRole = (role: string) => keycloak.hasResourceRole(role, "task-list")

const UserService = {
    initKeycloak,
    doLogin,
    doLogout,
    isLoggedIn,
    getToken,
    getUsername,
    hasRole,
};

export default UserService;