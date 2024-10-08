import { Image } from "expo-image"
import { useState,useEffect } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"

export function ChatHeader({data}) {

    const [getImage, setImage] = useState(require("../assets/images/profileDefault.png"))
    const [getName, setName] = useState("My name ")
    const [getAbout, setAbout] = useState("About")

    useEffect(() => {
        // if(data.image!="../assets/images/profileDefault.png"){
        //     setImage(data.image)
        // }
        if (data.image != "../assets/images/person-square.svg") {
            setImage(process.env.EXPO_PUBLIC_URL+data.image)
        }
        setName(data.name)
        setAbout(data.about)
    }, [])

    return (
        <Pressable style={styles.container}>
            <Image source={getImage} style={styles.image} />
            <View style={styles.textcontainer}>
                <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                <Text style={styles.message} numberOfLines={1}>{getAbout}</Text>
            </View>
        </Pressable>
    )
}

const styles = StyleSheet.create({
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
    textcontainer: {
        paddingLeft: 12,
        width: "100%",
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
        backgroundColor:"white",
        alignItems: "center",
        paddingVertical: 13,
        paddingHorizontal: 15,
        borderBottomColor: "#e3e3e3",
        borderBottomWidth: 1,
    }
})