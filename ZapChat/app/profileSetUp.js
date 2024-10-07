import { registerRootComponent } from "expo";
import { Alert, ScrollView, StyleSheet, Text, View } from "react-native";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";
import { SafeAreaView } from "react-native-safe-area-context";
import { router } from "expo-router";
import { useEffect, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

export default function profileSetUp() {

    const [getAbout, setAbout] = useState("")
    const [getImageResult, setImageResult] = useState("../assets/images/default.svg")
    const [getUser, setUser] = useState("")
    const [getNewImage, setNewImage] = useState(false)

    useEffect(() => {
        (async () => {

            let verified = await AsyncStorage.getItem("verified");
            let user = await AsyncStorage.getItem("user");

            if (verified == null || verified != "true" || user == null) {

                await AsyncStorage.removeItem("verified")
                await AsyncStorage.removeItem("user")

                router.replace("/")
            }

            const imageJson = await AsyncStorage.getItem("profileImage")
            const image = JSON.parse(imageJson)
            const aboutJson = await AsyncStorage.getItem("profileAbout")
            const about = JSON.parse(aboutJson)

            setImageResult(image)
            setAbout(about)

        })()

    }, [])

    async function request() {

        if (getUser == "") {
            let sessionId = await AsyncStorage.getItem("user")
            if (sessionId == null) {

                await AsyncStorage.removeItem("verified");
                await AsyncStorage.removeItem("user");

                router.replace("/")
            } else {
                setUser(sessionId)
            }
        }

        // if (getImageResult.assets!=null) {
        //     setNewImage(true)
        // } else {
        //     setNewImage(false)
        // }

        // console.warn(getNewImage)

        if (getImageResult.assets!=null) {

            let imageTypeArr = [".png", ".jpg", ".jpeg"]

            if (getImageResult.assets[0].uri.trim() == "") {
                Alert.alert("Missing Image")

            } else if (getImageResult.assets[0].type != "image") {
                Alert.alert("Not a Image")

            } if (!imageTypeArr.includes(getImageResult.assets[0].uri.slice(getImageResult.assets[0].uri.lastIndexOf('.')).toLowerCase())) {
                Alert.alert("Invalid Image Type")

            }
        }

        if (getAbout.trim() == "") {
            Alert.alert("Missing About")

        } else if (getAbout.length > 45) {
            Alert.alert("About Too Long")

        } else {

            let sessionId = getUser
            let url = process.env.EXPO_PUBLIC_URL + "/Profile"

            let formData = new FormData()
            formData.append("about", getAbout)
            formData.append("isNewImage", getImageResult.assets!=null)

            if (getImageResult.assets!=null) {

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
                headers: {
                    'Cookie': `JSESSIONID=${sessionId}`
                }
            })
            if (response.ok) {

                let obj = await response.json()
                if (obj.success) {

                    await AsyncStorage.setItem("profileImage", JSON.stringify(obj.data))
                    await AsyncStorage.setItem("profileAbout", JSON.stringify(getAbout))

                    Alert.alert("Profile Update Success");

                } else {
                    Alert.alert(obj.data);
                    console.log(obj.data)
                }

            } else {
                Alert.alert("Please Try Again Later");
                console.log(response)
            }

        }
    }

    return (
        <SafeAreaView style={styles.safearea}>

            <Text style={styles.title}>My Profile</Text>
            <View style={styles.scrolView}>

                <Profile getFunc={getImageResult} setFunc={setImageResult} />

                <View style={styles.inputFields}>
                    <InputField params={{ lableText: "About", maxLength: 45, func: setAbout, getFunc: getAbout }} />

                    <Button style={styles.btn} text={"Update Profile"} func={request} />
                </View>
            </View>

        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    btn: {
        marginTop: 20,
        width: "100%"
    },
    inputFields: {
        width: "80%",
        marginTop: 70
    },
    safearea: {
        flex: 1
    },
    title: {
        color: "#ff5b6b",
        fontSize: 23,
        fontWeight: "bold",
        // marginTop: 75,
        paddingLeft: 35,
    },
    safearea: {
        flex: 1
    },
    scrolView: {
        flex: 1,
        // justifyContent: "center",
        // backgroundColor: "red",
        alignItems: "center",
        // paddingTop:90
    }
})