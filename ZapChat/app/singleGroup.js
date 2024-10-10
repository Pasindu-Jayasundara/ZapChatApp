import { registerRootComponent } from "expo";
import { Alert, KeyboardAvoidingView, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatHeader } from "../components/ChatHeader";
import { StatusBar } from "expo-status-bar";
import { GroupChatBuble } from "../components/GroupChatBuble";
import { Date } from "../components/Date";
import { ChatFooter } from "../components/ChatFooter";
import { useContext, useEffect, useRef, useState } from "react";
import { router, useLocalSearchParams } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { FlashList } from "@shopify/flash-list";
import { GroupHeader } from "../components/GroupHeader";
import { GroupChatFooter } from "../components/GroupChatFooter";
import { WebSocketContext } from "./WebSocketProvider";

export default function singleGroup() {

    const data = useLocalSearchParams();
    const { socket, getChat, setChat } = useContext(WebSocketContext)


    const [getUser, setUser] = useState("")
    // const [getChat, setChat] = useState([])
    const [getIsNew, setIsNew] = useState(false)

    let date;
    let time;

    function toBoolean(value) {
        if (typeof value === 'boolean') {
            return value; 
        }
        if (typeof value === 'string') {
            return value.toLowerCase() === 'true'; 
        }
        return !!value; 
    }

    // useEffect(() => {
    //     (async () => {
    //         let verified = await AsyncStorage.getItem("verified");
    //         let user = await AsyncStorage.getItem("user");

    //         if (verified == null || verified != "true" || user == null) {

    //             await AsyncStorage.removeItem("verified")
    //             await AsyncStorage.removeItem("user")

    //             router.replace("/")
    //         }

    //         // if((typeof data.isNew)=="string"){
    //             setIsNew(toBoolean(data.isNew))
    
    //         // }else{
    //         //     setIsNew(data.isNew==true)
    
    //         // }
    //     })()
    // }, [])

    useEffect(() => {

        (async () => {
            setIsNew(toBoolean(data.isNew))

            try {
                
                let parsedUser;

                if (getUser == null) {
                    let user = await AsyncStorage.getItem("user");

                    parsedUser = JSON.parse(user); // Parse the JSON string to an object
                    setUser(parsedUser); // Set the parsed object in the state

                } else {
                    parsedUser = getUser
                }
            let url = process.env.EXPO_PUBLIC_URL + "/SingleGroup"

            let obj = {
                groupId: data.groupId,
                user:parsedUser
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

        } catch (error) {
            console.log(error)
        }
        })()

    }, [])

    return (
        <SafeAreaView style={styles.safearea}>
            <GroupHeader data={data} />
            <View style={styles.body}>

                <FlashList
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

                        return <GroupChatBuble params={item} isNewDate={isNewDate} isNewTime={isNewTime} />;
                    }}
                    estimatedItemSize={200}
                />
            </View>

            {getIsNew ? (
                <View style={styles.newView}>
                    <Text style={styles.newText}>Join Now to Send Messages</Text>
                </View>
            ) : (
                <GroupChatFooter data={data} func={setChat} />
            )}
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    newText:{
        color:"#ff5b6b",
    },
    newView:{
        width:"100%",
        justifyContent:"center",
        alignItems:"center",
        paddingVertical:15
    },
    body: {
        flexGrow: 1,
        backgroundColor: "rgb(235, 235, 235)"
    },
    safearea: {
        flex: 1
    }
})