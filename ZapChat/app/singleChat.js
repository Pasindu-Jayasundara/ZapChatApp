import { registerRootComponent } from "expo";
import { Alert, ScrollView, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatHeader } from "../components/ChatHeader";
import { StatusBar } from "expo-status-bar";
import { ChatBuble } from "../components/ChatBuble";
import { Date } from "../components/Date";
import { ChatFooter } from "../components/ChatFooter";
import { useEffect, useState } from "react";
import { router, useLocalSearchParams } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { FlashList } from "@shopify/flash-list";

export default function singleChat() {

    const data = useLocalSearchParams();
    const [getUser, setUser] = useState("")
    const [getChat, setChat] = useState([])

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
                } else {

                    let url = "https://redbird-suitable-conversely.ngrok-free.app/ZapChatBackend/SingleChat"

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
                            console.log(obj.data)
                        }

                    } else {
                        Alert.alert("Please Try Again Later");
                        console.log(response)
                    }

                }

            } catch (error) {
                console.error(error)
            }

        })()

    }, [])

    return (
        <SafeAreaView style={styles.safearea}>

            <ChatHeader data={data} />

            <FlashList contentContainerStyle={styles.body} data={getChat} renderItem={({ item }) => <ChatBuble params={item} />} keyExtractor={item.messageId} />

            <ChatFooter data={data} func={setChat}/>
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    body: {
        flexGrow: 1
    },
    safearea: {
        flex: 1
    }
})