import { registerRootComponent } from "expo";
import { ScrollView, StyleSheet, Text, View } from "react-native";
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

            try {
                if (getUser == "") {
                    let sessionId = await AsyncStorage.getItem("user")
                    if(sessionId==null){
                        router.replace("/")
                    }else{
                        setUser(sessionId)
                    }
                }

                let url = "https://redbird-suitable-conversely.ngrok-free.app/ZapChatBackend/Home"

                let response = await fetch(url, {
                    method: "POST",
                    body: JSON.stringify(getSearchText),
                    headers: {
                        "Content-Type": "application/json",
                        'Cookie': `JSESSIONID=${sessionId}`
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

    }, [getSearchText])

    return (
        <SafeAreaView style={styles.safearea}>
            <Header func={setSearchText} />

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
        flexGrow: 1,
        paddingTop: 15,
        // backgroundColor:"green"
    },
    safearea: {
        flex: 1
    }
})