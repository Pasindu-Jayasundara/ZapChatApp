import { FlashList } from "@shopify/flash-list";
import { Image } from "expo-image";
import { Alert, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatCard } from "../components/ChatCard";
import { useContext, useEffect, useState } from "react";
import { router } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { WebSocketContext } from "./WebSocketProvider";
import useStateRef from "react-usestateref";

const searchIcon = require("../assets/images/search.svg")

export default function newChat() {

    const {
        getUser, setUser, getHeaderImage, setHeaderImage
    } = useContext(WebSocketContext)

    const [getDataArray, setDataArray] = useState([])
    const [getMobile, setMobile] = useState("")
    const [getIsFound, setIsFound] = useState(false)
    const [getTryCount, setTryCount, tryCountRef] = useStateRef(0)

    const search = async () => {

        try {

            if (getUser != null) {
                let url = process.env.EXPO_PUBLIC_URL + "/NewChat"

                if (getMobile.length == 10) {

                    let obj = {
                        mobile: getMobile,
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

                            if (obj.data.isFound) {
                                //registered user

                                setIsFound(true)
                                setDataArray(obj.data.data)

                            } else {
                                // not registered
                                setDataArray([])
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
                        }

                    } else {
                        Alert.alert("Please Try Again Later");
                        console.log(response)
                    }
                } else {
                    Alert.alert("Invalid Mobile Number");
                }
            } else {

                console.log("Trying... " + tryCountRef.current)

                if (tryCountRef.current < 3) {
                    setTryCount(tryCountRef.current++)

                    let user = await AsyncStorage.getItem("user");
                    let parsedUser = await JSON.parse(user);
                    setUser(parsedUser);

                    search()

                } else {
                    router.replace("/")
                }
            }
        } catch (error) {
            console.error(error)
        }
    }


    return (
        <SafeAreaView style={styles.body}>

            <View style={styles.head}>
                <TextInput
                    placeholder="Enter Mobile Number .."
                    numberOfLines={1}
                    style={styles.input}
                    onChangeText={(text) => { setMobile(text) }}
                    value={getMobile}
                    inputMode="tel"
                />

                <Pressable onPress={search}>
                    <Image source={searchIcon} style={styles.icon} />
                </Pressable>
            </View>

            {getIsFound ? (

                <FlashList
                    renderItem={({ item }) => <ChatCard data={item} />}
                    data={getDataArray}
                    keyExtractor={item => item.userId}
                    contentContainerStyle={styles.list}
                    estimatedItemSize={200}
                />

            ) : (

                <View style={styles.noView}>
                    <Text style={styles.noText}>No Chats !</Text>
                </View>

            )}

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
        justifyContent: "center",
    },
    icon: {
        width: 22,
        height: 22,
    },
    head: {
        flexDirection: "row",
        columnGap: 15,
        justifyContent: "center",
        alignItems: "center",
        marginVertical: 20
    },
    input: {
        backgroundColor: "#e3e3e3",
        borderRadius: 5,
        minHeight: 40,
        maxHeight: 120,
        paddingHorizontal: 10,
        color: "#ff5b6b",
        width: "80%",
        paddingVertical: 2
    },
    body: {
        flex: 1
    }
})