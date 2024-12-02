import { toast } from "react-toastify";

export const successNotification = (message: string) => {
    return toast.success(message, {
        position: "bottom-right",
        closeButton: false,
        hideProgressBar: true
    })
}

export const errorNotification = (message: string) => {
    return toast.error(message, {
        position: "bottom-right",
        closeButton: false,
        hideProgressBar: true
    })
}