import { ImageSlider } from "@pembajak/react-native-image-slider-banner"
import { Image } from "expo-image"
import { router } from "expo-router"
import { useEffect, useState } from "react"
import { Pressable, StyleSheet, Text, View } from "react-native"
import Modal from "react-native-modal";

const defaultImage = require("../assets/images/profileDefault.png")

export function StatusCard({ data }) {

    const [getImage, setImage] = useState(defaultImage)
    const [getName, setName] = useState("")
    const [getTime, setTime] = useState("")
    const [getModalStatus, setModalStatus] = useState(false)
    const [getScrollStatus, setScrollStatus] = useState(false)
    const [getStatusImages, setStatusImages] = useState([
        { img: 'https://vastphotos.com/files/uploads/photos/10642/big-sur-coastline-l.jpg?v=20220712073521' },
        { img: 'https://thumbs.dreamstime.com/b/environment-earth-day-hands-trees-growing-seedlings-bokeh-green-background-female-hand-holding-tree-nature-field-gra-130247647.jpg' },
        { img: 'https://cdn.pixabay.com/photo/2015/04/19/08/32/marguerite-729510__340.jpg' }
    ])
    const [getTextArr, setTextArr] = useState(["abc","def","hij"])
    const [getText, setText] = useState("")

    let index = 0

    useEffect(() => {

        // setText(getTextArr[0])
        // setTextIndex(1)
        
    }, [])

    const modalhide = ()=>{
        setScrollStatus(false)
        setModalStatus(false)
    }

    const modalshow = ()=>{
        setScrollStatus(true)
        setModalStatus(true)
    }

    const nextText = ()=>{

        setText(getTextArr[index])

        if(index < getTextArr.length){
            let newIndex = index+1
            index = newIndex
        }else{
            index = 0
            setText(getTextArr[0])
        }
    }

    return (
        <>
            <Pressable style={styles.container} onPress={modalshow}>
                <Image source={getImage} style={styles.image} />
                <View style={styles.textcontainer}>
                    <Text style={styles.name} numberOfLines={1}>{getName}</Text>
                    <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                </View>
            </Pressable>

            <Modal isVisible={getModalStatus} style={styles.modal} onBackButtonPress={modalhide} onBackdropPress={modalhide}>

                <View style={[styles.container,{position:"absolute",top:50}]}>
                    <Image source={getImage} style={styles.image} />
                    <View style={styles.textcontainer}>
                        <Text style={[styles.name,{color:"white"}]} numberOfLines={1}>{getName}</Text>
                        <Text style={styles.time} numberOfLines={1}>{getTime}</Text>
                    </View>
                </View>

                <ImageSlider
                    data={getStatusImages}
                    onItemChanged={nextText}
                    onClick={() => { setScrollStatus(!getScrollStatus) }}
                    autoPlay={getScrollStatus}
                    timer={8000}
                    indicatorContainerStyle={{position:"absolute",bottom:-200}}
                >
                    <View style={styles.textVew}>
                        <Text style={styles.extratext}>{getText}</Text>
                    </View>
                </ImageSlider>

            </Modal>

        </>

    )
}

const styles = StyleSheet.create({
    extratext:{ 
        color: 'white', 
        fontSize: 15, 
        letterSpacing:2,
        textAlign:"center" 
    },
    textVew:{
        alignItems: 'center' 
    },
    modal:{ 
        backgroundColor: "black", 
        margin: 0, 
        flex:1,
        justifyContent:"center",
        position:"relative"
    },
    msgStatus: {
        width: 17,
        height: 17,
    },
    time: {
        color: "#919190",
        fontSize: 13,
    },
    message: {
        // backgroundColor:"red",
        width: "80%",
        fontSize: 13,
        color: "#919190"
    },
    name: {
        fontWeight: "bold",
        fontSize: 15
    },
    // view1: {
    //     flexDirection: "row",
    //     justifyContent: "space-between",
    //     // backgroundColor:"green",
    //     // width:"100%"
    // },
    textcontainer: {
        paddingLeft: 12,
        width: "85%",
        rowGap: 5
    },
    image: {
        width: 60,
        height: 60,
        // backgroundColor:"red",
        alignItems: "center",
        justifyContent: "center",
        borderRadius: 10
    },
    container: {
        width: "100%",
        flexDirection: "row",
        // backgroundColor:"blue",
        alignItems: "center",
        paddingVertical: 5,
        paddingHorizontal: 10,
    }
})