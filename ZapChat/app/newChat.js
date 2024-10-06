import { FlashList } from "@shopify/flash-list";
import { Image } from "expo-image";
import { Alert, Pressable, StyleSheet, TextInput, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatCard } from "../components/ChatCard";
import { useState } from "react";
import { router } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";

const searchIcon = require("../assets/images/search.svg")

export default function newChat() {

    const [getDataArray, setDataArray] = useState([])
    const [getMobile, setMobile] = useState("")
    const [getUser, setUser] = useState("")
    const [getIsFound, setIsFound] = useState(false)

    useEffect(() => {
        (async () => {

            let verified = await AsyncStorage.getItem("verified");
            let user = await AsyncStorage.getItem("user");

            if (verified == null || verified != "true" || user == null) {

                await AsyncStorage.removeItem("verified")
                await AsyncStorage.removeItem("user")

                router.replace("/")
            } else {
                setUser(user)
            }
        })()
    }, [])

    const search = async () => {

        let url = process.env.EXPO_PUBLIC_URL + "/NewChat"

        if (getMobile.length == 10) {

            let obj = {
                mobile: getMobile,
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
        } else {
            Alert.alert("Invalid Mobile Number");
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
                    <Image source={searchIcon} style={styles.icon}/>
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
    noText:{
        fontSize:20,
        fontWeight:"bold",
        color:"#919190"
    },
    noView:{
        width:"100%",
        alignItems:"center",
        flex:1,
        justifyContent:"center"
    },
    icon: {
        width: 22,
        height: 22,
    },
    head: {
        flexDirection: "row",
        columnGap: 15
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