import { createContext, useEffect, useRef, useState } from 'react';

export const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
    const [socket, setSocket] = useState(null);

    const socketRef = useRef(socket);

    useEffect(() => {
        socketRef.current = socket;
    }, [socket])

    useEffect(() => {
        const ws = new WebSocket(process.env.EXPO_PUBLIC_URL+"/WebSocket");

        ws.onopen = () => {
            console.log('WebSocket connected successfully');
            setSocket(ws);
        };

        ws.onmessage = (event) => {

            console.log(event.data);
            
            const data = JSON.parse(event.data);
            if (data.success) {
                switch (data.content.type) {
                    case 'send_chat':
                        break;
                    default:
                        console.log('Unknown message type', data.type);
                }
            }
        };

        ws.onclose = (event) => {
            console.log('WebSocket closed', event);
            setSocket(null);
        };

        ws.onerror = (error) => {
            console.log('WebSocket error:', error);
            setSocket(null);
        };

        return () => {
            ws.close();
        };
    }, []);

    return (
        <WebSocketContext.Provider value={{ socket}}>
            {children}
        </WebSocketContext.Provider>
    );
};