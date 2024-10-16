import { Alert, Image, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { Link, router } from "expo-router";
import { createContext, useContext, useEffect, useState } from "react";
import { WebSocketContext } from "./WebSocketProvider";
import useStateRef from "react-usestateref";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useFonts } from 'expo-font';
import * as SplashScreen from 'expo-splash-screen';

const logoIcon = require("../assets/images/logo.gif");
const defImg = require("../assets/images/default.svg");

export default function index() {
    const { getUser, setUser, getHeaderImage, setHeaderImage, } = useContext(WebSocketContext)

    const [getMobile, setMobile] = useState("")
    const [getPassword, setPassword] = useState("")
    const [getButtonText, setButtonText, ref] = useStateRef("Let's Go")

    const check = async () => {

        if (getUser != null) {
            router.replace("/home")
        }
    }
    useEffect(() => {
        check()
    }, [])

    async function request() {

        setButtonText("Wait ...")

        if (getMobile.trim().length == 10) {
            if (getPassword.trim().length >= 8) {

                let obj = {
                    location: "login",
                    mobile: getMobile,
                    password: getPassword
                }

                let url = process.env.EXPO_PUBLIC_URL + "/Login"

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

                        try {

                            let jsonuser = JSON.stringify(obj.data.user)
                            await AsyncStorage.setItem("user", jsonuser)

                            let jsonpi = JSON.stringify(obj.data.profileImage)
                            await AsyncStorage.setItem("profileImage", jsonpi)

                            let jsonab = JSON.stringify(obj.data.profileAbout)
                            await AsyncStorage.setItem("profileAbout", jsonab)

                            setUser(obj.data.user)
                            if (getHeaderImage != "../assets/images/default.svg") {
                                setHeaderImage({ uri: process.env.EXPO_PUBLIC_URL + obj.data.user.profile_image })
                            } else {
                                setHeaderImage(defImg)
                            }

                            router.replace("/home")

                        } catch (error) {
                            console.log(error)

                        }

                    } else {
                        if (obj.data == "Please LogIn") {

                            await AsyncStorage.removeItem("verified");
                            await AsyncStorage.removeItem("user");

                            setUser(null)
                            router.replace("/")
                        } else {
                            Alert.alert(obj.data);
                        }
                        console.log(obj.data)
                    }
                    setButtonText("Let's Go")

                } else {
                    Alert.alert("Please Try Again Later");
                    console.log(response)
                    setButtonText("Let's Go")

                }


            } else {
                Alert.alert("Password must be between 8-20 letters")
                setButtonText("Let's Go")

            }
        } else {
            Alert.alert("Mobile number must have 10 numbers")
            setButtonText("Let's Go")

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
                            <Button text={ref.current} style={{ marginTop: 18 }} func={request} />
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
        alignItems: "center",
    },
    secondView: {
        flex: 1,
        marginTop: 60,
        alignItems: "center"
    }
});
