import { registerRootComponent } from "expo";
import { Alert, ScrollView, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatHeader } from "../components/ChatHeader";
import { StatusBar } from "expo-status-bar";
import { ChatBuble } from "../components/ChatBuble";
import { Date } from "../components/Date";
import { ChatFooter } from "../components/ChatFooter";
import { useEffect, useRef, useState } from "react";
import { router, useLocalSearchParams } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { FlashList } from "@shopify/flash-list";

export default function singleChat() {

    const data = useLocalSearchParams();
    const [getUser, setUser] = useState("")
    const [getChat, setChat] = useState([])
    
    let date;
    let time;

    useEffect(() => {
        (async () => {

            let verified = await AsyncStorage.getItem("verified");
            let user = await AsyncStorage.getItem("user");

            if (verified == null || verified != "true" || user == null) {

                await AsyncStorage.removeItem("verified")
                await AsyncStorage.removeItem("user")

                router.replace("/")
            }
        })()
    }, [])

    useEffect(() => {

        (async () => {
            if (getUser == "") {

                let sessionId = await AsyncStorage.getItem("user")
                if (sessionId == null) {

                    await AsyncStorage.removeItem("verified");
                    await AsyncStorage.removeItem("user");

                    router.replace("/")

                } else {
                    setUser(sessionId)

                }
            }

            let url = process.env.EXPO_PUBLIC_URL + "/SingleChat"

            let obj = {
                otherUserId: data.userId
            }
            let response = await fetch(url, {
                method: "POST",
                body: JSON.stringify(obj),
                headers: {
                    "Content-Type": "application/json",
                    'Cookie': `JSESSIONID=${getUser}`
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

        })()

    }, [])

    return (
        <SafeAreaView style={styles.safearea}>
            <ChatHeader data={data} />
            <FlashList
                contentContainerStyle={styles.body}
                data={getChat}
                renderItem={({ item }) => {

                    const isNewDate = (date == item.date) ? false : true;
                    if (isNewDate) {
                        date = item.date;
                    }
                    const isNewTime = (time == item.time) ? false : true;
                    if (isNewTime) {
                        time = item.time;
                    }

                    return <ChatBuble params={item} isNewDate={isNewDate} isNewTime={isNewTime}/>;
                }}
                estimatedItemSize={200}
            />

            <ChatFooter data={data} func={setChat} />
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    body: {
        // flexGrow: 1
        backgroundColor:"rgb(235, 235, 235)"
    },
    safearea: {
        flex: 1
    }
})