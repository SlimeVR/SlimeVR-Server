import { createContext, Dispatch, useContext, useReducer } from "react";

type AppStateAction =
 | { type: 'debug', value: boolean }



export interface AppState {
    debug: boolean;
}


export interface AppContext {
    state: AppState;
    dispatch: Dispatch<AppStateAction>;
    setDebug: (value: boolean) => void;
}


export function reducer(state: AppState, action: AppStateAction) {
    switch (action.type) {
        case 'debug':
            return { ...state, debug: action.value };
        default:
            throw new Error(`unhandled state action ${action.type}`);
    }
}



export function useProvideAppContext(): AppContext {

    const [state, dispatch] = useReducer(reducer, { debug: false })


    return {
        state,
        dispatch,
        setDebug: (value: boolean) => dispatch({ type: 'debug', value }),
    }
}




export const AppContextC = createContext<AppContext>(undefined as any);

export function useAppContext() {
    const context = useContext<AppContext>(AppContextC);
    if (!context) {
        throw new Error('useWebsocketAPI must be within a WebSocketApi Provider')
    }
    return context;

}