import { Image } from "expo-image";
import { useEffect, useState } from "react";
import { BackHandler, Modal, Pressable, TextInput } from "react-native";
import { StyleSheet, View } from "react-native";
import EmojiModal from 'react-native-emoji-modal-goldin';

const attachIcon = require("../assets/images/attach.svg")
const sendIcon = require("../assets/images/send-fill.svg")

export function ChatFooter() {

    const [getInputFieldHeight, setInputFieldHeight] = useState(40)
    const [getEmoji, setEmoji] = useState("")
    const [getModalStatus, setModalStatus] = useState({ display: "none" })
    const [getText, setText] = useState("")

    const modal = () => {
        if (getModalStatus == '{display:"flex"}') {
            setModalStatus({ display: "none" })
        } else {
            setModalStatus('{display:"flex"}')
        }
    }

    const send =()=>{
        if (getModalStatus == '{display:"flex"}') {
            setModalStatus({ display: "none" })
        } 


    }
    // const text =(text)=>{
    //     setText(getText+text)
    // }
    return (
        <View style={styles.container}>
            <View style={[styles.modalView, getModalStatus]}>
                <EmojiModal onEmojiSelected={(emoji) => { setEmoji(emoji) }} columns={10} />
            </View>
            <Pressable style={styles.pressable} onPress={modal}>
                <Image source={attachIcon} style={styles.icon} />
            </Pressable>
            <TextInput value={getText} style={[styles.input, getInputFieldHeight]} multiline={true} onContentSizeChange={(event) => {
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