import { Image } from "expo-image"
import { useState, useEffect } from "react"
import { Alert, Pressable, StyleSheet, Text, View } from "react-native"
import { Button } from "./Button"
import AsyncStorage from "@react-native-async-storage/async-storage";
import { router } from "expo-router";

const defaultTeam = require("../assets/images/team.png");

export function GroupHeader({ data }) {

    const [getImage, setImage] = useState(defaultTeam)
    const [getName, setName] = useState("Group name")
    const [getMembers, setMembers] = useState("Members")
    const [getUser, setUser] = useState("")
    const [getIsNew, setIsNew] = useState(false)
    const [getNewStyle, setNewStyle] = useState()

    function toBoolean(value) {
        if (typeof value === 'boolean') {
            return value; 
        }
        if (typeof value === 'string') {
            return value.toLowerCase() === 'true'; 
        }
        return !!value; 
    }

    useEffect(() => {

        if (data.image != "../assets/images/team.png") {
            setImage(process.env.EXPO_PUBLIC_URL + data.image)
        }
        setName(data.name)
        setMembers(data.members + " members")

        // console.log(typeof data.isNew)
        // if(typeof data.isNew=="string"){
        //     let value = data.isNew=="true"
        //     setIsNew(value)
        //     console.log(getIsNew)

        // }else{
            setIsNew(toBoolean(data.isNew))

        // }

        if(getIsNew){
            setNewStyle(styles.space)
        }else{
            setNewStyle(null)
        }
    }, [])

    const joinGroup = async () => {

        try {
            if (getUser == "") {
                let user = await AsyncStorage.getItem("user")
                if (user == null) {

                    await AsyncStorage.removeItem("verified");
                    await AsyncStorage.removeItem("user");

                    router.replace("/")
                } else {
                    setUser(user)
                }
            }

            let url = process.env.EXPO_PUBLIC_URL + "/JoinGroup"

            let obj = {
                groupId: data.groupId,
                user:getUser
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

                    setIsNew(false)
                    setNewStyle()

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

        } catch (error) {
            console.error(error)
        }

    }

    return (
        <Pressable style={[styles.container,getNewStyle]}>
            <Image source={getImage} style={styles.image} />
            <View style={styles.textcontainer}>
                <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                <Text style={styles.message} numberOfLines={1}>{getMembers}</Text>
            </View>
            {getIsNew? (
                <Button text={"Join"} style={styles.joinButton} func={joinGroup} />
            ) : null}
        </Pressable>
    )
}

const styles = StyleSheet.create({
    joinButton: {
        backgroundColor: "#803038",
        width: "auto",
        paddingHorizontal: 20,
        paddingVertical: 8,
        borderRadius: 20
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
        width: "60%",
        rowGap: 3,
        // backgroundColor:"red"
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
        backgroundColor: "white",
        alignItems: "center",
        paddingVertical: 13,
        paddingHorizontal: 15,
        borderBottomColor: "#e3e3e3",
        borderBottomWidth: 1,
    },
    space:{
        justifyContent: "space-between"
    }
})