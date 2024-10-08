import { Alert, Dimensions, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Header } from "../components/Header";
import { ChatCard } from "../components/ChatCard";
import { FloatingAction } from "react-native-floating-action";
import { FlashList } from "@shopify/flash-list";
import { useContext, useEffect, useState } from "react";
import { router, useNavigation } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { StatusCard } from "../components/StatusCard";
import { GroupCard } from "../components/GroupCard";
import { WebSocketContext } from "./WebSocketProvider";

// const profileDefault = require("../assets/images/default.svg")`

export default function home() {

    const {
        socket,
        getChatDataArr, setChatDataArr,
        getGroupDataArr, setGroupDataArr,
        getStatusDataArr, setStatusDataArr,
        getHeaderImage, setHeaderImage,
        getSearchText, setSearchText,
        getCategory, setCategory
    } = useContext(WebSocketContext)

    // const [getChatDataArr, setChatDataArr] = useState([])
    // const [getGroupDataArr, setGroupDataArr] = useState([])
    // const [getStatusDataArr, setStatusDataArr] = useState([])
    // const [getHeaderImage, setHeaderImage] = useState(profileDefault)

    // const [getSearchText, setSearchText] = useState("")
    const [getUser, setUser] = useState(null)
    // const [getCategory, setCategory] = useState("chat")
    const [getFirstTime, setFirstTime] = useState(true)
    const [getIsFound, setIsFound] = useState(false)

    const navigation = useNavigation();

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
        },
        {
            text: "New Status",
            icon: require("../assets/images/pencil.png"),
            name: "3",
            position: 3,
        }
    ]

    // const send = async () => {
    //     setEmojiModal(false)

    //     try {
    //         let parsedUser;

    //         if (getUser == null) {
    //             let user = await AsyncStorage.getItem("user");

    //             parsedUser = JSON.parse(user); // Parse the JSON string to an object
    //             setUser(parsedUser); // Set the parsed object in the state

    //         } else {
    //             parsedUser = getUser
    //         }

    //         console.log("send")
    //         if (socket && socket.readyState == socket.OPEN) {

    //             console.log("obj")
    //             let obj = {
    //                 location:"home",
    //                 searchText: getSearchText,
    //                 category: getCategory,
    //                 user: parsedUser
    //             }

    //             socket.send(JSON.stringify(obj))

    //         }

    //     } catch (error) {
    //         console.error(error)
    //     }

    // }
    // useEffect(() => {
    //     (async () => {

    //         let verified = await AsyncStorage.getItem("verified");
    //         let user = await AsyncStorage.getItem("user");

    //         if (verified == null || verified != "true" || user == null) {

    //             await AsyncStorage.removeItem("verified")
    //             await AsyncStorage.removeItem("user")

    //             router.replace("/")
    //         }
    //     })()
    // }, [])

    useEffect(() => {

        (async () => {

            try {
                let parsedUser;

                if (getUser == null) {
                    let user = await AsyncStorage.getItem("user");

                    parsedUser = JSON.parse(user); // Parse the JSON string to an object
                    setUser(parsedUser); // Set the parsed object in the state

                }else{
                    parsedUser = getUser
                }

                let url = process.env.EXPO_PUBLIC_URL + "/Home"

                let obj = {
                    searchText: getSearchText,
                    category: getCategory,
                    user: parsedUser
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

                        if (getCategory == "chat") {
                            setChatDataArr(obj.data.data)

                        } else if (getCategory == "group") {
                            setGroupDataArr(obj.data.data)
                        } else if (getCategory == "status") {
                            setStatusDataArr(obj.data.data)

                        }
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
            <Header searchTextFunc={setSearchText} setCategoryFunc={setCategory} getCategoryFunc={getCategory} img={getHeaderImage} />

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
                        data={getGroupDataArr}
                        renderItem={({ item }) => <GroupCard data={item} />}
                        keyExtractor={item => item.groupId}
                        estimatedItemSize={200}
                    />

                ) : getCategory == "status" ? (

                    <FlashList
                        contentContainerStyle={styles.body}
                        data={getStatusDataArr}
                        renderItem={({ item }) => <StatusCard data={item} />}
                        keyExtractor={item => item.statusId}
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
                        navigation.navigate('newGroup', { data: JSON.stringify(getGroupDataArr) })
                        // router.push({pathname:"/newGroup",params:getGroupDataArr})
                    } else if (name == 2) {
                        router.push("/newChat")
                    } else if (name == 3) {
                        router.push("/newStatus")
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