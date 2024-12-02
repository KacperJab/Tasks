import {useEffect, useState} from "react";
import {useAppDispatch} from "../state/hooks";
import previous from "../images/previous-page.svg"
import next from "../images/next-page.svg"
import first from "../images/previous.svg"
import last from "../images/last-page.svg"



export type PaginatedProps = {
    children: any;
    totalElements: number;
    elementsPerPage: number;
    getElementsFunction: any;
}

const Pagination = (props: PaginatedProps) => {

    const [currentMin, setCurrentMin] = useState(Math.min(1, props.totalElements))
    const [currentMax, setCurrentMax] = useState(Math.min(props.elementsPerPage, props.totalElements))
    const [currentPage, setCurrentPage] = useState(1);

    const dispatch = useAppDispatch()

    const onNextPage = () => {
        setCurrentPage(currentMax < props.totalElements ? currentPage + 1 : currentPage)
    }

    const onPreviousPage = () => {
        setCurrentPage(currentPage === 1 ? 1 : currentPage - 1)
    }

    const onLastPage = () => {
        setCurrentPage(Math.ceil(props.totalElements / props.elementsPerPage))
    }

    const onFirstPage = () => {
        setCurrentPage(1)
    }

    useEffect(() => {
        setCurrentMin(Math.min((currentPage - 1) * props.elementsPerPage + 1, props.totalElements))
        setCurrentMax(Math.min((currentPage) * props.elementsPerPage, props.totalElements))
        dispatch(props.getElementsFunction(currentMin, currentMax))
    }, [currentPage])


    return (
        <div>
            <div className="pagination__content">
                {props.children}
            </div>
            <div className="pagination__controls">
                <div className="pagination__page-info">
                    <label>
                        {currentMin} -  {currentMax} of {props.totalElements}
                    </label>
                </div>
                <div className="pagination__buttons">
                    <img className={currentMin === 0 ? "pagination__first-button__disabled" : "pagination__first-button__active"}
                         onClick={onFirstPage} src={first} alt="first" >
                    </img>
                    <img className={currentMin === 0 ? "pagination__previous-button__disabled" : "pagination__previous-button__active" }
                         onClick={onPreviousPage} src={previous} alt="previous">
                    </img>
                    <img className={currentMax === props.totalElements ? "pagination__next-button__disabled" : "pagination__next-button__active"}
                         onClick={onNextPage} src={next} alt="next">
                    </img>
                    <img className={currentMax === props.totalElements ? "pagination__last-button__disabled" : "pagination__last-button__active"}
                         onClick={onLastPage} src={last} alt="last">
                    </img>
                </div>
            </div>
        </div>
    )
}

export default Pagination