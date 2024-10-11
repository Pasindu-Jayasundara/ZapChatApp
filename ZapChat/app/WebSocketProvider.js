import AsyncStorage from '@react-native-async-storage/async-storage';
import { router } from 'expo-router';
import { createContext, useEffect, useRef, useState } from 'react';
import { Alert } from 'react-native';
import useStateRef from 'react-usestateref';

export const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {

    const [socket, setSocket] = useState(null);

    //my
    const [getChatDataArr, setChatDataArr,chatref] = useStateRef([])
    const [getGroupDataArr, setGroupDataArr] = useState([])
    const [getStatusDataArr, setStatusDataArr] = useState([])
    const [getChat, setChat] = useState([])
    const [getCategory, setCategory] = useState("chat")
    const [getUser, setUser] = useState(null)
    const [getHeaderImage, setHeaderImage] = useState(null)
    const [getSearchText, setSearchText] = useState("")

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
                switch (dto_obj.data.location) {
                    case 'status':

                        setStatusDataArr([dto_obj.data, ...getStatusDataArrRef.current])

                        break;
                    case 'send_group_chat':

                        setChat([...chatRef.current, dto_obj.data])
                        setGroupDataArr([dto_obj.data, ...getGroupDataArrRef.current])

                        break;
                    case 'home':

                        console.log("homedata:" + dto_obj.data)
                        // console.log(JSON.stringify([dto_obj.data.data,...getChatDataArrRef.current.filter(obj => obj.userId !== dto_obj.data.data.userId)]))

                        const rest = getChatDataArrRef.current.filter(obj => obj.userId !== dto_obj.data.data.userId)
                        getCategory == "chat" ? (
                            setChatDataArr([dto_obj.data.data,...rest])
                        ) : getCategory == "group" ? (
                            setGroupDataArr(dto_obj.data.data)
                        ) : getCategory == "status" ? (
                            setStatusDataArr(dto_obj.data.data)
                        ) : null

                        break;
                    case 'send_chat':
                        
                        setChat([...chatRef.current, dto_obj.data])

                        let obj = {
                            location: "home",
                            searchText: getSearchText,
                            category: getCategory,
                            userId: dto_obj.data.fromUserId,
                            otherUserId: dto_obj.data.otherUserId,
                        }
                        console.log("to home:" + JSON.stringify(obj))
                        ws.send(JSON.stringify(obj))

                        break;
                    case 'login':

                        (async () => {
                            try {

                                if (dto_obj.data.success) {

                                    let jsonuser = JSON.stringify(dto_obj.data.user)
                                    await AsyncStorage.setItem("user", jsonuser)

                                    let jsonpi = JSON.stringify(dto_obj.data.profileImage)
                                    await AsyncStorage.setItem("profileImage", jsonpi)

                                    let jsonab = JSON.stringify(dto_obj.data.profileAbout)
                                    await AsyncStorage.setItem("profileAbout", jsonab)

                                    setUser(dto_obj.data.user)
                                    if (getHeaderImage != "../assets/images/default.svg") {
                                        setHeaderImage({ uri: process.env.EXPO_PUBLIC_URL + dto_obj.data.user.profile_image })
                                    }else {
                                        setHeaderImage(defImg)
                                    }

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
            getChatDataArr, setChatDataArr,chatRef,
            getGroupDataArr, setGroupDataArr,getChatDataArrRef,
            getStatusDataArr, setStatusDataArr,
            getChat, setChat,
            getCategory, setCategory,
            getUser, setUser,
            getHeaderImage, setHeaderImage,
            getSearchText, setSearchText
        }}>
            {children}
        </WebSocketContext.Provider>
    );
};