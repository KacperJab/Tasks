import {ReactNode} from "react";

type ModalProps = {
    visible: boolean,
    onCloseAction: any,
    content: ReactNode
}

const Modal = (props: ModalProps) => {

    const showHideClassName = props.visible ? "modal display-block" : "modal display-none";
    console.log(showHideClassName)
    return (
        <div className={showHideClassName}>
            <div className="modal-main">
                {props.content}
            </div>
        </div>
    );
};

export default Modal