import { Image } from "expo-image"
import { useEffect, useState } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"

export function ChatCard({data}) {

    const [getImage, setImage] = useState(require("../assets/images/profileDefault.png"))
    const [getName, setName] = useState("")
    const [getLastMsg, setLastMessage] = useState("")
    const [getTime, setTime] = useState("")

    useEffect(()=>{

        // setImage(data.)
        // setName(data.)
        // setLastMessage(data.)
        // setTime(data.)

    },[data])

    return (
        <Pressable style={styles.container}>
            <Image source={getImage} style={styles.image} />
            <View style={styles.textcontainer}>
                <View style={styles.view1}>
                    <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                    <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                </View>
                <Text style={styles.message} numberOfLines={1}>{getLastMsg}</Text>
                
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