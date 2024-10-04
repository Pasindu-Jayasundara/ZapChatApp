import { Image } from "expo-image";
import { useEffect, useState } from "react";
import { StyleSheet, Text, View } from "react-native";

const sendTick = require("../assets/images/send.svg")
const receivedTick = require("../assets/images/received.svg")
const readTick = require("../assets/images/read.svg")

export function ChatBuble({ params }) {

    const [getTime, setTime] = useState("19:25")
    const [getSide, setSide] = useState("left")
    const [getMessage, setMessage] = useState("left")

    const [getWrapperStyle, setWrapperStyle] = useState({})
    const [getContainerStyle, setContainerStyle] = useState({})
    const [getTextStyle, setTextStyle] = useState({})

    const rightSideWrapper = {
        alignSelf: 'flex-end',
        marginTop: 0,
    }
    const rightSideContainer = {
        backgroundColor: "#afafafa1",
    }
    const rightSideText = {
        color: "black"
    }

    useEffect(() => {
        setSide(params.side)
        setTime(params.time)
        setMessage(params.message)
    }, [params])

    useEffect(() => {
        if (getSide == "right") {
            setWrapperStyle(rightSideWrapper);
            setContainerStyle(rightSideContainer);
            setTextStyle(rightSideText);
        } else {
            setWrapperStyle({});
            setContainerStyle({});
            setTextStyle({});
        }

    }, [getSide])

    return (
        <View style={[styles.wrapper, getWrapperStyle]}>
            <View style={[styles.container, getContainerStyle]}>
                <Text style={[styles.message, getTextStyle]}>{getMessage}</Text>
            </View>
            <View style={styles.detail}>
                <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                {getSide=="right"?<Image source={receivedTick} style={styles.tick} />:""}
            </View>
        </View>
    )
}
const styles = StyleSheet.create({
    detail: {
        flexDirection: "row",
        // backgroundColor:"green",
        justifyContent:"flex-end",
        columnGap:5
    },
    tick: {
        width: 18,
        height: 18
    },
    wrapper: {
        alignSelf: 'flex-start',
        margin: 5,
    },
    container: {
        backgroundColor: "#f52f19a1",
        padding: 10,
        borderRadius: 10,
        maxWidth: '80%',
    },
    message: {
        color: "#fff",
    },
    time: {
        fontSize: 12,
        color: "#999",
        // alignSelf: "flex-start",
        marginTop: 2,
    },
})
