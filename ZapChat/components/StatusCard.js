import { Image } from "expo-image"
import { useEffect, useState } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"
import Modal from "react-native-modal";
import SwiperFlatList from "react-native-swiper-flatlist"
import { StatusDisplay } from "./StatusDisplay"
import Animated, { FadeIn, FadeOut } from "react-native-reanimated";

const defaultImage = require("../assets/images/profileDefault.png")
const AnimatedPressable = Animated.createAnimatedComponent(Pressable);

export function StatusCard({ data }) {

    const [getImage, setImage] = useState(defaultImage)
    const [getName, setName] = useState("")
    const [getTime, setTime] = useState("")
    const [getModalStatus, setModalStatus] = useState(false)
    const [getStatusArr, setStatusArr] = useState([])
    const [getAutoPlay, setAutoPlay] = useState(false)

    useEffect(() => {

        if (data.image != "../assets/images/default.svg") {
            setImage({ uri: process.env.EXPO_PUBLIC_URL + data.image })
        }
 
        setName(data.name)
        setTime(data.datetime)

        setStatusArr(data.status)

    }, [data])

    const modalhide = () => {
        setModalStatus(false)
    }

    const modalshow = () => {
        setModalStatus(true)
    }

    return (
        <>
            <AnimatedPressable entering={FadeIn} exiting={FadeOut} style={styles.container} onPress={modalshow}>
                <Image source={getImage} style={styles.image} />
                <View style={styles.textcontainer}>
                    <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                    <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                </View>
            </AnimatedPressable>

            <Modal isVisible={getModalStatus} style={styles.modal} onBackButtonPress={modalhide} onBackdropPress={modalhide}>

                <View style={[styles.container, {marginTop:30,paddingLeft:20 }]}>
                    <Image source={getImage} style={styles.image} />
                    <View style={styles.textcontainer}>
                        <Text style={[styles.name, { color: "white" }]} numberOfLines={1}>{getName}</Text>
                        <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                    </View>
                </View>

                <SwiperFlatList
                    autoplay={getAutoPlay}
                    autoplayDelay={10}
                    showPagination
                    data={getStatusArr}
                    renderItem={({ item }) => (
                        
                        <StatusDisplay data={item} func={setAutoPlay}/>

                    )}
                    style={{width:"100%"}}
                />

            </Modal>

        </>

    )
}

const styles = StyleSheet.create({
    modal: {
        backgroundColor: "black",
        margin: 0,
        flex: 1,
        justifyContent: "center",
    },
    time: {
        color: "#919190",
        fontSize: 13,
    },
    name: {
        fontWeight: "bold",
        fontSize: 15
    },
    textcontainer: {
        paddingLeft: 12,
        width: "85%",
        rowGap: 5
    },
    image: {
        width: 50,
        height: 50,
        alignItems: "center",
        justifyContent: "center",
        borderRadius: 10,
        borderColor:"#ff5b6b",
        borderWidth:2,
        borderStyle:"solid"
    },
    container: {
        width: "100%",
        flexDirection: "row",
        alignItems: "center",
        paddingVertical: 9,
        paddingHorizontal: 10,
    }
})