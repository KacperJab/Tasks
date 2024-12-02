import React from 'react';
import UnauthorizedAccess from "./UnauthorizedAccess";

export type RenderProps = {
    children: any;
    if: any;
    showErrorPage?: boolean
}

const RenderOnCondition = ({children = null, if: isTruth = false, showErrorPage = false}: RenderProps) => {
    let content = null;
    if (isTruth) {
        content = children;
    }
    else if (showErrorPage) {
        content = UnauthorizedAccess();
    }
    return <>{content}</>;
};

export default RenderOnCondition;