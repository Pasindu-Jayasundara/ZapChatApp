import AsyncStorage from "@react-native-async-storage/async-storage";
import { Image } from "expo-image";
import { router } from "expo-router";
import { useContext, useEffect, useState } from "react";
import { Alert, BackHandler, Pressable, TextInput } from "react-native";
import { StyleSheet, View } from "react-native";
import EmojiModal from 'react-native-emoji-modal-goldin';
import Modal from "react-native-modal";
import * as ImagePicker from 'expo-image-picker';
import { WebSocketContext } from "../app/WebSocketProvider";
import useStateRef from "react-usestateref";

const attachIcon = require("../assets/images/attach.svg")
const emojiIcon = require("../assets/images/emoji.svg")
const sendIcon = require("../assets/images/send-fill.svg")

export function GroupChatFooter({ data }) {

    const { socket, getUser, setUser } = useContext(WebSocketContext)

    const [getInputFieldHeight, setInputFieldHeight] = useState(40)
    // const [getModalStatus, setModalStatus] = useState({ display: "none" })
    const [getText, setText] = useState("")
    const [getEmojiModal, setEmojiModal] = useState(false)
    const [getImage, setImage] = useState("")
    const [getTryCount, setTryCount, tryCountRef] = useStateRef(0)

    const send = async () => {
        setEmojiModal(false)

        try {

            if (getUser != null) {
                setTryCount(0)

                // if (socket && socket.readyState == socket.OPEN) {

                //     let obj = {
                //         location: "send_group_chat",
                //         groupId: data.groupId,
                //         contentType: "Message",
                //         content: getText,
                //         user: getUser
                //     }

                //     socket.send(JSON.stringify(obj))

                // }

                let obj = {
                    location: "send_group_chat",
                    groupId: data.groupId,
                    contentType: "Message",
                    content: getText,
                    user: getUser
                }
                let url = process.env.EXPO_PUBLIC_URL + "/SendGroupMessage"

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

                        setText("")

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
                    setResent(!getResent)

                } else {
                    Alert.alert("Please Try Again Later");
                    console.log(response)
                }

            } else {

                console.log("Trying... " + tryCountRef.current)

                if (tryCountRef.current < 3) {
                    setTryCount(tryCountRef.current++)

                    let user = await AsyncStorage.getItem("user");
                    let parsedUser = await JSON.parse(user);
                    setUser(parsedUser);

                    send()

                } else {
                    router.replace("/")
                }
            }
        } catch (error) {
            console.error(error)
        }

    }

    const pickImage = async () => {
        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.All,
            allowsEditing: true,
            aspect: [4, 3],
            quality: 1,
        });

        console.log(result);

        if (!result.canceled) {
            setImage(result.assets[0].uri);
        }
    };

    return (
        <View style={styles.container}>

            <Modal isVisible={getEmojiModal} onBackButtonPress={() => { setEmojiModal(false) }} onBackdropPress={() => { setEmojiModal(false) }}>
                <View style={styles.modalView}>
                    <EmojiModal onEmojiSelected={(emoji) => setText((prevText) => prevText + emoji)} columns={10} />
                </View>
            </Modal>
            <>
                <Pressable style={styles.pressable} onPress={pickImage}>
                    <Image source={attachIcon} style={styles.icon} />
                </Pressable>
                <Pressable style={styles.pressable} onPress={() => { setEmojiModal(!getEmojiModal) }}>
                    <Image source={emojiIcon} style={styles.icon} />
                </Pressable>
            </>
            <TextInput onChangeText={text => setText(text)} value={getText} style={[styles.input, getInputFieldHeight]} multiline={true} onContentSizeChange={(event) => {
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
        // justifyContent: "space-around",
        alignItems: "center",
        flexDirection: "row"
    },
    input: {
        backgroundColor: "#e3e3e3",
        borderRadius: 5,
        minHeight: 40,
        maxHeight: 120,
        paddingHorizontal: 10,
        color: "#ff5b6b",
        width: "75%",
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