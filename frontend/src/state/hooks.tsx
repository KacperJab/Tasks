import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux'
import type { AppDispatch } from './store'
import { StateType } from "./reducers";

export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<StateType> = useSelector