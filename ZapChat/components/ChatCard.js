import { Image } from "expo-image"
import { router } from "expo-router"
import { useEffect, useState } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"
import Animated, { FadeIn, FadeOut } from "react-native-reanimated";

// const AnimatedPressable = Animated.createAnimatedComponent(Pressable);

export function ChatCard({ data, getUser, func }) {

    const [getImage, setImage] = useState(require("../assets/images/person-square.svg"))
    const [getName, setName] = useState("")
    const [getLastMsg, setLastMessage] = useState("")
    const [getTime, setTime] = useState("")
    const [getMessageStatus, setMessageStatus] = useState("")
    const [getShowUnreadMessageCount, setShowUnreadMessageCount] = useState(false)

    useEffect(() => {

        if (data.image != "../assets/images/person-square.svg") {
            setImage(process.env.EXPO_PUBLIC_URL + data.image)
        }
        setName(data.name)
        setLastMessage(data.lastMessage)
        setTime(data.datetime)

        let status = data.messageStatus;
        if (status == "Send") {
            setMessageStatus(require("../assets/images/send.svg"))

        } else if (status == "Received") {
            setMessageStatus(require("../assets/images/received.svg"))

        } else if (status == "Read") {
            setMessageStatus(require("../assets/images/read.svg"))

        }

        if (data.unreadCount > 0) {
            setShowUnreadMessageCount(true)
        } else {
            setShowUnreadMessageCount(false)
        }

    }, [data])

    function toBoolean(value) {
        if (typeof value === 'boolean') {
            return value;
        }
        if (typeof value === 'string') {
            return value.toLowerCase() === 'true';
        }
        return !!value;
    }

    function gotoSingleChat() {
        router.push({ pathname: "/singleChat", params: data })
    }

    function deleteChat() {

        Alert.alert(
            "Delete Chat From: " + data.name,
            "Are you sure you want to delete this Chat?",
            [
                {
                    text: "Cancel",
                    onPress: () => console.log("Cancel Pressed"),
                    style: "cancel"
                },
                {
                    text: "Delete",
                    onPress: () => handleDelete()
                }
            ],
            { cancelable: true }
        );
    }

    async function handleDelete() {

        let url = process.env.EXPO_PUBLIC_URL + "/DeleteChat"

        let obj = {
            chatId: data.chatId,
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

                func()

            } else {
                console.log(obj.data)
            }

        } else {
            Alert.alert("Please Try Again Later");
            console.log(response)

        }

    }

    return (
        <Pressable entering={FadeIn} exiting={FadeOut} style={styles.container} onPress={gotoSingleChat} onLongPress={deleteChat}>
            <Image source={getImage} style={styles.image} contentFit="cover" />
            <View style={styles.textcontainer}>
                <View style={styles.view1}>
                    <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                    <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                </View>
                <View style={styles.view1}>
                    <Text style={styles.message} numberOfLines={1}>{getLastMsg}</Text>
                    {getShowUnreadMessageCount ? (
                        <Text style={styles.unreadCountStyle}>{data.unreadCount}</Text>
                    ) : null}
                    {toBoolean(data.showTick) ? (
                        <Image source={getMessageStatus} style={[styles.msgStatus]} />
                    ) : null}
                </View>

            </View>
        </Pressable>
    )
}

const styles = StyleSheet.create({
    unreadCountStyle: {
        fontSize: 7,
        color: "white",
        padding: 2,
        backgroundColor: "#ff5b6b",
        borderRadius: 50
    },
    msgStatus: {
        width: 17,
        height: 17,
    },
    time: {
        color: "#919190",
        fontSize: 13,
    },
    message: {
        // backgroundColor:"red",
        width: "80%",
        fontSize: 13,
        color: "#919190"
    },
    name: {
        fontWeight: "bold",
        fontSize: 16
    },
    view1: {
        flexDirection: "row",
        justifyContent: "space-between",
        // backgroundColor:"green",
        // width:"100%"
    },
    textcontainer: {
        paddingLeft: 12,
        width: "85%",
        rowGap: 3
    },
    image: {
        width: 45,
        height: 45,
        // backgroundColor:"red",
        alignItems: "center",
        justifyContent: "center",
        borderRadius: 10
    },
    container: {
        width: "100%",
        flexDirection: "row",
        // backgroundColor:"blue",
        alignItems: "center",
        paddingVertical: 9,
        paddingHorizontal: 10,
    }
})