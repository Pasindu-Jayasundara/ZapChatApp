import { Alert, BackHandler, ScrollView, StyleSheet, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatHeader } from "../components/ChatHeader";
import { ChatBuble } from "../components/ChatBuble";
import { ChatFooter } from "../components/ChatFooter";
import { useContext, useEffect, useRef, useState } from "react";
import { router, useLocalSearchParams } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { FlashList } from "@shopify/flash-list";
import { WebSocketContext } from "./WebSocketProvider";
import useStateRef from "react-usestateref";

export default function singleChat() {

    const data = useLocalSearchParams();
    const { getUser, setUser } = useContext(WebSocketContext)
    const [getTryCount, setTryCount, tryCountRef] = useStateRef(0)
    const [getResent, setResent] = useState(true)
    const [getChat, setChat] = useState([])
    const [getSetIntervalId, setSetIntervalId] = useState(0)

    const chatRef = useRef(getChat);
    useEffect(() => {
        chatRef.current = getChat;
    }, [getChat])

    let date;
    let time;

    const loadchat = (async () => {

        try {

            if (getUser != null) {

                if (getResent) {

                    setResent(false)

                    setTryCount(0)
                    let url = process.env.EXPO_PUBLIC_URL + "/SingleChat"

                    let obj = {
                        otherUserId: data.userId,
                        user: getUser
                    }
                    let response = await fetch(url, {
                        method: "POST",
                        body: JSON.stringify(obj),
                        headers: {
                            "Content-Type": "application/json",
                        }
                    })

                    if (response.ok) {

                        let obj = await response.json()
                        if (obj.success) {

                            setChat(obj.data)

                        } else {

                            if (obj.data == "Please LogIn") {

                                await AsyncStorage.removeItem("verified");
                                await AsyncStorage.removeItem("user");

                                setUser(null)

                                router.replace("/")
                            } else {
                                Alert.alert(obj.data);
                            }
                            console.log("c: " + obj.data)
                        }

                    } else {
                        Alert.alert("Please Try Again Later");
                        console.log(response)
                    }

                    setResent(true)
                }
            } else {
                setResent(true)

                console.log("Trying... " + tryCountRef.current)

                if (tryCountRef.current < 3) {
                    setTryCount(tryCountRef.current++)

                    let user = await AsyncStorage.getItem("user");
                    let parsedUser = await JSON.parse(user);
                    setUser(parsedUser);

                    loadchat()

                } else {
                    router.replace("/")
                }
            }
        } catch (error) {
            setResent(true)

            console.log(error)
        }

    })

    useEffect(() => {
        const handleBackPress = () => {
            clearInterval(getSetIntervalId)
            router.back()

            return true;
        };

        const backHandler = BackHandler.addEventListener('hardwareBackPress', handleBackPress);

        return () => {
            if (backHandler && backHandler.remove) {
                backHandler.remove(); 
            }
        };
    }, [getSetIntervalId]);

    useEffect(() => {
        let id = setInterval(() => {
            loadchat()
        }, 2000);

        setSetIntervalId(id)
    }, [])

    return (
        <SafeAreaView style={styles.safearea}>
            <ChatHeader data={data} />
            <View style={styles.body}>
                <FlashList
                    data={chatRef.current}
                    renderItem={({ item }) => {

                        const isNewDate = (date == item.date) ? false : true;
                        if (isNewDate) {
                            date = item.date;
                        }
                        const isNewTime = (time == item.time) ? false : true;
                        if (isNewTime) {
                            time = item.time;
                        }

                        return <ChatBuble params={item} isNewDate={isNewDate} isNewTime={isNewTime} />;
                    }}
                    estimatedItemSize={200}
                    keyExtractor={item => item.chatId.toString()}
                />
            </View>
            <ChatFooter data={data} />
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    body: {
        flexGrow: 1,
        backgroundColor: "rgb(235, 235, 235)"
    },
    safearea: {
        flex: 1
    }
})