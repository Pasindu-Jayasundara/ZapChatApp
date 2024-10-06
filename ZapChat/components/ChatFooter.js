import AsyncStorage from "@react-native-async-storage/async-storage";
import { Image } from "expo-image";
import { router } from "expo-router";
import { useEffect, useState } from "react";
import { Alert, BackHandler, Modal, Pressable, TextInput } from "react-native";
import { StyleSheet, View } from "react-native";
import EmojiModal from 'react-native-emoji-modal-goldin';

const attachIcon = require("../assets/images/attach.svg")
const sendIcon = require("../assets/images/send-fill.svg")

export function ChatFooter({data,func}) {

    const [getInputFieldHeight, setInputFieldHeight] = useState(40)
    const [getModalStatus, setModalStatus] = useState({ display: "none" })
    const [getText, setText] = useState("")

    const modal = () => {
        if (getModalStatus == '{display:"flex"}') {
            setModalStatus({ display: "none" })
        } else {
            setModalStatus({ display: "flex" })
        }
    }

    const send = async () => {
        if (getModalStatus == '{display:"flex"}') {
            setModalStatus({ display: "none" })
        }

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

                let url = process.env.EXPO_PUBLIC_URL+"/SendMessage"

                let obj = {
                    otherUserId: data.userId,
                    contentType:"Message",
                    content:getText
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

                        func(obj.data)
                        setText("")

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

    }


    const type = (text) => {
        setText(text)
    }
    return (
        <View style={styles.container}>
            <View style={[styles.modalView, getModalStatus]}>
                <EmojiModal onEmojiSelected={(emoji) => { type(emoji) }} columns={10} />
            </View>
            <Pressable style={styles.pressable} onPress={modal}>
                <Image source={attachIcon} style={styles.icon} />
            </Pressable>
            <TextInput onChangeText={text => type(text)} value={getText} style={[styles.input, getInputFieldHeight]} multiline={true} onContentSizeChange={(event) => {
                setInputFieldHeight(event.nativeEvent.contentSize.height);
            }} />
            <Pressable style={styles.pressable} onPress={send}>
                <Image source={sendIcon} style={styles.icon} />
            </Pressable>
        </View>
    )
}

const styles = StyleSheet.create({

    modalView: {
        backgroundColor: "green",
        position: "absolute",
        left: 0,
        right: 0,
        bottom: 90,
    },
    modal: {
        backgroundColor: "black",
    },
    pressable: {
        // backgroundColor:"red",
        justifyContent: "center",
        alignItems: "center",
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
    icon: {
        width: 25,
        height: 25,
    },
    container: {
        width: "100%",
        flexDirection: "row",
        justifyContent: "space-between",
        paddingHorizontal: 15,
        paddingVertical: 25,
        alignContent: "center"
    }

})