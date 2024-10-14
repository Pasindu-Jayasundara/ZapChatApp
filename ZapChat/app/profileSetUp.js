import { Alert, ScrollView, StyleSheet, Text, View } from "react-native";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";
import { SafeAreaView } from "react-native-safe-area-context";
import { router } from "expo-router";
import { useContext, useEffect, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { WebSocketContext } from "./WebSocketProvider";
import useStateRef from "react-usestateref";

const defaultProfileImage = require("../assets/images/profile-page-empty.svg")

export default function profileSetUp() {

    const {
        getUser, setUser, getHeaderImage, setHeaderImage
    } = useContext(WebSocketContext)

    const [getAbout, setAbout] = useState("")
    const [getImageResult, setImageResult] = useState("")
    const [getButtonText, setButtonText] = useState("Update Profile")
    const [getTryCount, setTryCount, tryCountRef] = useStateRef(0)

    useEffect(() => {
        (async () => {

            try {
                const aboutJson = await AsyncStorage.getItem("profileAbout")
                const about = JSON.parse(aboutJson)

                setImageResult(getHeaderImage)
                setAbout(about)
            } catch (error) {
                console.log(error)
            }


        })()

    }, [])

    async function request() {

        setButtonText("Updating ...")
        try {
            console.log("user: " + getUser)

            if (getUser != null) {
                setTryCount(0)

                if (getImageResult.assets != null) {

                    let imageTypeArr = [".png", ".jpg", ".jpeg"]

                    if (getImageResult.assets[0].uri.trim() == "") {
                        Alert.alert("Missing Image")

                    } else if (getImageResult.assets[0].type != "image") {
                        Alert.alert("Not a Image")

                    } else if (!imageTypeArr.includes(getImageResult.assets[0].uri.slice(getImageResult.assets[0].uri.lastIndexOf('.')).toLowerCase())) {
                        Alert.alert("Invalid Image Type")

                    }
                }

                if (getAbout.trim() == "") {
                    Alert.alert("Missing About")

                } else if (getAbout.length > 45) {
                    Alert.alert("About Too Long")

                } else {

                    let url = process.env.EXPO_PUBLIC_URL + "/Profile"

                    let formData = new FormData()
                    formData.append("about", getAbout)
                    formData.append("isNewImage", getImageResult.assets != null)
                    formData.append("user", getUser)

                    if (getImageResult.assets != null) {

                        let extention;
                        let invalid;

                        if (getImageResult.assets[0].mimeType == "image/jpeg") {
                            extention = ".jpeg"

                        } else if (getImageResult.assets[0].mimeType == "image/jpg") {
                            extention = ".jpg"

                        } else if (getImageResult.assets[0].mimeType == "image/png") {
                            extention = ".png"

                        } else {
                            Alert.alert("Invalid Extention")
                            invalid = true
                        }

                        if (!invalid) {
                            formData.append("image", {
                                name: "profileImage" + extention,
                                type: getImageResult.assets[0].mimeType,
                                uri: getImageResult.assets[0].uri,
                            })
                            formData.append("extention", extention)
                        }
                    }

                    let response = await fetch(url, {
                        method: "POST",
                        body: formData,
                    })
                    if (response.ok) {

                        let obj = await response.json()
                        if (obj.success) {

                            await AsyncStorage.setItem("profileImage", JSON.stringify(obj.data))
                            setHeaderImage(obj.data)

                            await AsyncStorage.setItem("profileAbout", JSON.stringify(getAbout))

                            Alert.alert("Profile Update Success");
                        } else {
                            Alert.alert(obj.data);
                        }
                        setButtonText("Update Profile")

                    } else {
                        Alert.alert("Please Try Again Later");
                        setButtonText("Update Profile")

                    }

                }

            } else {

                console.log("Trying... " + tryCountRef.current)

                if (tryCountRef.current < 3) {
                    setTryCount(tryCountRef.current++)

                    let user = await AsyncStorage.getItem("user");
                    let parsedUser = await JSON.parse(user);
                    setUser(parsedUser);

                    request()

                } else {
                    router.replace("/")
                }
            }
        } catch (error) {
            console.error(error)
            setButtonText("Update Profile")
        }
    }

    function logout() {

        try {

            Alert.alert(
                "Confirm Logout",
                "Are you sure you want to Logout?",
                [
                    {
                        text: "Cancel",
                        onPress: () => console.log("Cancel Pressed"),
                        style: "cancel"
                    },
                    {
                        text: "OK",
                        onPress: async () => {

                            await AsyncStorage.removeItem("user")
                            await AsyncStorage.removeItem("profileImage")
                            await AsyncStorage.removeItem("profileAbout")

                            setUser(null)

                            router.replace("/")

                        }
                    }
                ],
                { cancelable: false }
            );

        } catch (error) {
            Alert.alert("Please Try Again Later!")
        }

    }

    return (
        <SafeAreaView style={styles.safearea}>

            <Text style={styles.title}>My Profile</Text>
            <View style={styles.scrolView}>

                <Profile getFunc={getImageResult} setFunc={setImageResult} icon={defaultProfileImage} text={"Profile Picture"} style={{}} />

                <View style={styles.inputFields}>
                    <InputField params={{ lableText: "About", maxLength: 45, func: setAbout, getFunc: getAbout }} />

                    <Button style={styles.btn} text={getButtonText} func={request} />
                </View>
                <Button style={styles.logoutBtn} textStyle={{color: "red"}} text={"Log out"} func={logout} />

            </View>


        </SafeAreaView>
    )
}

const styles = StyleSheet.create({

    logoutBtn: {
        marginTop: 20,
        width: "100%",
        backgroundColor: "transparent",
        alignSelf: "center"
    },
    btn: {
        marginTop: 20,
        width: "100%"
    },
    inputFields: {
        width: "80%",
        marginTop: 70
    },
    safearea: {
        flex: 1,
        paddingTop: 20
    },
    title: {
        color: "#ff5b6b",
        fontSize: 23,
        fontWeight: "bold",
        // marginTop: 75,
        paddingLeft: 35,
    },
    scrolView: {
        flex: 1,
        alignItems: "center",
    }
})