import { Alert, BackHandler, KeyboardAvoidingView, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { GroupChatBuble } from "../components/GroupChatBuble";
import { useContext, useEffect, useRef, useState } from "react";
import { router, useLocalSearchParams } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { FlashList } from "@shopify/flash-list";
import { GroupHeader } from "../components/GroupHeader";
import { GroupChatFooter } from "../components/GroupChatFooter";
import { WebSocketContext } from "./WebSocketProvider";
import useStateRef from "react-usestateref";

export default function singleGroup() {

    const data = useLocalSearchParams();
    const { getUser, setUser } = useContext(WebSocketContext)

    const [getIsNew, setIsNew, isNewRef] = useStateRef(false)
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

    function toBoolean(value) {
        if (typeof value === 'boolean') {
            return value;
        }
        if (typeof value === 'string') {
            return value.toLowerCase() === 'true';
        }
        return !!value;
    }

    const loadgroup = (async () => {
        setIsNew(toBoolean(data.isNew))

        try {
            if (getUser != null) {
                if (getResent) {
                    setResent(false)
                    setTryCount(0)
                    let url = process.env.EXPO_PUBLIC_URL + "/SingleGroup"

                    let obj = {
                        groupId: data.groupId,
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
                        }
                        setResent(true)
                    } else {
                        Alert.alert("Please Try Again Later");
                        console.log(response)

                        setResent(true)
                    }
                }
            } else {

                console.log("Trying... " + tryCountRef.current)

                if (tryCountRef.current < 3) {
                    setTryCount(tryCountRef.current++)

                    let user = await AsyncStorage.getItem("user");
                    let parsedUser = await JSON.parse(user);
                    setUser(parsedUser);

                    loadgroup()

                } else {
                    router.replace("/")
                }
            }
        } catch (error) {
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
        loadgroup()
    }, [isNewRef.current])

    useEffect(() => {
        let id = setInterval(() => {
            loadgroup()
        }, 2000);

        setSetIntervalId(id)
    }, [])

    return (
        <SafeAreaView style={styles.safearea}>
            <GroupHeader data={data} getUser={getUser} setUser={setUser} setIsNew={isNewRef.current} isNewRef={isNewRef.current} />
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

                        return <GroupChatBuble params={item} isNewDate={isNewDate} isNewTime={isNewTime} />;
                    }}
                    estimatedItemSize={200}
                />
            </View>

            {isNewRef.current ? (
                <View style={styles.newView}>
                    <Text style={styles.newText}>Join Now to Send Messages</Text>
                </View>
            ) : (
                <GroupChatFooter data={data} />
            )}
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    newText: {
        color: "#ff5b6b",
    },
    newView: {
        width: "100%",
        justifyContent: "center",
        alignItems: "center",
        paddingVertical: 15
    },
    body: {
        flexGrow: 1,
        backgroundColor: "rgb(235, 235, 235)"
    },
    safearea: {
        flex: 1
    }
})