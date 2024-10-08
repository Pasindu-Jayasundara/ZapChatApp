import { Alert, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Header } from "../components/Header";
import { ChatCard } from "../components/ChatCard";
import { FloatingAction } from "react-native-floating-action";
import { FlashList } from "@shopify/flash-list";
import { useEffect, useState } from "react";
import { router } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { StatusCard } from "../components/StatusCard";
import { GroupCard } from "../components/GroupCard";

const profileDefault = require("../assets/images/default.svg")

export default function home() {

    const [getChatDataArr, setChatDataArr] = useState([])
    const [getSearchText, setSearchText] = useState("")
    const [getUser, setUser] = useState("")
    // const [getCategory, setCategory] = useState("chat")
    const [getCategory, setCategory] = useState("group")
    const [getFirstTime, setFirstTime] = useState(true)
    const [getIsFound, setIsFound] = useState(false)
    const [getHeaderImage, setHeaderImage] = useState(profileDefault)


    const actions = [
        {
            text: "New Group",
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

                let url = process.env.EXPO_PUBLIC_URL + "/Home"

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

                        setIsFound(obj.data.isFound)

                        if (getFirstTime) {

                            if (obj.data.profile != "../assets/images/default.svg") {
                                setHeaderImage({ uri: process.env.EXPO_PUBLIC_URL + obj.data.profile })
                            } else {
                                setHeaderImage(obj.data.profile)
                            }
                            setFirstTime(false)
                        }

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

    }, [getSearchText, getCategory])

    return (
        <SafeAreaView style={styles.safearea}>
            <Header searchTextFunc={setSearchText} setCategoryFunc={setCategory} img={getHeaderImage} />

            {getIsFound ? (
                getCategory == "chat" ? (

                    <FlashList
                        contentContainerStyle={styles.body}
                        data={getChatDataArr}
                        renderItem={({ item }) => <ChatCard data={item} />}
                        keyExtractor={item => item.chatId}
                        estimatedItemSize={200}
                    />

                ) : getCategory == "group" ? (

                    <FlashList
                        contentContainerStyle={styles.body}
                        data={getChatDataArr}
                        renderItem={({ item }) => <GroupCard data={item} />}
                        keyExtractor={item => item.chatId}
                        estimatedItemSize={200}
                    />

                ) : getCategory == "status" ? (

                    <FlashList
                        contentContainerStyle={styles.body}
                        data={getChatDataArr}
                        renderItem={({ item }) => <StatusCard data={item} />}
                        keyExtractor={item => item.chatId}
                        estimatedItemSize={200}
                    />

                ) : null

            ) : (

                <View style={styles.noView}>
                    <Text style={styles.noText}>No {getCategory.charAt(0).toUpperCase() + getCategory.substring(1)} !</Text>
                </View>

            )}

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
    noText: {
        fontSize: 20,
        fontWeight: "bold",
        color: "#919190"
    },
    noView: {
        width: "100%",
        alignItems: "center",
        flex: 1,
        justifyContent: "center"
    },
    body: {
        paddingTop: 15,
    },
    safearea: {
        flex: 1
    }
})