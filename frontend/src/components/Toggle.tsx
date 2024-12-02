import React from 'react';

type toggleProps = {
    name: string;
    label: string;
    func: any;
    toggled: boolean
}

const ToggleSwitch = (props: toggleProps) => {

        return (
            <div className="toggle" id= {props.name + "-switch"}>
                <label className="toggle__switch">
                    <input
                        type="checkbox"
                        className="toggle__checkbox"
                        name={props.name}
                        id={props.name}
                        onChange={props.func}
                        checked={props.toggled}
                    />
                    <span className="toggle__slider"/>
                </label>
                <label className="toggle__label">{props.label}</label>
            </div>
        );
}

export default ToggleSwitch;