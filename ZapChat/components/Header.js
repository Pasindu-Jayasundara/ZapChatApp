import { Image } from "expo-image"
import { BackHandler, Pressable, StyleSheet, Text, TextInput, TouchableWithoutFeedback } from "react-native"
import { useCallback, useEffect, useRef, useState } from "react";
import { View } from "react-native";
import { Tab } from "./Tab";
import { router, useFocusEffect } from "expo-router";
import useStateRef from "react-usestateref";
import AsyncStorage from "@react-native-async-storage/async-storage";

const search = require("../assets/images/search.svg")
const backArrow = require("../assets/images/back-arrow.svg")

export function Header({ searchTextFunc, setCategoryFunc, getCategoryFunc, img, loadHome }) {

    const [getStatus, setStatus] = useState(false)
    const [getText, setText] = useState("")

    const searchButtonPress = (() => {

        let newStatus = getStatus ? false : true
        setStatus(newStatus)
    })

    console.log(img)
    useEffect(() => {
        const handleBackPress = () => {
            searchTextFunc("")
            loadHome()
            if (getStatus) {
                setStatus(false);
                return true;
            }
            return false;
        };

        const backHandler = BackHandler.addEventListener('hardwareBackPress', handleBackPress);

        return () => backHandler.remove();
    }, [getStatus]);

    function searchText(text) {
        setText(text)
        searchTextFunc(text)
    }



    return (
        <>
            {getStatus ? (
                <View style={[styles.iconView, styles.backView]}>
                    <Pressable onPress={searchButtonPress}>
                        <Image source={backArrow} style={styles.icon} />
                    </Pressable>
                    <TextInput value={getText} style={styles.input} placeholder={"Search " + getCategoryFunc + " by name...."} onChangeText={text => searchText(text)} />
                    <Pressable onPress={loadHome}>
                        <Image source={search} style={styles.icon} />
                    </Pressable>
                </View>
            ) : (
                <View style={styles.container}>
                    <View style={styles.container2}>
                        <Text style={styles.title}>ZapChat</Text>
                        <View style={styles.iconView}>
                            <Pressable onPress={searchButtonPress}>
                                <Image source={search} style={styles.icon} />
                            </Pressable>
                            <Pressable onPress={() => { router.push("/profileSetUp") }}>
                                <Image source={img} style={[styles.icon, styles.icon2]} />
                            </Pressable>
                        </View>
                    </View>
                    <Tab setFunc={setCategoryFunc} getFunc={getCategoryFunc} />
                </View>
            )}
        </>
    )
}

const styles = StyleSheet.create({
    icon2: {
        width: 22,
        height: 22,
    },
    backView: {
        justifyContent: "center",
        alignItems: "center",
        paddingVertical: 20
    },
    input: {
        width: '70%',
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
        alignItems: "center",
        columnGap: 15,
    },
    container2: {
        width: "100%",
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        paddingRight: 20,
        paddingVertical: 20,
        paddingLeft: 10
    },
    container: {
        width: "100%",
        justifyContent: "space-between",
        borderBottomColor: "#e3e3e3",
        borderBottomWidth: 1,
        paddingBottom: 8
    }
})