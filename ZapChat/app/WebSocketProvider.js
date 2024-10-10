import AsyncStorage from '@react-native-async-storage/async-storage';
import { router } from 'expo-router';
import { createContext, useEffect, useRef, useState } from 'react';
import { Alert } from 'react-native';

export const WebSocketContext = createContext(null);
const profileDefault = require("../assets/images/default.svg")

export const WebSocketProvider = ({ children }) => {

    const [socket, setSocket] = useState(null);

    //my
    const [getChatDataArr, setChatDataArr] = useState([])
    const [getGroupDataArr, setGroupDataArr] = useState([])
    const [getStatusDataArr, setStatusDataArr] = useState([])
    const [getHeaderImage, setHeaderImage] = useState(profileDefault)
    const [getChat, setChat] = useState([])
    const [getText, setText] = useState("")
    const [getSearchText, setSearchText] = useState("")
    const [getCategory, setCategory] = useState("chat")

    //single chat
    const chatRef = useRef(getChat);
    useEffect(() => {
        chatRef.current = getChat;
    }, [getChat])

    // chat
    const getChatDataArrRef = useRef(getChatDataArr);
    useEffect(() => {
        getChatDataArrRef.current = getChatDataArr;
    }, [getChatDataArr])

    //group
    const getGroupDataArrRef = useRef(getGroupDataArr);
    useEffect(() => {
        getGroupDataArrRef.current = getGroupDataArr;
    }, [getGroupDataArr])

    //status
    const getStatusDataArrRef = useRef(getStatusDataArr);
    useEffect(() => {
        getStatusDataArrRef.current = getStatusDataArr;
    }, [getStatusDataArr])
    //end - my

    const socketRef = useRef(socket);
    useEffect(() => {
        socketRef.current = socket;
    }, [socket])

    useEffect(() => {
        const ws = new WebSocket(process.env.EXPO_PUBLIC_URL + "/WebSocket");

        ws.onopen = () => {
            console.log('WebSocket connected successfully');
            setSocket(ws);
        };

        ws.onmessage = (event) => {

            console.log("e1: " + event.data);
            const dto_obj = JSON.parse(event.data);

            if (dto_obj.success) {
                // switch (data.content.type) {
                switch (dto_obj.data.location) {
                    case 'status':

                        setStatusDataArr([dto_obj.data,...getStatusDataArrRef.current])

                        break;
                    case 'send_group_chat':

                        setChat([...chatRef.current, dto_obj.data])

                        setGroupDataArr([dto_obj.data,...getGroupDataArrRef.current])

                        break;
                    case 'home':

                        console.log("homedata:" + dto_obj.data)
                        getCategory == "chat" ? (
                            setChatDataArr(dto_obj.data.data)
                        ) : getCategory == "group" ? (
                            setGroupDataArr(dto_obj.data.data)
                        ) : getCategory == "status" ? (
                            setStatusDataArr(dto_obj.data.data)
                        ) : null

                        break;
                    case 'send_chat':
                        setChat([...chatRef.current, dto_obj.data])

                        setChatDataArr([dto_obj.data,...getChatDataArrRef.current])
                        // let obj = {
                        //     location: "home",
                        //     searchText: getSearchText,
                        //     category: getCategory,
                        //     userId: dto_obj.fromUserId,
                        //     otherUserId: dto_obj.otherUserId,
                        // }
                        // console.log("to home:" + JSON.stringify(obj))
                        // ws.send(JSON.stringify(obj))

                        break;
                    case 'login':

                        (async () => {
                            try {

                                // console.log(JSON.stringify(dto_obj.data))
                                // console.log(dto_obj.data.isSuccess)
                                // console.log(dto_obj.data.profileImage)

                                if (dto_obj.data.success) {

                                    await AsyncStorage.setItem("user", JSON.stringify(dto_obj.data.user))
                                    await AsyncStorage.setItem("profileImage", JSON.stringify(dto_obj.data.profileImage))
                                    await AsyncStorage.setItem("profileAbout", JSON.stringify(dto_obj.data.profileAbout))

                                    router.replace("/home")
                                } else {
                                    // Alert.alert("")
                                    console.log("no")
                                }



                            } catch (error) {
                                console.log(error)

                            }
                        })()

                        break;
                    default:
                        console.log('Unknown message type', dto_obj.type);
                }
            } else {
                console.log("a : " + event.data)
                Alert.alert(dto_obj.data.msg)
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
        <WebSocketContext.Provider value={{
            socket,
            getChatDataArr, setChatDataArr,
            getGroupDataArr, setGroupDataArr,
            getStatusDataArr, setStatusDataArr,
            getHeaderImage, setHeaderImage,
            getChat, setChat,
            getText, setText,
            getSearchText, setSearchText,
            getCategory, setCategory
        }}>
            {children}
        </WebSocketContext.Provider>
    );
};