import { Image } from "expo-image";
import { useEffect, useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { Date } from "./Date";

const sendTick = require("../assets/images/send.svg")
const receivedTick = require("../assets/images/received.svg")
const readTick = require("../assets/images/read.svg")

export function GroupChatBuble({ params, isNewDate, isNewTime }) {

    const [getImage, setImage] = useState(require("../assets/images/person-square.svg"))

    const [getTime, setTime] = useState("")
    const [getSide, setSide] = useState("")
    const [getMessage, setMessage] = useState("")

    const [getWrapperStyle, setWrapperStyle] = useState({})
    const [getContainerStyle, setContainerStyle] = useState({})
    const [getTextStyle, setTextStyle] = useState({})

    const [getTic, setTic] = useState("")
    const [getContentType, setContentType] = useState("")
    const [getFilePath, setFilePath] = useState("")

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
        setContentType(params.type)

        if (params.type == "Message") {
            setMessage(params.message)
        } else if (params.type == "File") {
            setFilePath(params.path)
        }

        if (params.messageStatus == "Send") {
            setTic(sendTick)

        } else if (params.messageStatus == "Received") {
            setTic(receivedTick)

        } else if (params.messageStatus == "Read") {
            setTic(readTick)

        }

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
        <>
            {isNewDate ? <Date date={params.date} /> : ""}

            <View style={styles.outerView}>
                <Image source={getImage} style={styles.image} contentFit="cover" />

                <View style={[styles.wrapper, getWrapperStyle]}>
                    <Text style={[styles.container, styles.name]}>{params.name}</Text>
                    <View style={[styles.container, getContainerStyle]}>
                        <Text style={[styles.message, getTextStyle]}>{getMessage}</Text>
                    </View>
                    <View style={styles.detail}>
                        {isNewTime ? <Text style={styles.time} numberOfLines={1}>{getTime}</Text> : ""}
                        {getSide == "right" ? <Image source={getTic} style={styles.tick} /> : ""}
                    </View>
                </View>
            </View>
        </>
    )
}
const styles = StyleSheet.create({
    outerView:{
        flexDirection:"row",
        alignItems:"flex-start"
    },
    name: {
        fontSize: 15,
        color: "black"
    },
    image: {
        width: 30,
        height: 30,
        // backgroundColor:"red",
        alignItems: "center",
        justifyContent: "center",
        borderRadius: 10
    },
    detail: {
        flexDirection: "row",
        // backgroundColor:"green",
        justifyContent: "flex-end",
        columnGap: 5
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

