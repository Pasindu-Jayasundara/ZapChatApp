import { registerRootComponent } from "expo";
import { Alert, ScrollView, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatHeader } from "../components/ChatHeader";
import { StatusBar } from "expo-status-bar";
import { ChatBuble } from "../components/ChatBuble";
import { Date } from "../components/Date";
import { ChatFooter } from "../components/ChatFooter";
import { useEffect, useState } from "react";
import { useLocalSearchParams } from "expo-router";

export default function singleChat() {

    const data = useLocalSearchParams();

    useEffect(() => {

        (async () => {
 
            try {
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

                let url = "https://redbird-suitable-conversely.ngrok-free.app/ZapChatBackend/SingleChat"

                let obj = {
                    searchText: getSearchText,
                    category: getCategory
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

                        setChatDataArr(obj.data.data)
                        setHeaderImage(obj.data.profile)

                    } else {
                        if (obj.data == "Please LogIn") {

                            await AsyncStorage.removeItem("verified");
                            await AsyncStorage.removeItem("user");

                            router.replace("/")
                        } else {
                            Alert.alert(obj.data); 
                        }
                        console.log(obj.data)
                    }

                } else {
                    Alert.alert("Please Try Again Later");
                    console.log(response)
                }

            } catch (error) {
                console.error(error)
            }

        })()

    }, [])

    return (
        <SafeAreaView style={styles.safearea}>

            <ChatHeader data={data} />

            <ScrollView contentContainerStyle={styles.body}>

                <Date />

                <ChatBuble params={{ side: "left", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "right", time: "19:20", message: "message" }} />

                <Date />

                <ChatBuble params={{ side: "left", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "right", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "left", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "right", time: "19:20", message: "message" }} />

            </ScrollView>

            <ChatFooter />
        </SafeAreaView>
    )
}

// registerRootComponent(SingleChat)

const styles = StyleSheet.create({
    body: {
        flexGrow: 1
    },
    safearea: {
        flex: 1
    }
})