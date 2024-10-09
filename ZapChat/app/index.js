import { Alert, Image, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { Link, router } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { createContext, useContext, useEffect, useState } from "react";
import * as SplashScreen from 'expo-splash-screen';

const logoIcon = require("../assets/images/logo.gif");

// const ws = new WebSocket(process.env.EXPO_PUBLIC_URL+"/WebSocket");
// export const socket = createContext(ws)

SplashScreen.preventAutoHideAsync();

export default function index() {

    const [getMobile, setMobile] = useState("")
    const [getPassword, setPassword] = useState("")

    useEffect(() => {

        (async () => {
            try {

                let user = await AsyncStorage.getItem("user")
                if (user != null) {

                    let verified = await AsyncStorage.getItem("verified");
                    
                    if (verified != null && verified == "true") {
                        router.replace("/home")
                    } else {
                        await AsyncStorage.removeItem("verified");
                        await AsyncStorage.removeItem("user");
                    }
                }else{ 
                    SplashScreen.hideAsync();
                }

            } catch (error) {
                console.log(error)
            }
        })()

    }, [])

    async function request() {

        if (getMobile.trim().length == 10) {
            if (getPassword.trim().length >= 8) {

                let url = process.env.EXPO_PUBLIC_URL+"/Login"
                let data = {
                    mobile: getMobile,
                    password: getPassword
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

                            await AsyncStorage.setItem("user", JSON.stringify(obj.data.sessionId))
                            await AsyncStorage.setItem("verified", "true")
                            await AsyncStorage.setItem("profileImage", JSON.stringify(obj.data.profileImage))
                            await AsyncStorage.setItem("profileAbout", JSON.stringify(obj.data.profileAbout))

                            router.replace("/home")

                        } catch (error) {
                            Alert.alert("Something Went Wrong")
                            console.log(error)
                        }

                    } else {
                        Alert.alert(obj.data.msg);
                        console.log(obj.data.msg)
                    }

                } else {
                    Alert.alert("Please Try Again Later");
                    console.log(response)
                }
            } else {
                Alert.alert("Password must be between 8-20 letters")
            }
        } else {
            Alert.alert("Mobile number must have 10 numbers")
        }

    }

    return (
        <SafeAreaView style={styles.safeAreaView}>
            <ScrollView contentContainerStyle={styles.scrollView}>

                <View style={styles.container}>

                    <View style={styles.firstView}>
                        <Image source={logoIcon} style={styles.logo} />
                        <View style={styles.textView}>
                            <Text style={styles.text1}>LogIn</Text>
                            <Text style={styles.text2}>Welcome back to ZapChat</Text>
                            <Text style={[styles.text2, styles.text3]}> Let's connect together !</Text>
                        </View>
                    </View>

                    <View style={styles.secondView}>
                        <View style={styles.fields}>
                            <InputField params={{ lableText: "Mobile", inputMode: "tel", secureTextEntry: false, func: setMobile }} />
                            <InputField params={{ lableText: "Password", inputMode: "text", secureTextEntry: true, func: setPassword }} />
                            <Button text={"Let's Go"} style={{ marginTop: 18 }} func={request} />
                            <Text style={styles.linkText}>
                                New to ZapChat?
                                <Link href={"/register"} style={styles.link}> Register Now</Link>
                            </Text>
                        </View>
                    </View>

                </View>

            </ScrollView>
        </SafeAreaView>
    );

}

const styles = StyleSheet.create({
    link: {
        color: "#ff5b6b"
    },
    linkText: {
        color: "#919190",
        alignSelf: "center"
    },
    fields: {
        width: "75%",
        rowGap: 15,
    },
    scrollView: {
        flexGrow: 1,
        justifyContent: 'center',
    },
    container: {
        flex: 1,
        // backgroundColor: "red",
    },
    logo: {
        width: 150,
        height: 150
    },
    text3: {
        marginTop: -25,
        fontSize: 17,
        color: "#ff5b6b"
    },
    text2: {
        fontSize: 19,
        textAlign: "center"
    },
    text1: {
        fontSize: 30,
        fontWeight: "bold",
        marginTop: 25
    },
    textView: {
        justifyContent: "center",
        alignItems: "center",
        rowGap: 30,
    },
    safeAreaView: {
        flex: 1,
        backgroundColor: "white",
    },
    firstView: {
        flex: 1,
        justifyContent: "flex-end",
        // backgroundColor: "blue",
        alignItems: "center",
    },
    secondView: {
        flex: 1,
        marginTop: 60,
        // backgroundColor: "green",
        alignItems: "center"
    }
});
