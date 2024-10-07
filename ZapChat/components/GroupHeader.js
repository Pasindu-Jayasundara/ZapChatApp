import { Image } from "expo-image"
import { useState,useEffect } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"
import { Button } from "./Button"

export function GroupHeader({data}) {

    const [getImage, setImage] = useState(require("../assets/images/team.png"))
    const [getName, setName] = useState("Group name")
    const [getMembers, setMembers] = useState("Members")

    useEffect(() => {
        if (data.image != "../assets/images/person-square.svg") {
            setImage(process.env.EXPO_PUBLIC_URL+data.image)
        }
        setName(data.name)
        setMembers(data.members+" members")
    }, [])

    const joinGroup = async () =>{

    }

    return (
        <Pressable style={styles.container}>
            <Image source={getImage} style={styles.image} />
            <View style={styles.textcontainer}>
                <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                <Text style={styles.message} numberOfLines={1}>{getMembers}</Text>
            </View>
            <Button text={"Join"} style={styles.joinButton} func={joinGroup}/>
        </Pressable>
    )
}

const styles = StyleSheet.create({
    joinButton:{
        backgroundColor:"#ff5b6b"
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