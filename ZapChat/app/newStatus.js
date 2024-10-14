import { Alert, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";
import { InputField } from "../components/InputField";
import { useContext, useEffect, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { WebSocketContext } from "./WebSocketProvider";
import { router } from "expo-router";
import useStateRef from "react-usestateref";

const newStatusIcon = require("../assets/images/pencil.png")

export default function newStatus() {

    const { getUser, setUser } = useContext(WebSocketContext)

    const [getStatusText, setStatusText] = useState("")
    const [getStatusButtonText, setStatusButtonText] = useState("Post")
    const [getStatusImage, setStatusImage] = useState("../assets/images/pencil.png")
    const [getTryCount, setTryCount, tryCountRef] = useStateRef(0)

    const addNewStatus = async () => {

        try {

            if (getUser != null) {
                setTryCount(0)
                setStatusButtonText("Posting ...")
                let isImage = false;
                let isText = false;

                let extention;

                if (getStatusImage.assets != null) {

                    let imageTypeArr = [".png", ".jpg", ".jpeg"]

                    if (getStatusImage.assets[0].uri.trim() == "") {
                        Alert.alert("Missing Image")

                    } else if (getStatusImage.assets[0].type != "image") {
                        Alert.alert("Not a Image")

                    } else if (!imageTypeArr.includes(getStatusImage.assets[0].uri.slice(getStatusImage.assets[0].uri.lastIndexOf('.')).toLowerCase())) {
                        Alert.alert("Invalid Image Type")

                    } else {
                        if (getStatusImage.assets[0].mimeType == "image/jpeg") {
                            extention = ".jpeg"
                            isImage = true;

                        } else if (getStatusImage.assets[0].mimeType == "image/jpg") {
                            extention = ".jpg"
                            isImage = true;

                        } else if (getStatusImage.assets[0].mimeType == "image/png") {
                            extention = ".png"
                            isImage = true;

                        } else {
                            Alert.alert("Invalid Extention")
                        }
                    }
                }

                if (getStatusText.trim() != "") {

                    if (getStatusText.length > 150) {
                        Alert.alert("Text Too Long")

                    } else {
                        isText = true
                    }
                }

                let url = process.env.EXPO_PUBLIC_URL + "/AddNewStatus"

                let formData = new FormData()
                formData.append("isImage", isImage)
                formData.append("isText", isText)

                if (isText) {
                    formData.append("text", getStatusText)
                }
                if (isImage) {
                    formData.append("image", {
                        name: "statusImage" + extention,
                        type: getStatusImage.assets[0].mimeType,
                        uri: getStatusImage.assets[0].uri,
                    })
                    formData.append("extention", extention)
                }
                formData.append("user", JSON.stringify(getUser))

                if (isImage || isText) {

                    let response = await fetch(url, {
                        method: "POST",
                        body: formData,
                    })

                    if (response.ok) {

                        console.log("response: "+response)

                        let obj = await response.json()
                        if (obj.success) {

                            setStatusText("")
                            setStatusImage("../assets/images/pencil.png")

                            isImage = false;
                            isText = false;

                            extention = undefined

                            Alert.alert("Status Posting Success");

                        } else {
                            Alert.alert(obj.data);
                            console.log(obj.data)
                        }

                    } else {
                        Alert.alert("Please Try Again Later");
                        console.log(response)
                    }
                } else {
                    Alert.alert("Nothing to Post")
                }
                setStatusButtonText("Post")
            } else {

                console.log("Trying... " + tryCountRef.current)

                if (tryCountRef.current < 3) {
                    setTryCount(tryCountRef.current++)

                    let user = await AsyncStorage.getItem("user");
                    let parsedUser = await JSON.parse(user);
                    setUser(parsedUser);

                } else {
                    router.replace("/")
                }
            }
        } catch (error) {
            console.log(error)
        }

    }

    return (
        <SafeAreaView style={styles.body}>

            <ScrollView contentContainerStyle={styles.modalView}>
                <Text style={styles.title}>Add New Status</Text>
                <Profile getFunc={getStatusImage} setFunc={setStatusImage} icon={newStatusIcon} text={"Status Image"} style={styles.profileview} />
                <View style={styles.bottom}>
                    <InputField params={{ lableText: "Text", secureTextEntry: false, inputMode: "text", maxLength: 45, func: setStatusText, getFunc: getStatusText }} />
                    <Button text={getStatusButtonText} style={[styles.newButton, styles.create]} func={addNewStatus} />
                </View>
            </ScrollView>

        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    profileview: {
        borderColor: "#d1d1d1",
        borderWidth: 3,
        borderStyle: "solid",
        width: 350,
        height: 350,
    },
    title: {
        color: "#ff5b6b",
        fontWeight: "bold",
        fontSize: 20,
        letterSpacing: 1.5,
        alignSelf: "flex-start",
        justifyContent: "flex-start",
        // backgroundColor:"green",
    },
    bottom: {
        marginTop: 60,
        width: "100%"
    },
    create: {
        marginTop: 30,
        width: "100%"
    },
    modalView: {
        justifyContent: "center",
        alignItems: "center",
        // width: "80%",
        // backgroundColor:"red",
        flexGrow: 1,
        paddingHorizontal: 30
    },
    newButton: {
        alignSelf: "center",
        borderRadius: 5,
        width: "92%",
        marginBottom: 25
    },
    icon: {
        width: 22,
        height: 22,
    },
    head: {
        flexDirection: "row",
        columnGap: 15,
        justifyContent: "center",
        alignItems: "center",
        marginBottom: 20
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
    body: {
        flex: 1,
        // backgroundColor:"green"
    }
})