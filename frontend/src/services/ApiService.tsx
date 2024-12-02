import axios, {AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse} from "axios";
import UserService from "./UserService";
import {config} from "../constants"

const onRequest = (config: AxiosRequestConfig): AxiosRequestConfig => {
    console.info(`[request] [${JSON.stringify(config)}]`);
    console.info(`[token] [${JSON.stringify(config.headers!.Authorization)}]`)
    return config;
}

const onRequestError = (error: AxiosError): Promise<AxiosError> => {
    console.error(`[request error] [${JSON.stringify(error)}]`);
    return Promise.reject(error);
}

const onResponse = (response: AxiosResponse): AxiosResponse => {
    console.info(`[response] [${JSON.stringify(response)}]`);
    return response;
}

const onResponseError = (error: AxiosError): Promise<AxiosError> => {
    console.error(`[response error] [${JSON.stringify(error)}]`);
    return Promise.reject(error);
}

const onRequestInterceptor = (instance: AxiosInstance) => {
    instance.interceptors.request.use(function (config) {
        if (UserService.isLoggedIn()) {
            config.headers!.Authorization =  'Bearer '+ UserService.getToken()!;
        }
        return config;
    });
}

export function setupInterceptorsTo(axiosInstance: AxiosInstance): AxiosInstance {
    onRequestInterceptor(axiosInstance);
    axiosInstance.interceptors.request.use(onRequest, onRequestError)
    axiosInstance.interceptors.response.use(onResponse, onResponseError)
    return axiosInstance;
}

const _axios = axios.create({
    baseURL: config.url.API_URL
})

setupInterceptorsTo(_axios);


const getAxiosClient = () => _axios;

const HttpService = {
    getAxiosClient,
};

export default HttpService;

