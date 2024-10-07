import { Image } from "expo-image"
import { router } from "expo-router"
import { useEffect, useState } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"

export function StatusCard({ data }) {

    const [getImage, setImage] = useState(require("../assets/images/profileDefault.png"))
    const [getName, setName] = useState("")
    const [getLastMsg, setLastMessage] = useState("")
    const [getTime, setTime] = useState("")
    const [getMessageStatus, setMessageStatus] = useState("")

    useEffect(() => {
        console.log(data)
    }, [data])

    return (
        <Pressable style={styles.container} onPress={() => { router.push({ pathname: "/singleChat", params: data }) }}>
            <Image source={getImage} style={styles.image} />
            <View style={styles.textcontainer}>
                <View style={styles.view1}>
                    <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                    <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                </View>
                <View style={styles.view1}>
                    <Text style={styles.message} numberOfLines={1}>{getLastMsg}</Text>
                    <Image source={getMessageStatus} style={[styles.msgStatus]} />
                </View>

            </View>
        </Pressable>
    )
}

const styles = StyleSheet.create({
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
        width: 50,
        height: 50,
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
        paddingVertical: 5,
        paddingHorizontal: 10,
    }
})