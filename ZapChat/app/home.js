import { Alert, Animated, Dimensions, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Header } from "../components/Header";
import { ChatCard } from "../components/ChatCard";
import { FloatingAction } from "react-native-floating-action";
import { FlashList } from "@shopify/flash-list";
import { useContext, useEffect, useRef, useState } from "react";
import { router, useNavigation } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { StatusCard } from "../components/StatusCard";
import { GroupCard } from "../components/GroupCard";
import { WebSocketContext } from "./WebSocketProvider";
import useStateRef from "react-usestateref";
import { FadeIn, FadeOut } from "react-native-reanimated";

const defImg = require("../assets/images/default.svg")
const AnimatedView = Animated.createAnimatedComponent(View);

export default function home() {

    const {
        getUser, setUser, getHeaderImage, setHeaderImage
    } = useContext(WebSocketContext)

    const [getIsFound, setIsFound] = useState(false)
    const [getActionText, setActionText] = useState("No")
    const [getResent, setResent] = useState(true)
    const [getTryCount, setTryCount, tryCountRef] = useStateRef(0)
    const [getHomeChat, setHomeChat, homeChatRef] = useStateRef([])
    const [getHomeStatus, setHomeStatus, homeStatusRef] = useStateRef([])
    const [getChatDataArr, setChatDataArr, chatref] = useStateRef([])
    const [getGroupDataArr, setGroupDataArr] = useState([])
    const [getStatusDataArr, setStatusDataArr] = useState([])
    const [getCategory, setCategory] = useState("chat")
    const [getSearchText, setSearchText] = useState("")
    const [getSetIntervalId, setSetIntervalId] = useState(0)

    const getChatDataArrRef = useRef(getChatDataArr);
    useEffect(() => {
        getChatDataArrRef.current = getChatDataArr;
    }, [getChatDataArr])

    const getGroupDataArrRef = useRef(getGroupDataArr);
    useEffect(() => {
        getGroupDataArrRef.current = getGroupDataArr;
    }, [getGroupDataArr])

    const getStatusDataArrRef = useRef(getStatusDataArr);
    useEffect(() => {
        getStatusDataArrRef.current = getStatusDataArr;
    }, [getStatusDataArr])

    const getSetIntervalIdRef = useRef(getSetIntervalId);
    useEffect(() => {
        getSetIntervalIdRef.current = getSetIntervalId;
    }, [getSetIntervalId])

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

    const loadHome = (async () => {
        try {

            if (getUser != null) {

                if (getResent) {


                    setResent(false)

                    setTryCount(0)
                    setActionText("Looking for")
                    let url = process.env.EXPO_PUBLIC_URL + "/Home"

                    let obj = {
                        searchText: getSearchText,
                        category: getCategory,
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

                            if (getCategory == "chat") {
                                setChatDataArr(obj.data.data)
                                // setHomeChat(obj.data.data)
                            } else if (getCategory == "group") {
                                setGroupDataArr(obj.data.data)
                            } else if (getCategory == "status") {
                                setStatusDataArr(obj.data.data)
                                setHomeStatus(obj.data.data)
                            }
                            setIsFound(obj.data.isFound)

                            if (getHeaderImage == null) {

                                if ((obj.data.profile != "../assets/images/default.svg") && (obj.data.profile != getHeaderImage)) {
                                    setHeaderImage({ uri: process.env.EXPO_PUBLIC_URL + obj.data.profile })
                                } else {
                                    setHeaderImage(defImg)
                                }

                            }

                        } else {
                            if (obj.data == "Please LogIn") {

                                await AsyncStorage.removeItem("verified");
                                await AsyncStorage.removeItem("user");

                                setUser(null)
                                router.replace("/")
                            } else {
                                Alert.alert(obj.data);
                            }
                            console.log(obj.data)
                        }
                        setResent(true)

                    } else {
                        Alert.alert("Please Try Again Later");
                        console.log(response)

                        setResent(true)
                    }
                    setActionText("No")
                }
            } else {
                setResent(true)
                console.log("Trying... " + tryCountRef.current)

                if (tryCountRef.current < 3) {
                    setTryCount(tryCountRef.current + 1)

                    let user = await AsyncStorage.getItem("user");
                    let parsedUser = await JSON.parse(user);
                    setUser(parsedUser);

                    loadHome()

                } else {
                    router.replace("/")
                }
            }
        } catch (error) {
            setResent(true)
            console.error(error)
        }
    })

    useEffect(() => {
        loadHome()
    }, [getCategory])


    return (
        <SafeAreaView style={styles.safearea}>
            <Header searchTextFunc={setSearchText} setCategoryFunc={setCategory} getCategoryFunc={getCategory} img={getHeaderImage} loadHome={loadHome} />

            {getIsFound ? (
                getCategory == "chat" ? (

                    <FlashList
                        contentContainerStyle={styles.body}
                        data={getChatDataArrRef.current}
                        renderItem={({ item }) => <ChatCard data={item}/>}
                        keyExtractor={(item) => Math.random().toString()}
                        estimatedItemSize={200}
                    />

                ) : getCategory == "group" ? (

                    <FlashList
                        contentContainerStyle={styles.body}
                        data={getGroupDataArr}
                        renderItem={({ item }) => <GroupCard data={item} />}
                        keyExtractor={item => item.groupId.toString()}
                        estimatedItemSize={200}
                    />

                ) : getCategory == "status" ? (

                    <FlashList
                        contentContainerStyle={styles.body}
                        data={homeStatusRef.current}
                        renderItem={({ item }) => <StatusCard data={item} />}
                        keyExtractor={(item) => (item.statusId ? item.statusId.toString() : Math.random().toString())}
                        estimatedItemSize={200}
                    />

                ) : null

            ) : (

                <AnimatedView entering={FadeIn} exiting={FadeOut} style={styles.noView}>
                    <Text style={styles.noText}>{getActionText} {getCategory.charAt(0).toUpperCase() + getCategory.substring(1)} !</Text>
                </AnimatedView>

            )}

            <FloatingAction
                color="#fc384b"
                actions={actions}
                onPressItem={name => {
                    if (name == 1) {
                        navigation.navigate('newGroup', { data: JSON.stringify(getGroupDataArr) })
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