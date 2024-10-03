import { Image } from "expo-image"
import { useState } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"

export function ChatCard() {

    const [getImage, setStatusimage] = useState(require("../assets/images/Avatar.png"))
    const [getName, setName] = useState("My name ")
    const [getMsg, setMsg] = useState("My Last Message")
    const [getTime, setTime] = useState("2024/12/11")
    return (
        <Pressable style={styles.container}>
            <Image source={getImage} style={styles.image} />
            <View style={styles.textcontainer}>
                <View style={styles.view1}>
                    <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                    <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                </View>
                <Text style={styles.message} numberOfLines={1}>{getMsg}</Text>
                
            </View>
        </Pressable>
    )
}

const styles = StyleSheet.create({
    time:{
        color:"#919190",
        fontSize:13,
    },
    message:{
        // backgroundColor:"red",
        width:"80%",
        fontSize:13,
        color:"#919190"
    },
    name:{
        fontWeight:"bold",
        fontSize:16
    },
    view1: {
        flexDirection:"row",
        justifyContent:"space-between",
        // backgroundColor:"green",
        // width:"100%"
    },
    textcontainer: {
        paddingLeft:12,
        width:"85%",
        rowGap:3
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
        paddingVertical:5,
        paddingHorizontal:10,
    }
})