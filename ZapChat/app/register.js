import { Image, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Button } from "../components/Button";
import { Link, router } from "expo-router";
import { useState } from "react";

const logoIcon = require("../assets/images/logo.gif");

export default function register() {

    const design = {
        marginTop: 50,
        width: "72%"
    }

    return (
        <SafeAreaView style={styles.safeAreaView}>
            <View style={styles.container}>

                <Image source={logoIcon} style={styles.logo} />
                <View style={styles.textView}>
                    <Text style={styles.text1}>Register</Text>
                    <Text style={styles.text2}>Welcome to ZapChat</Text>
                    <Text style={[styles.text2, styles.text3]}> Stay connected with friends and family through fast, secure messaging</Text>
                </View>
                <Button text={"Get Started"} style={design} func={()=>{router.push("/registerGetData")}} />
                <Text style={styles.linkText}>
                    Already have an Account?
                    <Link href={"/"} style={styles.link}> LogIn</Link>
                </Text>
            </View>
        </SafeAreaView>
    );

}

const styles = StyleSheet.create({
    link: {
        color: "#ff5b6b"
    },
    linkText: {
        color: "#919190",
        alignSelf: "center",
        marginTop: 18
    },
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
    },
    logo: {
        width: 150,
        height: 150
    },
    text3: {
        marginTop: -22,
        fontSize: 16,
        color: "#ff5b6b",
        width: 300
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
});
