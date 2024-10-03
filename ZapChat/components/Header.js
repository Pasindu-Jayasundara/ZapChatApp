import { Image } from "expo-image"
import { BackHandler, Pressable, StyleSheet, Text, TextInput, TouchableWithoutFeedback } from "react-native"
import { useEffect, useState } from "react";
import { View } from "react-native";
import { Tab } from "./Tab";

const search = require("../assets/images/search.svg")
const dot = require("../assets/images/dot.svg")
const backArrow = require("../assets/images/back-arrow.svg")

export function Header() {

    const [getStatus, setStatus] = useState(false)
    const searchButtonPress = (() => {

        let newStatus = getStatus ? false : true
        setStatus(newStatus)
    })

    useEffect(() => {
        const handleBackPress = () => {
            if (getStatus) {
                setStatus(false);
                return true;
            }
            return false;
        };

        const backHandler = BackHandler.addEventListener('hardwareBackPress', handleBackPress);

        return () => backHandler.remove();
    }, [getStatus]);

    return (
        <>
            {getStatus ? (
                <View style={[styles.iconView, styles.backView]}>
                    <Pressable onPress={searchButtonPress}>
                        <Image source={backArrow} style={styles.icon} />
                    </Pressable>
                    <TextInput style={styles.input} placeholder="Search ...." />
                </View>
            ) : (
                <View  style={styles.container}>
                    <View style={styles.container2}>
                        <Text style={styles.title}>ZapChat</Text>
                        <View style={styles.iconView}>
                            <Pressable onPress={searchButtonPress}>
                                <Image source={search} style={styles.icon} />
                            </Pressable>
                            <Image source={dot} style={styles.icon} />
                        </View>
                    </View>
                    <Tab />
                </View>
            )}
        </>
    )
}

const styles = StyleSheet.create({
    backView: {
        justifyContent: "center",
        alignItems: "center",
        paddingVertical: 20
    },
    input: {
        width: '80%',
        backgroundColor: "#e3e3e3",
        borderRadius: 5,
        height: 40,
        paddingHorizontal: 10,
        color: "#ff5b6b"
    },
    icon: {
        width: 22,
        height: 22,
    },
    title: {
        color: "#ff5b6b",
        fontWeight: "bold",
        fontSize: 20,
        letterSpacing: 1.5
    },
    iconView: {
        flexDirection: "row",
        columnGap: 15
    },
    container2: {
        width: "100%",
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        paddingRight: 20,
        paddingVertical: 20,
        paddingLeft:10
        // borderBottomColor: "#e3e3e3",
        // borderBottomWidth: 1
        // backgroundColor: "#f5be87"
    },
    container: {
        width: "100%",
        justifyContent: "space-between",
        borderBottomColor: "#e3e3e3",
        borderBottomWidth: 1,
        paddingBottom:8
        // backgroundColor: "#f5be87"
    }
})