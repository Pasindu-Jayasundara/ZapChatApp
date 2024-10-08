import { Zoomable } from "@likashefqet/react-native-image-zoom";
import { Image } from "expo-image"
import { useEffect, useState } from "react";
import { Dimensions, StyleSheet, Text, View } from "react-native"
import { GestureHandlerRootView } from "react-native-gesture-handler";

const windowWidth = Dimensions.get('window').width;
const img = require("../assets/images/Avatar.png")
export function StatusDisplay({ data, func }) {

    const [getImageLoad, setImageLoad] = useState(true)
    const [getTextLoad, setTextLoad] = useState(true)

    useEffect(() => {

        if (data.isImage == true && !getImageLoad) {
            func(true)
        }

    }, [getImageLoad, getTextLoad])

    return (

        <GestureHandlerRootView style={styles.container}>
            <Zoomable doubleTapScale={3} isDoubleTapEnabled={true} maxScale={5} minScale={1} style={styles.zoom}>

                {data.isImage == true ? (
                    <Image
                        source={data.image}
                        style={[styles.image, { width: windowWidth }]}
                        onLoadStart={() => { setImageLoad(true) }}
                        onLoadEnd={() => { setImageLoad(false) }}
                    />
                ) : null}

                {data.isText == true ? (
                    <Text
                        style={[styles.text, { width: windowWidth }]}
                        onLoadEnd={() => { setTextLoad(false) }}
                        onLoadStart={() => { setTextLoad(true) }}
                    >
                        {data.text}
                    </Text>
                ) : null}

            </Zoomable>
        </GestureHandlerRootView>

    )
}

const styles = StyleSheet.create({
    zoom: {
        justifyContent: "center"
    },
    text: {
        color: "white",
        textAlign: "center",
        fontSize: 15,
        letterSpacing: 2,
        // width:windowWidth,
        // backgroundColor:"red",
        flexWrap: "wrap",
        alignSelf: "center",
        paddingHorizontal: 30
    },
    footer: {
        width: "100%",
        alignItems: 'center',
        // backgroundColor: "blue"
    },
    image: {
        // width: width,
        // height: undefined,
        // aspectRatio: 1,
        contentFit: "contain",
        // alignSelf:"center",
        // backgroundColor: "red",
        minHeight: "50%"
    },
    container: {
        flex: 1,
        // width: width,
        justifyContent: "center",
        // backgroundColor: "green",
        flexDirection: "row",
    }
})