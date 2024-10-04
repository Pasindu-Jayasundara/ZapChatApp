import { registerRootComponent } from "expo";
import { Alert, StyleSheet, Text, View } from "react-native";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { SafeAreaView } from "react-native-safe-area-context";
import { router } from "expo-router";
import { useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

export default function verifyRegister() {

    const [getOtp, setOtp] = useState("")

    async function request() {

        if (getOtp.trim().length < 8) {
            Alert.alert("Otp must be between 8-20 letters")

        } else {

            let sessionId = await AsyncStorage.getItem("user")

            let url = "https://redbird-suitable-conversely.ngrok-free.app/ZapChatBackend/VerifyUser"
            let data = {
                otp: getOtp,
            }

            let response = await fetch(url, {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json",
                    'Cookie': `JSESSIONID=${sessionId}`
                }
            })
            if (response.ok) {

                let obj = await response.json()
                if (obj.success) {

                    await AsyncStorage.setItem("verified", JSON.stringify(true))
                    router.replace("/home")

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
            <View style={styles.container}>
                <Text style={styles.title}>Verify Mobile Number</Text>
                <Text style={styles.subtitle}>An otp number has been send your number via SMS</Text>

                <View style={styles.field}>
                    <InputField params={{ lableText: "OTP", func: setOtp }} />
                    <Button style={{}} text={"Verify Number"} func={request} />
                </View>
            </View>
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    field: {
        marginTop: 40,
        rowGap: 20
    },
    container: {
        justifyContent: "center",
        width: "80%"
    },
    subtitle: {
        fontSize: 15,
    },
    title: {
        color: "#ff5b6b",
        fontSize: 23,
        fontWeight: "bold",
        // backgroundColor:"green",
        marginTop: 50,
        paddingVertical: 10
    },
    safearea: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center"
    },
})