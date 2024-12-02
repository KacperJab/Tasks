import {createSlice} from '@reduxjs/toolkit'

interface FilterSliceState {
    important: boolean,
    open: boolean
}

const initialState : FilterSliceState = {
    important: false,
    open: false
}

export const filterSlice = createSlice({
    name: 'filter',
    initialState,
    reducers: {
        changeImportant: (state) => {
            state.important = !state.important
        },
        changeOpen: (state) => {
            state.open = !state.open
        },
    },
})

export const {changeImportant, changeOpen} = filterSlice.actions;
