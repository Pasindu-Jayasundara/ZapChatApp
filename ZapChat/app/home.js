import { registerRootComponent } from "expo";
import { Alert, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Header } from "../components/Header";
import { ChatCard } from "../components/ChatCard";
import { FloatingAction } from "react-native-floating-action";
import { FlashList } from "@shopify/flash-list";
import { useEffect, useState } from "react";
import { router } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";

export default function home() {

    const [getChatDataArr, setChatDataArr] = useState([])
    const [getSearchText, setSearchText] = useState("")
    const [getUser, setUser] = useState("")
    const [getCategory, setCategory] = useState("chat")


    const actions = [
        {
            text: "Create New Group",
            icon: require("../assets/images/people.png"),
            name: "1",
            position: 1,
        },
        {
            text: "New Chat",
            icon: require("../assets/images/chat.png"),
            name: "2",
            position: 2,
        }
    ]

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

                let url = "https://redbird-suitable-conversely.ngrok-free.app/ZapChatBackend/Home"

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

                        setChatDataArr(obj.data)

                    } else {
                        Alert.alert(obj.data);
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

    }, [getSearchText, getCategory])

    return (
        <SafeAreaView style={styles.safearea}>
            <Header searchTextFunc={setSearchText} categoryFunc={setCategory} />

            <FlashList contentContainerStyle={styles.body} data={getChatDataArr} renderItem={({ item }) => <ChatCard data={item} />} estimatedItemSize={200} />

            <FloatingAction
                color="#fc384b"
                actions={actions}
                onPressItem={name => {
                    if (name == 1) {
                        router.push("/newGroup")
                    } else if (name == 2) {
                        router.push("/newChat")
                    }
                }}
            />
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    body: {
        // flexGrow: 1,
        paddingTop: 15,
        // backgroundColor:"green"
    },
    safearea: {
        flex: 1
    }
})