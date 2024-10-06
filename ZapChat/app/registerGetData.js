import { registerRootComponent } from "expo";
import { Alert, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { router } from "expo-router";
import { useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

export default function registerGetData() {

    const [getFirstName, setFirstName] = useState("")
    const [getLastName, setLastName] = useState("")
    const [getMobile, setMobile] = useState("")
    const [getPassword, setPassword] = useState("")
    const [getReTypePassword, setReTypePassword] = useState("")

    const design = {
        marginTop: 50,
        width: "100%"
    }

    async function request() {

        if (getFirstName.trim() == "") {
            Alert.alert("Please enter your First Name")

        } else if (getLastName.trim() == "") {
            Alert.alert("Please enter your Last Name")

        } else if (getMobile.trim().length != 10) {
            Alert.alert("Mobile number must have 10 numbers")

        } else if (getPassword.trim().length < 8) {
            Alert.alert("Password must be between 8-20 letters")

        } else if (getReTypePassword.trim().length < 8) {
            Alert.alert("Password must be between 8-20 letters")

        } else {

            let url = process.env.EXPO_PUBLIC_URL+"/Register"
            let data = {
                fName: getFirstName,
                lName: getLastName,
                mobile: getMobile,
                password: getPassword,
                reTypePassword: getReTypePassword
            }

            let response = await fetch(url, {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            })
            if (response.ok) {

                let obj = await response.json()
                if (obj.success) {

                    try {

                        await AsyncStorage.setItem("user", obj.data)
                        await AsyncStorage.setItem("verified", JSON.stringify(false))
                        router.push("/verifyRegister")

                    } catch (error) {
                        Alert.alert("Something Went Wrong")
                        console.log(error)
                    }

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
            <Text style={styles.title}>Fill Your Details</Text>
            <ScrollView contentContainerStyle={styles.scrolView}>
                <View style={styles.container}>
                    <InputField params={{ lableText: "First Name", inputMode: "text", secureTextEntry: false, func: setFirstName }} />
                    <InputField params={{ lableText: "Last Name", inputMode: "text", secureTextEntry: false, func: setLastName }} />
                    <InputField params={{ lableText: "Mobile Number", inputMode: "tel", secureTextEntry: false, func: setMobile }} />
                    <InputField params={{ lableText: "Password", inputMode: "text", secureTextEntry: true, func: setPassword }} />
                    <InputField params={{ lableText: "Re-type Password", inputMode: "text", secureTextEntry: true, func: setReTypePassword }} />

                    <Button text={"Next"} style={design} func={request} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}

// registerRootComponent(RegisterGetData)

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: "center",
        width: "80%",
        rowGap: 22
    },
    title: {
        color: "#ff5b6b",
        fontSize: 23,
        fontWeight: "bold",
        // backgroundColor:"green",
        // marginTop: 50,
        paddingLeft: 35,
        paddingVertical: 10
    },
    safearea: {
        flex: 1
    },
    scrolView: {
        flexGrow: 1,
        justifyContent: "flex-start",
        // backgroundColor: "red",
        paddingTop: 30,
        alignItems: "center"
    }
})